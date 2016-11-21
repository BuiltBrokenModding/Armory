package com.builtbroken.armory.content.items;

import com.builtbroken.armory.content.entity.EntityThrowableExplosive;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import com.builtbroken.mc.api.items.explosives.IExplosiveItem;
import com.builtbroken.mc.lib.world.explosive.ExplosiveRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Item used to handle actions for throwable explosives
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/9/2015.
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
            {
                --itemstack.stackSize;
            }

            throwItem(itemstack, world, player);
        }
        return itemstack;
    }

    protected void throwItem(ItemStack itemstack, World world, EntityPlayer player)
    {
        //TODO add better sound
        world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!world.isRemote)
        {
            //TODO add way to override entity
            world.spawnEntityInWorld(new EntityThrowableExplosive(world, player).setExplosive(getExplosive(itemstack)));
        }
    }

    @Override
    public IExplosiveHandler getExplosive(ItemStack stack)
    {
        //TODO create a way to get explosive type dynamically
        return ExplosiveRegistry.get("TNT");
    }

    @Override
    public NBTTagCompound getAdditionalExplosiveData(ItemStack stack)
    {
        //TODO implement
        return null;
    }

    @Override
    public double getExplosiveSize(ItemStack stack)
    {
        //TODO implement
        return 0;
    }
}
