package com.builtbroken.armory.data;

import com.builtbroken.mc.api.data.weapon.IData;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.json.imp.IJsonProcessor;
import com.builtbroken.mc.lib.json.processors.JsonGenData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * All armory objects extend this for common shared data and functions
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/15/2016.
 */
public abstract class ArmoryEntry extends JsonGenData implements IData
{
    /** Simple name for the item, used as translation key as backup */
    private String name;
    /** Type of the item {ammo, ammoType, clip, gun} */
    private String type;

    /** Weight of the object in grams */
    public int mass = -1;
    /** Translation key used for langauge files */
    public String translationKey;
    /** Group to cluster the items in, normally this is the mod domain or author name */
    public String contentGroup;

    public Item itemFile;
    public int meta;

    /** Unique save ID, Use mod:type.name.subName */
    public final String ID;

    /**
     * Creates a new instance
     *
     * @param type - Type of the item {ammo, ammoType, clip, gun}
     * @param name - simple name of the item
     */
    public ArmoryEntry(IJsonProcessor processor, String id, String type, String name)
    {
        super(processor);
        this.type = type;
        this.name = name;
        this.ID = id;
    }

    public void set(Item item, int meta)
    {
        this.itemFile = item;
        this.meta = meta;
    }

    public ItemStack toStack()
    {
        return new ItemStack(itemFile, 1, meta % 32000);
    }

    /**
     * Simple name of the item
     *
     * @return string
     */
    public final String name()
    {
        return name;
    }

    @Override
    public final String getDataType()
    {
        return type;
    }

    @Override
    public final String getUniqueID()
    {
        return ID;
    }

    @Override
    public String getContentID()
    {
        return getUniqueID();
    }

    public final String getUnlocalizedName()
    {
        if (translationKey == null)
        {
            String group = (contentGroup != null && !contentGroup.isEmpty() ? contentGroup + ":" : "");
            if (group.isEmpty())
            {
                group = (author != null && !author.isEmpty() ? author + ":" : "");
            }
            translationKey = type + "." + group + name();
        }
        return translationKey;
    }

    @Override
    public String getDisplayString()
    {
        String translation = LanguageUtility.getLocalName(getUnlocalizedName());
        if (translation != null && !translation.isEmpty())
        {
            return translation;
        }
        return name;
    }

    @Override
    public void register()
    {
        ArmoryDataHandler.INSTANCE.get(type).add(this);
    }

    @Override
    public String toString()
    {
        return "ArmoryEntry[" + name + ", " + type + "]@" + hashCode();
    }
}
