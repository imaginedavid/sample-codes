package jmh_benchmarks.sample;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 运行结果
 *
 * 线程：1
 * Benchmark                         Mode  Cnt    Score   Error   Units
 * TestJmhLongAdder.testAtomicLong  thrpt    5  157.126 ± 4.327  ops/us
 * TestJmhLongAdder.testLongAdder   thrpt    5  103.983 ± 1.000  ops/us
 * TestJmhLongAdder.testSync        thrpt    5   57.526 ± 3.976  ops/us
 *
 * 线程：5
 * Benchmark                         Mode  Cnt   Score   Error   Units
 * TestJmhLongAdder.testAtomicLong  thrpt    5  64.417 ± 1.622  ops/us
 * TestJmhLongAdder.testLongAdder   thrpt    5  42.911 ± 4.436  ops/us
 * TestJmhLongAdder.testSync        thrpt    5  18.593 ± 0.319  ops/us
 *
 * 线程池：10
 * Benchmark                         Mode  Cnt   Score   Error   Units
 * TestJmhLongAdder.testAtomicLong  thrpt    5  51.583 ± 3.852  ops/us
 * TestJmhLongAdder.testLongAdder   thrpt    5  49.303 ± 6.087  ops/us
 * TestJmhLongAdder.testSync        thrpt    5  18.512 ± 0.293  ops/us
 *
 * 线程池：30
 * Benchmark                         Mode  Cnt   Score   Error   Units
 * TestJmhLongAdder.testAtomicLong  thrpt    5  59.443 ± 5.355  ops/us
 * TestJmhLongAdder.testLongAdder   thrpt    5  62.675 ± 2.132  ops/us
 * TestJmhLongAdder.testSync        thrpt    5  12.260 ± 0.237  ops/us
 *
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@Fork(1)
@State(Scope.Benchmark)
public class TestJmhLongAdder {

    private static AtomicLong count = new AtomicLong();
    private static LongAdder longAdder = new LongAdder();
    private Object obj = new Object();
    private int i;

    @Benchmark
    @Threads(5)
    public long testAtomicLong() {
        return count.getAndIncrement();
    }

    @Benchmark
    @Threads(5)
    public long testLongAdder() {
        longAdder.increment();
        return longAdder.longValue();
    }

    @Benchmark
    @Threads(5)
    public long testSync() {
        synchronized (obj) {
            return i++;
        }
    }
}
