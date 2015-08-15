package com.builtbroken.armory.content.mediveal;

import com.builtbroken.armory.Armory;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * Created by Spencer on 8/13/2015.
 */
public class ItemSteelArmor extends ItemArmor {

    public ItemSteelArmor(ArmorMaterial p_i45325_1_, int p_i45325_2_, int p_i45325_3_) {
        super(p_i45325_1_, p_i45325_2_, p_i45325_3_);
    }

    public String getarmorTexture(ItemStack stack, Entity entity, int slot, String type) {

        if(stack.getItem() == MedivealModular.steelNasal || stack.getItem() == MedivealModular.steelCurass || stack.getItem() == MedivealModular.steelSabaton) {
            return Armory.ASSETS_PATH + Armory.TEXTURE_PATH + "armor/steelArmorSet1-1.png";
        } else if(stack.getItem() == MedivealModular.steelChausses){
            return Armory.ASSETS_PATH + Armory.TEXTURE_PATH + "armor/steelArmorSet1-2.png";
        } else if(stack.getItem() == MedivealModular.steelArmet || stack.getItem() == MedivealModular.steelFauld || stack.getItem() == MedivealModular.steelSolleret) {
            return Armory.ASSETS_PATH + Armory.TEXTURE_PATH + "armor/steelArmorSet2-1.png";
        } else if(stack.getItem() == MedivealModular.steelTasset){
            return Armory.ASSETS_PATH + Armory.TEXTURE_PATH + "armor/steelArmorSet2-2.png";
        } else {
            return null;
        }

    }

}
