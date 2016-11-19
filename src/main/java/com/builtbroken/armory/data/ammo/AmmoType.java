package com.builtbroken.armory.data.ammo;

import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ArmoryEntry;
import com.builtbroken.armory.data.projectiles.EnumProjectileTypes;
import com.builtbroken.mc.api.items.weapons.IAmmoType;

import java.util.ArrayList;
import java.util.List;

/**
 * Category for ammo
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class AmmoType extends ArmoryEntry implements IAmmoType
{
    /** Ammo data that is registered to this type of ammo */
    public final List<AmmoData> ammoDataObjects = new ArrayList();
    /** Type of projectile of the ammo type */
    public final EnumProjectileTypes projectileType;

    public AmmoType(String name, EnumProjectileTypes ammoType)
    {
        super("ammoType", name);
        this.projectileType = ammoType;
    }

    @Override
    public void register()
    {
        ArmoryDataHandler.add(this);
    }

    @Override
    public String getAmmoCategory()
    {
        return projectileType.catName;
    }

    @Override
    public String getAmmoType()
    {
        return name();
    }
}
