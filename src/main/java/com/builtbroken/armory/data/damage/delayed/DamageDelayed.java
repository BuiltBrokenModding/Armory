package com.builtbroken.armory.data.damage.delayed;

import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.jlib.data.Colors;
import com.builtbroken.mc.framework.json.imp.IJsonProcessor;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * Work around for adding knock back or pulling force to weapons
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/28/2017.
 */
public class DamageDelayed extends DamageData
{
    /** Max range in meters */
    public final int ticks;
    public DamageData damageToApply;
    public boolean moveWithTarget = true;
    public boolean destroyProjectile = true;

    public DamageDelayed(IJsonProcessor processor, DamageData damageToApply, int ticks)
    {
        super(processor);
        this.damageToApply = damageToApply;
        this.ticks = ticks;
    }

    @Override
    public boolean onImpact(Entity attacker, Entity entity, double hitX, double hitY, double hitZ, float velocity, float scale)
    {
        if (damageToApply != null)
        {
            new DelayedActionDamage(attacker.worldObj, ticks, false)
                    .setDamage(damageToApply)
                    .setAttacker(attacker)
                    .setTarget(entity)
                    .setHit(hitX, hitY, hitZ)
                    .setVelocity(velocity)
                    .setScale(scale)
                    .setMoveWithTarget(moveWithTarget)
                    .start();
        }
        return destroyProjectile;
    }

    @Override
    public boolean onImpact(Entity attacker, World world, int x, int y, int z, double hitX, double hitY, double hitZ, float velocity, float scale)
    {
        if (damageToApply != null)
        {
            new DelayedActionDamage(world, ticks, false)
                    .setDamage(damageToApply)
                    .setAttacker(attacker)
                    .setTarget(x, y, z)
                    .setHit(hitX, hitY, hitZ)
                    .setVelocity(velocity)
                    .setScale(scale)
                    .setMoveWithTarget(moveWithTarget)
                    .start();
        }
        return destroyProjectile;
    }

    @Override
    public String getDisplayString()
    {
        if (damageToApply == null)
        {
            return Colors.RED.code + "Error: no damage data";
        }
        return damageToApply.getDisplayString();
    }

    @Override
    public String toString()
    {
        return "DamageDelay[" + ticks + "t >> " + damageToApply + "m]@" + hashCode();
    }
}
