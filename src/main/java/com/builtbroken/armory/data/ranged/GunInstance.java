package com.builtbroken.armory.data.ranged;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.ClipInstance;
import com.builtbroken.armory.data.ammo.ClipInstanceItem;
import com.builtbroken.mc.api.ISave;
import com.builtbroken.mc.api.data.weapon.*;
import com.builtbroken.mc.api.items.weapons.IItemAmmo;
import com.builtbroken.mc.api.items.weapons.IItemClip;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.weapon.IClip;
import com.builtbroken.mc.api.modules.weapon.IGun;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.PacketSpawnParticle;
import com.builtbroken.mc.core.network.packet.PacketSpawnStream;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryIterator;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.module.AbstractModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.awt.*;

/**
 * The actual gun instance used for data accessing and handling
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class GunInstance extends AbstractModule implements ISave, IGun
{
    /** How fast a projectile can travel before a ray trace is used instead */
    public static final float PROJECTILE_SPEED_LIMIT = 0.05f; // 20th of a block thickness a tick
    /** Who is holding the weapon */
    public final Entity entity;
    /** Properties of the weapon */
    protected final IGunData gunData;

    /** Clip that is feed into the weapon */
    protected IClip _clip;

    protected IAmmoData chamberedRound;

    /** Last time the weapon was fired, milliseconds */
    public Long lastTimeFired = 0L;

    public GunInstance(ItemStack gunStack, Entity entity, IGunData gun)
    {
        super(gunStack, "armory:gun");
        this.entity = entity;
        this.gunData = gun;
        if (isManuallyFeedClip())
        {
            _clip = new ClipInstance(gunData.getBuiltInClipData());
        }
        if (gunStack.getTagCompound() != null)
        {
            load(gunStack.getTagCompound());
        }
    }

    /**
     * Called to fire the weapon
     *
     * @param stack      - weapon stack
     * @param world      -world fired in
     * @param ticksFired - number of ticks fired for
     */
    public void fireWeapon(ItemStack stack, World world, int ticksFired)
    {
        Long deltaTime = System.currentTimeMillis() - lastTimeFired;
        if (entity instanceof EntityLivingBase && (lastTimeFired == 0L || deltaTime > gunData.getFiringDelay()))
        {
            lastTimeFired = System.currentTimeMillis();
            _doFire(world, ((EntityLivingBase) entity).rotationYawHead, ((EntityLivingBase) entity).rotationPitch);
        }
    }

    protected void _doFire(World world, float yaw, float pitch)
    {
        //If no ammo reload the weapon
        if (!hasAmmo())
        {
            reloadWeapon(getInventory());
            //TODO return if animation needs to play
        }
        //TODO return and allow reload animations
        //TODO add safety checks
        if (getChamberedRound() != null || hasAmmo())
        {
            //Load next round to fire if empty
            chamberNextRound();

            //Only fire if we have a round in the chamber
            if (getChamberedRound() != null)
            {
                //TODO apply upgrades

                //Notes for player statistics to add
                //TODO track damage by Weapon and Entity(If player) - do not store in weapon data
                //TODO track shots fired - do not store in weapon data
                //TODO track shots hit - do not store in weapon data
                //TODO track kills - do not store in weapon data
                //TODO allow data to be cleared & disabled
                //TODO allow sorting of the data and graphing
                //TODO allow other users to see each other's weapon data (with a permission system)

                final Pos bulletStartPoint = getBulletSpawnPoint(yaw, pitch);
                final Pos aim = getAim(yaw, pitch);

                //Send effect packet to client to render shot was taken
                if (Engine.instance != null)
                {
                    //TODO spawn smoke based on weapon data
                    //TODO send effect packet so all effects are client generator (reduces packets)
                    int flames = world.rand.nextInt(5);
                    int smoke = world.rand.nextInt(10);

                    for (int i = 0; i < flames; i++)
                    {
                        Pos vel = aim.multiply(0.2f).addRandom(world.rand, 0.05f);
                        PacketSpawnParticle packet = new PacketSpawnParticle("flame", world.provider.dimensionId,
                                bulletStartPoint.x() + aim.x(), bulletStartPoint.y() + aim.y(), bulletStartPoint.z() + aim.z(),
                                vel.xf(), vel.yf(), vel.zf());
                        Engine.instance.packetHandler.sendToAllAround(packet, new Location(world, bulletStartPoint), 100);
                    }

                    for (int i = 0; i < smoke; i++)
                    {
                        Pos vel = aim.multiply(0.2f).addRandom(world.rand, 0.05f);
                        PacketSpawnParticle packet = new PacketSpawnParticle("smoke", world.provider.dimensionId,
                                bulletStartPoint.x(), bulletStartPoint.y(), bulletStartPoint.z(),
                                vel.xf(), vel.yf(), vel.zf());
                        Engine.instance.packetHandler.sendToAllAround(packet, new Location(world, bulletStartPoint), 100);
                    }
                }

                //Fire round out of gun
                if (getChamberedRound().getProjectileVelocity() < 0 || getChamberedRound().getProjectileVelocity() / 20f > PROJECTILE_SPEED_LIMIT)
                {
                    _doRayTrace(world, yaw, pitch, getChamberedRound(), bulletStartPoint, aim);
                }
                else
                {
                    _createAndFireEntity(world, yaw, pitch, getChamberedRound(), bulletStartPoint, aim);
                }

                //Clear current round as it has been fired
                chamberedRound = null;

                //TODO eject brass/waste from the weapon
                //TODO generate heat
                //TODO apply recoil
                //TODO damage weapon
                //Load next round to fire
                chamberNextRound();
            }
            updateEntityStack();
        }
    }

    public boolean chamberNextRound()
    {
        if (getChamberedRound() == null && hasAmmo())
        {
            chamberedRound = getLoadedClip().getAmmo().peek();
            getLoadedClip().consumeAmmo(1);
        }
        return getChamberedRound() != null;
    }

    protected void _doRayTrace(World world, float yaw, float pitch, IAmmoData nextRound, Pos start, Pos aim)
    {
        final Pos end = start.add(aim.multiply(500));

        //Debug ray trace
        if (Engine.instance != null && Engine.runningAsDev)
        {
            PacketSpawnStream packet = new PacketSpawnStream(world.provider.dimensionId, start, end, 2);
            packet.red = (Color.blue.getRed() / 255f);
            packet.green = (Color.blue.getGreen() / 255f);
            packet.blue = (Color.blue.getBlue() / 255f);
            Engine.instance.packetHandler.sendToAllAround(packet, new Location(entity), 200);
        }

        MovingObjectPosition hit = start.rayTrace(world, end);
        if (hit != null && hit.typeOfHit != MovingObjectPosition.MovingObjectType.MISS)
        {
            if (hit.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)
            {
                nextRound.onImpactEntity(entity, hit.entityHit, nextRound.getProjectileVelocity()); //TODO scale velocity by distance
            }
            else
            {
                nextRound.onImpactGround(entity, world, hit.blockX, hit.blockY, hit.blockZ, hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord, nextRound.getProjectileVelocity());
            }
        }
    }

    protected void _createAndFireEntity(World world, float yaw, float pitch, IAmmoData nextRound, Pos start, Pos aim)
    {
        //TODO spawn projectile
    }

    /**
     * Calculates the point that the bullet or ray traces starts at
     *
     * @param yaw   - player rotation yaw
     * @param pitch - player rotation pitch
     * @return position in the world
     */
    protected Pos getBulletSpawnPoint(float yaw, float pitch)
    {
        //Find our hand position so to position starting point near barrel of the gun
        final float rotationHand = MathHelper.wrapAngleTo180_float(yaw + 90);
        final double r = Math.toRadians(rotationHand);
        final Vec3 hand = Vec3.createVectorHelper(
                (Math.cos(r) - Math.sin(r)) * 0.5,
                0,
                (Math.sin(r) + Math.cos(r)) * 0.5
        );

        return new Pos(entity.posX, entity.posY + 1.1, entity.posZ).add(hand);
    }

    protected void consumeAmmo()
    {
        getLoadedClip().consumeAmmo(1);
    }

    protected Pos getAim(float yaw, float pitch)
    {
        float f1 = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f3 = -MathHelper.cos(-pitch * 0.017453292F);
        float f4 = MathHelper.sin(-pitch * 0.017453292F);
        return new Pos((double) (f2 * f3), (double) f4, (double) (f1 * f3));
    }

    public boolean hasAmmo()
    {
        return getLoadedClip() != null && getLoadedClip().getAmmoCount() > 0;
    }

    public void reloadWeapon(IInventory inventory)
    {
        if (inventory != null)
        {
            if (isManuallyFeedClip())
            {
                loadRound(inventory);
            }
            else
            {
                unloadWeapon(inventory);
                loadBestClip(inventory);
            }
        }
    }

    private void loadBestClip(IInventory inventory)
    {
        InventoryIterator it = new InventoryIterator(inventory, true);
        IClip bestClip = null;
        int slot = -1;
        for (ItemStack stack : it)
        {
            if (stack.getItem() instanceof IItemClip)
            {
                IItemClip itemClip = ((IItemClip) stack.getItem());
                if (itemClip.isAmmo(stack) && itemClip.isClip(stack))
                {
                    IAmmoType type = itemClip.getClipData(stack).getAmmoType();
                    if (type == gunData.getAmmoType())
                    {
                        if (bestClip == null || bestClip.getAmmoCount() < itemClip.getAmmoCount(stack))
                        {
                            IClip clip = itemClip.toClip(stack);
                            if (clip != null)
                            {
                                bestClip = clip;
                                slot = it.slot();
                            }
                        }
                    }
                }
            }
        }
        if (bestClip != null)
        {
            if (_clip == null)
            {
                _clip = bestClip;
                inventory.decrStackSize(slot, 1);
            }
            updateEntityStack();
        }
    }

    private void loadRound(IInventory inventory)
    {
        InventoryIterator it = new InventoryIterator(inventory, true);
        for (ItemStack stack : it)
        {
            if (stack.getItem() instanceof IItemAmmo)
            {
                IItemAmmo ammo = ((IItemAmmo) stack.getItem());
                if (ammo.isAmmo(stack) && !ammo.isClip(stack))
                {
                    IAmmoData data = ammo.getAmmoData(stack);
                    IAmmoType type = data.getAmmoType();
                    if (type == gunData.getAmmoType())
                    {
                        if (getLoadedClip().getAmmoCount() < getLoadedClip().getClipData().getMaxAmmo())
                        {
                            inventory.decrStackSize(it.slot(), getLoadedClip().loadAmmo(data, ammo.getAmmoCount(stack)));
                        }
                    }
                }
            }
        }
        updateEntityStack();
    }

    private void updateEntityStack()
    {
        if (entity instanceof EntityPlayer)
        {
            ItemStack stack = ((EntityPlayer) entity).getHeldItem();
            ItemStack updated = toStack();
            if (stack.isItemEqual(updated))
            {
                ((EntityPlayer) entity).inventory.setInventorySlotContents(((EntityPlayer) entity).inventory.currentItem, updated);
                ((EntityPlayer) entity).inventoryContainer.detectAndSendChanges();
            }
        }
    }

    public void unloadWeapon(IInventory inventory)
    {
        if (_clip != null)
        {
            if (isManuallyFeedClip())
            {
                int i = 0;
                while (!getLoadedClip().getAmmo().isEmpty())
                {
                    ItemStack ammoStack = getLoadedClip().getAmmo().peek().toStack();
                    for (; i < inventory.getSizeInventory(); i++)
                    {
                        ItemStack slotStack = inventory.getStackInSlot(i);
                        int roomLeft = InventoryUtility.roomLeftInSlot(inventory, i);
                        if (slotStack == null)
                        {
                            inventory.setInventorySlotContents(i, ammoStack);
                            getLoadedClip().consumeAmmo(1);
                            ammoStack = null;
                            break;
                        }
                        else if (roomLeft > 0 && InventoryUtility.stacksMatch(ammoStack, slotStack))
                        {
                            int insert = Math.min(roomLeft, ammoStack.stackSize);
                            slotStack.stackSize += insert;
                            ammoStack.stackSize -= insert;
                            inventory.setInventorySlotContents(i, slotStack);
                            getLoadedClip().consumeAmmo(1);
                            if (ammoStack.stackSize <= 0)
                            {
                                ammoStack = null;
                                break;
                            }
                        }
                    }
                    //No space to insert so drop
                    if (ammoStack != null && ammoStack.stackSize >= 0)
                    {
                        InventoryUtility.dropItemStack(new Location(entity), ammoStack);
                    }
                }
            }
            else if (_clip instanceof IModule)
            {
                //TODO send to inventory first, not hotbar
                ItemStack stack = ((IModule) _clip).toStack();
                for (int i = 0; i < inventory.getSizeInventory(); i++)
                {
                    ItemStack slotStack = inventory.getStackInSlot(i);
                    int roomLeft = InventoryUtility.roomLeftInSlot(inventory, i);
                    if (slotStack == null)
                    {
                        inventory.setInventorySlotContents(i, stack);
                        stack = null;
                        break;
                    }
                    else if (roomLeft > 0 && InventoryUtility.stacksMatch(stack, slotStack))
                    {
                        int insert = Math.min(roomLeft, stack.stackSize);
                        slotStack.stackSize += insert;
                        stack.stackSize -= insert;
                        inventory.setInventorySlotContents(i, slotStack);
                        break;
                    }
                }
                //No space to insert so drop
                if (stack != null && stack.stackSize >= 0)
                {
                    InventoryUtility.dropItemStack(new Location(entity), stack);
                }
                _clip = null;
            }
            updateEntityStack();
        }
    }

    public boolean isManuallyFeedClip()
    {
        return getGunData().getReloadType() == ReloadType.BREACH_LOADED
                || getGunData().getReloadType() == ReloadType.FRONT_LOADED
                || getGunData().getReloadType() == ReloadType.HAND_FEED;
    }

    public IInventory getInventory()
    {
        if (entity instanceof EntityPlayer)
        {
            return ((EntityPlayer) entity).inventory;
        }
        return null;
    }

    public boolean hasSights()
    {
        //TODO implement
        return false;
    }

    public void sightWeapon()
    {
        //TODO implement
    }

    public void load(NBTTagCompound tag)
    {
        if (tag.hasKey("chamberedRound"))
        {
            chamberedRound = (IAmmoData) ArmoryDataHandler.INSTANCE.get("ammo").get(tag.getString("chamberedRound"));
            if (chamberedRound == null)
            {
                error("Failed to load chambered round '" + tag.getShort("chamberedRound") + "' form NBT for " + this);
            }
        }
        if (tag.hasKey("clip"))
        {
            NBTTagCompound clipTag = tag.getCompoundTag("clip");
            if (clipTag.hasKey("data"))
            {
                IClipData data = (IClipData) ArmoryDataHandler.INSTANCE.get("clip").get(clipTag.getString("data"));
                //TODO ensure clip data is valid for weapon in case unique ID changes or data changes
                if (data != null)
                {
                    if (clipTag.hasKey("stack"))
                    {
                        //External clip loading
                        ItemStack stack = ItemStack.loadItemStackFromNBT(clipTag.getCompoundTag("stack"));
                        _clip = new ClipInstanceItem(stack, data);
                    }

                }
                else
                {
                    error("Failed to load clip data from NBT using unique id '" + clipTag.getString("data") + "' while loading " + this);
                }
            }
            else
            {
                //Internal clip loading
                ((ClipInstance) _clip).load(clipTag);
            }
        }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        if (getChamberedRound() != null)
        {
            nbt.setString("chamberedRound", getChamberedRound().getUniqueID());
        }
        if (_clip != null)
        {
            NBTTagCompound clipTag = new NBTTagCompound();
            if (_clip instanceof ClipInstance)
            {
                ((ClipInstance) _clip).save(clipTag);
            }
            else
            {
                clipTag.setString("data", _clip.getClipData().getUniqueID());
                clipTag.setTag("stack", ((ClipInstanceItem) _clip).save().writeToNBT(new NBTTagCompound()));
            }
            nbt.setTag("clip", clipTag);
        }
        return nbt;
    }

    private void error(String msg)
    {
        if (Armory.INSTANCE != null)
        {
            Armory.INSTANCE.logger().error(msg);
        }
        else
        {
            throw new RuntimeException(msg);
        }
    }

    @Override
    public IGunData getGunData()
    {
        return gunData;
    }

    @Override
    public IAmmoData getChamberedRound()
    {
        return chamberedRound;
    }

    @Override
    public IClip getLoadedClip()
    {
        return _clip;
    }

    @Override
    public String getSaveID()
    {
        return "armory:gun";
    }

    @Override
    public String toString()
    {
        return "GunInstance[" + entity + ", " + gunData + ", " + chamberedRound + ", " + _clip + "]";
    }
}
