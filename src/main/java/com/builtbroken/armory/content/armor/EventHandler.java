package com.builtbroken.armory.content.armor;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/9/2017.
 */
public class EventHandler
{
    public void livingHurtEvent(LivingHurtEvent event)
    {
        //TODO use to reduce damage when wearing armor
    }

    public void livingUpdateEvent(LivingEvent.LivingUpdateEvent event)
    {
        //TODO use this for AOE effects on armor
    }

    public void livingJumpEvent(LivingEvent.LivingJumpEvent event)
    {
        //TODO use to modify jump power
    }
}
