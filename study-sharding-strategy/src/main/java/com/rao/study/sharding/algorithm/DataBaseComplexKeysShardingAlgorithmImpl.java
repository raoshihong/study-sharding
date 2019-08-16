package com.rao.study.sharding.algorithm;

import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import java.util.Collection;

public class DataBaseComplexKeysShardingAlgorithmImpl implements ComplexKeysShardingAlgorithm {
    @Override
    public Collection<String> doSharding(Collection availableTargetNames, ComplexKeysShardingValue shardingValue) {

        //根据多个字段来分片
        return null;
    }
}
