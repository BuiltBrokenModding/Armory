package com.builtbroken.armory.data.ranged;

import com.builtbroken.armory.data.Weapon;
import com.builtbroken.mc.api.data.weapon.IAmmoType;
import com.builtbroken.mc.framework.json.imp.IJsonProcessor;

/**
 * Any kind of weapon that is based on a ranged attack
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/15/2016.
 */
public abstract class RangeWeaponData extends Weapon
{
    /** Type of ammo this weapon uses for firing */
    public final IAmmoType ammoType;

    public RangeWeaponData(IJsonProcessor processor, String id, String type, String name, IAmmoType ammoType)
    {
        super(processor, id, type, name);
        this.ammoType = ammoType;
    }
}
