package com.vsware.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import static java.util.Base64.getEncoder;
import static org.openjdk.jmh.util.FileUtils.readAllLines;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(value = 1, jvmArgsAppend = {"-Xmx2048m", "-server"})
@Measurement(iterations = 10, time = 3, timeUnit = TimeUnit.SECONDS)
@Warmup(iterations = 20, time = 3, timeUnit = TimeUnit.SECONDS)
@State(Scope.Thread)
public class JsonSerializeTest {

    private JsonNode object;

    private ObjectMapper jsonObjectMapper;
    private ObjectMapper cborObjectMapper;
    private ObjectMapper smileObjectMapper;
    private ObjectMapper messagePackObjectMapper;

    @Setup
    public void setup() throws Exception {
        jsonObjectMapper = new ObjectMapper(new MappingJsonFactory());
        cborObjectMapper = new ObjectMapper(new CBORFactory());
        smileObjectMapper = new ObjectMapper(new SmileFactory());
        messagePackObjectMapper = new ObjectMapper(new MessagePackFactory());

        String jsonString = resourceAsString("/generated.json");
        object = jsonObjectMapper.readValue(jsonString, JsonNode.class);
    }

    @Benchmark
    public String serializeWithJsonObjectMapper() throws JsonProcessingException {
        return jsonObjectMapper.writeValueAsString(object);
    }

    @Benchmark
    public String serializeStringWithCborObjectMapper() throws JsonProcessingException {
        return getEncoder().encodeToString(cborObjectMapper.writeValueAsBytes(object));
    }

    @Benchmark
    public String serializeStringWithSmileObjectMapper() throws JsonProcessingException {
        return getEncoder().encodeToString(smileObjectMapper.writeValueAsBytes(object));
    }

    @Benchmark
    public String serializeStringWithMsgPackObjectMapper() throws JsonProcessingException {
        return getEncoder().encodeToString(messagePackObjectMapper.writeValueAsBytes(object));
    }

    @Benchmark
    public byte[] serializeWithCborObjectMapper() throws JsonProcessingException {
        return cborObjectMapper.writeValueAsBytes(object);
    }

    @Benchmark
    public byte[] serializeWithSmileObjectMapper() throws JsonProcessingException {
        return smileObjectMapper.writeValueAsBytes(object);
    }

    @Benchmark
    public byte[] serializeWithMsgPackObjectMapper() throws JsonProcessingException {
        return messagePackObjectMapper.writeValueAsBytes(object);
    }

    public static String resourceAsString(String jsonFileName) throws IOException {
        return String.join("", readAllLines(new InputStreamReader(
                JsonSerializeTest.class.getResourceAsStream(jsonFileName))));
    }
}
