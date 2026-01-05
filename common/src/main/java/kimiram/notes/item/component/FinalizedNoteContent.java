package kimiram.notes.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kimiram.notes.Image;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.network.Filterable;

import java.util.List;

public record FinalizedNoteContent(Filterable<Component> text, List<Image> images) {
    public static final Codec<FinalizedNoteContent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Filterable.codec(ComponentSerialization.CODEC).fieldOf("text").forGetter(FinalizedNoteContent::text),
            Image.CODEC.listOf().fieldOf("images").forGetter(FinalizedNoteContent::images)
    ).apply(builder, FinalizedNoteContent::new));
}
