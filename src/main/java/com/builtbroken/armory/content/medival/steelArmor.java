package com.builtbroken.armory.content.medival;

import com.builtbroken.armory.Armory;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * Created by Spencer on 8/13/2015.
 */
public class steelArmor extends ItemArmor {

    public steelArmor(ArmorMaterial p_i45325_1_, int p_i45325_2_, int p_i45325_3_) {
        super(p_i45325_1_, p_i45325_2_, p_i45325_3_);
    }

    public String getarmorTexture(ItemStack stack, Entity entity, int slot, String type) {

        if(stack.getItem() == AddedItemBlocks.steelNasal || stack.getItem() == AddedItemBlocks.steelCurass || stack.getItem() == AddedItemBlocks.steelSabaton) {
            return Armory.ASSETS_PATH + Armory.TEXTURE_PATH + "armor/steelArmorSet1-1.png";
        } else if(stack.getItem() == AddedItemBlocks.steelChausses){
            return Armory.ASSETS_PATH + Armory.TEXTURE_PATH + "armor/steelArmorSet1-2.png";
        } else if(stack.getItem() == AddedItemBlocks.steelArmet || stack.getItem() == AddedItemBlocks.steelFauld || stack.getItem() == AddedItemBlocks.steelSolleret) {
            return Armory.ASSETS_PATH + Armory.TEXTURE_PATH + "armor/steelArmorSet2-1.png";
        } else if(stack.getItem() == AddedItemBlocks.steelTasset){
            return Armory.ASSETS_PATH + Armory.TEXTURE_PATH + "armor/steelArmorSet2-2.png";
        } else {
            return null;
        }

    }

}
