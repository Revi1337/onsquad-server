package revi1337.onsquad.common.fixture;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import org.springframework.mock.web.MockMultipartFile;

public class RequestFixture {

    public static final String DEFAULT_MULTIPART_NAME = "file";

    public static MockMultipartFile JSON_MULTIPART(String name, String originalFileName, String content) {
        return new MockMultipartFile(name, originalFileName, APPLICATION_JSON_VALUE, content.getBytes(UTF_8));
    }

    public static MockMultipartFile JSON_MULTIPART(String name, String content) {
        return new MockMultipartFile(name, name, APPLICATION_JSON_VALUE, content.getBytes(UTF_8));
    }

    public static MockMultipartFile PNG_MULTIPART(String name, String originalFileName) {
        byte[] contents = {
                (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A
        };
        return withMultipart(name, originalFileName, contents);
    }

    private static MockMultipartFile withMultipart(String name, String originalFileName, byte[] bytes) {
        return new MockMultipartFile(name, originalFileName, MULTIPART_FORM_DATA_VALUE, bytes);
    }
}
