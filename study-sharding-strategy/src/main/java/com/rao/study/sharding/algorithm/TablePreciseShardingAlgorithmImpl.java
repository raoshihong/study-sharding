package com.rao.study.sharding.algorithm;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * 自定义表的分片策略算法
 */
public class TablePreciseShardingAlgorithmImpl implements PreciseShardingAlgorithm<Integer> {
    int splitCount = 3;
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Integer> shardingValue) {
        //在这里实现自己的分片策略算法
        for(String availableTargetName:availableTargetNames){
            if (availableTargetName.endsWith(String.valueOf(shardingValue.getValue()%splitCount))) {
                return availableTargetName;
            }
        }
        throw new UnsupportedOperationException();
    }
}
