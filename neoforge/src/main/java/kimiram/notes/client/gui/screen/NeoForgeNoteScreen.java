package kimiram.notes.client.gui.screen;

import kimiram.notes.networking.FinalizeNoteC2SPacket;
import kimiram.notes.networking.SaveNoteC2SPacket;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class NeoForgeNoteScreen extends NoteScreen {
    public NeoForgeNoteScreen(ItemStack stack) {
        super(stack);
    }

    @Override
    public void onClose() {
        saveNote();
        PacketDistributor.SERVER.noArg().send(new SaveNoteC2SPacket(stack));
        super.onClose();
    }

    @Override
    protected void finalizeNote() {
        saveNote();
        PacketDistributor.SERVER.noArg().send(new FinalizeNoteC2SPacket(stack));
        super.finalizeNote();
    }
}
