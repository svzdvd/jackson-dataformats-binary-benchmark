package net.davidesavazzi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import org.apache.commons.io.IOUtils;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.openjdk.jmh.annotations.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(value = 1, jvmArgsAppend = {"-Xmx2048m", "-server"})
@Measurement(iterations = 10, time = 3, timeUnit = TimeUnit.SECONDS)
@Warmup(iterations = 20, time = 3, timeUnit = TimeUnit.SECONDS)
@State(Scope.Thread)
public class JsonDeserializeTest {

    private String jsonString;
    private String cborString;
    private byte[] cborBytes;
    private String smileString;
    private byte[] smileBytes;
    private String messagePackString;
    private byte[] messagePackBytes;
    private byte[] gzipBytes;

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

        jsonString = JsonSerializeTest.resourceAsString("/generated.json");
        JsonNode object = jsonObjectMapper.readValue(jsonString, JsonNode.class);

        cborBytes = cborObjectMapper.writeValueAsBytes(object);
        cborString = getEncoder().encodeToString(cborBytes);

        smileBytes = smileObjectMapper.writeValueAsBytes(object);
        smileString = getEncoder().encodeToString(smileBytes);

        messagePackBytes = messagePackObjectMapper.writeValueAsBytes(object);
        messagePackString = getEncoder().encodeToString(messagePackBytes);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        IOUtils.copy(new StringReader(jsonString), gzipOutputStream, Charset.defaultCharset());
        gzipOutputStream.close();
        gzipBytes = byteArrayOutputStream.toByteArray();

        System.out.println("\nJSON:    " + jsonString.getBytes().length);
        System.out.println("CBOR:    " + cborBytes.length);
        System.out.println("SMILE:   " + smileBytes.length);
        System.out.println("MSGPACK: " + messagePackBytes.length);
        System.out.println("GZIP: " + byteArrayOutputStream.toByteArray().length);
    }

    @Benchmark
    public JsonNode deserializeStringWithJsonObjectMapper() throws IOException {
        return jsonObjectMapper.readValue(jsonString, JsonNode.class);
    }

    @Benchmark
    public JsonNode deserializeStringWithCborObjectMapper() throws IOException {
        return cborObjectMapper.readValue(getDecoder().decode(cborString), JsonNode.class);
    }

    @Benchmark
    public JsonNode deserializeStringWithSmileObjectMapper() throws IOException {
        return smileObjectMapper.readValue(getDecoder().decode(smileString), JsonNode.class);
    }

    @Benchmark
    public JsonNode deserializeStringWithMsgPackObjectMapper() throws IOException {
        return messagePackObjectMapper.readValue(getDecoder().decode(messagePackString), JsonNode.class);
    }

    @Benchmark
    public JsonNode deserializeWithCborObjectMapper() throws IOException {
        return cborObjectMapper.readValue(cborBytes, JsonNode.class);
    }

    @Benchmark
    public JsonNode deserializeWithSmileObjectMapper() throws IOException {
        return smileObjectMapper.readValue(smileBytes, JsonNode.class);
    }

    @Benchmark
    public JsonNode deserializeWithMsgPackObjectMapper() throws IOException {
        return messagePackObjectMapper.readValue(messagePackBytes, JsonNode.class);
    }

    @Benchmark
    public JsonNode deserializeGzipWithJsonObjectMapper() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(gzipBytes);
        GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
        StringWriter stringWriter = new StringWriter();
        IOUtils.copy(gzipInputStream, stringWriter, Charset.defaultCharset());

        return jsonObjectMapper.readValue(stringWriter.toString(), JsonNode.class);
    }
}
