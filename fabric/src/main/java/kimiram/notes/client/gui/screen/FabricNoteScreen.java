package kimiram.notes.client.gui.screen;

import kimiram.notes.item.ModDataComponents;
import kimiram.notes.networking.FinalizeNoteC2SPayload;
import kimiram.notes.networking.SaveNoteC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.item.ItemStack;

public class FabricNoteScreen extends NoteScreen {
    public FabricNoteScreen(ItemStack stack) {
        super(stack, ModDataComponents.NOTE_COMPONENT_TYPE);
    }

    @Override
    public void onClose() {
        saveNote();
        ClientPlayNetworking.send(new SaveNoteC2SPayload(this.stack));
        super.onClose();
    }

    @Override
    public void finalizeNote() {
        saveNote();
        ClientPlayNetworking.send(new FinalizeNoteC2SPayload(this.stack));
        super.onClose();
    }
}
