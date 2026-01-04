package kimiram.notes.networking;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;

import static kimiram.notes.Constants.MOD_ID;
import static kimiram.notes.Notes.FINALIZED_NOTE;
import static kimiram.notes.Notes.NOTE;

public record FinalizeNoteC2SPacket(ItemStack stack) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "finalize_note_packet");

    public FinalizeNoteC2SPacket(FriendlyByteBuf buf) {
        this(buf.readItem());
    }

    public static void handle(FinalizeNoteC2SPacket payload, PlayPayloadContext context) {
        context.workHandler().execute(() -> {
            ItemStack stack = new ItemStack(FINALIZED_NOTE.get());
            if (payload.stack().getTag() != null) {
                CompoundTag tag = payload.stack().getTag().copy();
                tag.put("finalized_note_content", tag.getCompound("note_content"));
                tag.remove("note_content");
                stack.setTag(tag);
            }
            if (context.player().get().getItemInHand(InteractionHand.MAIN_HAND).is(NOTE)) {
                context.player().get().setItemInHand(InteractionHand.MAIN_HAND, stack);
            } else if (context.player().get().getItemInHand(InteractionHand.OFF_HAND).is(NOTE)) {
                context.player().get().setItemInHand(InteractionHand.OFF_HAND, stack);
            }
        });
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeItem(stack);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
