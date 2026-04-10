package kimiram.notes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kimiram.imagelib.ImageLib;

import java.util.Objects;

public record Image(String url, ImageLib.Type type, int x, int y, int width, int height) {
    public static final Codec<ImageLib.Type> TYPE_CODEC = Codec.stringResolver(
            Image::typeToString,
            Image::stringToType
    );

    public static String typeToString(ImageLib.Type type) {
        if (type == ImageLib.Type.GIF) {
            return "gif";
        } else if (type == ImageLib.Type.STATIC_IMAGE) {
            return "static_image";
        } else {
            return null;
        }
    }

    public static ImageLib.Type stringToType(String string) {
        if (Objects.equals(string, "gif")) {
            return ImageLib.Type.GIF;
        } else if (Objects.equals(string, "static_image")) {
            return ImageLib.Type.STATIC_IMAGE;
        } else {
            return null;
        }
    }

    public static final Codec<Image> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.STRING.fieldOf("url").forGetter(Image::url),
            TYPE_CODEC.fieldOf("type").orElse(ImageLib.Type.STATIC_IMAGE).forGetter(Image::type),
            Codec.INT.fieldOf("x").forGetter(Image::x),
            Codec.INT.fieldOf("y").forGetter(Image::y),
            Codec.INT.fieldOf("width").forGetter(Image::width),
            Codec.INT.fieldOf("height").forGetter(Image::height)
    ).apply(builder, Image::new));
}