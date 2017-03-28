package com.builtbroken.tests.processors;

import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoType;
import com.builtbroken.mc.api.data.weapon.ReloadType;
import com.builtbroken.armory.data.projectiles.EnumProjectileTypes;
import com.builtbroken.armory.data.ranged.GunData;
import com.builtbroken.armory.json.processor.GunJsonProcessor;
import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import org.junit.runner.RunWith;

import java.io.File;
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
        AmmoType ammoType = new AmmoType(null, "9mm", "9mm", EnumProjectileTypes.BULLET);
        ArmoryDataHandler.INSTANCE.add(new ArmoryDataHandler.ArmoryData(new File("tmp"), "ammoType"));
        ArmoryDataHandler.INSTANCE.get("ammoType").add(ammoType);

        JsonReader jsonReader = new JsonReader(new StringReader(
                "{\n" +
                        "  \"author\": {\n" +
                        "    \"name\": \"armory\"\n" +
                        "  },\n" +
                        "  \"gun\": {\n" +
                        "    \"name\": \"handgun.9mm.test\",\n" +
                        "    \"gunType\": \"handgun\",\n" +
                        "    \"ID\": \"armory:handgun.9mm.test\",\n" +
                        "    \"ammoType\": \"9mm\",\n" +
                        "    \"reloadType\": \"clip\",\n" +
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
        assertEquals("handgun.9mm.test", data.name());
        assertEquals("handgun", data.gunType);
        assertEquals("armory:handgun.9mm.test", data.ID);
        assertSame(ammoType, data.ammoType);
        assertSame(ReloadType.CLIP, data.reloadType);
        assertEquals(40, data.reloadTime);
        assertEquals(240, data.getRateOfFire());
        assertEquals(630, data.mass);
    }
}
