package com.builtbroken.armory.content.mediveal;

import com.builtbroken.armory.Armory;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * Created by Spencer on 8/13/2015.
 */
public class ItemBronArmor extends ItemArmor {

    public ItemBronArmor(ArmorMaterial p_i45325_1_, int p_i45325_2_, int p_i45325_3_) {
        super(p_i45325_1_, p_i45325_2_, p_i45325_3_);
    }

    public String getarmorTexture(ItemStack stack, Entity entity, int slot, String type) {

        if(stack.getItem() == MedivealModular.bronNasal || stack.getItem() == MedivealModular.bronCurass || stack.getItem() == MedivealModular.bronSabaton) {
            return Armory.ASSETS_PATH + Armory.TEXTURE_PATH + "armor/bronArmorSet1-1.png";
        } else if(stack.getItem() == MedivealModular.bronChausses){
            return Armory.ASSETS_PATH + Armory.TEXTURE_PATH + "armor/bronArmorSet1-2.png";
        } else if(stack.getItem() == MedivealModular.bronArmet || stack.getItem() == MedivealModular.bronFauld || stack.getItem() == MedivealModular.bronSolleret) {
            return Armory.ASSETS_PATH + Armory.TEXTURE_PATH + "armor/bronArmorSet2-1.png";
        } else if(stack.getItem() == MedivealModular.bronTasset){
            return Armory.ASSETS_PATH + Armory.TEXTURE_PATH + "armor/bronArmorSet2-2.png";
        } else {
            return null;
        }

    }

}
