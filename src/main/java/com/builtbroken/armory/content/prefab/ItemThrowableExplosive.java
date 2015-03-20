package com.builtbroken.armory.content.prefab;

import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.api.items.IExplosiveItem;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by robert on 3/9/2015.
 */
public class ItemThrowableExplosive extends Item implements IExplosiveItem
{
    public ItemThrowableExplosive()
    {
        this.setCreativeTab(CreativeTabs.tabCombat);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
    {
        if (getExplosive(itemstack) != null)
        {
            if (!player.capabilities.isCreativeMode)
                --itemstack.stackSize;

            throwItem(itemstack, world, player);
        }
        return itemstack;
    }

    protected void throwItem(ItemStack itemstack, World world, EntityPlayer player)
    {
        world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!world.isRemote)
        {
            world.spawnEntityInWorld(new EntityThrowableExplosive(world, player).setExplosive(getExplosive(itemstack)));
        }
    }

    @Override
    public IExplosiveHandler getExplosive(ItemStack stack)
    {
        return ExplosiveRegistry.get("TNT");
    }
}
