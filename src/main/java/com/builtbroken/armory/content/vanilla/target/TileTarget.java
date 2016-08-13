package com.builtbroken.armory.content.vanilla.target;

import com.builtbroken.armory.content.vanilla.target.types.ITargetType;
import com.builtbroken.mc.prefab.tile.Tile;
import net.minecraft.block.material.Material;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 8/12/2016.
 */
public class TileTarget extends Tile
{
    private EntityTarget target;
    private ITargetType type;

    private boolean redstoneTick;
    private int deployDelay = 10;
    private int deployCount = 0;
    private int yaw = 0;

    public TileTarget()
    {
        super("attackTarget", Material.sand);
    }

    @Override
    public Tile newTile()
    {
        return new TileTarget();
    }

    @Override
    public void update()
    {
        super.update();
        if (isServer() && type != null)
        {
            //If redstone was not ticked check every other tick
            if (!redstoneTick && ticks % 2 == 0)
            {
                redstoneTick = world().isBlockIndirectlyGettingPowered(xi(), yi(), zi());
            }
            //If redstone count down
            if (redstoneTick && deployCount-- <= deployDelay)
            {
                deploy();
            }
        }
    }

    /** Called to tell the target do deploy */
    public void deploy()
    {
        deployCount = deployDelay;
        if (type != null)
        {
            target = type.getTarget(world());
            target.setLocationAndAngles(xi(), yi(), zi(), yaw, 0);
            type.applyTranslation(target);
            world().spawnEntityInWorld(target);
        }
    }
}
