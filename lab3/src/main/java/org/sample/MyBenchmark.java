/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

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
