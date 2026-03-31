package kimiram.notes.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class NoteCloningRecipe extends CustomRecipe {
    public final Ingredient source;
    public final Ingredient material;
    public final ItemStackTemplate result;

    public NoteCloningRecipe(Ingredient source, Ingredient material, ItemStackTemplate result) {
        this.source = source;
        this.material = material;
        this.result = result;
    }

    public static final MapCodec<NoteCloningRecipe> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                Ingredient.CODEC.fieldOf("source").forGetter(o -> o.source),
                Ingredient.CODEC.fieldOf("material").forGetter(o -> o.material),
                ItemStackTemplate.CODEC.fieldOf("result").forGetter(o -> o.result)
            ).apply(i, NoteCloningRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, NoteCloningRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            o -> o.source,
            Ingredient.CONTENTS_STREAM_CODEC,
            o -> o.material,
            ItemStackTemplate.STREAM_CODEC,
            o -> o.result,
            NoteCloningRecipe::new
    );

    @Override
    public boolean matches(CraftingInput input, @NotNull Level level) {
        List<ItemStack> stacks = input.items();
        boolean bl1 = false, bl2 = false;
        for (ItemStack stack: stacks) {
            if (!stack.isEmpty()) {
                if (source.test(stack)) {
                    if (bl1) {
                        return false;
                    }

                    bl1 = true;
                } else {
                    if (!material.test(stack)) {
                        return false;
                    }

                    bl2 = true;
                }
            }
        }
        return bl1 && bl2;
    }

    @Override
    public @NonNull ItemStack assemble(CraftingInput input) {
        List<ItemStack> stacks = input.items();
        ItemStack finNote = ItemStack.EMPTY;
        int cnt = 0;
        for (ItemStack stack: stacks) {
            if (!stack.isEmpty()) {
                if (source.test(stack)) {
                    if (!finNote.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    finNote = stack;
                } else {
                    if (!material.test(stack)) {
                        return ItemStack.EMPTY;
                    }

                    cnt++;
                }
            }
        }

        if (cnt > 0) {
            return TransmuteRecipe.createWithOriginalComponents(result, finNote, cnt);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public @NotNull RecipeSerializer<NoteCloningRecipe> getSerializer() {
        return ModRecipes.NOTE_CLONING_RECIPE_RECIPE_SERIALIZER;
    }

    public static class Type implements RecipeType<NoteCloningRecipe> {
        public static final Type INSTANCE = new Type();

        private Type() {
        }

        @Override
        public String toString() {
            return "crafting_special_notecloning";
        }
    }
}
