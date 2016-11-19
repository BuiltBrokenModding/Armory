package com.builtbroken.armory.content.items;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.data.ArmoryDataHandler;
import com.builtbroken.armory.data.ranged.GunData;
import com.builtbroken.armory.data.ranged.GunInstance;
import com.builtbroken.jlib.type.Pair;
import com.builtbroken.mc.api.IWorldPosition;
import com.builtbroken.mc.api.items.IMouseButtonHandler;
import com.builtbroken.mc.api.items.weapons.IAmmoType;
import com.builtbroken.mc.api.items.weapons.IReloadableWeapon;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketReceiver;
import com.builtbroken.mc.core.network.packet.PacketPlayerItem;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.NBTUtility;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class ItemGun extends ItemWeapon implements IMouseButtonHandler, IReloadableWeapon, IPostInit, IPacketReceiver
{
    private static final Long saveTimeLimit = TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS);
    private static boolean initGunData = false;

    /** Map of weapons to meta values for fast access */
    public static final HashMap<Integer, GunData> metaToGun = new HashMap();
    /** Cache of the last weapon the entity has out */
    public static final HashMap<Entity, Pair<GunInstance, ItemStack>> gunCache = new HashMap();
    /** Who has the left click held down */
    public static final Map<EntityPlayer, Integer> leftClickHeld = new HashMap();
    //TODO handle what type of gun
    //TODO handle damage to weapon
    //TODO handle damage to weapon parts
    //TODO handle to & from stack conversions
    //TODO handle ammo
    //TODO handle reloading
    //TODO handle firing
    //TODO handle aiming

    public ItemGun()
    {
        this.setUnlocalizedName(Armory.PREFIX + "gun");
        this.setCreativeTab(CreativeTabs.tabCombat);
        this.setHasSubtypes(true);
    }

    @Override
    public void mouseClick(ItemStack stack, EntityPlayer player, int button, boolean state)
    {
        if (!player.worldObj.isRemote)
        {
            if (button == 0)
            {
                if (state)
                {
                    if (!leftClickHeld.containsKey(player))
                    {
                        leftClickHeld.put(player, 0);
                    }
                }
                else
                {
                    leftClickHeld.remove(player);
                }
            }
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean bool)
    {
        if (!world.isRemote)
        {
            if (entity instanceof EntityPlayer)
            {
                if (leftClickHeld.containsKey(entity))
                {
                    int ticks = leftClickHeld.get(entity) + 1;
                    leftClickHeld.put((EntityPlayer) entity, ticks);
                    onLeftClickHeld(stack, getGunInstance(stack, (EntityPlayer) entity), world, (EntityPlayer) entity, slot, ticks);
                }
            }
        }
    }

    /**
     * Called every tick the left button is held down for firing the gun
     *
     * @param stack  - weapon stack
     * @param gun    - weapon instance
     * @param world  - world fired in
     * @param player - player who is firing the weapon
     * @param slot   - slot the stack is in
     * @param ticks  - how many ticks has the click been held
     */
    public void onLeftClickHeld(ItemStack stack, GunInstance gun, World world, EntityPlayer player, int slot, int ticks)
    {
        gun.fireWeapon(stack, world, ticks);
    }

    /**
     * Called to load the gun instance
     * <p>
     * All instances are cached, and caches are checked
     * before being returned. As long as the stacks match
     * then the cached value is returned.
     *
     * @param stack  - weapon stack
     * @param player - player who will be referenced in the cache value
     * @return gun instance, or null if something goes completely wrong
     */
    public GunInstance getGunInstance(ItemStack stack, EntityPlayer player)
    {
        //TODO check performance against reloaded the stack vs doing NBT check
        if (gunCache.containsKey(player))
        {
            Pair<GunInstance, ItemStack> pair = gunCache.get(player);
            if (pair != null && pair.left() != null && pair.right() != null && InventoryUtility.stacksMatch(pair.right(), stack))
            {
                GunInstance gunInstance = pair.left();
                if (gunInstance.entity == player && gunInstance.gun != null)
                {
                    return gunInstance;
                }
            }
        }
        GunInstance instance = loadInstance(player, getGun(stack), stack.getTagCompound());
        if (instance != null)
        {
            gunCache.put(player, new Pair<GunInstance, ItemStack>(instance, stack));
            return instance;
        }
        return null;
    }

    /**
     * Loads a gun instance from a save
     *
     * @param entity - entity who will use the weapon
     * @param data   - gun data
     * @param tag    - save data
     * @return gun instance
     */
    public GunInstance loadInstance(Entity entity, GunData data, NBTTagCompound tag)
    {
        GunInstance instance = new GunInstance(entity, data);
        instance.load(tag);
        return instance;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
    {
        return true;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float xf, float yf, float zf)
    {
        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        return stack;
    }

    public GunData getGun(ItemStack stack)
    {
        return getGun(stack.getItemDamage());
    }

    public GunData getGun(int meta)
    {
        return metaToGun.containsKey(meta) ? metaToGun.get(meta) : null;
    }

    @Override
    public ItemStack loadAmmo(ItemStack weapon, ItemStack ammo, IAmmoType type, boolean isClip)
    {
        //TODO implement
        return ammo;
    }

    @Override
    public boolean canContainAmmo(ItemStack weapon)
    {
        return true;
    }

    @Override
    public void onPostInit()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        if (event.world.provider.dimensionId == 0 && !initGunData)
        {
            initGunData = true;
            final File save = new File(NBTUtility.getSaveDirectory(), "bbm/armory/gunIndex.json");
            final HashMap<String, Long> keyToWriteTime = new HashMap();

            if (save.exists())
            {
                loadGunDataFromFile(save, keyToWriteTime);
            }
            else
            {
                generateNew();
            }
            saveGunDataToFile(save, keyToWriteTime);
        }
    }

    @SubscribeEvent
    public void onConnect(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (!event.player.worldObj.isRemote && event.player instanceof EntityPlayerMP)
        {
            sendSyncPacket((EntityPlayerMP) event.player);
        }
    }

    /**
     * Inits the meta with new data. Only used
     * for first time installs or error correctly.
     */
    public static void generateNew()
    {
        int meta = 0;
        for (Map.Entry<String, GunData> entry : ArmoryDataHandler.GUN_DATA.entrySet())
        {
            metaToGun.put(meta, entry.getValue());
            meta += 1;
        }
    }

    /**
     * Gets the gun meta data as a json object. Used for both saving and
     * client packet syncing
     *
     * @param keyToWriteTime - when the keys where written last, used for legacy loading when mods are disabled
     * @return data as a json object
     */
    public static JsonObject getGunDataAsJson(HashMap<String, Long> keyToWriteTime)
    {
        JsonObject object = new JsonObject();
        JsonArray array = new JsonArray();
        object.add("guns", array);

        for (Map.Entry<Integer, GunData> entry : metaToGun.entrySet())
        {
            JsonObject obj = new JsonObject();
            obj.add("ID", new JsonPrimitive(entry.getValue().ID));
            obj.add("meta", new JsonPrimitive(entry.getKey()));
            obj.add("writeTime", new JsonPrimitive(keyToWriteTime.containsKey(entry.getValue().ID) ? keyToWriteTime.get(entry.getValue().ID) : System.currentTimeMillis()));
            array.add(obj);
        }
        return object;
    }

    /**
     * Saves the gun data to the save folder so it can be loaded
     * next time the game is run.
     *
     * @param save           - location to save the file
     * @param keyToWriteTime - when the keys where written last, used for legacy loading when mods are disabled
     */
    public static void saveGunDataToFile(File save, HashMap<String, Long> keyToWriteTime)
    {
        try (FileWriter file = new FileWriter(save))
        {
            file.write(getGunDataAsJson(keyToWriteTime).toString());
        }
        catch (Exception e)
        {
            Armory.INSTANCE.logger().error("Failed to write gun data to save folder [" + save + "]", e);
        }
    }

    /**
     * Loads gun data from file so meta values are consistent between mod changes.
     *
     * @param save           - location to save the file
     * @param keyToWriteTime - when the keys where written last, used for legacy loading when mods are disabled
     */
    public static void loadGunDataFromFile(File save, HashMap<String, Long> keyToWriteTime)
    {
        int slotSearchIndex = 0;
        HashMap<Integer, String> metaToKeyMap = new HashMap();
        HashMap<String, Integer> keyToMetaMap = new HashMap();
        try (FileReader stream = new FileReader(save))
        {
            JsonReader jsonReader = new JsonReader(new BufferedReader(stream));
            JsonObject data = Streams.parse(jsonReader).getAsJsonObject();
            JsonArray array = data.get("guns").getAsJsonArray();
            for (int i = 0; i < array.size(); i++)
            {
                JsonObject element = array.get(i).getAsJsonObject();
                Long lastWriteTime = element.getAsJsonPrimitive("writeTime").getAsLong();
                Long delta = System.currentTimeMillis() - lastWriteTime;
                String name = element.getAsJsonPrimitive("ID").getAsString();
                //If saved X many days ago and not contains then do not store the data (Del in other words)
                if (delta < saveTimeLimit || ArmoryDataHandler.getGunData(name) != null)
                {
                    int meta = element.getAsJsonPrimitive("meta").getAsInt();
                    metaToKeyMap.put(meta, name);
                    keyToMetaMap.put(name, meta);
                    keyToWriteTime.put(name, lastWriteTime);
                }
            }
        }
        catch (Exception e)
        {
            Armory.INSTANCE.logger().error("Failed to load gun data from save folder [" + save + "]", e);
        }

        //Loop threw existing entries and match them to known gun data
        for (Map.Entry<String, GunData> entry : ArmoryDataHandler.GUN_DATA.entrySet())
        {
            //Data is already mapped so add to meta values
            if (keyToMetaMap.containsKey(entry.getKey()))
            {
                metaToGun.put(keyToMetaMap.get(entry.getKey()), entry.getValue());
            }
            //New data, find a free slot to use
            else
            {
                while (true)
                {
                    if (!metaToKeyMap.containsKey(slotSearchIndex))
                    {
                        metaToKeyMap.put(slotSearchIndex, entry.getKey());
                        keyToMetaMap.put(entry.getKey(), slotSearchIndex);
                        metaToGun.put(slotSearchIndex, ArmoryDataHandler.getGunData(entry.getKey()));
                        slotSearchIndex++;
                        break;
                    }
                    slotSearchIndex++;
                }
            }
        }
    }

    @Override
    public void read(ByteBuf buf, EntityPlayer player, PacketType packet)
    {
        int size = buf.readInt();
        if (size > 0)
        {
            metaToGun.clear();
            for (int i = 0; i < size; i++)
            {
                metaToGun.put(buf.readInt(), ArmoryDataHandler.getGunData(ByteBufUtils.readUTF8String(buf)));
            }
        }
    }

    public void sendSyncPacket(EntityPlayerMP player)
    {
        PacketPlayerItem packet = new PacketPlayerItem(Item.getIdFromItem(this) * -1);
        packet.data().writeInt(metaToGun.size());
        for (Map.Entry<Integer, GunData> entry : metaToGun.entrySet())
        {
            packet.data().writeInt(entry.getKey());
            ByteBufUtils.writeUTF8String(packet.data(), entry.getValue().ID);
        }
        Engine.instance.packetHandler.sendToPlayer(packet, player);
    }

    @Override
    public boolean shouldReadPacket(EntityPlayer player, IWorldPosition receiveLocation, PacketType packet)
    {
        return player.worldObj.isRemote;
    }
}
