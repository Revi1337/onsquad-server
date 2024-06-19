package revi1337.onsquad.factory;

import revi1337.onsquad.image.domain.Image;

public class ImageFactory {

    public static final byte[] JPG_IMAGE = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}; // JPG, JPEG
    public static final byte[] PNG_IMAGE = {(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A}; // PNG

    public static Image defaultImage() {
        return new Image(JPG_IMAGE);
    }
}
