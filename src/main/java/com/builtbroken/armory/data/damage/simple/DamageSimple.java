package com.builtbroken.armory.data.damage.simple;

import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.mc.lib.json.imp.IJsonProcessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;

import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/28/2017.
 */
public class DamageSimple extends DamageData
{
    public static final HashMap<String, DamageType> damageTypes = new HashMap();

    static
    {

    }

    public final float damage;
    public final String damageSource;


    public DamageSimple(IJsonProcessor processor, String type, float damage)
    {
        super(processor);
        this.damageSource = type.toLowerCase();
        this.damage = damage;
    }

    @Override
    public boolean onImpact(Entity attacker, Entity entity, double hitX, double hitY, double hitZ, float velocity, float scale)
    {
        //TODO create damage source with shooter, gun data, and damage type
        //TODO calculate armor
        //TODO apply force
        if (entity != null)
        {
            DamageSource damageSource = null;
            if (damageTypes.containsKey(damageSource))
            {
                //TODO wrapper so attacker is marked as source
                DamageSource damageSource1 = damageTypes.get(damageSource).createDamage(attacker);
                if (damageSource1 != null)
                {
                    damageSource = damageSource1;
                }
            }
            else
            {
                //TODO wrapper so attacker is marked as source
                DamageSource damageSource1 = fromMinecraft(attacker);
                if (damageSource1 != null)
                {
                    damageSource = damageSource1;
                }
            }

            if (damageSource == null)
            {
                if (attacker instanceof EntityPlayer)
                {
                    entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) attacker).setProjectile(), damage * scale);
                }
                else if (attacker instanceof EntityLivingBase)
                {
                    entity.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase) attacker).setProjectile(), damage * scale);
                }
                else
                {
                    entity.attackEntityFrom(new EntityDamageSourceIndirect("projectile", attacker, attacker).setProjectile(), damage * scale);
                }
            }
            else
            {
                entity.attackEntityFrom(damageSource, damage * scale);
            }
        }

        return true;
    }

    protected final DamageSource fromMinecraft(Entity attacker)
    {
        if (damageSource.equalsIgnoreCase("inFire"))
        {
            return DamageSource.inFire;
        }
        else if (damageSource.equalsIgnoreCase("onFire"))
        {
            return DamageSource.onFire;
        }
        else if (damageSource.equals("lava"))
        {
            return DamageSource.lava;
        }
        else if (damageSource.equalsIgnoreCase("inWall"))
        {
            return DamageSource.inWall;
        }
        else if (damageSource.equals("drown"))
        {
            return DamageSource.drown;
        }
        else if (damageSource.equals("starve"))
        {
            return DamageSource.starve;
        }
        else if (damageSource.equals("cactus"))
        {
            return DamageSource.cactus;
        }
        else if (damageSource.equals("fall"))
        {
            return DamageSource.fall;
        }
        else if (damageSource.equalsIgnoreCase("outOfWorld"))
        {
            return DamageSource.outOfWorld;
        }
        else if (damageSource.equalsIgnoreCase("generic"))
        {
            return DamageSource.generic;
        }
        else if (damageSource.equalsIgnoreCase("magic"))
        {
            return DamageSource.magic;
        }
        else if (damageSource.equalsIgnoreCase("wither"))
        {
            return DamageSource.wither;
        }
        else if (damageSource.equalsIgnoreCase("anvil"))
        {
            return DamageSource.anvil;
        }
        else if (damageSource.equalsIgnoreCase("fallingBlock"))
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
}
