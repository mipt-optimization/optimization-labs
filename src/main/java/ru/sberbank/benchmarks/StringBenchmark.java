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

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class StringBenchmark {

    @Benchmark
    public String stringConcatinationDefault() {
        String initial = "Hello";
        for (int i = 0; i < 1_000; i++) {
            initial += i;
        }
        return initial;
    }

    @Benchmark
    public String stringConcatWithStringBuffer() {
        StringBuffer buffer = new StringBuffer("Hello");
        for (int i = 0; i < 1_000; i++) {
            buffer.append(i);
        }
        return buffer.toString();
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(StringBenchmark.class.getSimpleName())
                .warmupIterations(2)
                .measurementIterations(10)
                .threads(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
