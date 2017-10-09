package com.builtbroken.armory.content.armor;

import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/9/2017.
 */
public class ItemArmorPart extends ItemArmor
{
    public ItemArmorPart(ArmorMaterial p_i45325_1_, int p_i45325_2_, int p_i45325_3_)
    {
        super(p_i45325_1_, p_i45325_2_, p_i45325_3_);
    }

    @Override
    public Multimap getAttributeModifiers(ItemStack stack)
    {
        /**  Applied in {@link net.minecraft.entity.EntityLivingBase#onUpdate() }*/
        //TODO apply modifiers to entity
        return this.getItemAttributeModifiers();
    }

    @Override
    public boolean isValidArmor(ItemStack stack, int armorType, Entity entity)
    {
        if (this instanceof ItemArmor)
        {
            return ((ItemArmor) this).armorType == armorType;
        }

        if (armorType == 0)
        {
            return this == Item.getItemFromBlock(Blocks.pumpkin) || this == Items.skull;
        }

        return false;
    }
}
