package com.builtbroken.tests.processors;

import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoType;
import com.builtbroken.armory.data.ammo.ClipTypes;
import com.builtbroken.armory.data.projectiles.EnumProjectileTypes;
import com.builtbroken.armory.data.ranged.GunData;
import com.builtbroken.armory.json.processor.GunJsonProcessor;
import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import org.junit.runner.RunWith;

import java.io.StringReader;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
@RunWith(VoltzTestRunner.class)
public class TestGunProcessor extends AbstractTest
{
    public void testLoading()
    {
        AmmoType ammoType = new AmmoType("9mm", EnumProjectileTypes.BULLET);
        ArmoryDataHandler.add(ammoType);

        JsonReader jsonReader = new JsonReader(new StringReader(
                "{\n" +
                        "  \"author\": {\n" +
                        "    \"name\": \"${Armory}\"\n" +
                        "  },\n" +
                        "  \"gun\": {\n" +
                        "    \"name\": \"handgun\",\n" +
                        "    \"type\": \"handgun1\",\n" +
                        "    \"translationKey\": \"handgun2\",\n" +
                        "    \"ID\": \"amory.test.hangun\",\n" +
                        "    \"ammo\": \"9mm\",\n" +
                        "    \"clipType\": 3,\n" +
                        "    \"fallOff\": \"0.05D\",\n" +
                        "    \"mass\": 630,\n" +
                        "    \"rateOfFire\": 240,\n" +
                        "    \"reloadTime\": 40\n" +
                        "  }\n" +
                        "}"
        ));
        JsonElement element = Streams.parse(jsonReader);
        GunJsonProcessor processor = new GunJsonProcessor();
        GunData data = processor.process(element.getAsJsonObject().get("gun"));
        //At this point we have tested what we need but lets do 100%
        assertEquals("handgun", data.name());
        assertEquals("handgun1", data.type());
        assertEquals("gun.handgun2", data.translationKey);
        assertEquals("amory.test.hangun", data.ID);
        assertSame(ammoType, data.ammoType);
        assertSame(ClipTypes.values()[3], data.clipType);
        assertEquals(40, data.reloadTime);
        assertEquals(240, data.getRateOfFire());
        assertEquals(630, data.mass);
    }
}
