package com.builtbroken.armory.data.damage.physics;

import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.mc.framework.json.imp.IJsonProcessor;
import com.builtbroken.mc.imp.transform.vector.Pos;
import net.minecraft.entity.Entity;

/**
 * Work around for adding knock back or pulling force to weapons
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/28/2017.
 */
public class DamageForce extends DamageData
{
    /** Max range in meters */
    public final float power;

    public DamageForce(IJsonProcessor processor, float power)
    {
        super(processor);
        this.power = power;
    }

    @Override
    public boolean onImpact(Entity attacker, Entity entity, double hitX, double hitY, double hitZ, float velocity, float scale)
    {
        if (entity != null && entity.isEntityAlive())
        {
            Pos pos = new Pos(attacker.posX, attacker.posY + (attacker.height / 2), attacker.posZ).sub(hitX, hitY, hitZ);
            //TODO get vector for attack to apply force correctly
            //TODO apply spin if hit is more to one side (make sure spin is low and customizable)
            pos = pos.normalize().multiply(-power);
            entity.addVelocity(pos.x(), pos.y(), pos.z());
        }

        return true;
    }

    @Override
    public String getDisplayString()
    {
        if (power > 0)
        {
            return "knock back of " + power + "m";
        }
        return "pull of " + power + "m";
    }

    @Override
    public String toString()
    {
        return "DamageForce[" + power + "m/s]@" + hashCode();
    }
}
