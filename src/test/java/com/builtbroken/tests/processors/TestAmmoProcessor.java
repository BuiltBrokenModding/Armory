package com.builtbroken.tests.processors;

import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ammo.AmmoData;
import com.builtbroken.armory.data.ammo.AmmoType;
import com.builtbroken.armory.data.damage.DamageSimple;
import com.builtbroken.armory.data.projectiles.EnumProjectileTypes;
import com.builtbroken.armory.json.processor.AmmoJsonProcessor;
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
public class TestAmmoProcessor extends AbstractTest
{
    public void testLoading()
    {
        AmmoType ammoType = new AmmoType(null, "9mm", "9mm", EnumProjectileTypes.BULLET);
        ArmoryDataHandler.INSTANCE.add(new ArmoryDataHandler.ArmoryData(new File("tmp"), "ammoType"));
        ArmoryDataHandler.INSTANCE.get("ammoType").add(ammoType);

        JsonReader jsonReader = new JsonReader(new StringReader(
                "{ \"ammo\": {\n" +
                        "    \"name\": \"9mm.basic\",\n" +
                        "    \"id\": \"armory:ammo.9mm.basic\",\n" +
                        "    \"ammoType\": \"9mm\",\n" +
                        "    \"damage\": {\n" +
                        "      \"value\": 5,\n" +
                        "      \"type\": \"impact\"\n" +
                        "    }\n" +
                        "  }}"
        ));
        JsonElement element = Streams.parse(jsonReader);
        AmmoJsonProcessor processor = new AmmoJsonProcessor();
        AmmoData data = processor.process(element.getAsJsonObject().get("ammo"));
        assertEquals(1, data.damageData.size());
        assertTrue(data.damageData.get(0) instanceof DamageSimple);
        assertEquals("impact", ((DamageSimple) data.damageData.get(0)).damageSource);
        assertEquals(5f, ((DamageSimple) data.damageData.get(0)).damage);
        assertSame(ammoType, data.ammoType);
    }
}
