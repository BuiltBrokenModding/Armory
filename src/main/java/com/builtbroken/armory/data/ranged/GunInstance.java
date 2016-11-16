package com.builtbroken.armory.data.ranged;

import com.builtbroken.armory.data.ammo.ClipInstance;
import net.minecraft.entity.Entity;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class GunInstance
{
    public final Entity entity;
    public final GunData gun;
    public ClipInstance clip;
    //TODO clip
    //TODO ammo left
    //TODO parts
    //TODO damage

    public GunInstance(Entity entity, GunData gun)
    {
        this.entity = entity;
        this.gun = gun;
    }


    public void fireWeapon()
    {

    }

    public void reloadWeapon()
    {

    }

    public void unreleadWeapon()
    {

    }

    public boolean hasSights()
    {
        return false;
    }

    public void sightWeapon()
    {

    }
}
