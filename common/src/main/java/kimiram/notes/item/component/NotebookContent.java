package kimiram.notes.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record NotebookContent(List<FinalizedNoteContent> pages) {
    public static final Codec<NotebookContent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            FinalizedNoteContent.CODEC.listOf().fieldOf("pages").forGetter(NotebookContent::pages)
    ).apply(builder, NotebookContent::new));
}
