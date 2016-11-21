package com.builtbroken.armory.data.ranged;

import com.builtbroken.armory.data.ammo.ClipInstance;
import com.builtbroken.mc.api.ISave;
import com.builtbroken.mc.api.data.weapon.IAmmoData;
import com.builtbroken.mc.api.data.weapon.IAmmoType;
import com.builtbroken.mc.api.data.weapon.IGunData;
import com.builtbroken.mc.api.data.weapon.ReloadType;
import com.builtbroken.mc.api.items.weapons.IItemAmmo;
import com.builtbroken.mc.api.items.weapons.IItemClip;
import com.builtbroken.mc.api.modules.weapon.IClip;
import com.builtbroken.mc.api.modules.weapon.IGun;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.PacketSpawnStream;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryIterator;
import com.builtbroken.mc.prefab.module.AbstractModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
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
    /** Who is holding the weapon */
    public final Entity entity;
    /** Properties of the weapon */
    protected final IGunData gunData;

    /** Clip that is feed into the weapon */
    protected IClip clip;

    /** Last time the weapon was fired, milliseconds */
    private Long lastTimeFired = 0L;

    public GunInstance(ItemStack gunStack, Entity entity, IGunData gun)
    {
        super(gunStack, "armory:gun");
        this.entity = entity;
        this.gunData = gun;
        if (isManuallyFeedClip())
        {
            clip = new ClipInstance(gun.getBuiltInClipData());
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
        if (entity instanceof EntityPlayer && (lastTimeFired == 0L || deltaTime > gunData.getRateOfFire()))
        {
            lastTimeFired = System.currentTimeMillis();
            _doFire(world, ((EntityPlayer) entity).rotationYawHead, ((EntityPlayer) entity).rotationPitch);
        }
    }

    protected void _doFire(World world, float yaw, float pitch)
    {
        if (!hasAmmo())
        {
            reloadWeapon(getInventory());
        }
        //TODO return and allow reload animations
        if (hasAmmo())
        {
            consumeAmmo();
            //Figure out where the player is aiming
            final Pos aim = getAim(yaw, pitch);

            //Find our hand position so to position starting point near barrel of the gun
            float rotationHand = MathHelper.wrapAngleTo180_float(((EntityPlayer) entity).renderYawOffset + 90);
            final Pos hand = new Pos(
                    Math.cos(Math.toRadians(rotationHand)) - Math.sin(Math.toRadians(rotationHand)),
                    0,
                    Math.sin(Math.toRadians(rotationHand)) + Math.cos(Math.toRadians(rotationHand))
            ).multiply(0.5);

            final Pos entityPos = new Pos(entity.posX, entity.posY + 1.1, entity.posZ).add(hand);

            final Pos start = entityPos;
            final Pos end = entityPos.add(aim.multiply(500));

            PacketSpawnStream packet = new PacketSpawnStream(world.provider.dimensionId, start, end, 2);
            packet.red = (Color.blue.getRed() / 255f);
            packet.green = (Color.blue.getGreen() / 255f);
            packet.blue = (Color.blue.getBlue() / 255f);
            Engine.instance.packetHandler.sendToAllAround(packet, new Location(entity), 200);

            MovingObjectPosition hit = start.rayTrace(world, end);
            if (hit != null && hit.typeOfHit != MovingObjectPosition.MovingObjectType.MISS)
            {
                if (hit.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)
                {
                    onHit(hit.entityHit);
                }
                else
                {
                    onHit(hit.blockX, hit.blockY, hit.blockZ);
                }
            }
        }
    }

    protected void consumeAmmo()
    {
        clip.consumeAmmo(1);
    }

    protected void onHit(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            ((EntityPlayer) entity).addChatComponentMessage(new ChatComponentText("Hit: " + entity));
        }
    }

    protected void onHit(int x, int y, int z)
    {
        if (entity instanceof EntityPlayer)
        {
            ((EntityPlayer) entity).addChatComponentMessage(new ChatComponentText("Hit: " + x + "x " + y + "y " + z + "z "));
        }
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
        return clip != null && clip.getAmmoCount() > 0;
    }

    public void reloadWeapon(IInventory inventory)
    {
        if (isManuallyFeedClip())
        {
            loadRound(inventory);
        }
        else
        {
            loadBestClip(inventory);
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
            if (bestClip != null)
            {
                unloadWeapon(inventory);
                if (clip == null)
                {
                    clip = bestClip;
                    inventory.decrStackSize(slot, 1);
                }
            }
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
                        if (clip.getAmmoCount() < clip.getClipData().getMaxAmmo())
                        {
                            clip.loadAmmo(data, ammo.getAmmoCount(stack));
                        }
                    }
                }
            }
        }
    }

    public void unloadWeapon(IInventory inventory)
    {
        if (clip != null)
        {
            if (isManuallyFeedClip())
            {
                //TODO place all rounds into inventory
            }
            else
            {
                //TODO place clip into inventory
            }
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
        //TODO implement
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        //TODO implement
        return nbt;
    }

    @Override
    public IGunData getGunData()
    {
        return gunData;
    }

    @Override
    public IClip getLoadedClip()
    {
        return clip;
    }

    @Override
    public String getSaveID()
    {
        return "armory:gun";
    }
}
