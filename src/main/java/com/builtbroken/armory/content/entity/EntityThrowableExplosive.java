package com.builtbroken.armory.content.entity;

import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.explosive.IExplosive;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * Entity that handles runtime logic for throwable explosives
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on ?/?/?
 */
public class EntityThrowableExplosive extends EntityThrowable implements IExplosive
{
    protected IExplosiveHandler explosive;
    protected NBTTagCompound explosive_data;
    protected double size = 1;

    //TODO implement triggers from ICBM
    //TODO implement data object

    public EntityThrowableExplosive(World world)
    {
        super(world);
    }

    public EntityThrowableExplosive(World world, EntityLivingBase thrower)
    {
        super(world, thrower);
    }

    public EntityThrowableExplosive setExplosive(IExplosiveHandler explosive)
    {
        this.explosive = explosive;
        return this;
    }

    @Override
    protected void onImpact(MovingObjectPosition hit)
    {
        if (!this.worldObj.isRemote)
        {
            if (getExplosive() != null)
            {
                TriggerCause cause = new TriggerCause.TriggerCauseEntity(getThrower() != null ? getThrower() : this);
                getExplosive().createBlastForTrigger(worldObj, posX, posY, posZ, cause, size, explosive_data);
            }

            this.setDead();
        }
    }

    @Override
    public IExplosiveHandler getExplosive()
    {
        return explosive;
    }

    @Override
    public NBTTagCompound getAdditionalExplosiveData()
    {
        return explosive_data;
    }

    @Override
    public double getExplosiveSize()
    {
        return size;
    }
}