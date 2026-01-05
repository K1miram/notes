package kimiram.notes.recipe;

import kimiram.notes.Notes;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NoteCloningRecipe extends CustomRecipe {
    public NoteCloningRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, @NotNull Level level) {
        List<ItemStack> stacks = input.items();
        boolean bl1 = false, bl2 = false;
        for (ItemStack stack: stacks) {
            if (!stack.isEmpty()) {
                if (stack.is(Notes.FINALIZED_NOTE)) {
                    if (bl1) {
                        return false;
                    }

                    bl1 = true;
                } else {
                    if (!stack.is(Notes.NOTE)) {
                        return false;
                    }

                    bl2 = true;
                }
            }
        }
        return bl1 && bl2;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingInput input, HolderLookup.@NotNull Provider registries) {
        List<ItemStack> stacks = input.items();
        ItemStack finNote = ItemStack.EMPTY;
        int cnt = 0;
        for (ItemStack stack: stacks) {
            if (!stack.isEmpty()) {
                if (stack.is(Notes.FINALIZED_NOTE)) {
                    if (!finNote.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    finNote = stack;
                } else {
                    if (!stack.is(Notes.NOTE)) {
                        return ItemStack.EMPTY;
                    }

                    cnt++;
                }
            }
        }

        if (cnt > 0) {
            return finNote.copyWithCount(cnt + 1);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public @NotNull RecipeSerializer<NoteCloningRecipe> getSerializer() {
        return Notes.NOTE_CLONING_RECIPE_RECIPE_SERIALIZER;
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
