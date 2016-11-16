package com.builtbroken.armory.data.ranged;

import com.builtbroken.armory.data.ammo.ClipData;
import com.builtbroken.armory.data.ammo.ClipTypes;

/**
 * Holds all data about a gun
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/15/2016.
 */
public class GunData extends RangeWeaponData
{
    /** Type of reload/clip this can accept */
    public ClipTypes clipType;
    /** Every weapon gets a single fire reload ability */
    public ClipData singleFireClipData;

    public GunData(String type, String name, String projectileType)
    {
        super(type, name, projectileType);
    }
}
