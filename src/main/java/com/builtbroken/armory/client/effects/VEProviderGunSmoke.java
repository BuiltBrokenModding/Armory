package com.builtbroken.armory.client.effects;

import com.builtbroken.mc.client.effects.VisualEffectProvider;
import com.builtbroken.mc.imp.transform.vector.Pos;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Spawns a shock wave effect at the location
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 2/15/2017.
 */
public class VEProviderGunSmoke extends VisualEffectProvider
{
    public VEProviderGunSmoke()
    {
        super("armory:gunSmoke");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void displayEffect(World world, double x, double y, double z, double mx, double my, double mz, NBTTagCompound otherData)
    {
        float reduction = 0.2f;
        float random = 0.05f;
        int flames = world.rand.nextInt(5);
        int smoke = world.rand.nextInt(10);

        final Pos aim = new Pos(mx, my, mz);

        for (int i = 0; i < flames; i++)
        {
            Pos vel = aim.multiply(reduction).addRandom(world.rand, random);
            Minecraft.getMinecraft().thePlayer.worldObj.spawnParticle("flame", x, y, z, vel.xf(), vel.yf(), vel.zf());
        }

        for (int i = 0; i < smoke; i++)
        {
            Pos vel = aim.multiply(reduction).addRandom(world.rand, random);
            Minecraft.getMinecraft().thePlayer.worldObj.spawnParticle("smoke", x, y, z, vel.xf(), vel.yf(), vel.zf());
        }
    }
}
