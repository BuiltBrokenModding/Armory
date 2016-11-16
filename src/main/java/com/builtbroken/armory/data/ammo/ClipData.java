package com.builtbroken.armory.data.ammo;

import com.builtbroken.armory.data.ArmoryEntry;

/**
 * Holds data about the clip
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class ClipData extends ArmoryEntry
{
    /** Type of clip */
    public final ClipTypes type;
    /** What type of ammo can the clip hold */
    public final AmmoType ammoType;
    /** Max size of the clip */
    public final int maxAmmo;

    public ClipData(String name, ClipTypes clip, AmmoType ammo, int maxAmmo)
    {
        super("clip", name);
        this.type = clip;
        this.ammoType = ammo;
        this.maxAmmo = maxAmmo;
    }
}
