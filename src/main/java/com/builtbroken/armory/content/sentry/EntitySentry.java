package com.builtbroken.armory.content.sentry;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.data.ranged.GunInstance;
import com.builtbroken.armory.data.sentry.SentryData;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.PacketSpawnParticle;
import com.builtbroken.mc.imp.transform.rotation.EulerAngle;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.entity.EntityBase;
import com.builtbroken.mc.prefab.entity.selector.EntitySelectors;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.Collections;
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
    protected static double ROTATION_SPEED = 10.0;

    /** Desired aim angle, updated every tick if target != null */
    protected final EulerAngle aim = new EulerAngle(0, 0, 0);
    /** Current aim angle, updated each tick */
    protected final EulerAngle currentAim = new EulerAngle(0, 0, 0);
    /** Default aim to use when not targeting things */
    protected final EulerAngle defaultAim = new EulerAngle(0, 0, 0);

    protected Pos center;
    protected Pos aimPoint;

    public SentryData data;
    public GunInstance gunInstance;
    public TileSentry base;

    protected Entity target;

    protected int targetSearchTimer = 0;
    protected int targetingDelay = 0;
    protected int targetingLoseTimer = 0;

    /** Last time rotation was updated, used in {@link EulerAngle#lerp(EulerAngle, double)} function for smooth rotation */
    protected long lastRotationUpdate = System.nanoTime();
    /** Percent of time that passed since last tick, should be 1.0 on a stable server */
    protected double deltaTime;

    /** Areas to search for targets */
    protected AxisAlignedBB searchArea;

    /** Offset to use to prevent clipping self with ray traces */
    public double halfWidth = 0;

    public Pos bulletSpawnOffset;

    public EntitySentry(World world)
    {
        super(world);
        this.noClip = true;
        this.setSize(0.7f, 0.7f);
    }

    @Override
    protected void setSize(float width, float height)
    {
        super.setSize(width, height);
        halfWidth = Math.sqrt((width * width) * 2) / 2f;
    }


    @Override
    public void setPosition(double x, double y, double z)
    {
        super.setPosition(x, y, z);
        center = new Pos(x, y + (height / 2f), z);
        searchArea = null;
    }

    /**
     * Callculates the offset point to use
     * for ray tracing and bullet spawning
     */
    protected void calculateBulletSpawnOffset()
    {
        float yaw = rotationYaw;
        while (yaw < 0)
        {
            yaw += 360;
        }
        while (yaw > 360)
        {
            yaw -= 360;
        }
        final double radianYaw = Math.toRadians(-yaw - 45 - 90);

        float pitch = rotationPitch;
        while (pitch < 0)
        {
            pitch += 360;
        }
        while (pitch > 360)
        {
            pitch -= 360;
        }
        final double radianPitch = Math.toRadians(pitch);

        bulletSpawnOffset = new Pos(
                (Math.cos(radianYaw) - Math.sin(radianYaw)) * halfWidth,
                halfWidth * Math.sin(radianYaw) * Math.sin(radianPitch),
                (Math.sin(radianYaw) + Math.cos(radianYaw)) * halfWidth
        );
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
    public boolean interactFirst(EntityPlayer player)
    {
        return base != null && base.onPlayerRightClick(player, 1, new Pos());
    }

    @Override
    public void onEntityUpdate()
    {
        //Calculate bullet offset
        calculateBulletSpawnOffset();

        //Update logic every other tick
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
                if (Engine.runningAsDev)
                {
                    Pos hand = center.add(bulletSpawnOffset);
                    PacketSpawnParticle packetSpawnParticle = new PacketSpawnParticle("smoke", world().provider.dimensionId, hand.x(), hand.y(), hand.z(), 0, 0, 0);
                    Engine.instance.packetHandler.sendToAll(packetSpawnParticle);

                    packetSpawnParticle = new PacketSpawnParticle("flame", world().provider.dimensionId, center.x(), center.y(), center.z(), 0, 0, 0);
                    Engine.instance.packetHandler.sendToAll(packetSpawnParticle);
                }

                if (gunInstance == null)
                {
                    gunInstance = new GunInstance(new ItemStack(Armory.blockSentry), this, data.getGunData());
                    if (Engine.runningAsDev)
                    {
                        gunInstance.doDebugRayTracesOnTthisGun = true;
                    }
                }
                //If no target try to find one
                if (target == null)
                {
                    targetingDelay = 0;
                    targetingLoseTimer = 0;

                    if (targetSearchTimer++ >= data.getTargetSearchDelay())
                    {
                        targetSearchTimer = 0;
                        findTargets();
                    }
                }
                //If target and valid try to attack
                else if (isValidTarget(target))
                {
                    //Delay before attack
                    if (targetingDelay >= data.getTargetAttackDelay())
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
                else if (target != null && targetingLoseTimer++ >= data.getTargetLossTimer())
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
            searchArea = AxisAlignedBB.getBoundingBox(posX, posY, posZ, posX, posY, posZ).expand(data.getRange(), data.getRange(), data.getRange());
        }

        List<Entity> entityList = world().getEntitiesWithinAABBExcludingEntity(this, searchArea, getEntitySelector());
        Collections.sort(entityList, new SentryEntityTargetSorter(center));

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

    protected IEntitySelector getEntitySelector()
    {
        return EntitySelectors.MOB_SELECTOR.selector();
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
            if (distance <= data.getRange())
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
        if (!gunInstance.hasAmmo())
        {
            //TODO add reload timer and delay
            gunInstance.reloadWeapon(base, true);
        }

        //Debug
        gunInstance.debugRayTrace(center, aim.toPos(), aimPoint, bulletSpawnOffset, rotationYaw, rotationPitch);

        //Check fi has ammo, then fire
        if (gunInstance.hasAmmo())
        {
            gunInstance.fireWeapon(world(), 1, aimPoint, aim.toPos()); //TODO get firing ticks
        }
    }
}
