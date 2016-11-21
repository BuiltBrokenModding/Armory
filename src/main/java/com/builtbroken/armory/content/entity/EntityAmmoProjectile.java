package com.builtbroken.armory.content.entity;

import com.builtbroken.mc.api.data.weapon.IAmmoData;
import com.builtbroken.mc.api.modules.weapon.IGun;
import com.builtbroken.mc.prefab.entity.EntityProjectile;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * Entity representation of ammo
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/21/2016.
 */
public class EntityAmmoProjectile extends EntityProjectile
{
    protected IAmmoData data;
    protected IGun weapon;

    public EntityAmmoProjectile(World world)
    {
        super(world);
        this.setSize(0.1F, 0.1F); //TODO set size based on projectile data
    }

    public EntityAmmoProjectile(World world, IAmmoData data, IGun firingWeapon, Entity shooter)
    {
        this(world);
        this.data = data;
        this.weapon = firingWeapon;
        this.shootingEntity = shooter;
        this.shootingEntityUUID = shooter.getUniqueID();
    }

    @Override
    protected void onImpactTile()
    {
        if (data.onImpactGround(shootingEntity, worldObj, xTile, yTile, zTile, posX, posY, posZ, getVelocity()))
        {
            this.setDead();
        }
        else
        {
            //TODO figure out how to handle based on projectile
        }
    }

    @Override
    protected void onImpactEntity(Entity entityHit, float velocity)
    {
        if (entityHit instanceof EntityProjectile)
        {
            //TODO implement special handling for impacting bullets
            //TODO for now ignore bullet collisions
            return;
        }
        if (data.onImpactEntity(entityHit, shootingEntity, velocity))
        {
            this.setDead();
        }
        else
        {
            //TODO figure out how to handle based on projectile
        }
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity)
    {
        //TODO ?
        return null;
    }
}
