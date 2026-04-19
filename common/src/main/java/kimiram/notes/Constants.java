package kimiram.notes;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.InteractionHand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
    public static final String MOD_ID = "notes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final StreamCodec<ByteBuf, InteractionHand> HAND_STREAM_CODEC = ByteBufCodecs.idMapper(
            ByIdMap.continuous(
                    hand -> hand == InteractionHand.MAIN_HAND ? 0 : 1,
                    InteractionHand.values(),
                    ByIdMap.OutOfBoundsStrategy.ZERO
            ),
            hand -> hand == InteractionHand.MAIN_HAND ? 0 : 1
    );
}
