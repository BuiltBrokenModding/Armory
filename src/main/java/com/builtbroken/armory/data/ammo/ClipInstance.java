package com.builtbroken.armory.data.ammo;

import com.builtbroken.mc.api.data.weapon.IAmmoData;
import com.builtbroken.mc.api.data.weapon.IClipData;
import com.builtbroken.mc.api.modules.weapon.IClip;

import java.util.Stack;

/**
 * Actual instance used by the player for a clip
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class ClipInstance implements IClip
{
    public final IClipData clipData;
    /** Current index of the clip */
    public int currentAmmo = 0;
    /** Actual ammo in the clip */
    public Stack<IAmmoData> ammo;

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
        return currentAmmo;
    }

    @Override
    public Stack<IAmmoData> getAmmo()
    {
        return ammo;
    }
}
