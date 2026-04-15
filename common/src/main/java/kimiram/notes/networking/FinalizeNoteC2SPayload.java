package kimiram.notes.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static kimiram.notes.Constants.MOD_ID;

public record FinalizeNoteC2SPayload(ItemStack stack, InteractionHand hand) implements CustomPacketPayload {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(MOD_ID, "finalize_note");
    public static final CustomPacketPayload.Type<FinalizeNoteC2SPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, FinalizeNoteC2SPayload> CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            FinalizeNoteC2SPayload::stack,
            InteractionHand.STREAM_CODEC,
            FinalizeNoteC2SPayload::hand,
            FinalizeNoteC2SPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
