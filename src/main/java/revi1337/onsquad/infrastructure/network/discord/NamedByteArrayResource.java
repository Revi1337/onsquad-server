package revi1337.onsquad.infrastructure.network.discord;

import org.springframework.core.io.ByteArrayResource;

public class NamedByteArrayResource extends ByteArrayResource {

    private final String fileName;

    public NamedByteArrayResource(String fileName, byte[] byteArray) {
        super(byteArray);
        this.fileName = fileName;
    }

    @Override
    public String getFilename() {
        return this.fileName;
    }
}
