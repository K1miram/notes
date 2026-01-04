package kimiram.notes.recipe;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

import static kimiram.notes.Constants.MOD_ID;

public class ModRecipes {
    public static final RecipeSerializer<NoteCloningRecipe> NOTE_CLONING_RECIPE_RECIPE_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            new ResourceLocation(MOD_ID, "crafting_special_notecloning"),
            new SimpleCraftingRecipeSerializer<>(NoteCloningRecipe::new));

    public static final RecipeType<NoteCloningRecipe> NOTE_CLONING_RECIPE_TYPE = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            new ResourceLocation(MOD_ID, "crafting_special_notecloning"),
            NoteCloningRecipe.Type.INSTANCE);

    public static void initialize() {

    }
}
