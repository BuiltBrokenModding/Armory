package com.builtbroken.armory.data.ammo;

import com.builtbroken.armory.data.ArmoryEntry;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class AmmoData extends ArmoryEntry
{
    private final String ammoTypeName;
    private final String damageSourceName;

    private final float damage;

    //TODO add optional damage types
    //TODO add effect handlers
    //TODO add damage calculations

    public AmmoData(String name, String ammoType, String source, float damage)
    {
        super("ammo", name);
        this.ammoTypeName = ammoType;
        this.damageSourceName = source;
        this.damage = damage;
    }
}
