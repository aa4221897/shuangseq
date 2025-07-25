# shuangseq - 彩票查询应用

## 质量保障流程

### 本地开发
```bash
# 运行完整检查
./scripts/run_checks.sh

# 仅运行测试
./gradlew test jacocoTestReport

# 检查覆盖率
./scripts/check_coverage.sh
```

### 预提交要求
- 单元测试必须全部通过
- 代码覆盖率≥80%
- 性能基准测试无退化

### CI流程
每次push/PR触发：
1. 运行单元测试
2. 生成覆盖率报告
3. 执行性能基准
4. 验证API契约

[查看最新CI结果](https://github.com/your-repo/actions)

## 核心功能
- 双色球、大乐透等彩票开奖结果查询
- 历史开奖数据展示
- 开奖倒计时功能
- 奖金计算功能

## 技术栈
- Android SDK
- Kotlin
- Jetpack Compose

## 注意事项
本项目为私人使用，不包含任何广告和追踪代码。
