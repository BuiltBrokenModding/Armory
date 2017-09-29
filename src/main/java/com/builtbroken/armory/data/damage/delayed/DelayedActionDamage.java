package com.builtbroken.armory.data.damage.delayed;

import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.mc.framework.thread.delay.DelayedAction;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/28/2017.
 */
public class DelayedActionDamage extends DelayedAction
{
    DamageData damage;
    Entity attacker;
    Entity entity;
    int x, y, z;
    double hitX, hitY, hitZ;
    float velocity, scale;

    private boolean blockImpact;
    private boolean moveWithTarget;

    public DelayedActionDamage(World world, int ticks, boolean blockImpact)
    {
        super(world, ticks);
        this.blockImpact = blockImpact;
    }

    public DelayedActionDamage setDamage(DamageData damage)
    {
        this.damage = damage;
        return this;
    }

    public DelayedActionDamage setAttacker(Entity attacker)
    {
        this.attacker = attacker;
        return this;
    }

    public DelayedActionDamage setTarget(Entity entity)
    {
        this.entity = entity;
        return this;
    }

    public DelayedActionDamage setTarget(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public DelayedActionDamage setHit(double hitX, double hitY, double hitZ)
    {
        this.hitX = hitX;
        this.hitY = hitY;
        this.hitZ = hitZ;
        return this;
    }

    public DelayedActionDamage setVelocity(float velocity)
    {
        this.velocity = velocity;
        return this;
    }

    public DelayedActionDamage setScale(float scale)
    {
        this.scale = scale;
        return this;
    }

    public DelayedActionDamage setMoveWithTarget(boolean b)
    {
        this.moveWithTarget = b;
        return this;
    }

    @Override
    public boolean trigger()
    {
        if (blockImpact)
        {
            damage.onImpact(attacker, world, x, y, z, hitX, hitY, hitZ, velocity, scale);
        }
        else
        {
            if(moveWithTarget)
            {
                hitX += entity.posX;
                hitY += entity.posY;
                hitZ += entity.posZ;
            }
            damage.onImpact(attacker, entity, hitX, hitY, hitZ, velocity, scale);
        }
        return true;
    }

    @Override
    public void start()
    {
       super.start();
        if(moveWithTarget && moveWithTarget)
        {
            hitX -= entity.posX;
            hitY -= entity.posY;
            hitZ -= entity.posZ;
            //TODO store target rotation and rotate hit position to match post movement of entity
        }

    }
}
