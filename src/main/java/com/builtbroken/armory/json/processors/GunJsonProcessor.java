package com.builtbroken.armory.json.processors;

import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoType;
import com.builtbroken.armory.data.clip.ClipData;
import com.builtbroken.armory.data.ranged.GunData;
import com.builtbroken.armory.json.ArmoryEntryJsonProcessor;
import com.builtbroken.mc.api.data.weapon.ReloadType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class GunJsonProcessor extends ArmoryEntryJsonProcessor<GunData>
{
    public GunJsonProcessor()
    {
        super("gun");
    }

    @Override
    public String getJsonKey()
    {
        return "gun";
    }

    @Override
    public String getLoadOrder()
    {
        return "after:ammo";
    }

    @Override
    public GunData process(JsonElement element)
    {
        final JsonObject object = element.getAsJsonObject();
        ensureValuesExist(object, "ID", "name", "gunType", "reloadType", "ammoType");

        //common data
        final String name = object.get("name").getAsString();
        final String ID = object.get("ID").getAsString();

        //Gun type
        final String type = object.get("gunType").getAsString();

        //Get the reload type of the gun
        JsonPrimitive clipTypeValue = object.getAsJsonPrimitive("reloadType");
        ReloadType reloadType = ReloadType.get(clipTypeValue.getAsString());

        //Get and validate ammo type
        final String ammoTypeValue = object.get("ammoType").getAsString();
        final AmmoType ammoType = (AmmoType) ArmoryDataHandler.INSTANCE.get("ammoType").get(ammoTypeValue);
        if (ammoType == null)
        {
            throw new IllegalArgumentException("Invalid ammo type " + ammoType + " while reading " + element);
        }

        //Build single fire clip type used to breach load the weapon, also doubles as the clip type for muskets & bold action rifles
        final ClipData builtInClip;
        if (reloadType == ReloadType.FRONT_LOADED)
        {
            builtInClip = new ClipData(this, ID, name + "@frontLoaded", ReloadType.FRONT_LOADED, ammoType, 1);
        }
        else if (reloadType == ReloadType.HAND_FEED)
        {
            ensureValuesExist(object, "clipSize");
            builtInClip = new ClipData(this, ID, name + "@handFeed", ReloadType.HAND_FEED, ammoType, object.getAsJsonPrimitive("clipSize").getAsInt());
        }
        else
        {
            builtInClip = new ClipData(this, ID, name + "@singleFire", ReloadType.BREACH_LOADED, ammoType, 1);
        }

        //Make gun object
        final GunData data = new GunData(this, ID, type, name, ammoType, reloadType, builtInClip);

        //Process extra data that all objects share
        processExtraData(object, data);

        //Optional data
        final JsonElement fallOff = object.get("fallOff");
        final JsonElement rateOfFire = object.get("rateOfFire");
        final JsonElement reloadTime = object.get("reloadTime");
        final JsonElement sightRequired = object.get("sightToFire");

        if (sightRequired != null)
        {
            data.sightToFire = sightRequired.getAsBoolean();
        }

        if (fallOff != null)
        {
            //TODO create equation processor
        }

        if (rateOfFire != null)
        {
            if (rateOfFire.isJsonPrimitive())
            {
                data.setRateOfFire(rateOfFire.getAsInt());
            }
            else
            {
                throw new IllegalArgumentException("Invalid rate of fire value " + rateOfFire + " when reading " + element);
            }
        }

        if (reloadTime != null)
        {
            if (reloadTime.isJsonPrimitive())
            {
                data.reloadTime = reloadTime.getAsInt();
            }
            else
            {
                throw new IllegalArgumentException("Invalid reload time value " + reloadTime + " when reading " + element);
            }
        }
        return data;
    }
}
