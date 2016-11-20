package com.builtbroken.armory.content.items;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.content.prefab.ItemMetaArmoryEntry;
import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.ClipData;
import com.builtbroken.mc.api.data.weapon.IAmmoData;
import com.builtbroken.mc.api.data.weapon.IAmmoType;
import com.builtbroken.mc.api.items.weapons.IItemClip;
import com.builtbroken.mc.api.items.weapons.IItemReloadableWeapon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/19/2016.
 */
public class ItemClip extends ItemMetaArmoryEntry<ClipData> implements IItemClip
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
        return getData(stack).ammoType;
    }

    @Override
    public int getAmmoCount(ItemStack clipStack)
    {
        return clipStack.getTagCompound() != null ? clipStack.getTagCompound().getInteger("ammo") : 0;
    }

    /**
     * Sets the ammo count in the clip. Ammo count is used
     * just as a quick reference.
     *
     * @param clipStack
     * @param count
     */
    public void setAmmoCount(ItemStack clipStack, int count)
    {
        if (clipStack.getTagCompound() == null)
        {
            clipStack.setTagCompound(new NBTTagCompound());
        }
        clipStack.getTagCompound().setInteger("ammo", Math.max(0, count));
    }

    /**
     * Fills the clip with the define ammo
     *
     * @param clipStack - clip
     * @param data      - ammo type to load
     * @param count     - number of rounds to load
     */
    public void loadAmmoCount(ItemStack clipStack, IAmmoData data, int count)
    {
        if (clipStack.getTagCompound() == null)
        {
            clipStack.setTagCompound(new NBTTagCompound());
        }
        Queue<IAmmoData> clip = getStoredAmmo(clipStack);
        for (int i = 0; i < count; i++)
        {
            clip.add(data);
        }
    }

    @Override
    public Queue<IAmmoData> getStoredAmmo(ItemStack clipStack)
    {
        //TODO if array size becomes a problem with memory switch to array list
        Queue<IAmmoData> clip = new LinkedList();
        if (clipStack.getTagCompound() != null)
        {
            if (clipStack.getTagCompound().hasKey("ammoData"))
            {
                NBTTagCompound tag = clipStack.getTagCompound().getCompoundTag("ammoData");
                int number = tag.getInteger("number");
                for (int i = 0; i < number; i++)
                {
                    Object ammoData = ArmoryDataHandler.INSTANCE.get("ammo").get(tag.getString("round" + i));
                    if (ammoData instanceof IAmmoData)
                    {
                        clip.add((IAmmoData) ammoData);
                    }
                    else
                    {
                        Armory.INSTANCE.logger().error("Failed to load NBT ammo data tag '" + tag.getString("round" + i) + "' when loading clip data in stack " + clipStack + " this may result in loss of ammo in clip.");
                    }
                }
            }
        }
        return clip;
    }

    /**
     * Sets the ammo stored in the clip
     *
     * @param clipStack - clip
     * @param ammoData  - rounds of ammo
     */
    public void setStoredAmmo(ItemStack clipStack, Queue<IAmmoData> ammoData)
    {
        if (clipStack.getTagCompound() == null)
        {
            clipStack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound save = new NBTTagCompound();
        int count = 0;
        for (IAmmoData data : ammoData)
        {
            save.setString("round" + count, data.getUniqueID());
            count++;
        }
        clipStack.getTagCompound().setTag("ammoData", save);
        setAmmoCount(clipStack, count);
    }

    @Override
    public void consumeAmmo(IItemReloadableWeapon weapon, ItemStack weaponStack, ItemStack clipStack, int shotsFired)
    {
        int ammo = getAmmoCount(clipStack);
        if (ammo > 0)
        {
            Queue<IAmmoData> ammoData = getStoredAmmo(clipStack);
            for (int i = 0; i < shotsFired; i++)
            {
                ammoData.poll();
                ammo--;
                if (ammo <= 0)
                {
                    break;
                }
            }
            setStoredAmmo(clipStack, ammoData);
        }
    }

    @SideOnly(Side.CLIENT)
    protected void getSubItems(Item item, int meta, ClipData clip, CreativeTabs tab, List items)
    {
        final ItemStack emptyClipStack = new ItemStack(item, 1, meta);
        items.add(emptyClipStack);
        for (IAmmoData data : clip.ammoType.getAmmoData())
        {
            ItemStack stack = new ItemStack(item, 1, meta);
            loadAmmoCount(stack, data, clip.maxAmmo);
            items.add(stack);
        }
    }
}