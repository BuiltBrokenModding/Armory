package com.builtbroken.armory.content.sentry.entity;

import com.builtbroken.armory.content.sentry.Sentry;
import com.builtbroken.armory.content.sentry.imp.ISentryHost;
import com.builtbroken.mc.api.energy.IEnergyBuffer;
import com.builtbroken.mc.api.energy.IEnergyBufferProvider;
import com.builtbroken.mc.api.tile.provider.IInventoryProvider;
import com.builtbroken.mc.imp.transform.rotation.EulerAngle;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.entity.EntityBase;
import com.builtbroken.mc.prefab.tile.Tile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * AI driven entity for handling how the sentry gun works
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2017.
 */
public class EntitySentry extends EntityBase implements IEnergyBufferProvider, ISentryHost, IInventoryProvider
{
    /** Host that is managing this entity */
    public ISentryHost host;
    protected Sentry sentry;

    /** Last time rotation was updated, used in {@link EulerAngle#lerp(EulerAngle, double)} function for smooth rotation */
    protected long lastSentryUpdate = System.nanoTime();
    /** Percent of time that passed since last tick, should be 1.0 on a stable server */
    protected float deltaTime;

    public EntitySentry(World world)
    {
        super(world);
        this.noClip = true;
        this.setSize(0.7f, 0.7f);
    }

    public EntitySentry(World world, Sentry sentry, ISentryHost host)
    {
        this(world);
        this.host = host;
        setSentry(sentry);
    }

    @Override
    protected void setSize(float width, float height)
    {
        super.setSize(width, height);
        sentry.halfWidth = Math.sqrt((width * width) * 2) / 2f;
    }


    @Override
    public void setPosition(double x, double y, double z)
    {
        super.setPosition(x, y, z);
        sentry.center = new Pos(x, y + (height / 2f), z);
        if (sentry.getSentryData() != null && sentry.getSentryData().getCenterOffset() != null)
        {
            sentry.center = sentry.center.add(sentry.getSentryData().getCenterOffset());
        }
        sentry.searchArea = null;
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
        if (getSentry() != null)
        {
            //Keep track of time between ticks to provide smooth animation
            deltaTime = (float) ((System.nanoTime() - lastSentryUpdate) / 100000000.0); // time / time_tick, client uses different value
            if (getSentry().update(ticksExisted, deltaTime))
            {
                lastSentryUpdate = System.nanoTime();
            }
        }
    }

    @Override
    public boolean interactFirst(EntityPlayer player)
    {
        return host instanceof Tile && ((Tile) host).onPlayerActivated(player, 1, new Pos());
    }

    @Override
    public IEnergyBuffer getEnergyBuffer(ForgeDirection side)
    {
        return host instanceof IEnergyBufferProvider ? ((IEnergyBufferProvider) host).getEnergyBuffer(side) : null;
    }

    public Sentry getSentry()
    {
        return sentry;
    }

    public void setSentry(Sentry sentry)
    {
        this.sentry = sentry;
        setSize(sentry.getSentryData().getBodyWidth(), sentry.getSentryData().getBodyHeight());
    }

    @Override
    public IInventory getInventory()
    {
        return host instanceof IInventoryProvider ? ((IInventoryProvider) host).getInventory() : null;
    }
}
