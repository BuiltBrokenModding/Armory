package com.builtbroken.armory.data.user;

import com.builtbroken.mc.imp.transform.vector.Pos;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/11/2017.
 */
public class WeaponUserEntity<E extends Entity> implements IWeaponUser
{
    public final E entity;

    public WeaponUserEntity(E entity)
    {
        this.entity = entity;
    }

    @Override
    public Pos getEntityPosition()
    {
        return new Pos(x(), y() + (entity.height / 2f), z());
    }

    @Override
    public IInventory getInventory()
    {
        return entity instanceof IInventory ? (IInventory) entity : null; //TODO maybe use fake inventory to reduce chance of NPE
    }

    @Override
    public boolean isAmmoSlot(int slot)
    {
        return true;
    }

    @Override
    public double yaw()
    {
        return entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw);
    }

    @Override
    public double pitch()
    {
        return entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch);
    }

    @Override
    public double roll()
    {
        return 0;
    }

    @Override
    public World world()
    {
        return entity.worldObj;
    }

    @Override
    public double x()
    {
        return entity.posX;
    }

    @Override
    public double y()
    {
        return entity.posY;
    }

    @Override
    public double z()
    {
        return entity.posZ;
    }

    @Override
    public Entity getShooter()
    {
        return entity;
    }
}
