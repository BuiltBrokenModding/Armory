package com.builtbroken.armory.content.sentry;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.content.sentry.gui.ContainerSentry;
import com.builtbroken.armory.content.sentry.gui.GuiSentry;
import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ranged.GunData;
import com.builtbroken.armory.data.sentry.SentryData;
import com.builtbroken.mc.api.tile.access.IGuiTile;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Reference point and storage point for {@link EntitySentry}
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 3/26/2017.
 */
public class TileSentry extends TileModuleMachine<ExternalInventory> implements IGuiTile
{
    protected SentryData sentryData;
    protected EntitySentry sentry;

    protected ItemStack sentryStack;

    protected boolean running = false;
    protected boolean turnedOn = true;

    public TileSentry()
    {
        super("sentry", Material.iron);
        // this.itemBlock = ItemBlockSentry.class; TODO use item instead of an item block
        bounds = new Cube(.1, 0, .1, .9, .2, .9);
    }

    @Override
    public void update()
    {
        super.update();
        if (sentryData == null)
        {
            GunData data = (GunData) ArmoryDataHandler.INSTANCE.get("gun").get("armory:handgun.9mm.test");
            sentryData = new SentryData(null, "test.sentry", "test.sentry");
            sentryData.gunData = data;
        }
        if (isServer())
        {
            //TODO add support for batter slows
            if (sentryData != null && turnedOn)
            {
                if (sentry == null)
                {
                    sentry = new EntitySentry(world());
                    sentry.setPosition(xi() + 0.5, yi() + 0.5, zi() + 0.5); //TODO adjust based on data
                    sentry.data = sentryData;
                    sentry.base = this;
                    world().spawnEntityInWorld(sentry);
                }

                if (sentry != null)
                {
                    sentry.setPosition(xi() + 0.5, yi() + 0.5, zi() + 0.5);
                }

                //Consume energy per tick
                if (sentryData.energyCost > 0)
                {
                    //energy is consumed even if there is not enough for a full cycle
                    int drained = getEnergyBuffer(ForgeDirection.UNKNOWN).removeEnergyFromStorage(sentryData.energyCost, true);
                    running = drained >= sentryData.energyCost;
                    //TODO add negative effects
                    //      TODO add percentage performance if energy is above 40%
                    //      TODO if bellow 40% start to check for brown out damage
                }
            }

            if (sentryData == null)
            {
                Engine.logger().error("Removing corrupted sentry tile from world, " + this);
                world().setBlockToAir(xCoord, yCoord, zCoord);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        running = nbt.getBoolean("running");
        turnedOn = nbt.getBoolean("enabled");
        if (nbt.hasKey("sentryStack"))
        {
            sentryStack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("sentryStack"));
            //if (sentryStack.getItem() instanceof ItemBlockSentry) TODO replace
            //{
            //    sentryData = ((ItemBlockSentry) sentryStack.getItem()).getData(sentryStack);
            //}
        }
        super.readFromNBT(nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("running", running);
        nbt.setBoolean("enabled", turnedOn);
        if (sentryStack != null)
        {
            nbt.setTag("sentryStack", sentryStack.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    protected ExternalInventory createInventory()
    {
        if (sentryData != null && sentryData.inventorySize > 0)
        {
            return new ExternalInventory(this, sentryData.inventorySize);
        }
        return null;
    }

    @Override
    public int getEnergyBufferSize()
    {
        if (sentryData != null && sentryData.inventorySize > 0)
        {
            return sentryData.energyBuffer;
        }
        return 0;
    }

    @Override
    public Tile newTile()
    {
        return new TileSentry();
    }

    @Override
    public String toString()
    {
        return "TileSentryBase[" + (world() != null && world().provider != null ? world().provider.dimensionId : "?") + "w, " + xCoord + "x, " + yCoord + "y, " + zCoord + "z, " + sentryData + "]@" + hashCode();
    }

    @Override
    protected boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (isServer())
        {
            openGui(player, Armory.INSTANCE);
        }
        return false;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerSentry(player, this);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiSentry(player, this);
    }
}
