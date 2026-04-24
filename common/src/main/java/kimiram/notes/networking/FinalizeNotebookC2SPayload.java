package kimiram.notes.networking;

import kimiram.notes.Constants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;

import static kimiram.notes.Constants.MOD_ID;

public record FinalizeNotebookC2SPayload(InteractionHand hand) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "finalize_notebook");
    public static final Type<FinalizeNotebookC2SPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, FinalizeNotebookC2SPayload> CODEC = StreamCodec.composite(
            Constants.HAND_STREAM_CODEC,
            FinalizeNotebookC2SPayload::hand,
            FinalizeNotebookC2SPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
