package kimiram.notes.item.component;

import net.minecraft.core.component.DataComponentType;

public class ModDataComponents {
    public static final DataComponentType<NoteContent> NOTE_COMPONENT_TYPE =
            DataComponentType.<NoteContent>builder().persistent(NoteContent.CODEC).build();

    public static final DataComponentType<FinalizedNoteContent> FINALIZED_NOTE_COMPONENT_TYPE =
            DataComponentType.<FinalizedNoteContent>builder().persistent(FinalizedNoteContent.CODEC).build();

    public static final DataComponentType<NotebookContent> NOTEBOOK_COMPONENT_TYPE =
            DataComponentType.<NotebookContent>builder().persistent(NotebookContent.CODEC).build();
}
