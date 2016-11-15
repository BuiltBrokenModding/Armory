package com.builtbroken.armory.content.target.types;

import com.builtbroken.armory.content.target.EntityTarget;
import net.minecraft.world.World;

/**
 * Interface applied to objects that hold data for customizing target entities for attack
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/12/2016.
 */
public interface ITargetType
{
    /** Called to render the target */
    void render(EntityTarget target);

    /**
     * Gets the target that will spawn, do not
     * apply translation or actually spawn
     *
     * @param world - world to spawn it in
     * @return target
     */
    EntityTarget getTarget(World world);

    /**
     * Applies minor translation to the target in case the target is rather large
     *
     * @param target - target centered 0.5 above the pad
     */
    void applyTranslation(EntityTarget target);
}
