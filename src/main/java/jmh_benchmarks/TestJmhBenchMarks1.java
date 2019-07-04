package jmh_benchmarks;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 可选参数：
 * Throughput: 整体吞吐量，例如“1秒内可以执行多少次调用”。
 * AverageTime: 调用的平均时间，例如“每次调用平均耗时xxx毫秒”。
 * SampleTime: 随机取样，最后输出取样结果的分布，例如“99%的调用在xxx毫秒以内，99.99%的调用在xxx毫秒以内”
 * SingleShotTime: 以上模式都是默认一次 iteration 是 1s，唯有 SingleShotTime 是只运行一次。往往同时把 warmup 次数设为0，用于测试冷启动时的性能。
 * All(“all”, “All benchmark modes”);
 *
 */
@BenchmarkMode(Mode.AverageTime)

/**
 * 统计时间单位：ms
 */
@OutputTimeUnit(TimeUnit.MILLISECONDS)

/**
 * 可选参数
 * Thread: 该状态为每个线程独享。
 * Group: 该状态为同一个组里面所有线程共享。
 * Benchmark: 所有线程共享实例。
 */
@State(Scope.Benchmark)

/**
 * 一个测试方法执行多少轮测试（每轮测试都会新起一个ForkMain java进程进行测试，以防止jvm虚拟机优化等影响测试结果）
 */
@Fork(value = 2, jvmArgs = {"-Xms1G", "-Xmx1G"})

/**
 * 预热次数，运行结果不算入统计结果
 */
@Warmup(iterations = 3)

/**
 * 一轮测试运行次数
 */
@Measurement(iterations = 8)

/**
 * 每个进程中的测试线程数
 */
//@Threads(8)
public class TestJmhBenchMarks1 {

    /**
     * 指定参数，多个参数时，每个参数会运行一次测试方法
     */
    @Param({"1000000"})
    private int N;

    private List<String> DATA_FOR_TESTING;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(TestJmhBenchMarks1.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    /**
     * 运行测试前执行
     *      默认参数：
     *          Level.Trial：运行测试前执行一次
     */
    @Setup
    public void setup() {
        DATA_FOR_TESTING = createData();
    }

    /**
     * 可选参数
     * Trial【默认参数】：在每次Benchmark的之前/之后执行。
     * Iteration：在每次Benchmark的iteration的之前/之后执行。
     * Invocation：每次调用Benchmark标记的方法之前/之后都会执行。
     */
    @TearDown
    public void teardown() {

    }

    /**
     * 标识为测试方法
     */
    @Benchmark
    public void loopFor(Blackhole bh) {
        for (int i = 0; i < DATA_FOR_TESTING.size(); i++) {
            String s = DATA_FOR_TESTING.get(i); //take out n consume, fair with foreach
            bh.consume(s);
        }
    }

    @Benchmark
    public void loopWhile(Blackhole bh) {
        int i = 0;
        while (i < DATA_FOR_TESTING.size()) {
            String s = DATA_FOR_TESTING.get(i);
            bh.consume(s);
            i++;
        }
    }

    @Benchmark
    public void loopForEach(Blackhole bh) {
        for (String s : DATA_FOR_TESTING) {
            bh.consume(s);
        }
    }

    @Benchmark
    public void loopIterator(Blackhole bh) {
        Iterator<String> iterator = DATA_FOR_TESTING.iterator();
        while (iterator.hasNext()) {
            String s = iterator.next();
            bh.consume(s);
        }
    }

    private List<String> createData() {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            data.add("Number : " + i);
        }
        return data;
    }

}

