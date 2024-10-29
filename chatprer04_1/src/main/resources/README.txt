1. 参数获取方式
   ChunkContext
   延迟绑定
-- 启动参数：batchDate(date)=2023/04/06 -longKey=11111

2. 参数校验
    DefaultJobParametersValidator
    自己实现JobParametersValidator
fileName=abc.txt -name=benson

3. 递增给定的参数
    默认RunIdIncrementer run.id
    实现JobParametersIncrementer

4. 监听JobExecutionEvent
    JobExecutionListener
    JobExecutionListenerSupport
    实现JobExecutionListener