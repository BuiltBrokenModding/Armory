package com.builtbroken.armory.data.ammo;

import com.builtbroken.armory.data.ArmoryEntry;
import com.builtbroken.mc.api.data.weapon.IAmmoType;
import com.builtbroken.mc.api.data.weapon.IClipData;
import com.builtbroken.mc.api.data.weapon.ReloadType;

/**
 * Holds data about the clip
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class ClipData extends ArmoryEntry implements IClipData
{
    /** Type of clip */
    public final ReloadType type;
    /** What type of ammo can the clip hold */
    public final IAmmoType ammoType;
    /** Max size of the clip */
    public final int maxAmmo;

    public ClipData(String id, String name, ReloadType clip, IAmmoType ammo, int maxAmmo)
    {
        super(id, "clip", name);
        this.type = clip;
        this.ammoType = ammo;
        this.maxAmmo = maxAmmo;
    }

    @Override
    public ReloadType getReloadType()
    {
        return type;
    }

    @Override
    public IAmmoType getAmmoType()
    {
        return ammoType;
    }

    @Override
    public int getMaxAmmo()
    {
        return maxAmmo;
    }

    @Override
    public String toString()
    {
        return "Clip[" + getUniqueID() + ", " + getReloadType() + ", " + ammoType + ", " + maxAmmo + "]@" + hashCode();
    }
}
