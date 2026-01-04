package kimiram.notes.networking;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static kimiram.notes.Constants.MOD_ID;

public record FinalizeNoteC2SPacket(ItemStack stack) implements FabricPacket {
    public static final PacketType<FinalizeNoteC2SPacket> TYPE = PacketType.create(
            new ResourceLocation(MOD_ID, "finalize_note_packet"), FinalizeNoteC2SPacket::new);

    public FinalizeNoteC2SPacket(FriendlyByteBuf buf) {
        this(buf.readItem());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeItem(stack);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
