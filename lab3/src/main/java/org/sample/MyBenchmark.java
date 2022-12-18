package org.sample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openjdk.jmh.annotations.*;
import org.sample.jackson.Dto;
import org.sample.protobuf.DtoMessage;

import java.io.IOException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

@BenchmarkMode(AverageTime)
@OutputTimeUnit(NANOSECONDS)
@Warmup(iterations = 5, time = 500, timeUnit = MILLISECONDS)
@Measurement(iterations = 5, time = 1000, timeUnit = MILLISECONDS)
public class MyBenchmark {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Dto jacksonDto = new Dto("Naruto", "Uzumaki", 20);
    private static final byte[] jacksonDtoString;
    private static final DtoMessage protobufDto = DtoMessage.newBuilder()
            .setName("Naruto").setLastname("Uzumaki").setAge(20).build();
    private static final byte[] protobufDtoString = protobufDto.toByteArray();

    static {
        try {
            jacksonDtoString = objectMapper.writeValueAsBytes(jacksonDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    public void jacksonSerialize() throws JsonProcessingException {
        objectMapper.writeValueAsString(jacksonDto);
    }

    @Benchmark
    public void jacksonDeserialize() throws IOException {
        objectMapper.readValue(jacksonDtoString, Dto.class);
    }

    @Benchmark
    public void protobufSerialize() {
        protobufDto.toByteString();
    }

    @Benchmark
    public void protobufDeserialize() throws IOException {
        DtoMessage.parseFrom(protobufDtoString);
    }

    @Benchmark
    public void empty() {
    }
}
