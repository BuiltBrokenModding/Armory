package com.builtbroken.armory.data.ammo;

/**
 * Actual instance used by the player for a clip
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class ClipInstance
{
    public final ClipData clipData;
    /** Current index of the clip */
    public int currentAmmo = 0;
    /** Actual ammo in the clip */
    public AmmoData[] ammo;

    public ClipInstance(ClipData clipData)
    {
        this.clipData = clipData;
    }
}
