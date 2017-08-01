package com.builtbroken.armory.api.events;

import com.builtbroken.armory.Armory;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/1/2017.
 */
public abstract class EventWeapon extends EntityEvent
{
    /** Reason displayed to user when an event is blocked */
    public String cancelReason = Armory.PREFIX + ":event.blocked";

    public EventWeapon(Entity entity)
    {
        super(entity);
    }
}
