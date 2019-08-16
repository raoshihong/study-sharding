package com.rao.study.sharding.service;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Maps;
import com.rao.study.sharding.algorithm.DataBasePreciseShardingAlgorithmImpl;
import com.rao.study.sharding.algorithm.TablePreciseShardingAlgorithmImpl;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Properties;

public class StandardShardingStrategyService {
    public Map<String,DataSource> createDataSourceMap(){
        //配置数据源,如果是分库则需要配置多个数据源,如果只是分表,则只需要配置一个数据源

        Map<String, DataSource> dataSourceMap = Maps.newHashMap();


        //第一个数据源
        DruidDataSource dataSource1 = new DruidDataSource();
        dataSource1.setDriverClassName("org.gjt.mm.mysql.Driver");
        dataSource1.setUrl("jdbc:mysql://localhost:3306/standard_sharding0");
        dataSource1.setUsername("root");
        dataSource1.setPassword("123456");

        dataSourceMap.put("standard_sharding0",dataSource1);//这里的key必须与数据库的名称一致,因为在 TableRule中会使用这个key与下面的actualDataNodes进行匹配

        //第二个数据源
        DruidDataSource dataSource2 = new DruidDataSource();
        dataSource2.setDriverClassName("org.gjt.mm.mysql.Driver");
        dataSource2.setUrl("jdbc:mysql://localhost:3306/standard_sharding1");
        dataSource2.setUsername("root");
        dataSource2.setPassword("123456");

        dataSourceMap.put("standard_sharding1",dataSource2);

        //第三个数据源
        DruidDataSource dataSource3 = new DruidDataSource();
        dataSource3.setDriverClassName("org.gjt.mm.mysql.Driver");
        dataSource3.setUrl("jdbc:mysql://localhost:3306/standard_sharding2");
        dataSource3.setUsername("root");
        dataSource3.setPassword("123456");

        dataSourceMap.put("standard_sharding2",dataSource3);

        return dataSourceMap;
    }

    public  void standardShardingStrategy() throws Exception{
        //配置对应的表规则,以t_order表为例,${0..1}使用的是groovy的语法,表示0-1的循环
        //standard_sharding${0..2}.t_order${0..2} 这种写法表示笛卡尔积
        TableRuleConfiguration orderTableRuleConfiguration = new TableRuleConfiguration("t_order","standard_sharding${0..2}.t_order${0..2}");//设置逻辑表表名和物理表表明,包括物理数据库

        //配置分库策略,指明数据库的分片根据t_order中的user_id来分片
        orderTableRuleConfiguration.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("user_id",new DataBasePreciseShardingAlgorithmImpl()));

        //配置分表策略,自定义分片算法,指明表的分片根据t_order中的order_id来分片
        orderTableRuleConfiguration.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("order_id",new TablePreciseShardingAlgorithmImpl()));

        //自定义主键分片策略,这里使用了默认提供的SNOWFLAKE分布式主键生成策略
//        KeyGeneratorConfiguration keyGeneratorConfiguration = new KeyGeneratorConfiguration("SNOWFLAKE","user_id");
//        orderTableRuleConfiguration.setKeyGeneratorConfig(keyGeneratorConfiguration);

        //配置分片规则 (分片规则包括数据库和表的分片规则)
        ShardingRuleConfiguration shardingRuleConfiguration = new ShardingRuleConfiguration();
        shardingRuleConfiguration.getTableRuleConfigs().add(orderTableRuleConfiguration);//添加表规则

        //获取数据源
        DataSource dataSource = ShardingDataSourceFactory.createDataSource(createDataSourceMap(),shardingRuleConfiguration,new Properties());

        Connection conn = dataSource.getConnection();
        PreparedStatement pstmt = null;
        for(int i=0;i<10;i++){
            pstmt = conn.prepareStatement("select max(order_id) from t_order");
            pstmt.executeQuery();
            int count = pstmt.getResultSet().getInt(1);

            pstmt =  conn.prepareStatement("insert into t_order(order_id,user_id,created_at) values(?,?,?)");//这里写的sql,表名用的是逻辑表名
            pstmt.setInt(1,count+1);//user_id必须传递值,不然策略算法无法进行计算
            pstmt.setInt(2,i);//order_id也必须传递值,因为TablePreciseShardingAlgorithmImpl表分片算法中需要使用到order_id的值,进行算法计算
            pstmt.setString(3,"2019-10-16 06:10:33");
            pstmt.execute();
        }

        pstmt = conn.prepareStatement("select * from t_order ");
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()){
            System.out.println(rs.getInt(1)+"=="+rs.getString(2) + "=="+rs.getDate(3));
        }
    }
}
