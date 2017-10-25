package com.builtbroken.armory.data.user;

import com.builtbroken.armory.data.ranged.GunInstance;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.imp.transform.vector.Pos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/11/2017.
 */
public class WeaponUserPlayer extends WeaponUserEntity<EntityPlayer> implements IInventory
{
    public WeaponUserPlayer(EntityPlayer entity)
    {
        super(entity);
    }

    @Override
    public Pos getEntityPosition()
    {
        double x = x();
        double y = y();
        double z = z();
        // isRemote check to revert changes to ray trace position due to adding the eye height clientside and entity yOffset differences
        y += (double) (oldWorld().isRemote ? entity.getEyeHeight() - entity.getDefaultEyeHeight() : entity.getEyeHeight());
        return new Pos(x, y, z);
    }


    @Override
    public IInventory getInventory()
    {
        return this;
    }

    @Override
    public boolean isAmmoSlot(int slot)
    {
        return slot >= 0 && slot < entity.inventory.getSizeInventory();
    }

    @Override
    public void updateWeaponStack(GunInstance instance, ItemStack updated, String name)
    {
        ItemStack stack = entity.getHeldItem();
        if (stack != null && stack.isItemEqual(updated))
        {
            getInventory().setInventorySlotContents(entity.inventory.currentItem, updated);
            entity.inventoryContainer.detectAndSendChanges();

            //Debug
            if (Engine.runningAsDev)
            {
                Engine.logger().info("Updated gun stack: " + name);
            }
        }
    }

    @Override
    public int getSizeInventory()
    {
        return entity.inventory.mainInventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        if (slot >= 0 && slot < getSizeInventory())
        {
            return entity.inventory.mainInventory[slot];
        }
        return null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int a)
    {
        if (getStackInSlot(slot) != null)
        {
            ItemStack itemstack;

            if (getStackInSlot(slot).stackSize <= a)
            {
                itemstack = getStackInSlot(slot);
                setInventorySlotContents(slot, null);
                return itemstack;
            }
            else
            {
                itemstack = getStackInSlot(slot).splitStack(a);

                if (getStackInSlot(slot).stackSize == 0)
                {
                    setInventorySlotContents(slot, null);
                }

                return itemstack;
            }
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        return getStackInSlot(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        if (slot >= 0 && slot < getSizeInventory())
        {
            entity.inventory.setInventorySlotContents(slot, stack);
        }
    }

    @Override
    public String getInventoryName()
    {
        return "Player Inventory";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return entity.inventory.getInventoryStackLimit();
    }

    @Override
    public void markDirty()
    {
        entity.inventory.markDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_)
    {
        return entity.inventory.isUseableByPlayer(p_70300_1_);
    }

    @Override
    public void openInventory()
    {
        entity.inventory.openInventory();
    }

    @Override
    public void closeInventory()
    {
        entity.inventory.closeInventory();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack item)
    {
        if (slot >= 0 && slot < getSizeInventory())
        {
            return entity.inventory.isItemValidForSlot(slot, item);
        }
        return false;
    }
}
