package com.builtbroken.armory.content.sentry;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.content.sentry.ai.EntityTargetSelector;
import com.builtbroken.armory.content.sentry.ai.SentryEntityTargetSorter;
import com.builtbroken.armory.content.sentry.imp.ISentryHost;
import com.builtbroken.armory.data.ranged.GunInstance;
import com.builtbroken.armory.data.sentry.SentryData;
import com.builtbroken.armory.data.user.IWeaponUser;
import com.builtbroken.jlib.data.network.IByteBufReader;
import com.builtbroken.jlib.data.network.IByteBufWriter;
import com.builtbroken.mc.api.ISave;
import com.builtbroken.mc.api.IWorldPosition;
import com.builtbroken.mc.api.energy.IEnergyBuffer;
import com.builtbroken.mc.api.energy.IEnergyBufferProvider;
import com.builtbroken.mc.api.tile.IFoFProvider;
import com.builtbroken.mc.api.tile.ILinkable;
import com.builtbroken.mc.api.tile.IPassCode;
import com.builtbroken.mc.api.tile.provider.IInventoryProvider;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.PacketSpawnStream;
import com.builtbroken.mc.imp.transform.rotation.EulerAngle;
import com.builtbroken.mc.imp.transform.rotation.IRotation;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.imp.transform.vector.Pos;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Actual sentry object that handles most of the functionality of the sentry
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/11/2017.
 */
public class Sentry implements IWorldPosition, IRotation, IWeaponUser, ISave, IByteBufReader, IByteBufWriter, IEnergyBufferProvider, ILinkable
{
    //TODO implement log system (enemy detected, enemy killed, ammo consumed, power failed, etc. with time stamps and custom log limits)
    /** Desired aim angle, updated every tick if target != null */
    protected final EulerAngle aim = new EulerAngle(0, 0, 0);
    /** Current aim angle, updated each tick */
    protected final EulerAngle currentAim = new EulerAngle(0, 0, 0);
    /** Default aim to use when not targeting things */
    protected final EulerAngle defaultAim = new EulerAngle(0, 0, 0); //TODO implement
    /** Data that defines this sentry instance */
    protected final SentryData sentryData;
    public final GunInstance gunInstance;
    /** Current host of this sentry */
    public ISentryHost host;

    public Pos center;
    public Pos aimPoint;
    public Pos bulletSpawnOffset;

    protected Entity target;


    protected int targetSearchTimer = 0;
    protected int targetingDelay = 0;
    protected int targetingLoseTimer = 0;

    /** Areas to search for targets */
    public AxisAlignedBB searchArea;

    /** Offset to use to prevent clipping self with ray traces */
    public double halfWidth = 0;

    //Status vars
    public String status = "loading";
    public boolean reloading = false;
    public boolean running = false;
    public boolean turnedOn = true;
    public boolean sentryHasAmmo = false;
    public boolean enableAimDebugRays = false;

    //Stats vars
    public float health = 0;

    /** Location of FoF station */
    public Pos fofStationPos;
    /** Cached fof station tile */
    public IFoFProvider fofStation;

    public EntityTargetSelector targetSelector;

    public final HashMap<String, TargetMode> targetModes = new HashMap();

    public Sentry(SentryData sentryData)
    {
        this.sentryData = sentryData;
        health = sentryData.getMaxHealth();
        gunInstance = new GunInstance(new ItemStack(Armory.blockSentry), this, getSentryData().getGunData());
        if (!gunInstance.getGunData().getReloadType().requiresItems() && sentryData.getAmmoData() != null)
        {
            gunInstance.overrideRound = sentryData.getAmmoData();
        }
        targetSelector = new EntityTargetSelector(this);
    }

