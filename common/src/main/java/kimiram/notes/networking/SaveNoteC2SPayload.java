package kimiram.notes.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static kimiram.notes.Constants.MOD_ID;

public record SaveNoteC2SPayload(ItemStack stack) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "save_note");
    public static final CustomPacketPayload.Type<SaveNoteC2SPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SaveNoteC2SPayload> CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            SaveNoteC2SPayload::stack,
            SaveNoteC2SPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
