package com.builtbroken.armory.content.medival;

import com.builtbroken.armory.Armory;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * Created by Spencer on 8/13/2015.
 */
public class bronArmor extends ItemArmor {

    public bronArmor(ArmorMaterial p_i45325_1_, int p_i45325_2_, int p_i45325_3_) {
        super(p_i45325_1_, p_i45325_2_, p_i45325_3_);
    }

    public String getarmorTexture(ItemStack stack, Entity entity, int slot, String type) {

        if(stack.getItem() == AddedItemBlocks.bronNasal || stack.getItem() == AddedItemBlocks.bronCurass || stack.getItem() == AddedItemBlocks.bronSabaton) {
            return Armory.ASSETS_PATH + Armory.TEXTURE_PATH + "armor/bronArmorSet1-1.png";
        } else if(stack.getItem() == AddedItemBlocks.bronChausses){
            return Armory.ASSETS_PATH + Armory.TEXTURE_PATH + "armor/bronArmorSet1-2.png";
        } else if(stack.getItem() == AddedItemBlocks.bronArmet || stack.getItem() == AddedItemBlocks.bronFauld || stack.getItem() == AddedItemBlocks.bronSolleret) {
            return Armory.ASSETS_PATH + Armory.TEXTURE_PATH + "armor/bronArmorSet2-1.png";
        } else if(stack.getItem() == AddedItemBlocks.bronTasset){
            return Armory.ASSETS_PATH + Armory.TEXTURE_PATH + "armor/bronArmorSet2-2.png";
        } else {
            return null;
        }

    }

}
