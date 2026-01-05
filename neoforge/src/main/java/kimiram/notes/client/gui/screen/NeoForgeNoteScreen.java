package kimiram.notes.client.gui.screen;

import kimiram.notes.Notes;
import kimiram.notes.networking.FinalizeNoteC2SPayload;
import kimiram.notes.networking.SaveNoteC2SPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class NeoForgeNoteScreen extends NoteScreen {
    public NeoForgeNoteScreen(ItemStack stack) {
        super(stack, Notes.NOTE_COMPONENT_TYPE);
    }

    @Override
    public void onClose() {
        saveNote();
        ClientPacketDistributor.sendToServer(new SaveNoteC2SPayload(stack));
        super.onClose();
    }

    @Override
    protected void finalizeNote() {
        saveNote();
        ClientPacketDistributor.sendToServer(new FinalizeNoteC2SPayload(stack));
        super.onClose();
    }
}
