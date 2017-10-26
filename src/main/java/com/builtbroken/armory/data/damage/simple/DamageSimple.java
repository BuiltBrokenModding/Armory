package com.builtbroken.armory.data.damage.simple;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.armory.data.damage.type.DamageType;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.framework.json.imp.IJsonProcessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/28/2017.
 */
public class DamageSimple extends DamageData
{
    public static final HashMap<String, DamageType> damageTypes = new HashMap();

    public final float damage;
    public final String damageName;

    public boolean doKnockBack = false;


    public DamageSimple(IJsonProcessor processor, String type, float damage)
    {
        super(processor);
        this.damageName = type.toLowerCase();
        this.damage = damage;
    }

    @Override
    public boolean onImpact(Entity attacker, Entity entity, double hitX, double hitY, double hitZ, float velocity, float scale)
    {
        //TODO calculate armor for some damage types
        //TODO knock back force to entity hit
        if (entity != null)
        {
            //Special case for fire damage
            if(damageName.equalsIgnoreCase("fire"))
            {
                entity.setFire((int)Math.floor(damage));
                return true;
            }

            DamageSource damageSource = DamageSource.generic;
            if (damageTypes.containsKey(damageName))
            {
                DamageSource damageSource1 = damageTypes.get(damageName).createDamage(attacker);
                if (damageSource1 != null)
                {
                    damageSource = damageSource1;
                }
            }
            else
            {
                DamageSource damageSource1 = fromMinecraft(attacker);
                if (damageSource1 != null)
                {
                    damageSource = damageSource1;
                }
            }

            //Fix for entities being immune to attacks after being hit once
            if (Armory.overrideDamageDelay)
            {
                entity.hurtResistantTime = 0;
            }

            if (attacker != null)
            {
                //Store velocity
                double mx = entity.motionX;
                double my = entity.motionY;
                double mz = entity.motionZ;

                //Store HP
                float hp = entity instanceof EntityLivingBase ? ((EntityLivingBase) entity).getHealth() : -1;

                if(!(damageSource instanceof EntityDamageSource))
                {
                    damageSource = new DamageSourceShooter(damageName, attacker, damageSource);
                }
                //Do attack
                boolean attackedEntity = entity.attackEntityFrom(damageSource, this.damage * scale);

                //Debug
                if (Engine.runningAsDev)
                {
                    if (attackedEntity)
                    {
                        Armory.INSTANCE.logger().info("Damage(" + attacker + ", " + entity + ", .... ) applied damage");
                    }
                    else
                    {
                        Armory.INSTANCE.logger().info("Damage(" + attacker + ", " + entity + ", .... ) applied no damage");
                    }
                }

                //Get hp after attack
                float hp2 = entity instanceof EntityLivingBase ? ((EntityLivingBase) entity).getHealth() : -1;

                //Get damage applied
                float damage = hp - hp2;

                //Debug
                if (Engine.runningAsDev)
                {
                    Armory.INSTANCE.logger().info("\tDamage = " + damage);
                }

                //Reset velocity
                if (!doKnockBack)
                {
                    //TODO set knock back resistance high instead as a wall to fix
                    /** {@link EntityLivingBase#knockBack(Entity, float, double, double)} */
                    entity.motionX = mx;
                    entity.motionY = my;
                    entity.motionZ = mz;
                    //TODO add way to control knock back amount and direction
                }
            }
            else
            {
                entity.attackEntityFrom(damageSource, this.damage * scale);
            }
        }

        return true;
    }

    protected final DamageSource fromMinecraft(Entity attacker)
    {
        if (damageName.equalsIgnoreCase("inFire"))
        {
            return DamageSource.inFire;
        }
        else if (damageName.equalsIgnoreCase("onFire"))
        {
            return DamageSource.onFire;
        }
        else if (damageName.equals("lava"))
        {
            return DamageSource.lava;
        }
        else if (damageName.equalsIgnoreCase("inWall"))
        {
            return DamageSource.inWall;
        }
        else if (damageName.equals("drown"))
        {
            return DamageSource.drown;
        }
        else if (damageName.equals("starve"))
        {
            return DamageSource.starve;
        }
        else if (damageName.equals("cactus"))
        {
            return DamageSource.cactus;
        }
        else if (damageName.equals("fall"))
        {
            return DamageSource.fall;
        }
        else if (damageName.equalsIgnoreCase("outOfWorld"))
        {
            return DamageSource.outOfWorld;
        }
        else if (damageName.equalsIgnoreCase("generic"))
        {
            return DamageSource.generic;
        }
        else if (damageName.equalsIgnoreCase("magic"))
        {
            return DamageSource.magic;
        }
        else if (damageName.equalsIgnoreCase("wither"))
        {
            return DamageSource.wither;
        }
        else if (damageName.equalsIgnoreCase("anvil"))
        {
            return DamageSource.anvil;
        }
        else if (damageName.equalsIgnoreCase("fallingBlock"))
        {
            return DamageSource.fallingBlock;
        }
        return null;
    }

    @Override
    public float getBaseDamage()
    {
        return damage;
    }

    @Override
    public String getDisplayString()
    {
        return damageName + " for " + damageName + "hp";
    }

    @Override
    public String toString()
    {
        return "DamageSimple[" + damageName + " " + damage + "hp]@" + hashCode();
    }
}
