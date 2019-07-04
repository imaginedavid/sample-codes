package jmh_benchmarks.official_sample;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 运行结果
 *
 * Benchmark                                (size)  Mode  Cnt     Score     Error  Units
 * JMHSample_34_SafeLooping.measureRight_1       1  avgt    5     3.864 ±   1.476  ns/op
 * JMHSample_34_SafeLooping.measureRight_1      10  avgt    5    19.925 ±   0.760  ns/op
 * JMHSample_34_SafeLooping.measureRight_1     100  avgt    5   198.117 ±  30.487  ns/op
 * JMHSample_34_SafeLooping.measureRight_1    1000  avgt    5  1850.113 ± 118.319  ns/op
 * JMHSample_34_SafeLooping.measureRight_2       1  avgt    5     2.979 ±   0.635  ns/op
 * JMHSample_34_SafeLooping.measureRight_2      10  avgt    5    16.496 ±   0.783  ns/op
 * JMHSample_34_SafeLooping.measureRight_2     100  avgt    5   158.098 ±   8.446  ns/op
 * JMHSample_34_SafeLooping.measureRight_2    1000  avgt    5  1520.177 ±  56.877  ns/op
 * JMHSample_34_SafeLooping.measureWrong_1       1  avgt    5     2.606 ±   0.179  ns/op
 * JMHSample_34_SafeLooping.measureWrong_1      10  avgt    5     3.568 ±   0.856  ns/op
 * JMHSample_34_SafeLooping.measureWrong_1     100  avgt    5     6.461 ±   0.212  ns/op
 * JMHSample_34_SafeLooping.measureWrong_1    1000  avgt    5    25.249 ±   0.558  ns/op
 * JMHSample_34_SafeLooping.measureWrong_2       1  avgt    5     2.560 ±   0.104  ns/op
 * JMHSample_34_SafeLooping.measureWrong_2      10  avgt    5     4.858 ±   0.165  ns/op
 * JMHSample_34_SafeLooping.measureWrong_2     100  avgt    5    30.473 ±  13.705  ns/op
 * JMHSample_34_SafeLooping.measureWrong_2    1000  avgt    5   286.102 ±  14.522  ns/op
 */

@State(Scope.Thread)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_34_SafeLooping {
    /*
     * JMHSample_11_Loops warns about the dangers of using loops in @Benchmark methods.
     * Sometimes, however, one needs to traverse through several elements in a dataset.
     * This is hard to do without loops, and therefore we need to devise a scheme for
     * safe looping.
     */
    /*
     * Suppose we want to measure how much it takes to execute work() with different
     * arguments. This mimics a frequent use case when multiple instances with the same
     * implementation, but different data, is measured.
     */
    static final int BASE = 42;
    static int work(int x) {
        return BASE + x;
    }
    /*
     * Every benchmark requires control. We do a trivial control for our benchmarks
     * by checking the benchmark costs are growing linearly with increased task size.
     * If it doesn't, then something wrong is happening.
     */
    @Param({"1", "10", "100", "1000"})
    int size;
    int[] xs;
    @Setup
    public void setup() {
        xs = new int[size];
        for (int c = 0; c < size; c++) {
            xs[c] = c;
        }
    }
    /*
     * First, the obviously wrong way: "saving" the result into a local variable would not
     * work. A sufficiently smart compiler will inline work(), and figure out only the last
     * work() call needs to be evaluated. Indeed, if you run it with varying $size, the score
     * will stay the same!
     */
    @Benchmark
    public int measureWrong_1() {
        int acc = 0;
        for (int x : xs) {
            acc = work(x);
        }
        return acc;
    }
    /*
     * Second, another wrong way: "accumulating" the result into a local variable. While
     * it would force the computation of each work() method, there are software pipelining
     * effects in action, that can merge the operations between two otherwise distinct work()
     * bodies. This will obliterate the benchmark setup.
     *
     * In this example, HotSpot does the unrolled loop, merges the $BASE operands into a single
     * addition to $acc, and then does a bunch of very tight stores of $x-s. The final performance
     * depends on how much of the loop unrolling happened *and* how much data is available to make
     * the large strides.
     */
    @Benchmark
    public int measureWrong_2() {
        int acc = 0;
        for (int x : xs) {
            acc += work(x);
        }
        return acc;
    }
    /*
     * Now, let's see how to measure these things properly. A very straight-forward way to
     * break the merging is to sink each result to Blackhole. This will force runtime to compute
     * every work() call in full. (We would normally like to care about several concurrent work()
     * computations at once, but the memory effects from Blackhole.consume() prevent those optimization
     * on most runtimes).
     */
    @Benchmark
    public void measureRight_1(Blackhole bh) {
        for (int x : xs) {
            bh.consume(work(x));
        }
    }
    /*
     * DANGEROUS AREA, PLEASE READ THE DESCRIPTION BELOW.
     *
     * Sometimes, the cost of sinking the value into a Blackhole is dominating the nano-benchmark score.
     * In these cases, one may try to do a make-shift "sinker" with non-inlineable method. This trick is
     * *very* VM-specific, and can only be used if you are verifying the generated code (that's a good
     * strategy when dealing with nano-benchmarks anyway).
     *
     * You SHOULD NOT use this trick in most cases. Apply only where needed.
     */
    @Benchmark
    public void measureRight_2() {
        for (int x : xs) {
            sink(work(x));
        }
    }
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public static void sink(int v) {
        // IT IS VERY IMPORTANT TO MATCH THE SIGNATURE TO AVOID AUTOBOXING.
        // The method intentionally does nothing.
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_34_SafeLooping.class.getSimpleName())
                .warmupIterations(3)
                .measurementIterations(5)
                .forks(1)
                .build();
        new Runner(opt).run();
    }
}
