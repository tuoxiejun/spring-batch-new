1. 基础SpringBatch创建
2. 参数传递相关案例
-- 基本参数传递
java -jar .\chatprer03_1-1.0-SNAPSHOT.jar name=Benson
java -jar .\chatprer03_1-1.0-SNAPSHOT.jar "batchDate(date)=2023/01/01"
java -jar .\chatprer03_1-1.0-SNAPSHOT.jar "longKey(long)=202300000000000"
java -jar .\chatprer03_1-1.0-SNAPSHOT.jar "doubleKey(double)=202.33"

-- 非识别性参数传递
java -jar .\chatprer03_1-1.0-SNAPSHOT.jar "longKey(long)=20230000000001 -name=nancy"
java -jar .\chatprer03_1-1.0-SNAPSHOT.jar "longKey(long)=20230000000001 -name=benson"



--