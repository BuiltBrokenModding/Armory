package com.builtbroken.armory.data.damage;

import com.builtbroken.mc.lib.json.imp.IJsonProcessor;
import com.builtbroken.mc.lib.json.processors.JsonGenData;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * Used to stored data about how ammo does damage
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/28/2017.
 */
public abstract class DamageData extends JsonGenData
{
    public DamageData(IJsonProcessor processor)
    {
        super(processor);
    }

    /**
     * Called when the projectile hits the entity
     *
     * @param attacker - who fired the projectile
     * @param entity   - entity that was hit
     * @param velocity - how fast the projectile is going per second
     * @return true if the projectile should stop or die
     */
    public boolean onImpact(Entity attacker, Entity entity, double hitX, double hitY, double hitZ, float velocity, float scale)
    {
        return true;
    }

    /**
     * Called when the projectile hits the entity
     *
     * @param attacker - who fired the projectile
     * @param world    - world the hit was marked
     * @param x        - location in the world
     * @param y        - location in the world
     * @param z        - location in the world
     * @param hitX     - location of the projectile
     * @param hitY     - location of the projectile
     * @param hitZ     - location of the projectile
     * @param velocity - how fast the projectile is going per second
     * @return true if the projectile should stop or die
     */
    public boolean onImpact(Entity attacker, World world, int x, int y, int z, double hitX, double hitY, double hitZ, float velocity, float scale)
    {
        return true;
    }

    /**
     * The base or average damage the projectile
     * does on impact. Used for display to the
     * player.
     *
     * @return damage value
     */
    public float getBaseDamage()
    {
        return 0;
    }

    @Override
    public String getContentID()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return "DamageData@" + hashCode();
    }
}
