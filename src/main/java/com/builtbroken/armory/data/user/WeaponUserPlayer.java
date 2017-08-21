package com.builtbroken.armory.data.user;

import com.builtbroken.armory.data.ranged.GunInstance;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.imp.transform.vector.Pos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/11/2017.
 */
public class WeaponUserPlayer extends WeaponUserEntity<EntityPlayer>
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
    public InventoryPlayer getInventory()
    {
        return entity.inventory;
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
            getInventory().setInventorySlotContents(getInventory().currentItem, updated);
            entity.inventoryContainer.detectAndSendChanges();

            //Debug
            if (Engine.runningAsDev)
            {
                Engine.logger().info("Updated gun stack: " + name);
            }
        }
    }
}
