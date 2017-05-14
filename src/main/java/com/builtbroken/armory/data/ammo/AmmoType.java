package com.builtbroken.armory.data.ammo;

import com.builtbroken.armory.data.ArmoryEntry;
import com.builtbroken.mc.api.data.EnumProjectileTypes;
import com.builtbroken.mc.api.data.weapon.IAmmoData;
import com.builtbroken.mc.api.data.weapon.IAmmoType;
import com.builtbroken.mc.lib.json.imp.IJsonProcessor;

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
    public final List<IAmmoData> ammoDataObjects = new ArrayList();

    /** Type of projectile of the ammo type */
    public final EnumProjectileTypes projectileType;

    public AmmoType(IJsonProcessor processor, String id, String name, EnumProjectileTypes ammoType)
    {
        super(processor, id, "ammoType", name);
        this.projectileType = ammoType;
    }

    @Override
    public List<IAmmoData> getAmmoData()
    {
        return ammoDataObjects;
    }

    @Override
    public boolean addAmmoData(IAmmoData data)
    {
        if (data.getAmmoType() == this && !ammoDataObjects.contains(data))
        {
            ammoDataObjects.add(data);
        }
        return false;
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

    @Override
    public EnumProjectileTypes getProjectileType()
    {
        return projectileType;
    }

    @Override
    public String toString()
    {
        return "AmmoType[" + getUniqueID() + "]@" + hashCode();
    }

}
