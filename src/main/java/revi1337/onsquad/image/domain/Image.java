package revi1337.onsquad.image.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

@Getter
@Entity
public class Image {

    private static final byte[] PNG_MAGIC_BYTE = {(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A}; // PNG
    private static final byte[] JPG_MAGIC_BYTE = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}; // JPG, JPEG
    private static final Set<byte[]> WHITELIST_MAGIC_BYTES = Set.of(PNG_MAGIC_BYTE, JPG_MAGIC_BYTE);
    private static final byte[] DEFAULT_IMAGE = new byte[0];

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "image", nullable = false)
    private byte[] image;

    public Image() {
        this.image = DEFAULT_IMAGE;
    }

    public Image(byte[] binary) {
        validate(binary);
        this.image = binary;
    }

    public void validate(byte[] binary) {
        if (binary == null) {
            throw new NullPointerException("이미지는 null 일 수 없습니다."); // TODO 커스텀 익셉션 필요
        }

        validateMagicByte(binary);
    }

    private void validateMagicByte(byte[] binary) {
        int counter = 0;
        for (byte[] whitelistMagicByte : WHITELIST_MAGIC_BYTES) {
            byte[] binaryMagicByte = Arrays.copyOfRange(binary, 0, whitelistMagicByte.length);
            if (!Arrays.equals(whitelistMagicByte, binaryMagicByte)) {
                counter += 1;
            }
        }
        if (counter == WHITELIST_MAGIC_BYTES.size()) {
            throw new IllegalArgumentException("대표이미지는 jpg, jpeg, png 만 가능합니다."); // TODO 커스텀 익셉션 필요.
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Image image)) return false;
        return id != null && Objects.equals(getId(), image.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}