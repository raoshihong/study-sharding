自定义策略算法
在最新的sharding-jdbc中已经提供了几个比较全的策略配置器
NoneShardingStrategyConfiguration   不需要分片
StandardShardingStrategyConfiguration  标准分片策略
InlineShardingStrategyConfiguration   使用行表达式分片策略
ComplexShardingStrategyConfiguration   复杂的分片策略
HintShardingStrategyConfiguration  强制分片策略(在一些应用场景中，分片条件并不存在于SQL，而存在于外部业务逻辑。因此需要提供一种通过外部指定分片结果的方式，在ShardingSphere中叫做Hint。)


上面除了第一个不需要分片的策略配置，其他都需要提供分片算法
StandardShardingStrategyConfiguration  -->  PreciseShardingAlgorithm 精准分片算法

ComplexShardingStrategyConfiguration   --->  ComplexKeysShardingAlgorithm  赋值分片算法

HintShardingStrategyConfiguration     ---->   HintShardingAlgorithm  强制分片算法

如果我们要使用上面的分片策略配置器,则需要实现对应的分片算法接口