package com.builtbroken.armory.data.ammo;

import com.builtbroken.armory.data.ArmoryEntry;
import com.builtbroken.mc.api.data.weapon.IAmmoData;
import com.builtbroken.mc.api.data.weapon.IAmmoType;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class AmmoData extends ArmoryEntry implements IAmmoData
{
    public final AmmoType ammoType;
    public final String damageSource;

    public final float damage;

    //TODO add optional damage types
    //TODO add effect handlers
    //TODO add damage calculations

    public AmmoData(String id, String name, AmmoType ammoType, String source, float damage)
    {
        super(id, "ammo", name);
        this.ammoType = ammoType;
        this.damageSource = source;
        this.damage = damage;
    }

    @Override
    public IAmmoType getAmmoType()
    {
        return ammoType;
    }

    @Override
    public float getBaseDamage()
    {
        return damage;
    }

    @Override
    public void onImpactEntity(Entity shooter, Entity entity)
    {
        if (damageSource != null && damage > 0)
        {
            //TODO create damage source with shooter, gun data, and damage type
            //TODO calculate armor
            //TODO apply force
            entity.attackEntityFrom(DamageSource.generic, damage);
        }
    }

    @Override
    public void onImpactGround(World world, int x, int y, int z, double hitX, double hitY, double hitZ)
    {

    }
}
