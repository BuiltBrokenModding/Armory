package com.builtbroken.armory.json.processor;

import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoType;
import com.builtbroken.armory.data.ammo.ClipData;
import com.builtbroken.armory.data.ammo.ClipTypes;
import com.builtbroken.armory.data.ranged.GunData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
        return "gun@after:ammoType";
    }

    @Override
    public GunData process(JsonElement element)
    {
        final JsonObject blockData = element.getAsJsonObject();
        if (blockData.has("type") && blockData.has("source") && blockData.has("damage"))
        {
            //Required data
            final String name = blockData.get("name").getAsString();
            final String type = blockData.get("type").getAsString();
            final String ID = blockData.get("ID").getAsString();

            //Get and validate clip type values
            final int clipTypeValue = blockData.getAsJsonPrimitive("clipType").getAsInt();
            if (clipTypeValue <= 0 || clipTypeValue >= ClipTypes.values().length)
            {
                throw new IllegalArgumentException("Invalid clip type " + clipTypeValue + " while reading " + element);
            }

            //Get and validate ammo type
            final String ammoTypeValue = blockData.get("ammo").getAsString();
            final AmmoType ammoType = ArmoryDataHandler.getAmmoType(ammoTypeValue);
            if (ammoType == null)
            {
                throw new IllegalArgumentException("Invalid ammo type " + ammoType + " while reading " + element);
            }

            final ClipTypes clipType = ClipTypes.get(clipTypeValue);

            //Build single fire clip type used to breach load the weapon, also doubles as the clip type for muskets & bold action rifles
            ClipData singleFireData = new ClipData(name + "@singleFire", clipType != ClipTypes.FRONT_LOADED ? ClipTypes.BREACH_LOADED : ClipTypes.FRONT_LOADED, ammoType, 1);

            //Make gun object
            final GunData data = new GunData(ID, type, name, ammoType, clipType, singleFireData);

            //Optional data
            JsonElement fallOff = blockData.get("fallOff");
            JsonElement mass = blockData.get("mass");
            JsonElement rateOfFire = blockData.get("rateOfFire");
            JsonElement reloadTime = blockData.get("reloadTime");

            return data;
        }
        else
        {
            throw new IllegalArgumentException("File is missing key parts " + element);
        }
    }
}
