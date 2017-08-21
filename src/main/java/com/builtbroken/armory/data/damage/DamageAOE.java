package com.builtbroken.armory.data.damage;

import com.builtbroken.armory.Armory;
import com.builtbroken.mc.framework.json.imp.IJsonProcessor;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

/**
 * Used to apply area of effect damage from a projectile.
 * <p>
 * This is used for explosives primarily
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/28/2017.
 */
public class DamageAOE extends DamageData
{
    /** Damage to apply, scaled by range normally */
    public final DamageData damageToApply;
    /** Max range in meters */
    public final float range;

    public DamageAOE(IJsonProcessor processor, DamageData damageToApply, float range)
    {
        super(processor);
        this.damageToApply = damageToApply;
        this.range = range;
    }

    @Override
    public boolean onImpact(Entity attacker, World world, int x, int y, int z, double hitX, double hitY, double hitZ, float velocity, float scale)
    {
        if (!world.isRemote)
        {
            doAOE(attacker, world, hitX, hitY, hitZ, velocity, scale);
        }
        return true;
    }

    @Override
    public boolean onImpact(Entity attacker, Entity entity, double hitX, double hitY, double hitZ, float velocity, float scale)
    {
        if (entity != null)
        {
            //TODO apply impact damage to hit entity
            doAOE(attacker, entity.worldObj, hitX, hitY, hitZ, velocity, scale);
        }
        return true;
    }

    protected void doAOE(Entity attacker, World world, double x, double y, double z, float velocity, float scale)
    {
        if (damageToApply != null)
        {
            AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(x, y, z, x, y, z).expand(range, range, range);
            List<Entity> entityList = world.getEntitiesWithinAABB(Entity.class, bounds);
            if (entityList != null && !entityList.isEmpty())
            {
                for (Entity entity : entityList)
                {
                    if (entity.isEntityAlive())
                    {
                        double distance = entity.getDistance(x, y, z);
                        if (distance <= range)
                        {
                            damageToApply.onImpact(attacker, entity, x, y, z, velocity, (float) (scale * (1 - (distance / range))));
                        }
                    }
                }
            }
        }
        else
        {
            Armory.INSTANCE.logger().error("doAOE(" + world.provider.dimensionId + ", " + x + "," + y + "," + z + "," + scale + ") was called without a damage type to apply.", new RuntimeException());
        }
    }

    @Override
    public String toString()
    {
        return "DamageAOE[" + damageToApply + "  " + range + "m]@" + hashCode();
    }
}
