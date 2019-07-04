TestJmhBenchMarks1例子有详细注解说明


############ 主要结果说明（以TestJmhBenchMarks1结果为例）

Benchmark                            (N)  Mode  Cnt  Score   Error  Units
TestJmhBenchMarks1.loopFor       1000000  avgt    8  7.287 ± 0.377  ms/op
TestJmhBenchMarks1.loopForEach   1000000  avgt    8  7.543 ± 0.307  ms/op
TestJmhBenchMarks1.loopIterator  1000000  avgt    8  7.629 ± 0.159  ms/op
TestJmhBenchMarks1.loopWhile     1000000  avgt    8  7.417 ± 0.255  ms/op

- Benchmark：测试方法
- (N)：@param参数
- mode：avgt平均时间
- Cnt：一个测试方法运行次数
- Score：值为（7.287 ± 0.377），执行一次测试方法的平均时间
- Error：通常为空
- Units：统计时间单位


############ 基准测试注意点

# 无用代码消除（Dead Code Elimination）
例子：JMHSample_08_DeadCode
说明：使用return返回结果，如有多个返回结果，使用Blackhole

# 常量折叠（Constant Folding）
例子：JMHSample_10_ConstantFold
说明：常量折叠是一种现代编译器优化策略，例如，i = 320 * 200 * 32，编译器会辨识出语句的结构，并在编译时期将数值计算出来（i = 2,048,000），
在微基准测试中，如果你的计算输入是可预测的，也不是一个@State实例变量，那么很可能会被JIT给优化掉。对此，JMH的建议是：1.永远从@State实例中读取你的方法输入；2.返回你的计算结果；3.或者考虑使用BlackHole对象；

# 循环展开（Loop Unwinding）
例子：JMHSample_34_SafeLooping
说明：由于编译器可能会对你的代码进行循环展开，因此JMH建议不要在你的测试方法中写任何循环。如果确实需要执行循环计算，可以结合@BenchmarkMode(Mode.SingleShotTime)和@Measurement(batchSize = N)来达到同样的效果
循环展开例子：
for (i = 1; i <= 60; i++)
  a[i] = a[i] * b + c;
可以展开成：
for (i = 1; i <= 60; i+=3)
{
 a[i] = a[i] * b + c;
 a[i+1] = a[i+1] * b + c;
 a[i+2] = a[i+2] * b + c;
}