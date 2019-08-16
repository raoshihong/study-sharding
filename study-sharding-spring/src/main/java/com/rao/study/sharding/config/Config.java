package com.rao.study.sharding.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

@Configuration
public class Config {

    //1.创建两个数据源
    @Bean(name = "sharding0")
    public DataSource createDataSource1(){
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName("org.gjt.mm.mysql.Driver");
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/sharding0");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("123456");
        return druidDataSource;
    }

    @Bean(name = "sharding1")
    public DataSource createDataSource2(){
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName("org.gjt.mm.mysql.Driver");
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/sharding1");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("123456");
        return druidDataSource;
    }

    //创建分片策略
    @Bean
    public ShardingRuleConfiguration shardingRuleConfiguration(){

        TableRuleConfiguration tableRuleConfiguration = new TableRuleConfiguration("t_user","sharding${0}.t_user${0},sharding${1}.t_user${1}");
        //数据库和表的策略计算
        tableRuleConfiguration.setDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("user_id","sharding${user_id %2}"));
        tableRuleConfiguration.setTableShardingStrategyConfig(new InlineShardingStrategyConfiguration("user_id","t_user${user_id % 2}"));

        ShardingRuleConfiguration shardingRuleConfiguration = new ShardingRuleConfiguration();
        shardingRuleConfiguration.getTableRuleConfigs().add(tableRuleConfiguration);
        return shardingRuleConfiguration;
    }


    //通过分片，返回DataSource
    @Bean
    public DataSource dataSource(Map<String,DataSource> dataSourceMap,ShardingRuleConfiguration shardingRuleConfiguration){
        try {
            return ShardingDataSourceFactory.createDataSource(dataSourceMap,shardingRuleConfiguration,new Properties());
        }catch (Exception e){

        }
        return null;
    }

    @Bean
    public DataSourceTransactionManager transactitonManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
//    @Primary
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mappers/*.xml"));
        return bean.getObject();
    }

    @Bean
    @Primary
    public SqlSessionTemplate testSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
