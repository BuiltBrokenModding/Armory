package com.builtbroken.armory.data.ammo;

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
    private int ammoCount = 0;
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
        return ammoCount;
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
        ammoCount = ammo.size();
    }

    @Override
    public void load(NBTTagCompound nbt)
    {

    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        return nbt;
    }
}
