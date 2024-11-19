package revi1337.onsquad.image.domain.vo;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SupportAttachmentType {

    JPG_JPEG(
            new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF},
            new int[][]{}
    ),
    JPEG_JFIF(
            new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, (byte) 0x00, (byte) 0x10, (byte) 0x4A,
                    (byte) 0x46, (byte) 0x49, (byte) 0x46, (byte) 0x00, (byte) 0x01},
            new int[][]{}
    ),
    JPEG_EXIF(
            new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE1, (byte) 0x90, (byte) 0x90, (byte) 0x45,
                    (byte) 0x78, (byte) 0x69, (byte) 0x66, (byte) 0x00, (byte) 0x00},
            new int[][]{{4, 6}}
    ),
    PNG(
            new byte[]{(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A,
                    (byte) 0x0A},
            new int[][]{}
    ),
    SVG(
            new byte[]{(byte) 0x3C, (byte) 0x3F, (byte) 0x78, (byte) 0x6D, (byte) 0x6C, (byte) 0x20, (byte) 0x76,
                    (byte) 0x65, (byte) 0x72, (byte) 0x73, (byte) 0x69, (byte) 0x6F, (byte) 0x6E, (byte) 0x3D},
            new int[][]{}
    ),
    WEBP(
            new byte[]{(byte) 0x52, (byte) 0x49, (byte) 0x46, (byte) 0x46, (byte) 0x90, (byte) 0x90, (byte) 0x90,
                    (byte) 0x90, (byte) 0x57, (byte) 0x45, (byte) 0x42, (byte) 0x50},
            new int[][]{{4, 8}}
    );

    private final byte[] magicByte;
    private final int[][] partialOffsets;

    public byte[] getMagicByte() {
        return magicByte.clone();
    }

    public int[][] partialOffsets() {
        return Arrays.stream(partialOffsets)
                .map(int[]::clone)
                .toArray(int[][]::new);
    }

    public static EnumSet<SupportAttachmentType> defaultEnumSet() {
        return EnumSet.allOf(SupportAttachmentType.class);
    }

    public boolean matches(byte[] binary) {
        byte[] magicByte = getMagicByte();
        int[][] partialOffsets = partialOffsets();

        if (partialOffsets.length == 0) {
            return Arrays.equals(Arrays.copyOfRange(binary, 0, magicByte.length), magicByte);
        }
        for (int[] partialOffset : partialOffsets) {
            int start = partialOffset[0];
            int end = partialOffset[1];
            for (int i = 0; i < magicByte.length; i++) {
                if (i < start || i >= end) {
                    if (binary.length <= i || binary[i] != magicByte[i]) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static String convertSupportedTypeString() {
        return defaultEnumSet().stream()
                .map(type -> type.toString().toLowerCase())
                .collect(Collectors.joining(", "));
    }
}
