package com.builtbroken.armory.data.clip;

import com.builtbroken.armory.content.items.ItemClip;
import com.builtbroken.mc.api.ISave;
import com.builtbroken.mc.api.data.weapon.IAmmoData;
import com.builtbroken.mc.api.data.weapon.IClipData;
import com.builtbroken.mc.api.modules.weapon.IClip;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Stack;

/**
 * Actual instance used by the player for a clip
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class ClipInstance implements IClip, ISave
{
    private final IClipData clipData;
    private Stack<IAmmoData> ammo = new Stack();

    public ClipInstance(IClipData clipData)
    {
        this.clipData = clipData;
    }

    @Override
    public IClipData getClipData()
    {
        return clipData;
    }

    @Override
    public int getAmmoCount()
    {
        return ammo.size();
    }

    @Override
    public Stack<IAmmoData> getAmmo()
    {
        return ammo;
    }

    @Override
    public void consumeAmmo(int count)
    {
        while (count > 0 && !ammo.isEmpty())
        {
            ammo.pop();
            count--;
        }
    }

    @Override
    public int loadAmmo(IAmmoData data, int count)
    {
        if (getAmmoCount() < clipData.getMaxAmmo())
        {
            int loaded = 0;
            for (int i = 0; i < count; i++)
            {
                getAmmo().add(data);
                loaded++;
                if (getAmmoCount() >= clipData.getMaxAmmo())
                {
                    break;
                }
            }
            return loaded;
        }
        return 0;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        ammo = ItemClip.getAmmoDataStackFromNBT("ammoData", nbt);
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        if(ammo.size() > 0)
        {
            ItemClip.setAmmoDataStackIntoNBT("ammoData", nbt, ammo);
        }
        return nbt;
    }
}
