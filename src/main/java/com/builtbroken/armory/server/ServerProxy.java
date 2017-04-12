package com.builtbroken.armory.server;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.CommonProxy;
import com.builtbroken.armory.content.sentry.tile.TileSentry;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/15/2016.
 */
public class ServerProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        super.preInit();
        Armory.blockSentry = Armory.INSTANCE.getManager().newBlock("sentryTile", TileSentry.class);
    }
}
