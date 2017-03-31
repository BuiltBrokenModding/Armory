package com.builtbroken.armory.content.sentry;

import com.builtbroken.armory.data.sentry.SentryData;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.PacketSpawnParticleStream;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.entity.EntityBase;
import com.builtbroken.mc.prefab.entity.selector.EntitySelectors;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

/**
 * AI driven entity for handling how the sentry gun works
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2017.
 */
public class EntitySentry extends EntityBase
{
    protected SentryData data;
    protected TileSentryBase base;

    protected Entity target;

    protected int targetSearchTimer = 0;
    protected int targetingDelay = 0;
    protected int targetingLoseTimer = 0;

    protected AxisAlignedBB searchArea;

    public EntitySentry(World world)
    {
        super(world);
        this.noClip = true;
        this.setSize(0.5f, 0.5f);
    }

    @Override
    public void onUpdate()
    {
        onEntityUpdate();

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;
        if (this.posY < -64.0D)
        {
            this.kill();
        }
    }

    @Override
    public void onEntityUpdate()
    {
        if (!world().isRemote)
        {
            //Invalid entity
            if (base == null || data == null)
            {
                kill();
            }
            else
            {
                //If no target try to find one
                if (target == null)
                {
                    target = null;
                    targetingDelay = 0;
                    targetingLoseTimer = 0;

                    if (targetSearchTimer++ >= data.targetSearchDelay)
                    {
                        targetSearchTimer = 0;
                        findTargets();
                    }
                }
                //If target and valid try to attack
                else if (isValidTarget(target))
                {
                    //Delay before attack
                    if (targetingDelay >= data.targetAttackDelay)
                    {
                        if (isAimed())
                        {
                            fireAtTarget();
                        }
                        else
                        {
                            aimAtTarget();
                        }
                    }
                    else
                    {
                        targetingDelay++;
                    }
                }
                //If target is not null and invalid, count until invalidated
                else if (target != null && targetingLoseTimer++ >= data.targetLossTimer)
                {
                    target = null;
                    targetingLoseTimer = 0;
                }
            }
        }
    }

    protected void findTargets()
    {
        //TODO thread
        if (searchArea == null)
        {
            searchArea = AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX, posY, posZ).expand(data.range, data.range, data.range);
        }

        List<Entity> entityList = world().getEntitiesWithinAABBExcludingEntity(this, searchArea, EntitySelectors.MOB_SELECTOR.selector()); //TODO replace selector with custom
        if (entityList != null && entityList.size() > 0)
        {
            //TODO sort by distance
            //TODO sort by hp & armor
            //TODO sort by threat
            //TODO add settings to control sorting
            for (Entity entity : entityList)
            {
                if (entity.isEntityAlive() && isValidTarget(entity))
                {
                    target = entity;
                    break;
                }
            }
        }
    }

    protected boolean isValidTarget(Entity entity)
    {
        //TODO is in range
        //TODO is attackable
        //TODO can be seen
        return true;
    }

    protected void aimAtTarget()
    {
        //TODO implement
    }

    protected boolean isAimed()
    {
        return true; //TODO implement
    }

    protected void fireAtTarget()
    {
        //TODO implement
        Engine.instance.packetHandler.sendToAllAround(new PacketSpawnParticleStream(world().provider.dimensionId, new Pos((Entity) this), new Pos(target)), new Location((Entity) this), 100);
    }

    protected void consumeAmmo(int count)
    {//TODO implement

    }

    protected boolean hasAmmo()
    {//TODO implement
        return true;
    }
}
