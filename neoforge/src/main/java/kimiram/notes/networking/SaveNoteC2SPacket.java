package kimiram.notes.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;

import static kimiram.notes.Constants.MOD_ID;
import static kimiram.notes.Notes.NOTE;

public record SaveNoteC2SPacket(ItemStack stack) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "save_note_packet");

    public SaveNoteC2SPacket(FriendlyByteBuf buf) {
        this(buf.readItem());
    }

    @Override
    public void write(@NotNull FriendlyByteBuf buf) {
        buf.writeItem(stack);
    }

    public static void handle(SaveNoteC2SPacket payload, PlayPayloadContext context) {
        context.workHandler().execute(() -> {
            if (context.player().get().getItemInHand(InteractionHand.MAIN_HAND).is(NOTE)) {
                context.player().get().setItemInHand(InteractionHand.MAIN_HAND, payload.stack());
            } else if (context.player().get().getItemInHand(InteractionHand.OFF_HAND).is(NOTE)) {
                context.player().get().setItemInHand(InteractionHand.OFF_HAND, payload.stack());
            }
        });
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
