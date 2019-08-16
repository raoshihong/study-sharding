package com.rao.study.sharding;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Maps;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Properties;

public class Application {

    /**
     * 创建真实的数据库和表(物理数据库和物理表)
     * @throws Exception
     */
    public static void createActualDBTable() throws Exception{
        Class.forName("org.gjt.mm.mysql.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/","root","123456");
        PreparedStatement preparedStatement = connection.prepareStatement("create database sharding0");
        preparedStatement.execute();
        preparedStatement.execute("use sharding0;");
        preparedStatement.execute("create table t_user0(user_id bigint(20) auto_increment primary key,user_name varchar(100))");
        preparedStatement.execute("create database sharding1");
        preparedStatement.execute("use sharding1;");
        preparedStatement.execute("create table t_user1(user_id bigint(20) auto_increment primary key,user_name varchar(100))");
    }

    /**
     * 使用sharding-jdbc进行逻辑表的拆分策略处理
     * @throws Exception
     */
    public static void useLogic() throws Exception{
        //配置数据源,如果是分库则需要配置多个数据源,如果只是分表,则只需要配置一个数据源

        Map<String, DataSource> dataSourceMap = Maps.newHashMap();


        //第一个数据源
        DruidDataSource dataSource1 = new DruidDataSource();
        dataSource1.setDriverClassName("org.gjt.mm.mysql.Driver");
        dataSource1.setUrl("jdbc:mysql://localhost:3306/sharding0");
        dataSource1.setUsername("root");
        dataSource1.setPassword("123456");

        dataSourceMap.put("sharding0",dataSource1);//这里的key必须与数据库的名称一致,因为在 TableRule中会使用这个key与下面的actualDataNodes进行匹配

        //第二个数据源
        DruidDataSource dataSource2 = new DruidDataSource();
        dataSource2.setDriverClassName("org.gjt.mm.mysql.Driver");
        dataSource2.setUrl("jdbc:mysql://localhost:3306/sharding1");
        dataSource2.setUsername("root");
        dataSource2.setPassword("123456");

        dataSourceMap.put("sharding1",dataSource2);

        //配置对应的表规则,以t_user表为例,${0..1}使用的是groovy的语法,表示0-1的循环
        // sharding${0..1}.t_user${0..1} 表达式 表示的是有这么几种情况, sharding0.t_user0,sharding0.t_user1,sharding1.t_user0,sharding1.t_user1 ,如果只想sharding0中只有t_user0, sharding1中只有t_user1
        // 那么写法为sharding${0}.t_user${0},sharding${1}.t_user${1}
        TableRuleConfiguration userTableRuleConfiguration = new TableRuleConfiguration("t_user","sharding${0}.t_user${0},sharding${1}.t_user${1}");//设置逻辑表表名和物理表表明,包括物理数据库

        //配置分库策略,数据库的名称根据哪个字段进行拆分存储数据  , 这里表示使用user_id这个字段的数据作为拆分,如果user_id为奇数的存到sharding1库中,user_id为偶数的存到sharding0库中
        /**
         * InlineShardingStrategyConfiguration 行表达式策略配置器,可以直接使用表达式,内置groovy脚本执行
         */
        userTableRuleConfiguration.setDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("user_id","sharding${user_id % 2}"));//根据user_id数据取模,查找物理数据库

        //配置分表策略,数据表的名称根据哪个字段进行拆分存储数据, 这里表示使用user_id这个字段作为数据作为拆分,如果user_id为奇数的存到t_user1表中,user_id为偶数的存到t_user0表中
        userTableRuleConfiguration.setTableShardingStrategyConfig(new InlineShardingStrategyConfiguration("user_id","t_user${user_id % 2}"));//根据user_id数据取模,查找物理表

        //自定义主键分片策略,这里使用了默认提供的SNOWFLAKE分布式主键生成策略
//        KeyGeneratorConfiguration keyGeneratorConfiguration = new KeyGeneratorConfiguration("SNOWFLAKE","user_id");
//        userTableRuleConfiguration.setKeyGeneratorConfig(keyGeneratorConfiguration);

        //配置分片规则 (分片规则包括数据库和表的分片规则)
        ShardingRuleConfiguration shardingRuleConfiguration = new ShardingRuleConfiguration();
        shardingRuleConfiguration.getTableRuleConfigs().add(userTableRuleConfiguration);//添加表规则

        //获取数据源
        DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap,shardingRuleConfiguration,new Properties());

        //通过数据源进行数据库操作,下面直接采用原生的jdbc进行操作
        Connection conn = dataSource.getConnection();
//        PreparedStatement pstmt = conn.prepareStatement("insert into t_user(user_name) values(?)");//这里写的sql,表名用的是逻辑表名
//        for(int i=0;i<10;i++){
//            pstmt.setString(1,"test_"+i);
//            pstmt.execute();
//        }


        PreparedStatement pstmt = null;
        for(int i=0;i<10;i++){
            pstmt = conn.prepareStatement("select max(user_id) from t_user");
            ResultSet rs = pstmt.executeQuery();
            int count = rs.getInt(1);
            pstmt =  conn.prepareStatement("insert into t_user(user_id,user_name) values(?,?)");//这里写的sql,表名用的是逻辑表名
            pstmt.setInt(1,count+1);
//            pstmt.setInt(1,i);//如果没有设置主键策略，则这里的user_id必须要主动传递,因为只有提前知道user_id的值了才知道分配到哪个表去
            pstmt.setString(2,"test_"+i);
            pstmt.execute();
        }

        pstmt = conn.prepareStatement("select * from t_user ");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()){
            System.out.println(rs.getInt(1)+"=="+rs.getString(2));
        }
    }


    public static void main(String[] args) throws Exception{

//        createActualDBTable();

        useLogic();

    }

}
