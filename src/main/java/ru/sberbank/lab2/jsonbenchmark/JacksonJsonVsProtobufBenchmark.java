package ru.sberbank.lab2.jsonbenchmark;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.protobuf.ProtobufMapper;
import com.fasterxml.jackson.dataformat.protobuf.schema.ProtobufSchema;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class JacksonJsonVsProtobufBenchmark {

    @Benchmark
    public void serializationAndDeserializationJson(Blackhole blackhole) throws Exception {
        // Создаем объект DTO
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1);
        userDTO.setName("John Doe");
        userDTO.setAge(30);

        // Сериализуем объект в JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(userDTO);

        // Десериализуем объект из JSON
        UserDTO deserializedUserDTO = mapper.readValue(json, UserDTO.class);
    }

    @Benchmark
    public void serializationAndDeserializationProtobuf(Blackhole blackhole) throws Exception {
        // Создаем объект DTO
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1);
        userDTO.setName("John Doe");
        userDTO.setAge(30);

        ProtobufMapper mapper = new ProtobufMapper();
        ProtobufSchema schema = mapper.generateSchemaFor(UserDTO.class);

        // Сериализуем объект в Protobuf
        byte[] bytes = mapper.writer(schema).writeValueAsBytes(userDTO);

        // Десериализуем объект из Protobuf
        UserDTO deserializedUserDTO = mapper.readerFor(UserDTO.class).with(schema).readValue(bytes);
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(JacksonJsonVsProtobufBenchmark.class.getSimpleName())
                .warmupIterations(10)
                .measurementIterations(10)
                .forks(1)
                .timeout(TimeValue.seconds(30))
                .build();

        new Runner(options).run();
    }
}