package com.builtbroken.armory.content.sentry;

import com.builtbroken.armory.content.prefab.ItemBlockMetaArmoryEntry;
import com.builtbroken.armory.data.sentry.SentryData;
import net.minecraft.block.Block;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2017.
 */
public class ItemBlockSentry extends ItemBlockMetaArmoryEntry<SentryData>
{
    public ItemBlockSentry(Block block)
    {
        super(block, "sentry");
    }
}
