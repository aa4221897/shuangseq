#!/bin/bash
# 数据库异步改造迁移脚本

echo "1. 验证新旧Repository实现"
grep -r "LotteryRepositoryContract" app/src/main/java/

echo "2. 更新DI配置"
sed -i 's/LotteryRepository/LotteryRepositoryV2/g' app/src/main/java/com/example/myapplication/di/RepositoryModule.kt

echo "3. 替换服务层引用"
find app/src/main/java/ -type f -name "*.kt" -exec sed -i '
    s/import com.example.myapplication.deepseek.data.LotteryRepository/import com.example.myapplication.deepseek.data.v2.LotteryRepository/g
    s/LotteryRepository(/LotteryRepositoryV2(/g
' {} +

echo "4. 遗留方法标记"
find app/src/main/java/ -type f -name "*.kt" -exec sed -i '
    /fun getRecords(/i\    @Deprecated("Migrated to Flow API", ReplaceWith("records"))
' app/src/main/java/com/example/myapplication/deepseek/data/LotteryRepository.kt

echo "迁移完成！请执行以下操作验证："
echo "1. 运行单元测试： ./gradlew test"
echo "2. 手动检查过时方法调用： grep -r \"getRecords(\" app/src/main/java/"