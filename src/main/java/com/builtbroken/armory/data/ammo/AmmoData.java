package com.builtbroken.armory.data.ammo;

import com.builtbroken.armory.data.ArmoryEntry;
import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.mc.api.data.weapon.IAmmoData;
import com.builtbroken.mc.api.data.weapon.IAmmoType;
import com.builtbroken.mc.lib.json.imp.IJsonProcessor;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class AmmoData extends ArmoryEntry implements IAmmoData
{
    public final AmmoType ammoType;

    public final List<DamageData> damageData = new ArrayList();
    public final float velocity;

    private float damageCached = -1;

    //TODO add optional damage types
    //TODO add effect handlers
    //TODO add damage calculations

    public AmmoData(IJsonProcessor processor, String id, String name, AmmoType ammoType, float velocity)
    {
        super(processor, id, "ammo", name);
        this.ammoType = ammoType;
        this.velocity = velocity;
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
        if (damageCached == -1)
        {
            damageCached = 0;
            for (DamageData data : damageData)
            {
                damageCached += data.getBaseDamage();
            }
        }
        return damageCached;
    }

    @Override
    public float getProjectileVelocity()
    {
        return velocity;
    }

    @Override
    public boolean onImpactEntity(Entity shooter, Entity entity, double hitX, double hitY, double hitZ, float velocity)
    {
        boolean destroy = false;
        for (DamageData data : damageData)
        {
            if (data.onImpact(shooter, entity, hitX, hitY, hitZ, velocity, 1))
            {
                destroy = true;
            }
        }
        return destroy;
    }

    @Override
    public boolean onImpactGround(Entity shooter, World world, int x, int y, int z, double hitX, double hitY, double hitZ, float velocity)
    {
        boolean destroy = false;
        for (DamageData data : damageData)
        {
            if (data.onImpact(shooter, world, x, y, z, hitX, hitY, hitZ, velocity, 1))
            {
                destroy = true;
            }
        }
        return destroy;
    }

    @Override
    public String toString()
    {
        return "Ammo[" + getUniqueID() + "]@" + hashCode();
    }
}
