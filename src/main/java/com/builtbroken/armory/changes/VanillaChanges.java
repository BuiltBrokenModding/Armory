package com.builtbroken.armory.changes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import java.util.Iterator;
import net.minecraft.init.Items;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Spencer on 8/9/2015.
 */
public class VanillaChanges {

    public static void RecipieRemover( List<ItemStack> itemList) {

        Iterator<IRecipe>  iterator = CraftingManager.getInstance().getRecipeList().iterator();

        while(iterator.hasNext()) {
            ItemStack output = iterator.next().getRecipeOutput();

            for(ItemStack stack : itemList) {
                if(output != null && output.isItemEqual(stack))
                    iterator.remove();
            }
        }

    }
}
