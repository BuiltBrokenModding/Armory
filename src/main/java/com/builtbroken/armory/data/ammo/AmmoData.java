package com.builtbroken.armory.data.ammo;

import com.builtbroken.armory.data.ArmoryEntry;
import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.armory.data.damage.DamageSimple;
import com.builtbroken.mc.api.data.weapon.IAmmoData;
import com.builtbroken.mc.api.data.weapon.IAmmoType;
import com.builtbroken.mc.lib.json.imp.IJsonProcessor;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class AmmoData extends ArmoryEntry implements IAmmoData
{
    public final AmmoType ammoType;

    public DamageData damageData;
    public final float velocity;

    //TODO add optional damage types
    //TODO add effect handlers
    //TODO add damage calculations

    public AmmoData(IJsonProcessor processor, String id, String name, AmmoType ammoType, DamageData damageData, float velocity)
    {
        super(processor, id, "ammo", name);
        this.ammoType = ammoType;
        this.damageData = damageData;
        this.velocity = velocity;
    }

    public AmmoData(IJsonProcessor processor, String id, String name, AmmoType ammoType, String source, float damage, float velocity)
    {
        this(processor, id, name, ammoType, new DamageSimple(source, damage), velocity);
    }

    @Override
    public void register()
    {
        super.register();
        ammoType.addAmmoData(this);
    }

    @Override
    public IAmmoType getAmmoType()
    {
        return ammoType;
    }

    @Override
    public float getBaseDamage()
    {
        return damageData != null ? damageData.getBaseDamage() : -1;
    }

    @Override
    public float getProjectileVelocity()
    {
        return velocity;
    }

    @Override
    public boolean onImpactEntity(Entity shooter, Entity entity, double hitX, double hitY, double hitZ, float velocity)
    {
        if (damageData != null)
        {
            return damageData.onImpact(shooter, entity, hitX, hitY, hitZ, velocity, 1);
        }
        return true;
    }

    @Override
    public boolean onImpactGround(Entity shooter, World world, int x, int y, int z, double hitX, double hitY, double hitZ, float velocity)
    {
        if (damageData != null)
        {
            return damageData.onImpact(shooter, world, x, y, z, hitX, hitY, hitZ, velocity, 1);
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "Ammo[" + getUniqueID() + "]@" + hashCode();
    }
}
