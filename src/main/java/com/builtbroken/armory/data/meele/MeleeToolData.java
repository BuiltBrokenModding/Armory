package com.builtbroken.armory.data.meele;

import com.builtbroken.armory.api.ArmoryAPI;
import com.builtbroken.armory.data.WeaponData;
import com.builtbroken.mc.framework.json.data.JsonBlockEntry;
import com.builtbroken.mc.framework.json.imp.IJsonProcessor;
import com.builtbroken.mc.framework.json.imp.JsonLoadPhase;
import com.builtbroken.mc.framework.json.loading.JsonProcessorData;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import java.util.HashMap;
import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/26/2017.
 */
public class MeleeToolData extends WeaponData
{
    protected int enchantability = 0;
    protected int durability = 250;
    protected int blockBreakDamage = 1;
    protected int useDuration = 0;

    protected boolean doesToolTakeDamage = true;

    /** Cache of block references to be triggered after blocks are loaded */
    protected HashMap<JsonBlockEntry, Float> _blockToBreakSpeed = new HashMap();

    /** Breakable blocks to speed */
    protected HashMap<Block, Float> blockToBreakSpeed = new HashMap();
    /** Breakable materials to speed */
    protected HashMap<Material, Float> materialToBreakSpeed = new HashMap();

    public MeleeToolData(IJsonProcessor processor, String id, String name)
    {
        super(processor, id, ArmoryAPI.MELEE_TOOL_ID, name);
    }

    public MeleeToolData(IJsonProcessor processor, String id, String type, String name)
    {
        super(processor, id, type, name);
    }


    public int getEnchantability()
    {
        return enchantability;
    }

    @JsonProcessorData(value = "enchantability", type = "int")
    public void setEnchantability(int value)
    {
        this.enchantability = value;
    }


    public boolean doesToolTakeDamage()
    {
        return doesToolTakeDamage;
    }

    @JsonProcessorData("takesDamage")
    public void setDoesToolTakeDamage(boolean b)
    {
        this.doesToolTakeDamage = b;
    }

    public int getToolDamageLimit()
    {
        return durability;
    }

    @JsonProcessorData(value = "durability", type = "int")
    public void setToolDamageLimit(int durability)
    {
        this.durability = durability;
    }


    public int getUseDuration()
    {
        return useDuration;
    }

    @JsonProcessorData(value = "useDuration", type = "int")
    public void setUseDuration(int useDuration)
    {
        this.useDuration = useDuration;
    }

    public float getBreakSpeed(Block block)
    {
        if (block != null)
        {
            //Check block
            if (blockToBreakSpeed.containsKey(block))
            {
                return blockToBreakSpeed.get(block);
            }

            //Check against material
            Material material = block.getMaterial();
            if (materialToBreakSpeed.containsKey(material))
            {
                return materialToBreakSpeed.get(material);
            }
            //TODO add property and ore name checks
        }
        return 0;
    }

    @JsonProcessorData(value = "blocksToBreak", type = "HashMap", args = {"block", "block", "speed", "float"})
    public void loadBlockBreakData(HashMap map)
    {
        map.entrySet().forEach(e ->
                setBreakSpeed((JsonBlockEntry) ((Map.Entry) e).getKey(), (float) ((Map.Entry) e).getValue())
        );
    }

    @JsonProcessorData(value = "materialsToBreak", type = "HashMap", args = {"material", "material", "speed", "float"})
    public void loadMaterialsBreakData(HashMap map)
    {
        map.entrySet().forEach(e ->
                setBreakSpeed((Material) ((Map.Entry) e).getKey(), (float) ((Map.Entry) e).getValue())
        );
    }

    public void setBreakSpeed(JsonBlockEntry block, float speed)
    {
        this._blockToBreakSpeed.put(block, speed);
    }

    public void setBreakSpeed(Block block, float speed)
    {
        if (block != null)
        {
            this.blockToBreakSpeed.put(block, speed);
        }
    }

    public void setBreakSpeed(Material material, float speed)
    {
        if (material != null)
        {
            this.materialToBreakSpeed.put(material, speed);
        }
    }

    @Override
    public void onPhase(JsonLoadPhase phase)
    {
        if (phase == JsonLoadPhase.COMPLETED)
        {
            for (Map.Entry<JsonBlockEntry, Float> entry : _blockToBreakSpeed.entrySet())
            {
                if (entry.getKey() != null)
                {
                    Block block = entry.getKey().getBlock();
                    if (block != null)
                    {
                        setBreakSpeed(block, entry.getValue());
                    }
                }
            }
        }
    }

    /**
     * Damage taken when a tool breaks a block
     *
     * @return
     */
    public int getDamageTakenBreakingBlocks()
    {
        return blockBreakDamage;
    }

}
