package com.builtbroken.armory.content.items;

import com.builtbroken.armory.content.prefab.ItemMetaArmoryEntry;
import com.builtbroken.armory.data.ammo.AmmoData;
import com.builtbroken.mc.api.data.weapon.IAmmoType;
import com.builtbroken.mc.api.items.weapons.IItemAmmo;
import com.builtbroken.mc.api.items.weapons.IItemReloadableWeapon;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/19/2016.
 */
public class ItemAmmo extends ItemMetaArmoryEntry<AmmoData> implements IItemAmmo
{
    public ItemAmmo()
    {
        super("ammo", "ammo");
    }

    @Override
    public boolean isAmmo(ItemStack stack)
    {
        return true;
    }

    @Override
    public boolean isClip(ItemStack stack)
    {
        return false;
    }

    @Override
    public IAmmoType getAmmoType(ItemStack stack)
    {
        return getData(stack).ammoType;
    }

    @Override
    public int getAmmoCount(ItemStack ammoStack)
    {
        return ammoStack.stackSize;
    }

    @Override
    public void consumeAmmo(IItemReloadableWeapon weapon, ItemStack weaponStack, ItemStack ammoStack, int shotsFired)
    {
        ammoStack.stackSize -= shotsFired;
    }
}
