package com.rao.study.sharding.algorithm;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * 对数据库分片的算法实现
 */
public class DataBasePreciseShardingAlgorithmImpl implements PreciseShardingAlgorithm<Integer> {

    int splitCount = 3;//数据库分了3个,所以这里为3

    /**
     * 因为是对数据库进行分片,所以availableTargetNames传递过来的是已配置的所有数据库名称
     * 根据shardingValue的值进行算法计算,计算后再根据相关的判断,返回指定的availableTargetName,表示分配到指定数据库中去
     * @param availableTargetNames
     * @param shardingValue
     * @return
     */
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Integer> shardingValue) {
        //在这里实现自己的分片策略算法
        for (String availableTargetName:availableTargetNames){
            if (availableTargetName.endsWith(String.valueOf(shardingValue.getValue()%splitCount))) {//这里就表示匹配那个库的后缀相同,则分配到那个库中去
                return availableTargetName;
            }
        }
        throw new UnsupportedOperationException();
    }

}