    public boolean update(int ticks, float deltaTime)
    {
        if (!world().isRemote)
        {
            if (ticks == 1)
            {
                for (String key : sentryData.getAllowedTargetTypes())
                {
                    if (!targetModes.containsKey(key))
                    {
                        boolean added = false;
                        for (String k : sentryData.getDefaultTargetTypes())
                        {
                            if (k.equals(key))
                            {
                                targetModes.put(key, TargetMode.HOSTILE);
                                added = true;
                                break;
                            }
                        }
                        if (!added)
                        {
                            targetModes.put(key, TargetMode.NONE);
                        }
                    }
                }
            }
            //Update logic every other tick
            else if (ticks % 2 == 0)
            {
                //Calculate bullet offset
                calculateBulletSpawnOffset();

                status = "unknown";

                //Invalid entity
                if (host == null || getSentryData() == null)
                {
                    status = "invalid";
                }
                else
                {
                    //Reset state system
                    running = false;
                    sentryHasAmmo = false;
                    //Debug
                    if (enableAimDebugRays && Engine.runningAsDev)
                    {
                        Pos hand = center.add(bulletSpawnOffset);
                        //Debug ray trace
                        PacketSpawnStream packet = new PacketSpawnStream(world().provider.dimensionId, center.add(0, 0.1, 0), hand.add(0, 0.1, 0), 2);
                        packet.red = (Color.MAGENTA.getRed() / 255f);
                        packet.green = (Color.MAGENTA.getGreen() / 255f);
                        packet.blue = (Color.MAGENTA.getBlue() / 255f);
                        Engine.instance.packetHandler.sendToAllAround(packet, new Location(this), 200);

                        hand = center.add(getEntityAim());
                        //Debug ray trace
                        packet = new PacketSpawnStream(world().provider.dimensionId, center.add(0, 0.2, 0), hand.add(0, 0.2, 0), 2);
                        packet.red = (Color.blue.getRed() / 255f);
                        packet.green = (Color.blue.getGreen() / 255f);
                        packet.blue = (Color.blue.getBlue() / 255f);
                        Engine.instance.packetHandler.sendToAllAround(packet, new Location(this), 200);

                        hand = center.add(aim.toPos());
                        //Debug ray trace
                        packet = new PacketSpawnStream(world().provider.dimensionId, center.add(0, 0.3, 0), hand.add(0, 0.3, 0), 2);
                        packet.red = (Color.CYAN.getRed() / 255f);
                        packet.green = (Color.CYAN.getGreen() / 255f);
                        packet.blue = (Color.CYAN.getBlue() / 255f);
                        Engine.instance.packetHandler.sendToAllAround(packet, new Location(this), 200);


                        if (aimPoint != null)
                        {
                            //Debug ray trace
                            packet = new PacketSpawnStream(world().provider.dimensionId, center, aimPoint, 2);
                            packet.red = (Color.yellow.getRed() / 255f);
                            packet.green = (Color.yellow.getGreen() / 255f);
                            packet.blue = (Color.yellow.getBlue() / 255f);
                            Engine.instance.packetHandler.sendToAllAround(packet, new Location(this), 200);
                        }
                    }

                    //Can only function if we have a gun
                    if (gunInstance != null)
                    {
                        //Trigger reload mod if out of ammo
                        if (!gunInstance.hasMagWithAmmo() && gunInstance.getChamberedRound() == null)
                        {
                            reloading = true;
                        }
                        else
                        {
                            sentryHasAmmo = true;
                        }

                        if (reloading)
                        {
                            status = "reloading";
                            loadAmmo();
                            if (gunInstance.isFullOnAmmo())
                            {
                                reloading = false;
                            }
                        }
                        else
                        {
                            //If no target try to find one
                            if (target == null)
                            {
                                status = "searching";
                                targetingDelay = 0;
                                targetingLoseTimer = 0;

                                if (targetSearchTimer++ >= getSentryData().getTargetSearchDelay())
                                {
                                    targetSearchTimer = 0;
                                    findTargets();
                                }
                            }
                            //If target and valid try to attack
                            else if (isValidTarget(target))
                            {
                                status = "aiming";
                                //Delay before attack
                                if (targetingDelay >= getSentryData().getTargetAttackDelay())
                                {
                                    //Update aim point
                                    aimPoint = getAimPoint(target);

                                    aim.set(center.toEulerAngle(aimPoint).clampTo360());

                                    if (isAimed())
                                    {
                                        status = "attacking";
                                        fireAtTarget();
                                    }
                                    else
                                    {
                                        aimAtTarget(deltaTime);
                                    }
                                }
                                else
                                {
                                    targetingDelay++;
                                }
                            }
                            //If target is not null and invalid, count until invalidated
                            else if (target != null && targetingLoseTimer++ >= getSentryData().getTargetLossTimer())
                            {
                                status = "target lost";
                                target = null;
                                targetingLoseTimer = 0;
                            }
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    public int getPacketRefreshRate()
    {
        return 2;
    }

    /**
     * Callculates the offset point to use
     * for ray tracing and bullet spawning
     */
    protected void calculateBulletSpawnOffset()
    {
        float width = (float) Math.max(sentryData != null ? sentryData.getBarrelLength() : 0, halfWidth);

        bulletSpawnOffset = (Pos) new Pos(0, 0, -width).transform(currentAim);

        if (sentryData != null && sentryData.getBarrelOffset() != null)
        {
            bulletSpawnOffset = bulletSpawnOffset.add(sentryData.getBarrelOffset());
        }
    }

    protected void loadAmmo()
    {
        if (gunInstance != null && getInventory() != null)
        {
            if (!gunInstance.reloadWeapon(getInventory(), true))
            {
                reloading = false;
            }
        }
    }

    protected void findTargets()
    {
        //TODO thread
        if (searchArea == null)
        {
            searchArea = AxisAlignedBB.getBoundingBox(x(), y(), z(), x(), y(), z()).expand(getSentryData().getRange(), getSentryData().getRange(), getSentryData().getRange());
        }

        List<Entity> entityList = world().getEntitiesWithinAABBExcludingEntity(host instanceof Entity ? (Entity) host : null, searchArea, getEntitySelector());
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
        return targetSelector;
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
        if (entity != null && entity.isEntityAlive() && (getFoFStation() == null || !getFoFStation().isFriendly(entity)))
        {
            //Get aim position of entity
            final Pos aimPoint = getAimPoint(entity); //TODO retry with lower and higher aim value

            //Check to ensure we are in range
            double distance = center.distance(aimPoint);
            if (distance <= getSentryData().getRange())
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
        AxisAlignedBB bounds = entity.getBoundingBox() != null ? entity.getBoundingBox() : entity.boundingBox;
        return new Pos(entity.posX, entity.posY + ((bounds.maxY - bounds.minY) / 2f), entity.posZ);
    }

    /**
     * Called to aim at the current target
     */
    protected void aimAtTarget(float deltaTime)
    {
        //Aim at point
        currentAim.moveTowards(aim, getSentryData().getRotationSpeed(), deltaTime).clampTo360();
    }

    /**
     * Called to check if we are aimed at the target
     *
     * @return
     */
    protected boolean isAimed()
    {
        return aim.isWithin(currentAim, (getSentryData().getRotationSpeed() / 2f));
    }

    /**
     * Called to fire at the target
     */
    protected void fireAtTarget()
    {
        //Debug
        gunInstance.debugRayTrace(center, getEntityAim(), aimPoint, bulletSpawnOffset);

        //Check fi has ammo, then fire
        if (gunInstance.chamberNextRound())
        {
            aimPoint = getAimPoint(target);
            gunInstance.fireWeapon(world(), 1, aimPoint, bulletSpawnOffset); //TODO get firing ticks
        }
    }

    public SentryData getSentryData()
    {
        return sentryData;
    }

    @Override
    public World world()
    {
        return host != null ? host.world() : null;
    }

    @Override
    public double x()
    {
        return host != null ? host.x() : 0;
    }

    @Override
    public double y()
    {
        return host != null ? host.y() : 0;
    }

    @Override
    public double z()
    {
        return host != null ? host.z() : 0;
    }

    @Override
    public double yaw()
    {
        return currentAim.yaw();
    }

    @Override
    public double pitch()
    {
        return currentAim.pitch();
    }

    @Override
    public double roll()
    {
        return currentAim.roll();
    }

    @Override
    public Entity getShooter()
    {
        return host instanceof Entity ? (Entity) host : null;
    }

    @Override
    public Pos getEntityPosition()
    {
        return center;
    }

    @Override
    public Pos getEntityAim()
    {
        return currentAim.toPos();
    }

    @Override
    public Pos getProjectileSpawnOffset()
    {
        return bulletSpawnOffset;
    }

    @Override
    public IInventory getInventory()
    {
        if (host instanceof IInventory)
        {
            return (IInventory) host;
        }
        else if (host instanceof IInventoryProvider)
        {
            return ((IInventoryProvider) host).getInventory();
        }
        return null;
    }

    @Override
    public boolean isAmmoSlot(int slot)
    {
        return slot >= getSentryData().getInventoryAmmoStart() && slot <= getSentryData().getInventoryAmmoEnd();
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        if (nbt.hasKey("turnedOn"))
        {
            turnedOn = nbt.getBoolean("turnedOn");
        }
        if (nbt.hasKey("health"))
        {
            health = nbt.getFloat("health");
        }
        if (nbt.hasKey("currentAim"))
        {
            currentAim.readFromNBT(nbt.getCompoundTag("currentAim"));
        }
        if (nbt.hasKey("gunInstance"))
        {
            gunInstance.load(nbt.getCompoundTag("gunInstance"));
        }
        if (nbt.hasKey("fofStationPos"))
        {
            fofStationPos = new Pos(nbt.getCompoundTag("fofStationPos"));
        }
        if (nbt.hasKey("targetModes"))
        {
            NBTTagCompound list = nbt.getCompoundTag("targetModes");
            for (String key : sentryData.getAllowedTargetTypes())
            {
                byte value = list.getByte(key);
                if (value >= 0 && value < TargetMode.values().length)
                {
                    targetModes.put(key, TargetMode.values()[value]);
                }
            }
        }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        nbt.setBoolean("turnedOn", turnedOn);
        nbt.setFloat("health", health);
        nbt.setTag("currentAim", currentAim.toNBT());
        //Save gun
        NBTTagCompound gunTag = new NBTTagCompound();
        gunInstance.save(gunTag);
        nbt.setTag("gunInstance", gunTag);
        if (fofStationPos != null)
        {
            nbt.setTag("fofStationPos", fofStationPos.toNBT());
        }
        if (!targetModes.isEmpty())
        {
            NBTTagCompound list = new NBTTagCompound();
            for (Map.Entry<String, TargetMode> entry : targetModes.entrySet())
            {
                list.setByte(entry.getKey(), (byte) entry.getValue().ordinal());
            }
            nbt.setTag("targetModes", list);
        }
        return nbt;
    }

    @Override
    public Sentry readBytes(ByteBuf buf)
    {
        running = buf.readBoolean();
        turnedOn = buf.readBoolean();
        sentryHasAmmo = buf.readBoolean();
        health = buf.readFloat();
        currentAim.readBytes(buf);
        aim.readBytes(buf);
        status = ByteBufUtils.readUTF8String(buf);

        //Load gun data
        NBTTagCompound gunTag = ByteBufUtils.readTag(buf);
        gunInstance.load(gunTag);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf buf)
    {
        buf.writeBoolean(running);
        buf.writeBoolean(turnedOn);
        buf.writeBoolean(sentryHasAmmo);
        buf.writeFloat(health);
        currentAim.writeBytes(buf);
        aim.writeBytes(buf);
        ByteBufUtils.writeUTF8String(buf, status);

        //Save gun data
        NBTTagCompound gunTag = new NBTTagCompound();
        gunInstance.save(gunTag);
        ByteBufUtils.writeTag(buf, gunTag);
        return buf;
    }

    @Override
    public IEnergyBuffer getEnergyBuffer(ForgeDirection side)
    {
        return host instanceof IEnergyBufferProvider ? ((IEnergyBufferProvider) host).getEnergyBuffer(side) : null;
    }


    public IFoFProvider getFoFStation()
    {
        if ((fofStation == null || fofStation instanceof TileEntity && ((TileEntity) fofStation).isInvalid()) && fofStationPos != null)
        {
            TileEntity tile = fofStationPos.getTileEntity(world());
            if (tile instanceof IFoFProvider)
            {
                fofStation = (IFoFProvider) tile;
            }
            else
            {
                fofStationPos = null;
            }
        }
        return fofStation;
    }

    @Override
    public String link(Location loc, short code)
    {
        //Validate location data
        if (loc.world != world())
        {
            return "link.error.world.match";
        }

        Pos pos = loc.toPos();
        if (!pos.isAboveBedrock())
        {
            return "link.error.pos.invalid";
        }
        if (center.distance(pos) > 200) //TODO place in static var with config
        {
            return "link.error.pos.distance.max";
        }

        //Compare tile pass code
        TileEntity tile = pos.getTileEntity(loc.world());
        if (tile instanceof IPassCode && ((IPassCode) tile).getCode() != code)
        {
            return "link.error.code.match";
        }
        else if (tile instanceof IFoFProvider)
        {
            IFoFProvider station = getFoFStation();
            if (station == tile)
            {
                return "link.error.tile.already.added";
            }
            else
            {
                fofStation = (IFoFProvider) tile;
                fofStationPos = new Pos(tile);
            }
            return "";
        }
        else
        {
            return "link.error.tile.invalid";
        }
    }
}
