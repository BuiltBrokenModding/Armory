package com.builtbroken.armory.content.prefab.armor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Dark on 8/20/2015.
 */
public class ItemArmorSet extends Item
{
    //TODO most likely this will need a damage event in order to block damage as it will not be handled by the default MC code
    //TODO add material types to nbt
    //TODO handle damage using metadata
    //TODO add upgrade system
    @Override
    public ItemStack onItemRightClick(ItemStack armorStack, World world, EntityPlayer player)
    {
        ArmorType type = getArmorType(armorStack);
        if (type != null)
        {
            int armorSlot = type.slot - 1;
            ItemStack currentArmorInSlot = player.getCurrentArmor(armorSlot);

            if (currentArmorInSlot == null)
            {
                player.setCurrentItemOrArmor(armorSlot + 1, armorStack.copy());
                armorStack.stackSize = 0;
            }
        }
        return armorStack;
    }

    /**
     * Gets the type of armor see {@link com.builtbroken.armory.content.prefab.armor.ItemArmorSet.ArmorType}
     *
     * @param stack - armor item
     * @return armor type or null if the stack was not set with the data needed
     */
    public ArmorType getArmorType(ItemStack stack)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("armorType"))
        {
            byte type = stack.getTagCompound().getByte("armorType");
            if (type >= 0 && type < ArmorType.values().length)
                return ArmorType.values()[type];
        }
        return null;
    }

    /**
     * Sets the item stack with the armor type data
     *
     * @param stack - armor item
     * @param type  - armor type see {@link com.builtbroken.armory.content.prefab.armor.ItemArmorSet.ArmorType}
     */
    public void setArmorType(ItemStack stack, ArmorType type)
    {
        if (stack.getTagCompound() == null)
            stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setByte("armorType", (byte) type.ordinal());

    }

    @Override
    public boolean isValidArmor(ItemStack stack, int armorType, Entity entity)
    {
        ArmorType type = getArmorType(stack);
        return type != null & type.ordinal() == armorType;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List items)
    {
        for (ArmorType type : ArmorType.values())
        {
            ItemStack armorStack = new ItemStack(item);
            setArmorType(armorStack, type);
            items.add(armorStack);
        }
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass)
    {
        ArmorType type = getArmorType(stack);
        if (type != null)
        {
            switch (type)
            {
                case HELM:
                    Items.iron_helmet.getIcon(stack, pass);
                case BODY:
                    Items.iron_chestplate.getIcon(stack, pass);
                case LEGS:
                    Items.iron_leggings.getIcon(stack, pass);
                case BOOTS:
                    Items.iron_boots.getIcon(stack, pass);
            }
        }
        return Items.iron_helmet.getIcon(stack, pass);
    }


    public enum ArmorType
    {
        HELM(4),
        BODY(3),
        LEGS(2),
        BOOTS(1);

        public final int slot;

        ArmorType(int slot)
        {
            this.slot = slot;
        }
    }
}
