package ru.sberbank.benchmarks;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ArrayListBenchmark {

    private static final int initialCapacity = 3000;

    @Benchmark
    public List<Integer> simpleListInstantiation() {
        List<Integer> list = new ArrayList<>(initialCapacity);
        for (int i = 0; i < initialCapacity; i++) {
            list.add(i);
        }
        return list;
    }

    @Benchmark
    public List<Integer> streamModificationList() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < initialCapacity; i++) {
            list.add(i);
        }
        return list;
    }



    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(ArrayListBenchmark.class.getSimpleName())
                .warmupIterations(2)
                .measurementIterations(10)
                .threads(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
