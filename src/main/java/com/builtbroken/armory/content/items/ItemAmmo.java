package com.builtbroken.armory.content.items;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.content.prefab.ItemMetaArmoryEntry;
import com.builtbroken.armory.data.ammo.AmmoData;
import com.builtbroken.mc.api.data.weapon.IAmmoData;
import com.builtbroken.mc.api.items.weapons.IItemAmmo;
import com.builtbroken.mc.api.items.weapons.IItemReloadableWeapon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

/**
 * Single round of ammo
 *
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
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        IAmmoData data = getData(stack);
        if (data != null)
        {
            //TODO translate
            list.add("Type: " + data.getAmmoType().getDisplayString());
            list.add("Damage: " + data.getAmmoType().getDisplayString());
            list.add("Velocity: " + data.getProjectileVelocity() + "m/s");
        }
        else
        {
            list.add("Error: Clip data is null");
        }
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
    public IAmmoData getAmmoData(ItemStack stack)
    {
        return getData(stack);
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

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        super.registerIcons(reg);
        itemIcon = reg.registerIcon(Armory.PREFIX + "bullet");
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected IIcon getFallBackIcon()
    {
        return itemIcon;
    }
}
