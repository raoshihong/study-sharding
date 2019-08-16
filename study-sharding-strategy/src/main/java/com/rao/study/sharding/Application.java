package com.rao.study.sharding;

import com.rao.study.sharding.service.ComplexShardingStrategyService;
import com.rao.study.sharding.service.InlineShardingStrategyService;
import com.rao.study.sharding.service.StandardShardingStrategyService;

public class Application {

    public static void main(String[] args) throws Exception{

//        new InlineShardingStrategyService().inlineShardingStrategy();

//        new StandardShardingStrategyService().standardShardingStrategy();
        new ComplexShardingStrategyService().complexShardingStrategy();
    }
}
