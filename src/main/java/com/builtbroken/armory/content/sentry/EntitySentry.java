package com.builtbroken.armory.content.sentry;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.data.ranged.GunInstance;
import com.builtbroken.armory.data.sentry.SentryData;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.PacketSpawnStream;
import com.builtbroken.mc.imp.transform.rotation.EulerAngle;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.entity.EntityBase;
import com.builtbroken.mc.prefab.entity.selector.EntitySelectors;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.awt.*;
import java.util.List;

/**
 * AI driven entity for handling how the sentry gun works
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2017.
 */
public class EntitySentry extends EntityBase
{
    /** Rotations per second */
    protected static double ROTATION_SPEED = 1.0;

    /** Desired aim angle, updated every tick if target != null */
    protected final EulerAngle aim = new EulerAngle(0, 0, 0);
    /** Current aim angle, updated each tick */
    protected final EulerAngle currentAim = new EulerAngle(0, 0, 0);
    /** Default aim to use when not targeting things */
    protected final EulerAngle defaultAim = new EulerAngle(0, 0, 0);

    protected Pos center;
    protected Pos aimPoint;

    protected SentryData data;
    protected GunInstance gunInstance;
    public TileSentry base;

    protected Entity target;

    protected int targetSearchTimer = 0;
    protected int targetingDelay = 0;
    protected int targetingLoseTimer = 0;

    /** Last time rotation was updated, used in {@link EulerAngle#lerp(EulerAngle, double)} function for smooth rotation */
    protected long lastRotationUpdate = System.nanoTime();
    /** Percent of time that passed since last tick, should be 1.0 on a stable server */
    protected double deltaTime;

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
    public void setPosition(double x, double y, double z)
    {
        super.setPosition(x, y, z);
        center = new Pos(x, y + (height / 2f), z);
    }

    @Override
    public void onEntityUpdate()
    {
        if (!world().isRemote && ticksExisted % 2 == 0)
        {
            deltaTime = (System.nanoTime() - lastRotationUpdate) / 100000000.0; // time / time_tick, client uses different value
            lastRotationUpdate = System.nanoTime();
            //Invalid entity
            if (base == null || data == null || base.isInvalid())
            {
                kill();
            }
            else
            {
                if (gunInstance == null)
                {
                    gunInstance = new GunInstance(new ItemStack(Armory.blockSentry), this, data.gunData);
                }
                //If no target try to find one
                if (target == null)
                {
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
                        //Update aim point
                        aimPoint = getAimPoint(target);

                        aim.set(center.toEulerAngle(aimPoint).clampTo360());

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

        List<Entity> entityList = world().getEntitiesWithinAABBExcludingEntity(this, searchArea, EntitySelectors.PLAYER_SELECTOR.selector()); //TODO replace selector with custom
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

    /**
     * Checks if the entity is valid
     * <p>
     * Checks distance
     * Checks ray trace
     * Checks for life
     * Checks if can attack
     * Checks if matches target type
     *
     * @param entity - potential or existing target
     * @return true if valid
     */
    protected boolean isValidTarget(Entity entity)
    {
        if (entity != null && entity.isEntityAlive())
        {
            //Get aim position of entity
            final Pos aimPoint = getAimPoint(entity); //TODO retry with lower and higher aim value

            //Check to ensure we are in range
            double distance = center.distance(aimPoint);
            if (distance <= data.range)
            {
                //Trace to make sure no blocks are between shooter and target
                EulerAngle aim = center.toEulerAngle(aimPoint).clampTo360();
                MovingObjectPosition hit = center.add(aim.toPos().multiply(1.3)).rayTraceBlocks(world(), aimPoint);

                return hit == null || hit.typeOfHit == MovingObjectPosition.MovingObjectType.MISS;
            }
        }
        return false;
    }

    /**
     * Gets the point to aim at the target
     *
     * @param entity
     * @return
     */
    protected Pos getAimPoint(Entity entity)
    {
        float height = (entity.height / 2f);
        return new Pos(entity.posX, entity.posY + height, entity.posZ);
    }

    /**
     * Called to aim at the current target
     */
    protected void aimAtTarget()
    {
        //Aim at point
        currentAim.moveTowards(aim, ROTATION_SPEED, deltaTime).clampTo360();
        setRotation((float) currentAim.yaw(), (float) currentAim.pitch());
    }

    /**
     * Called to check if we are aimed at the target
     *
     * @return
     */
    protected boolean isAimed()
    {
        return aim.isWithin(currentAim, ROTATION_SPEED); //TODO implement
    }

    /**
     * Called to fire at the target
     */
    protected void fireAtTarget()
    {
        //TODO implement
        if (Engine.runningAsDev)
        {
            PacketSpawnStream packet = new PacketSpawnStream(world().provider.dimensionId, center, aimPoint, 2);
            packet.red = (Color.blue.getRed() / 255f);
            packet.green = (Color.blue.getGreen() / 255f);
            packet.blue = (Color.blue.getBlue() / 255f);
            Engine.instance.packetHandler.sendToAllAround(packet, new Location((Entity) this), 200);
        }

        if (hasAmmo())
        {

        }
    }

    /**
     * Called to consume ammo
     *
     * @param count
     */
    protected void consumeAmmo(int count)
    {//TODO implement

    }

    /**
     * Called to see if we have ammo
     *
     * @return
     */
    protected boolean hasAmmo()
    {//TODO implement
        return true;
    }
}
