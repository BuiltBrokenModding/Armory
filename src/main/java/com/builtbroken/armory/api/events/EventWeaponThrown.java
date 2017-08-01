package com.builtbroken.armory.api.events;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.Entity;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/1/2017.
 */
public class EventWeaponThrown extends EventWeapon
{
    public EventWeaponThrown(Entity entity)
    {
        super(entity);
    }

    /**
     * Before the item is charged up for a throw.
     * <p>
     * Make sure to set {@link #cancelReason} if canceled.
     */
    @Cancelable
    public static class PreHold extends EventWeaponThrown
    {
        public PreHold(Entity entity)
        {
            super(entity);
        }
    }

    /**
     * Before the item is released for a throw
     * <p>
     * Make sure to set {@link #cancelReason} if canceled.
     */
    @Cancelable
    public static class PreThrow extends EventWeaponThrown
    {
        public PreThrow(Entity entity)
        {
            super(entity);
        }
    }

    /**
     * After the item has been thrown
     * <p>
     * Make sure to set {@link #cancelReason} if canceled.
     */
    @Cancelable
    public static class Post extends EventWeaponThrown
    {
        public Post(Entity entity)
        {
            super(entity);
        }
    }
}
