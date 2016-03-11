package com.builtbroken.armory.content.prefab;

import com.builtbroken.mc.api.event.TriggerCause;
import com.builtbroken.mc.api.explosive.IExplosive;
import com.builtbroken.mc.api.explosive.IExplosiveHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityThrowableExplosive extends EntityThrowable implements IExplosive
{
    protected IExplosiveHandler explosive;
    protected NBTTagCompound explosive_data;
    protected double size = 1;

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
            if(getExplosive() != null)
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