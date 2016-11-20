package com.builtbroken.armory.content.items;

import com.builtbroken.armory.content.prefab.ItemMetaArmoryEntry;
import com.builtbroken.mc.api.items.weapons.IAmmo;
import com.builtbroken.mc.api.items.weapons.IAmmoType;
import com.builtbroken.mc.api.items.weapons.IReloadableWeapon;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/19/2016.
 */
public class ItemClip extends ItemMetaArmoryEntry implements IAmmo
{
    public ItemClip()
    {
        super("clip", "clip");
    }

    @Override
    public boolean isAmmo(ItemStack stack)
    {
        return getAmmoCount(stack) > 0;
    }

    @Override
    public boolean isClip(ItemStack stack)
    {
        return true;
    }

    @Override
    public IAmmoType getAmmoType(ItemStack stack)
    {
        return null;
    }

    @Override
    public int getAmmoCount(ItemStack ammoStack)
    {
        return ammoStack.getTagCompound() != null ? ammoStack.getTagCompound().getInteger("ammo") : 0;
    }

    public void setAmmoCount(ItemStack ammoStack, int count)
    {
        if (ammoStack.getTagCompound() == null)
        {
            ammoStack.setTagCompound(new NBTTagCompound());
        }
        ammoStack.getTagCompound().setInteger("ammo", Math.max(0, count));
    }

    @Override
    public void fireAmmo(IReloadableWeapon weapon, ItemStack weaponStack, ItemStack ammoStack, Entity firingEntity)
    {

    }

    @Override
    public void consumeAmmo(IReloadableWeapon weapon, ItemStack weaponStack, ItemStack ammoStack, int shotsFired)
    {
        setAmmoCount(ammoStack, getAmmoCount(ammoStack) - shotsFired);
    }
}
