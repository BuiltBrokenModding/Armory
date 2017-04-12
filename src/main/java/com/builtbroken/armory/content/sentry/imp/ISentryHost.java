package com.builtbroken.armory.content.sentry.imp;

import com.builtbroken.armory.content.sentry.Sentry;
import com.builtbroken.mc.api.IWorldPosition;

/**
 * Applied to objects that host a sentry gun
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/11/2017.
 */
public interface ISentryHost extends IWorldPosition
{
    Sentry getSentry();
}
