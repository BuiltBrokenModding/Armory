package com.builtbroken.armory.content.vanilla.target.types;

import com.builtbroken.armory.content.vanilla.target.EntityTarget;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/12/2016.
 */
public class TargetType implements ITargetType
{
    @Override
    public void render(EntityTarget target)
    {

    }

    @Override
    public EntityTarget getTarget(World world)
    {
        return new EntityTarget(world, this);
    }
}
