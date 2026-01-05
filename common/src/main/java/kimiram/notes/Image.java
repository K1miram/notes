package kimiram.notes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Image(String url, int x, int y, int width, int height) {
    public static final Codec<Image> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.STRING.fieldOf("url").forGetter(Image::url),
            Codec.INT.fieldOf("x").forGetter(Image::x),
            Codec.INT.fieldOf("y").forGetter(Image::y),
            Codec.INT.fieldOf("width").forGetter(Image::width),
            Codec.INT.fieldOf("height").forGetter(Image::height)
    ).apply(builder, Image::new));
}