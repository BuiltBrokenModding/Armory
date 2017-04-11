package com.builtbroken.armory.data.ranged;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.content.entity.projectile.EntityAmmoProjectile;
import com.builtbroken.armory.content.sentry.EntitySentry;
import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.clip.ClipInstance;
import com.builtbroken.armory.data.clip.ClipInstanceItem;
import com.builtbroken.mc.api.ISave;
import com.builtbroken.mc.api.data.weapon.*;
import com.builtbroken.mc.api.items.weapons.IItemAmmo;
import com.builtbroken.mc.api.items.weapons.IItemClip;
import com.builtbroken.mc.api.modules.IModule;
import com.builtbroken.mc.api.modules.weapon.IClip;
import com.builtbroken.mc.api.modules.weapon.IGun;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.PacketSpawnStream;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryIterator;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.builtbroken.mc.prefab.module.AbstractModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The actual gun instance used for data accessing and handling
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class GunInstance extends AbstractModule implements ISave, IGun
{
    public static final String NBT_ROUND = "chamberedRound";
    public static final String NBT_CLIP = "clip";
    /** Enabled ray trace debug on all weapons, normally only client side */
    public static boolean debugRayTraces = false;
    /** Enabled ray trace debug on this weapon only */
    public boolean doDebugRayTracesOnTthisGun = false;


    /** How fast a projectile can travel before a ray trace is used instead */
    public static final float PROJECTILE_SPEED_LIMIT = 100;
    /** Who is holding the weapon */
    public final Entity entity;
    /** Properties of the weapon */
    protected final IGunData gunData;

    /** Clip that is feed into the weapon */
    public IClip _clip;

    public IAmmoData chamberedRound;

    /** Last time the weapon was fired, milliseconds */
    public Long lastTimeFired = 0L;
    /** Toggle to do reload */
    public boolean doReload = false;
    /** Is the weapon sighted */
    public boolean isSighted = false;
    /** True = infinite ammo */
    public boolean ignoreAmmo = false;
    /** Delay before reloading */
    public int reloadDelay = -1;

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
     * @param world      -world fired in
     * @param ticksFired - number of ticks fired for
     */
    public void fireWeapon(World world, int ticksFired)
    {
        fireWeapon(world, ticksFired, null, null);
    }

    /**
     * Called to fire the weapon
     *
     * @param world      -world fired in
     * @param ticksFired - number of ticks fired for
     */
    public void fireWeapon(World world, int ticksFired, Pos aimPoint, Pos aim)
    {
        if (isSighted || !gunData.isSightedRequiredToFire())
        {
            Long deltaTime = System.currentTimeMillis() - lastTimeFired;
            if (entity != null && (lastTimeFired == 0L || deltaTime > gunData.getFiringDelay()))
            {
                lastTimeFired = System.currentTimeMillis();
                float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch);
                float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw);

                _doFire(world, yaw, pitch, aimPoint, aim);
            }
        }
        else
        {
            //TODO play some other notation that the weapon should be sighted
            playAudio("error.aim.needed");
        }
    }

    protected void _doFire(World world, float yaw, float pitch, Pos aimPointOverride, Pos aimOverride)
    {        //TODO return and allow reload animations
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

                final Pos entityPos = getEntityPosition();
                final Pos bulletStartPoint = entityPos.add(getBulletSpawnOffset(yaw, pitch));
                final Pos aim = aimOverride != null ? aimOverride : getAim(yaw, pitch);
                final Pos target = aimPointOverride != null ? aimPointOverride : entityPos.add(aim.multiply(500));

                playAudio("round.fired");
                playEffect("round.fired", bulletStartPoint, aim); //TODO check with ammo if it has an effect to play then use this as backup

                //Fire round out of gun
                if (getChamberedRound().getProjectileVelocity() <= 0 || getChamberedRound().getProjectileVelocity() > PROJECTILE_SPEED_LIMIT)
                {
                    _doRayTrace(world, yaw, pitch, getChamberedRound(), entityPos.add(aim), target, aim);
                }
                else
                {
                    _createAndFireEntity(world, yaw, pitch, getChamberedRound(), bulletStartPoint, target, aim);
                }

                List<ItemStack> droppedItems = new ArrayList();
                chamberedRound.getEjectedItems(droppedItems);
                if (droppedItems != null && droppedItems.size() > 0)
                {
                    playAudio("round.eject");
                    //TODO get eject point and direction on gun to make a more realistic drop
                    for (ItemStack stack : droppedItems)
                    {
                        InventoryUtility.dropItemStack(world, bulletStartPoint.sub(aim.multiply(0.2f)), stack, 20, 0.1f);
                    }
                }

                //Clear current round as it has been fired
                consumeShot();

                //TODO generate heat
                //TODO apply recoil
                //TODO damage weapon
                //Load next round to fire
                chamberNextRound();
            }
            updateEntityStack("do fire");
        }

        if (!hasAmmo())
        {
            doReload = true;
            playAudio("empty");
        }
    }

    /**
     * Called to chamber next round
     *
     * @return true if chamber has round
     */
    public boolean chamberNextRound()
    {
        if (getChamberedRound() == null && hasAmmo())
        {
            chamberedRound = getLoadedClip().getAmmo().peek();
            getLoadedClip().consumeAmmo(1);
            updateEntityStack("chamber round");
            playAudio("round.chamber");
        }
        return getChamberedRound() != null;
    }

    /**
     * Called to run debug ray trace code
     * <p>
     * Has to be triggered in order to be used.
     */
    public void debugRayTrace()
    {
        float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch);
        float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw);

        final Pos entityPos = getEntityPosition();
        final Pos aim = getAim(yaw, pitch);
        final Pos target = entityPos.add(aim.multiply(500));

        final Pos bulletOffset = getBulletSpawnOffset(yaw, pitch);
        debugRayTrace(entityPos, aim, target, bulletOffset, yaw, pitch);
    }

    /**
     * Called to run debug ray trace code
     * <p>
     * Has to be triggered in order to be used.
     */
    public void debugRayTrace(Pos entityPos, Pos aim, Pos target, Pos bulletOffset, float yaw, float pitch)
    {
        if ((debugRayTraces || doDebugRayTracesOnTthisGun) && Engine.instance != null && Engine.runningAsDev)
        {
            final Pos bulletStartPoint = entityPos.add(bulletOffset);
            final Pos start = entityPos.add(aim);

            Pos end = target;
            MovingObjectPosition hit = start.rayTrace(entity.worldObj, end, false, true, false);
            if (hit != null)
            {
                end = new Pos(hit.hitVec);
            }

            //Debug ray trace
            PacketSpawnStream packet = new PacketSpawnStream(entity.worldObj.provider.dimensionId, start, end, 2);
            packet.red = (Color.blue.getRed() / 255f);
            packet.green = (Color.blue.getGreen() / 255f);
            packet.blue = (Color.blue.getBlue() / 255f);
            Engine.instance.packetHandler.sendToAllAround(packet, new Location(entity), 200);

            //Debug ray trace
            packet = new PacketSpawnStream(entity.worldObj.provider.dimensionId, bulletStartPoint, end, 2);
            packet.red = (Color.RED.getRed() / 255f);
            packet.green = (Color.RED.getGreen() / 255f);
            packet.blue = (Color.RED.getBlue() / 255f);
            Engine.instance.packetHandler.sendToAllAround(packet, new Location(entity), 200);
        }
    }

    protected void _doRayTrace(World world, float yaw, float pitch, IAmmoData nextRound, Pos start, Pos end, Pos aim)
    {
        MovingObjectPosition hit = start.rayTrace(world, end, false, true, false);
        if (hit != null && hit.typeOfHit != MovingObjectPosition.MovingObjectType.MISS)
        {
            if (hit.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)
            {
                nextRound.onImpactEntity(entity, hit.entityHit, hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord, nextRound.getProjectileVelocity()); //TODO scale velocity by distance
                if (Engine.runningAsDev)
                {
                    System.out.println(hit.entityHit);
                }
            }
            else
            {
                nextRound.onImpactGround(entity, world, hit.blockX, hit.blockY, hit.blockZ, hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord, nextRound.getProjectileVelocity());
            }
        }
    }

    protected void _createAndFireEntity(World world, float yaw, float pitch, IAmmoData nextRound, Pos start, Pos end, Pos aim)
    {
        Pos spawnPoint = getEntityPosition().add(aim).add(getBulletSpawnOffset(yaw, pitch));
        //TODO spawn projectile
        EntityAmmoProjectile projectile = new EntityAmmoProjectile(world, nextRound, this, entity);

        projectile.setPosition(spawnPoint.x(), spawnPoint.y(), spawnPoint.z());

        //Calculate deltas
        double deltaX = end.x() - start.x();
        double deltaY = end.y() - start.y();
        double deltaZ = end.z() - start.z();

        projectile.setThrowableHeading(deltaX, deltaY, deltaZ, 10, 0);

        world.spawnEntityInWorld(projectile);

    }

    /**
     * Calculates the point that the bullet or ray traces starts at
     *
     * @param yaw   - player rotation yaw
     * @param pitch - player rotation pitch
     * @return position in the world
     */
    protected Pos getBulletSpawnOffset(float yaw, float pitch)
    {
        if (entity instanceof EntityLivingBase)
        {
            //Find our hand position so to position starting point near barrel of the gun
            final float rotationHand = MathHelper.wrapAngleTo180_float(yaw + 90);
            final double r = Math.toRadians(rotationHand);
            final Pos hand = new Pos(
                    (Math.cos(r) - Math.sin(r)) * 0.5,
                    -0.5,
                    (Math.sin(r) + Math.cos(r)) * 0.5
            );
            return hand;
        }
        final float rotationHand = MathHelper.wrapAngleTo180_float(yaw);
        final double r = Math.toRadians(rotationHand);
        final Pos hand = new Pos(
                (Math.cos(r) - Math.sin(r)) * 0.5,
                0,
                (Math.sin(r) + Math.cos(r)) * 0.5
        );
        return hand;
    }

    protected Pos getEntityPosition()
    {
        double x = entity.posX;
        double y = entity.posY;
        double z = entity.posZ;

        if (entity instanceof EntityPlayer)
        {
            // isRemote check to revert changes to ray trace position due to adding the eye height clientside and entity yOffset differences
            y += (double) (entity.worldObj.isRemote ? entity.getEyeHeight() - ((EntityPlayer) entity).getDefaultEyeHeight() : entity.getEyeHeight());
        }
        else
        {
            y += (entity.height / 2f);
        }

        return new Pos(x, y, z);
    }

    protected void consumeShot()
    {
        if (chamberedRound != null)
        {
            if (!ignoreAmmo)
            {
                chamberedRound = null;
            }
            playAudio("round.consume");
            updateEntityStack("consume ammo");
        }
    }

    protected Pos getAim(float yaw, float pitch)
    {
        //Used to calculate x and z position
        float f3 = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f5 = -MathHelper.cos(-pitch * 0.017453292F);

        //used to calculate aim y
        float aimY = MathHelper.sin(-pitch * 0.017453292F);

        float aimX = f4 * f5;
        float aimZ = f3 * f5;
        return new Pos(aimX, aimY, aimZ);
    }

    /**
     * Called to see if the gun has an ammo item inserted.
     * Does not check chambered round, so will not work
     * for single shot weapons.
     *
     * @return true if has ammo inserted
     */
    public boolean hasAmmo()
    {
        return getLoadedClip() != null && getLoadedClip().getAmmoCount() > 0;
    }

    /**
     * Called to tick the gun for reload.
     */
    public void doReloadTick()
    {
        //If first tick set reload time
        if (reloadDelay == -1)
        {
            //Play reload init
            playAudio("reload");

            //Init reload delay
            reloadDelay = gunData.getReloadTime() * 20; //20 ticks a second, Reload time is in seconds, we update in ticks
        }
        //Tick reload timer
        if (reloadDelay-- <= 0)
        {
            //Reset timer
            reloadDelay = -1;

            //Detect if single shot weapon
            boolean singleShot = gunData.getReloadType() == ReloadType.BREACH_LOADED || gunData.getReloadType() == ReloadType.FRONT_LOADED;

            //If not single shot (clip) or single shot & no round, do reload
            if (!singleShot || chamberedRound == null)
            {
                //Reload weapon
                reloadWeapon(getInventory(), true);
                //Chamber next round
                chamberNextRound();
            }
        }
        //Play animation or audio while reloading
        else
        {
            //TODO run animation
            //TODO randomize audio TODO randomize volume TODO get gun position
            if (reloadDelay % 20 == 0)
            {
                //Play reload tick audio
                playAudio("reload.tick");
            }
        }
    }

    public void playAudio(String key)
    {
        //Checks for JUnit testing TODO fix
        if (Engine.instance != null && Engine.proxy != null && Engine.instance.packetHandler != null)
        {
            //TODO get weapon position
            Engine.proxy.playJsonAudio(entity.worldObj, gunData.getUniqueID() + "." + key, entity.posX, entity.posY + 1.1f, entity.posZ, 1, 1);
        }
    }

    public void playEffect(String key, Pos pos, Pos aim)
    {
        playEffect(key, pos.x(), pos.y(), pos.z(), aim.x(), aim.y(), aim.z());
    }

    public void playEffect(String key, double x, double y, double z, double mx, double my, double mz)
    {
        //Checks for JUnit testing TODO fix
        if (Engine.instance != null && Engine.proxy != null && Engine.instance.packetHandler != null)
        {
            //TODO get weapon position
            Engine.proxy.playJsonEffect(entity.worldObj, gunData.getUniqueID() + "." + key, x, y, z, mx, my, mz, null);
        }
    }

    /**
     * Called to reload the weapon from inventory
     *
     * @param inventory - inventory of the entity
     */
    public boolean reloadWeapon(IInventory inventory, boolean doAction)
    {
        boolean reloaded = false;
        if (inventory != null)
        {
            if (isManuallyFeedClip())
            {
                reloaded = loadRound(inventory, doAction);
            }
            else
            {
                if (doAction)
                {
                    unloadWeapon(inventory);
                }
                reloaded = loadBestClip(inventory, doAction);
            }
        }
        else if (Armory.INSTANCE != null)
        {
            Armory.INSTANCE.logger().error("Reload was called on '" + entity + "' but it had no inventory.");
        }
        if (doAction)
        {
            //Mark that reload was processed
            doReload = false;
        }
        if (reloaded && doAction)
        {
            updateEntityStack("reload weapon");
        }
        return reloaded;
    }

    private boolean loadBestClip(IInventory inventory, boolean doAction)
    {
        InventoryIterator it = new InventoryIterator(inventory, true);
        IClip bestClip = null;
        int slot = -1;
        for (ItemStack stack : it)
        {
            if (isAmmoSlot(it.slot()))
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
        }
        if (bestClip != null)
        {
            if (doAction)
            {
                if (_clip == null)
                {
                    playAudio("clip.load");
                    _clip = bestClip;
                    inventory.decrStackSize(slot, 1);
                }
            }
            return true;
        }
        else if (entity instanceof EntityPlayer)
        {
            ((EntityPlayer) entity).addChatComponentMessage(new ChatComponentText("There is no ammo of type '" + gunData.getAmmoType() + "' to load into the gun.")); //TODO translate
        }
        return false;
    }

    private boolean loadRound(IInventory inventory, boolean doAction)
    {
        int roundsLoad = 0;
        InventoryIterator it = new InventoryIterator(inventory, true);
        for (ItemStack stack : it)
        {
            if (isAmmoSlot(it.slot()))
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
                                //Decrease stack and load gun
                                if (doAction)
                                {
                                    int l = getLoadedClip().loadAmmo(data, ammo.getAmmoCount(stack));
                                    inventory.decrStackSize(it.slot(), l);
                                    roundsLoad += l;
                                }
                                else
                                {
                                    roundsLoad += 1;
                                }
                            }
                        }
                    }
                }

                //Exit loop if full
                if (getLoadedClip().getAmmoCount() >= getLoadedClip().getMaxAmmo())
                {
                    break;
                }
            }
        }
        if (roundsLoad > 0)
        {
            playAudio("round.load");
            return true;
        }
        else if (entity instanceof EntityPlayer)
        {
            ((EntityPlayer) entity).addChatComponentMessage(new ChatComponentText("There is no ammo of type '" + gunData.getAmmoType() + "' to load into the gun."));  //TODO translate
        }
        return false;
    }

    private void updateEntityStack(String name)
    {
        if (entity instanceof EntityPlayer)
        {
            ItemStack stack = ((EntityPlayer) entity).getHeldItem();
            ItemStack updated = toStack();
            if (stack != null && stack.isItemEqual(updated))
            {
                ((EntityPlayer) entity).inventory.setInventorySlotContents(((EntityPlayer) entity).inventory.currentItem, updated);
                ((EntityPlayer) entity).inventoryContainer.detectAndSendChanges();
                if (Engine.runningAsDev)
                {
                    Engine.logger().info("Updated gun stack: " + name);
                }
            }
        }
    }

    public void unloadWeapon(IInventory inventory)
    {
        if (_clip != null)
        {
            playAudio("unload");
            if (isManuallyFeedClip())
            {
                int i = 0;
                while (!getLoadedClip().getAmmo().isEmpty())
                {
                    ItemStack ammoStack = getLoadedClip().getAmmo().peek().toStack();
                    for (; i < inventory.getSizeInventory(); i++)
                    {
                        if (isAmmoSlot(i))
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
            }
            else if (_clip instanceof IModule)
            {
                //TODO send to inventory first, not hotbar
                ItemStack stack = ((IModule) _clip).toStack();
                for (int i = 0; i < inventory.getSizeInventory(); i++)
                {
                    if (isAmmoSlot(i))
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
                }
                //No space to insert so drop
                if (stack != null && stack.stackSize >= 0)
                {
                    InventoryUtility.dropItemStack(new Location(entity), stack);
                }
                _clip = null;
            }
            updateEntityStack("unloadWeapon");
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
        else if (entity instanceof EntitySentry)
        {
            return ((EntitySentry) entity).base;
        }
        return null;
    }

    public boolean isAmmoSlot(int slot)
    {
        if (entity instanceof EntityPlayer)
        {
            return slot >= 0 && slot < ((EntityPlayer) entity).inventory.getSizeInventory();
        }
        else if (entity instanceof EntitySentry && ((EntitySentry) entity).base != null)
        {
            EntitySentry sentry = ((EntitySentry) entity);
            return slot >= sentry.getData().getInventoryAmmoStart() && slot <= sentry.getData().getInventoryAmmoEnd();
        }
        else if (entity instanceof IInventory)
        {
            return slot >= 0 && slot < ((IInventory) entity).getSizeInventory();
        }
        return Engine.isJUnitTest();
    }

    public boolean hasSights()
    {
        //TODO implement
        return false;
    }

    public void sightWeapon()
    {
        isSighted = !isSighted;
    }

    public void load(NBTTagCompound tag)
    {
        if (tag.hasKey(NBT_ROUND))
        {
            chamberedRound = (IAmmoData) ArmoryDataHandler.INSTANCE.get("ammo").get(tag.getString(NBT_ROUND));
            if (chamberedRound == null)
            {
                error("Failed to load chambered round '" + tag.getString(NBT_ROUND) + "' form NBT for " + this);
            }
        }
        if (tag.hasKey(NBT_CLIP))
        {
            NBTTagCompound clipTag = tag.getCompoundTag(NBT_CLIP);
            //Contains data its a clip item
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
            //Else internal clip
            else
            {
                ((ClipInstance) _clip).load(clipTag);
            }
        }
    }

    @Override
    protected boolean saveTag()
    {
        return false;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        if (getChamberedRound() != null)
        {
            nbt.setString(NBT_ROUND, getChamberedRound().getUniqueID());
        }
        if (_clip != null)
        {
            NBTTagCompound clipTag = new NBTTagCompound();
            if (_clip instanceof ClipInstance)
            {
                if (_clip.getAmmoCount() > 0)
                {
                    ((ClipInstance) _clip).save(clipTag);
                    nbt.setTag(NBT_CLIP, clipTag);
                }
            }
            else
            {
                clipTag.setString("data", _clip.getClipData().getUniqueID());
                clipTag.setTag("stack", ((ClipInstanceItem) _clip).toStack().writeToNBT(new NBTTagCompound()));
                nbt.setTag(NBT_CLIP, clipTag);
            }
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
