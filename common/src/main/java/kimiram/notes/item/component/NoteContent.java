package kimiram.notes.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kimiram.notes.Image;

import java.util.List;

public record NoteContent(String text, List<Image> images) {
    public static final Codec<NoteContent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.STRING.fieldOf("text").forGetter(NoteContent::text),
            Image.CODEC.listOf().fieldOf("images").forGetter(NoteContent::images)
    ).apply(builder, NoteContent::new));
}
