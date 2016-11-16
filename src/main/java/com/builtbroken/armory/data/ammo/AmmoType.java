package com.builtbroken.armory.data.ammo;

import com.builtbroken.armory.data.ArmoryEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Category for ammo
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class AmmoType extends ArmoryEntry
{
    public final List<AmmoData> ammoDataObjects = new ArrayList();

    public AmmoType(String name)
    {
        super("ammoType", name);
    }
}
