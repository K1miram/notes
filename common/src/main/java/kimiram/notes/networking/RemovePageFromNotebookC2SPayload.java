package kimiram.notes.networking;

import kimiram.notes.Constants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;

import static kimiram.notes.Constants.MOD_ID;

public record RemovePageFromNotebookC2SPayload(InteractionHand hand, int pageIndex) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "remove_page_from_notebook");
    public static final CustomPacketPayload.Type<RemovePageFromNotebookC2SPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, RemovePageFromNotebookC2SPayload> CODEC = StreamCodec.composite(
            Constants.HAND_STREAM_CODEC,
            RemovePageFromNotebookC2SPayload::hand,
            ByteBufCodecs.INT,
            RemovePageFromNotebookC2SPayload::pageIndex,
            RemovePageFromNotebookC2SPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
