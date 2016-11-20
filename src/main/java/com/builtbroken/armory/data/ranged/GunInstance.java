package com.builtbroken.armory.data.ranged;

import com.builtbroken.armory.data.ammo.ClipInstance;
import com.builtbroken.mc.api.ISave;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.PacketSpawnStream;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
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
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class GunInstance implements ISave
{
    /** Who is holding the weapon */
    public final Entity entity;
    /** Properties of the weapon */
    public final GunData gun;

    /** Clip that is feed into the weapon */
    public ClipInstance clip;

    /** Last time the weapon was fired, milliseconds */
    private Long lastTimeFired = 0L;

    public GunInstance(Entity entity, GunData gun)
    {
        this.entity = entity;
        this.gun = gun;
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
        if (entity instanceof EntityPlayer && (lastTimeFired == 0L || deltaTime > gun.getRateOfFire()))
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
        return clip != null && clip.currentAmmo > 0;
    }

    public void reloadWeapon(IInventory inventory)
    {
        unloadWeapon(inventory);
        if (clip == null)
        {
            for (int i = 0; i < inventory.getSizeInventory(); i++)
            {

            }
        }
    }

    public void unloadWeapon(IInventory inventory)
    {
        if (clip != null)
        {

        }
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
        return false;
    }

    public void sightWeapon()
    {

    }

    public void load(NBTTagCompound tag)
    {

    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        return null;
    }
}
