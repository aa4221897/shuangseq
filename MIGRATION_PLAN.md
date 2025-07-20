# 架构迁移计划

## 预测引擎解耦
需修改文件：
1. `DeepSeekService.kt`
```kotlin
// 替换为：
private val analysisEngine = EngineAdapter()
```

## 执行步骤
1. 创建功能开关配置
2. 分批次灰度迁移
3. 监控性能指标
4. 最终移除旧实现
```