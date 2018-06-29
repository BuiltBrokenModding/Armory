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
import com.builtbroken.mc.api.abstraction.EffectInstance;
import com.builtbroken.mc.api.abstraction.world.IWorld;
import com.builtbroken.mc.api.energy.IEnergyBuffer;
import com.builtbroken.mc.api.energy.IEnergyBufferProvider;
import com.builtbroken.mc.api.tile.IFoFProvider;
import com.builtbroken.mc.api.tile.ILinkFeedback;
import com.builtbroken.mc.api.tile.ILinkable;
import com.builtbroken.mc.api.tile.IPassCode;
import com.builtbroken.mc.api.tile.provider.IInventoryProvider;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.References;
import com.builtbroken.mc.framework.access.AccessProfile;
import com.builtbroken.mc.framework.access.api.IProfileContainer;
import com.builtbroken.mc.framework.access.global.GlobalAccessProfile;
import com.builtbroken.mc.framework.access.global.GlobalAccessSystem;
import com.builtbroken.mc.framework.access.perm.Permissions;
import com.builtbroken.mc.imp.transform.rotation.EulerAngle;
import com.builtbroken.mc.imp.transform.rotation.IRotation;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.helper.MathUtility;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
public class Sentry implements IWorldPosition, IRotation, IWeaponUser, ISave, IByteBufReader, IByteBufWriter, IEnergyBufferProvider, ILinkable, IProfileContainer, ILinkFeedback, IPassCode
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

    //Target settings
    protected int targetSearchTimer = 0;
    protected int targetingDelay = 0;
    protected int targetingLoseTimer = 0;

    //EMP settings
    protected int empStunTimer = 0;

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


    /** Security code used to prevent remote linking */
    protected short link_code;

    /** User customized display name for the launcher */
    protected String customName;

    //Stats vars
    public float health = 0;

    /** Location of FoF station */
    public Pos fofStationPos;
    /** Cached fof station tile */
    public IFoFProvider fofStation;

    public String profileID = GlobalAccessSystem.FRIENDS_LIST_ID;
    public boolean profileGood = false;

    public EntityTargetSelector targetSelector;

    public final HashMap<String, TargetMode> targetModes = new HashMap();

    /** Client side field to bypass special handling from the main field */
    public String actualProfileID;

    public Sentry(SentryData sentryData)
    {
        this.sentryData = sentryData;
        health = sentryData.getMaxHealth();
        gunInstance = new GunInstance(new ItemStack(Armory.blockSentry), this, getSentryData().getGunData());
        targetSelector = new EntityTargetSelector(this);
    }

    public boolean update(int ticks, float deltaTime)
    {
        if (!oldWorld().isRemote)
        {
            profileGood = getAccessProfile() != null;

            if (ticks == 1) //TODO setup init check, as tick == 1 may not always work (sponge -.-)
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
                //Runs debug
                doDebugLasers();

                if (empStunTimer > 0)
                {
                    empStunTimer--;
                    status = "stunned"; //TODO play EMP audio
                }
                else if (!turnedOn)
                {
                    status = "powered down"; //TODO translate
                }
                else
                {
                    //Calculate bullet offset
                    calculateBulletSpawnOffset();

                    status = "unknown"; //TODO translate

                    //Invalid entity
                    if (host == null || getSentryData() == null)
                    {
                        status = "invalid"; //TODO translate
                    }
                    else
                    {
                        //Reset power state
                        running = false;
                        //Check if we have power and consume power
                        if (getSentryData().getEnergyCost() > 0)
                        {
                            IEnergyBuffer buffer = getEnergyBuffer(ForgeDirection.UNKNOWN);
                            if (buffer != null && buffer.removeEnergyFromStorage(getSentryData().getEnergyCost(), false) >= getSentryData().getEnergyCost())
                            {
                                running = true;
                                buffer.removeEnergyFromStorage(getSentryData().getEnergyCost(), true);
                            }
                        }
                        else
                        {
                            running = true;
                        }

                        //Reset ammo state
                        sentryHasAmmo = false;
                        //Trigger reload mod if out of ammo
                        if (!gunInstance.hasMagWithAmmo() && gunInstance.getChamberedRound() == null)
                        {
                            reloading = true;
                        }
                        else
                        {
                            sentryHasAmmo = true;
                        }

                        if (!running)
                        {
                            status = "no power";
                        }
                        //Can only function if we have a gun
                        else if (gunInstance != null)
                        {
                            if (reloading)
                            {
                                status = "reloading"; //TODO translate
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
                                    status = "searching"; //TODO translate
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
                                    status = "aiming"; //TODO translate
                                    //Delay before attack
                                    if (targetingDelay >= getSentryData().getTargetAttackDelay())
                                    {
                                        //Update aim point
                                        aimPoint = getAimPoint(target);

                                        aim.set(center.toEulerAngle(aimPoint));
                                        aim.setYaw(EulerAngle.clampAngle(aim.yaw(), 0, 360));

                                        if (isAimed())
                                        {
                                            status = "attacking"; //TODO translate
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
                                    status = "target lost"; //TODO translate
                                    target = null;
                                    targetingLoseTimer = 0;
                                }
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

        List<Entity> entityList = oldWorld().getEntitiesWithinAABBExcludingEntity(host instanceof Entity ? (Entity) host : null, searchArea, getEntitySelector());
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
        if (entity != null && entity.isEntityAlive() && getEntitySelector().isEntityApplicable(entity) && (getFoFStation() == null || !getFoFStation().isFriendly(entity)))
        {
            //Get aim position of entity
            final Pos aimPoint = getAimPoint(entity); //TODO retry with lower and higher aim value

            //Check to ensure we are in range
            double distance = center.distance(aimPoint);
            if (distance <= getSentryData().getRange())
            {
                //Trace to make sure no blocks are between shooter and target
                EulerAngle aim = center.toEulerAngle(aimPoint).clampTo360();
                MovingObjectPosition hit = center.add(aim.toPos().multiply(1.3)).rayTraceBlocks(oldWorld(), aimPoint);

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
            gunInstance.fireWeapon(oldWorld(), 1, aimPoint, bulletSpawnOffset); //TODO get firing ticks
        }
    }

    public SentryData getSentryData()
    {
        return sentryData;
    }

    @Override
    public World oldWorld()
    {
        return host != null ? host.oldWorld() : null;
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
        if (nbt.hasKey("profileID"))
        {
            profileID = nbt.getString("profileID");
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
        if (profileID != null && !profileID.isEmpty())
        {
            nbt.setString("profileID", profileID);
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
        profileID = ByteBufUtils.readUTF8String(buf);
        actualProfileID = ByteBufUtils.readUTF8String(buf);
        profileGood = buf.readBoolean();

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
        ByteBufUtils.writeUTF8String(buf, status != null ? status : "");
        ByteBufUtils.writeUTF8String(buf, profileID != null ? profileID : "");
        ByteBufUtils.writeUTF8String(buf, getAccessProfile() != null ? getAccessProfile().getID() : "");
        buf.writeBoolean(profileGood);

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
            TileEntity tile = fofStationPos.getTileEntity(oldWorld());
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
        if (loc.world != oldWorld())
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
        TileEntity tile = pos.getTileEntity(loc.oldWorld());
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

    @Override
    public GlobalAccessProfile getAccessProfile()
    {
        if (host != null)
        {
            if (profileID.equalsIgnoreCase(GlobalAccessSystem.FRIENDS_LIST_ID))
            {
                return GlobalAccessSystem.getFriendList(host.getOwnerName(), host.getOwnerID());
            }
            return GlobalAccessSystem.getProfile(profileID);
        }
        return null;
    }

    @Override
    public void setAccessProfile(AccessProfile profile)
    {
        //N/A sentry does not support local profiles
    }

    @Override
    public boolean canAccess(String username)
    {
        if (username.equalsIgnoreCase(host.getOwnerName()))
        {
            return true;
        }
        return hasNode(username, Permissions.machineOpen.id);
    }

    @Override
    public boolean hasNode(EntityPlayer player, String node)
    {
        return getAccessProfile() == null || getAccessProfile().hasNode(player, node);
    }

    @Override
    public boolean hasNode(String username, String node)
    {
        return getAccessProfile() == null || getAccessProfile().hasNode(username, node);
    }

    @Override
    public void onProfileChange()
    {

    }

    protected void doDebugLasers()
    {
        //Debug
        if (enableAimDebugRays && Engine.runningAsDev)
        {
            Pos hand = center.add(bulletSpawnOffset);

            IWorld world = Engine.minecraft.getWorld(oldWorld().provider.dimensionId); //TODO remove once world is switch over to wrapper

            //Test base aim
            EffectInstance effect = world.newEffect(References.LASER_EFFECT, center.add(0, 0.1, 0));
            effect.setEndPoint(hand.add(0, 0.1, 0));
            effect.addData("red", (Color.MAGENTA.getRed() / 255f)); //TODO maybe convert to int to save bandwidth?
            effect.addData("green", (Color.MAGENTA.getRed() / 255f));
            effect.addData("blue", (Color.MAGENTA.getRed() / 255f));
            effect.send();

            //Test entity aim
            hand = center.add(getEntityAim());
            effect.setPosition(center.add(0, 0.2, 0));
            effect.setEndPoint(hand.add(0, 0.2, 0));
            effect.addData("red", (Color.blue.getRed() / 255f));
            effect.addData("green", (Color.blue.getRed() / 255f));
            effect.addData("blue", (Color.blue.getRed() / 255f));
            effect.send();

            //Test aim
            hand = center.add(aim.toPos());
            effect.setPosition(center.add(0, 0.3, 0));
            effect.setEndPoint(hand.add(0, 0.3, 0));
            effect.addData("red", (Color.CYAN.getRed() / 255f));
            effect.addData("green", (Color.CYAN.getRed() / 255f));
            effect.addData("blue", (Color.CYAN.getRed() / 255f));
            effect.send();


            if (aimPoint != null)
            {
                effect.setPosition(center);
                effect.setEndPoint(aimPoint);
                effect.addData("red", (Color.yellow.getRed() / 255f));
                effect.addData("green", (Color.yellow.getRed() / 255f));
                effect.addData("blue", (Color.yellow.getRed() / 255f));
                effect.send();
            }
        }
    }

    /**
     * Called to apply stun time and damage to the sentry from the EMP
     *
     * @param power - power of the EMP
     */
    public void onEMP(double power)
    {
        int stun = (int) ((power / (double) getSentryData().getEmpStunTimerPerEnergyUnit()) * getSentryData().getEmpStunTimerPerEnergyUnit());
        empStunTimer = Math.min(getSentryData().getEmpMaxStun(), empStunTimer + stun);
    }

    @Override
    public short getCode()
    {
        if (link_code == 0)
        {
            link_code = MathUtility.randomShort();
        }
        return link_code;
    }

    @Override
    public void onLinked(Location location)
    {

    }
}
