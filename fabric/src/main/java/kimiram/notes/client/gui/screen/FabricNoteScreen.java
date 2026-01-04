package kimiram.notes.client.gui.screen;

import kimiram.notes.networking.FinalizeNoteC2SPacket;
import kimiram.notes.networking.SaveNoteC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.item.ItemStack;

public class FabricNoteScreen extends NoteScreen{
    public FabricNoteScreen(ItemStack stack) {
        super(stack);
    }

    @Override
    public void onClose() {
        saveNote();
        ClientPlayNetworking.send(new SaveNoteC2SPacket(this.stack));
        super.onClose();
    }

    @Override
    protected void finalizeNote() {
        saveNote();
        ClientPlayNetworking.send(new FinalizeNoteC2SPacket(this.stack));
        super.finalizeNote();
    }
}
