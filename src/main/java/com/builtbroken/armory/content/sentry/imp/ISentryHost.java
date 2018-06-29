package com.builtbroken.armory.content.sentry.imp;

import com.builtbroken.armory.content.sentry.Sentry;
import com.builtbroken.mc.api.IWorldPosition;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

/**
 * Applied to objects that host a sentry gun
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/11/2017.
 */
public interface ISentryHost extends IWorldPosition
{
    Sentry getSentry();

    String getOwnerName();

    UUID getOwnerID();

    boolean isOwner(EntityPlayer player);

    /**
     * Called to have the host send a packet to client or server
     * <p>
     * This is done as the host may be anything (tile, entity, item)
     * and may be unable to know what type of packet to use.
     *
     * @param id   - packet id
     * @param side - side to send
     * @param data - packet data
     */
    void sendDataPacket(int id, Side side, Object... data);
}
