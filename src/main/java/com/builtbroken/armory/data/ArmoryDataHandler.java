package com.builtbroken.armory.data;

import com.builtbroken.armory.Armory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Handles, stores, and offers access all data processed by the mod
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class ArmoryDataHandler
{
    /** Limit on how long to let a slot be used when a mod was uninstalled */
    private static final Long saveTimeLimit = TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS);
    /** All data sets used to hold information about each ArmoryEntry set */
    public static final HashMap<String, ArmoryData> DATA = new HashMap();
    /** Primary instance for Armory data */
    public static final ArmoryDataHandler INSTANCE = new ArmoryDataHandler();

    /**
     * Gets the ArmoryData set
     *
     * @param name - name of the set, should match
     *             entry type
     * @return set if one was registered
     */
    public ArmoryData get(String name)
    {
        return DATA.get(name);
    }

    /**
     * Adds a new data set
     *
     * @param data - set
     */
    public void add(ArmoryData data)
    {
        DATA.put(data.name, data);
    }

    /**
     * Adds an entry to a data set
     *
     * @param name  - name of the set
     * @param entry - entry
     * @param <E>   - set class, will throw casting exception if type does not match
     */
    public <E extends ArmoryEntry> void add(String name, E entry)
    {
        if (DATA.get(name) != null)
        {
            DATA.get(name).add(entry);
        }
        else
        {
            throw new RuntimeException("No data set is registered for name " + name);
        }
    }

    /**
     * Class used to store data for an armory entry set
     *
     * @param <E> - set class
     */
    public static class ArmoryData<E extends ArmoryEntry> extends HashMap<String, E>
    {
        /** Name of the data set */
        public final String name;
        /** Where data is saved to be loaded next time the game is loaded */
        public final File save;

        /** Metadata values used to store the entries in an item stack */
        public final HashMap<Integer, E> metaToEntry = new HashMap();

        /** Items that will be used to convert the entry into item stacks */
        private List<Item> items = new ArrayList();
        private boolean hasInit = false;

        public ArmoryData(File saveFolder, String name)
        {
            this.name = name;
            save = new File(saveFolder, "armory/" + name + "Index.json");
        }

        public void add(E entry)
        {
            put(entry.ID, entry);
        }

        public void add(Item item)
        {
            if (!items.contains(item))
            {
                items.add(item);
            }
        }

        public E get(String key)
        {
            return super.get(key);
        }

        /**
         * Called to load or init data from file.
         * <p>
         * Not all items use this load method. Some may choose
         * not to use a meta dependent solution.
         */
        public void init(Item item)
        {
            if (!hasInit)
            {
                hasInit = true;
                final HashMap<String, Long> keyToWriteTime = new HashMap();

                if (save.exists())
                {
                    loadDataFromFile(keyToWriteTime);
                }
                else
                {
                    int meta = 0;
                    for (Map.Entry<String, E> entry : entrySet())
                    {
                        metaToEntry.put(meta, entry.getValue());
                        meta += 1;
                    }
                }
                saveDataToFile(keyToWriteTime);

                //TODO rewrite the bellow to not be O(items * 32000)
                int index = 0;
                for (int i = 0; i < items.size(); i++)
                {
                    for (; index < (i + 1) * 32000; index++)
                    {
                        if (metaToEntry.containsKey(index))
                        {
                            metaToEntry.get(index).set(item, index);
                        }
                    }
                }
            }
        }

        public void readBytes(ByteBuf buf)
        {
            int size = buf.readInt();
            if (size > 0)
            {
                metaToEntry.clear();
                for (int i = 0; i < size; i++)
                {
                    metaToEntry.put(buf.readInt(), get(ByteBufUtils.readUTF8String(buf)));
                }
            }
        }

        public void writeBytes(ByteBuf buf)
        {
            buf.writeInt(metaToEntry.size());
            for (Map.Entry<Integer, E> entry : metaToEntry.entrySet())
            {
                buf.writeInt(entry.getKey());
                ByteBufUtils.writeUTF8String(buf, entry.getValue().ID);
            }
        }

        /**
         * Gets the gun meta data as a json object. Used for both saving and
         * client packet syncing
         *
         * @param keyToWriteTime - when the keys where written last, used for legacy loading when mods are disabled
         * @return data as a json object
         */
        public JsonObject getDataAsJson(HashMap<String, Long> keyToWriteTime)
        {
            JsonObject object = new JsonObject();
            JsonArray array = new JsonArray();
            object.add(name, array);

            for (Map.Entry<Integer, E> entry : metaToEntry.entrySet())
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
         * @param keyToWriteTime - when the keys where written last, used for legacy loading when mods are disabled
         */
        public void saveDataToFile(HashMap<String, Long> keyToWriteTime)
        {
            if (!save.getParentFile().exists())
            {
                save.getParentFile().mkdirs();
            }
            try (FileWriter file = new FileWriter(save))
            {
                file.write(getDataAsJson(keyToWriteTime).toString());
            }
            catch (Exception e)
            {
                //JUnit testing
                if (Armory.INSTANCE == null)
                {
                    throw new RuntimeException(e);
                }
                Armory.INSTANCE.logger().error("Failed to write gun data to save folder [" + save + "]", e);
            }
        }

        /**
         * Loads gun data from file so meta values are consistent between mod changes.
         *
         * @param keyToWriteTime - when the keys where written last, used for legacy loading when mods are disabled
         */
        public void loadDataFromFile(HashMap<String, Long> keyToWriteTime)
        {
            int slotSearchIndex = 0;
            HashMap<Integer, String> metaToKeyMap = new HashMap();
            HashMap<String, Integer> keyToMetaMap = new HashMap();
            try (FileReader stream = new FileReader(save))
            {
                JsonReader jsonReader = new JsonReader(new BufferedReader(stream));
                JsonObject data = Streams.parse(jsonReader).getAsJsonObject();
                JsonArray array = data.get(name).getAsJsonArray();
                for (int i = 0; i < array.size(); i++)
                {
                    JsonObject element = array.get(i).getAsJsonObject();
                    Long lastWriteTime = element.getAsJsonPrimitive("writeTime").getAsLong();
                    Long delta = System.currentTimeMillis() - lastWriteTime;
                    String name = element.getAsJsonPrimitive("ID").getAsString();
                    //If saved X many days ago and not contains then do not store the data (Del in other words)
                    if (delta < saveTimeLimit || get(name) != null)
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
                //JUnit testing
                if (Armory.INSTANCE == null)
                {
                    throw new RuntimeException(e);
                }
                Armory.INSTANCE.logger().error("Failed to load " + name + " data from save folder [" + save + "]", e);
            }

            //Loop threw existing entries and match them to known gun data
            for (Map.Entry<String, E> entry : entrySet())
            {
                //Data is already mapped so add to meta values
                if (keyToMetaMap.containsKey(entry.getKey()))
                {
                    metaToEntry.put(keyToMetaMap.get(entry.getKey()), entry.getValue());
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
                            metaToEntry.put(slotSearchIndex, get(entry.getKey()));
                            slotSearchIndex++;
                            break;
                        }
                        slotSearchIndex++;
                    }
                }
            }
        }
    }
}
