package com.builtbroken.armory.content.items;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.content.prefab.ItemMetaArmoryEntry;
import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.clip.ClipData;
import com.builtbroken.armory.data.clip.ClipInstanceItem;
import com.builtbroken.mc.api.data.weapon.IAmmoData;
import com.builtbroken.mc.api.data.weapon.IClipData;
import com.builtbroken.mc.api.items.weapons.IItemClip;
import com.builtbroken.mc.api.items.weapons.IItemReloadableWeapon;
import com.builtbroken.mc.api.modules.weapon.IClip;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import java.util.List;
import java.util.Stack;

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
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        IClipData data = getClipData(stack);
        if (data != null)
        {
            //TODO translate
            list.add("Type: " + LanguageUtility.capitalizeFirst(data.getReloadType().name().toLowerCase()));
            list.add("Ammo: " + data.getAmmoType().getDisplayString());
            if (getAmmoCount(stack) > 0)
            {
                IClip clip = toClip(stack);
                if (clip != null && clip.getAmmo().peek() != null)
                {
                    list.add("Next: " + clip.getAmmo().peek().getDisplayString());
                }
                else
                {
                    list.add("Next: Error Reading NBT");
                }
            }
            list.add("Rounds: " + getAmmoCount(stack) + "/" + data.getMaxAmmo());
        }
        else
        {
            list.add("Error: Clip data is null");
        }
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
    public IAmmoData getAmmoData(ItemStack stack)
    {
        return null;
    }

    @Override
    public int getAmmoCount(ItemStack clipStack)
    {
        return clipStack.getTagCompound() != null ? clipStack.getTagCompound().getInteger("ammoCount") : 0;
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
        clipStack.getTagCompound().setInteger("ammoCount", Math.max(0, count));
    }

    @Override
    public Stack<IAmmoData> getStoredAmmo(ItemStack clipStack)
    {
        return getAmmoDataStackFromNBT("ammoData", clipStack.getTagCompound());
    }

    /**
     * Loads ammo data from NBT
     *
     * @param tagKey - key to use
     * @param nbt    - tag to load it from, not the save tag itself but the global tag
     * @return
     */
    public static Stack<IAmmoData> getAmmoDataStackFromNBT(String tagKey, NBTTagCompound nbt)
    {
        Stack<IAmmoData> clip = new Stack();
        if (nbt != null)
        {
            if (nbt.hasKey(tagKey))
            {
                NBTTagCompound tag = nbt.getCompoundTag(tagKey);
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
                        Armory.INSTANCE.logger().error("Failed to load NBT ammo data tag '" + tag.getString("round" + i) + "' when loading clip data");
                    }
                }
            }
        }
        return clip;
    }

    /**
     * Set ammo data into NBT
     *
     * @param tagKey   - key to use
     * @param nbt      - tag to save the data into, not the save tag itself but the global tag
     * @param ammoData - data to save
     */
    public static void setAmmoDataStackIntoNBT(String tagKey, NBTTagCompound nbt, Stack<IAmmoData> ammoData)
    {
        NBTTagCompound save = new NBTTagCompound();
        int count = 0;
        for (IAmmoData data : ammoData)
        {
            save.setString("round" + count, data.getUniqueID());
            count++;
        }
        save.setInteger("number", count);
        nbt.setTag(tagKey, save);
    }

    @Override
    public int loadAmmo(ItemStack clipStack, IAmmoData data, int count)
    {
        if (getAmmoCount(clipStack) < getData(clipStack).getMaxAmmo())
        {
            if (clipStack.getTagCompound() == null)
            {
                clipStack.setTagCompound(new NBTTagCompound());
            }
            Stack<IAmmoData> clip = getStoredAmmo(clipStack);
            int loaded = 0;
            for (int i = 0; i < count; i++)
            {
                clip.add(data);
                loaded++;
                if (getAmmoCount(clipStack) >= getData(clipStack).getMaxAmmo())
                {
                    break;
                }
            }
            setAmmoStored(clipStack, clip);
            return loaded;
        }
        return 0;
    }

    @Override
    public IClip toClip(ItemStack clipStack)
    {
        if (clipStack == null || clipStack.getItem() != this)
        {
            return null;
        }
        ItemStack stack = clipStack.copy();
        stack.stackSize = 1;
        return new ClipInstanceItem(stack, getData(clipStack));
    }

    @Override
    public IClipData getClipData(ItemStack clipStack)
    {
        return getData(clipStack);
    }

    /**
     * Sets the ammo stored in the clip
     *
     * @param clipStack - clip
     * @param ammoData  - rounds of ammo
     */
    public void setAmmoStored(ItemStack clipStack, Stack<IAmmoData> ammoData)
    {
        if (clipStack.getTagCompound() == null)
        {
            clipStack.setTagCompound(new NBTTagCompound());
        }
        setAmmoDataStackIntoNBT("ammoData", clipStack.getTagCompound(), ammoData);
        setAmmoCount(clipStack, ammoData.size());
    }

    @Override
    public void consumeAmmo(IItemReloadableWeapon weapon, ItemStack weaponStack, ItemStack clipStack, int shotsFired)
    {
        int ammo = getAmmoCount(clipStack);
        if (ammo > 0)
        {
            Stack<IAmmoData> ammoData = getStoredAmmo(clipStack);
            for (int i = 0; i < shotsFired; i++)
            {
                ammoData.pop();
                ammo--;
                if (ammo <= 0)
                {
                    break;
                }
            }
            setAmmoStored(clipStack, ammoData);
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
            loadAmmo(stack, data, clip.maxAmmo);
            items.add(stack);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        super.registerIcons(reg);
        itemIcon = reg.registerIcon(Armory.PREFIX + "clip");
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected IIcon getFallBackIcon()
    {
        return itemIcon;
    }

    @Override
    public String getRenderKey(ItemStack stack)
    {
        if (getAmmoCount(stack) <= 0)
        {
            return "empty";
        }
        return null;
    }
}
