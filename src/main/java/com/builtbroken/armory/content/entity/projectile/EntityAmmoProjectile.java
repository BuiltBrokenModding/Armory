package com.builtbroken.armory.content.entity.projectile;

import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.mc.api.data.weapon.IAmmoData;
import com.builtbroken.mc.api.modules.weapon.IGun;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.prefab.entity.EntityProjectile;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * Entity representation of ammo
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/21/2016.
 */
public class EntityAmmoProjectile extends EntityProjectile implements IEntityAdditionalSpawnData
{
    protected IAmmoData ammoData;
    protected IGun weapon;

    public EntityAmmoProjectile(World world)
    {
        super(world);
        this.setSize(0.1F, 0.1F); //TODO set size based on projectile data
    }

    public EntityAmmoProjectile(World world, IAmmoData data, IGun firingWeapon, Entity shooter)
    {
        this(world);
        this.ammoData = data;
        this.weapon = firingWeapon;
        this.shootingEntity = shooter;
        this.shootingEntityUUID = shooter.getUniqueID();
    }

    @Override
    protected void onImpactTile(MovingObjectPosition hit)
    {
        if (Engine.runningAsDev)
        {
            Engine.logger().info("Projectile impact tile>> Client:" + worldObj.isRemote + " " + worldObj.provider.dimensionId + "d " + xTile + "x " + yTile + "y " + zTile + "z " + inBlockID + "b");
        }
        if (!worldObj.isRemote)
        {
            if (ammoData != null)
            {
                if (ammoData.onImpactGround(shootingEntity, worldObj, xTile, yTile, zTile, hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord, getVelocity()))
                {
                    this.setDead();
                }
                else
                {
                    //TODO figure out how to handle based on projectile
                }
            }
        }
    }

    @Override
    protected void onImpactEntity(Entity entityHit, float velocity, MovingObjectPosition hit)
    {
        if (Engine.runningAsDev)
        {
            Engine.logger().info("Projectile impact entity>> Client:" + worldObj.isRemote + " " + worldObj.provider.dimensionId + "d " + entityHit);
        }
        if (!worldObj.isRemote)
        {
            if (ammoData != null)
            {
                if (entityHit instanceof EntityProjectile)
                {
                    //TODO implement special handling for impacting bullets
                    //TODO for now ignore bullet collisions
                    return;
                }
                if (ammoData.onImpactEntity(entityHit, shootingEntity, hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord, velocity))
                {
                    this.setDead();
                }
                else
                {
                    //TODO figure out how to handle based on projectile
                }
            }
        }
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity)
    {
        //TODO ?
        return null;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (ammoData != null)
        {
            nbt.setString("ammoID", ammoData.getUniqueID());
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("ammoID"))
        {
            String id = nbt.getString("ammoID");
            Object object = ArmoryDataHandler.INSTANCE.get("ammo").get(id);
            if (object instanceof IAmmoData)
            {
                ammoData = (IAmmoData) object;
            }
        }
    }


    @Override
    public void writeSpawnData(ByteBuf buffer)
    {
        buffer.writeBoolean(ammoData != null);
        if (ammoData != null)
        {
            ByteBufUtils.writeUTF8String(buffer, ammoData.getUniqueID());
        }
    }

    @Override
    public void readSpawnData(ByteBuf additionalData)
    {
        if (additionalData.readBoolean())
        {
            String id = ByteBufUtils.readUTF8String(additionalData);
            Object object = ArmoryDataHandler.INSTANCE.get("ammo").get(id);
            if (object instanceof IAmmoData)
            {
                ammoData = (IAmmoData) object;
            }
        }
        else
        {
            ammoData = null;
        }
    }
}
