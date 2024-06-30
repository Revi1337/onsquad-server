package revi1337.onsquad.image.domain.vo;

import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum SupportAttachmentType {

    JPEG(new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}),
    JPG(new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}),
    PNG(new byte[]{(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A}),
    SVG(new byte[]{(byte) 0x3C, (byte) 0x3F, (byte) 0x78, (byte) 0x6D, (byte) 0x6C, (byte) 0x20, (byte) 0x76, (byte) 0x65, (byte) 0x72, (byte) 0x73, (byte) 0x69, (byte) 0x6F, (byte) 0x6E, (byte) 0x3D});

    private final byte[] magicByte;

    public byte[] getMagicByte() {
        return magicByte.clone();
    }

    public static EnumSet<SupportAttachmentType> defaultEnumSet() {
        return EnumSet.allOf(SupportAttachmentType.class);
    }

    public static String convertSupportedTypeString() {
        return defaultEnumSet().stream()
                .map(type -> type.toString().toLowerCase())
                .collect(Collectors.joining(", "));
    }
}
