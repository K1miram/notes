package kimiram.notes.item;

import kimiram.notes.item.component.FinalizedNoteContent;
import kimiram.notes.item.component.NoteContent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import static kimiram.notes.Constants.MOD_ID;

public class ModDataComponents {
    public static final DataComponentType<NoteContent> NOTE_COMPONENT_TYPE = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            new ResourceLocation(MOD_ID, "note_content"),
            DataComponentType.<NoteContent>builder().persistent(NoteContent.CODEC).build()
    );

    public static final DataComponentType<FinalizedNoteContent> FINALIZED_NOTE_COMPONENT_TYPE = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            new ResourceLocation(MOD_ID, "finalized_note_content"),
            DataComponentType.<FinalizedNoteContent>builder().persistent(FinalizedNoteContent.CODEC).build()
    );

    public static void initialize() {

    }
}
