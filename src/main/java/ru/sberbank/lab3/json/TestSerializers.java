package ru.sberbank.lab3.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.protobuf.InvalidProtocolBufferException;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import ru.sberbank.lab3.protobuf.Album;
import ru.sberbank.lab3.protobuf.AlbumProtos;


import java.io.IOException;
import java.util.concurrent.TimeUnit;


@BenchmarkMode(Mode.AverageTime)
public class TestSerializers {

    @Benchmark
    public void testSerializeJSON(BenchmarkState state, Blackhole blackhole) throws JsonProcessingException {
        blackhole.consume(state.getObjectMapper().writeValueAsString(state.getAlbum()));
    }

    @Benchmark
    public void testDeserializeJSON(BenchmarkState state, Blackhole blackhole) throws IOException {
        blackhole.consume(state.getObjectMapper().readValue(state.getAlbumJson(), Album.class));
    }

    @Benchmark
    public void testSerializeProtobuf(BenchmarkState state, Blackhole blackhole) {
        blackhole.consume(state.getAlbumProtoBytes());
    }

    @Benchmark
    public void testDeserializeProtobuf(BenchmarkState state, Blackhole blackhole) throws InvalidProtocolBufferException {
        blackhole.consume(AlbumProtos.Album.parseFrom(state.getAlbumProtoBytes()));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(TestSerializers.class.getSimpleName())
                .warmupIterations(3)
                .measurementIterations(10)
                .threads(1)
                .forks(1)
                .timeUnit(TimeUnit.NANOSECONDS)
                .build();

        new Runner(opt).run();
    }
}