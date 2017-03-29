package com.builtbroken.armory.data.clip;

import com.builtbroken.mc.api.data.weapon.IAmmoData;
import com.builtbroken.mc.api.data.weapon.IClipData;
import com.builtbroken.mc.api.items.weapons.IItemClip;
import com.builtbroken.mc.api.modules.weapon.IClip;
import com.builtbroken.mc.prefab.module.AbstractModule;
import net.minecraft.item.ItemStack;

import java.util.Stack;

/**
 * Wrapper for Item clip to make it act like a clip instance
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class ClipInstanceItem extends AbstractModule implements IClip
{
    public final IClipData clipData;

    public ClipInstanceItem(ItemStack clipStack, IClipData clipData)
    {
        super(clipStack, "armory:clip");
        this.clipData = clipData;
        //Should never happen and is mainly here for dev code & unit testing
        if (!(clipStack.getItem() instanceof IItemClip))
        {
            throw new IllegalArgumentException("ClipStack[" + save() + "] is not an instance of IItemClip");
        }
    }

    @Override
    public IClipData getClipData()
    {
        return clipData;
    }

    @Override
    public int getAmmoCount()
    {
        return ((IItemClip) item.getItem()).getAmmoCount(item);
    }

    @Override
    public Stack<IAmmoData> getAmmo()
    {
        return ((IItemClip) item.getItem()).getStoredAmmo(item);
    }

    @Override
    public void consumeAmmo(int count)
    {
        ((IItemClip) item.getItem()).consumeAmmo(null, null, item, count);
    }

    @Override
    public int loadAmmo(IAmmoData data, int count)
    {
        return ((IItemClip) item.getItem()).loadAmmo(item, data, count);
    }

    @Override
    public String getSaveID()
    {
        return "armory:clip";
    }
}
