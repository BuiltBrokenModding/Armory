package com.builtbroken.armory.data.meele;

import com.builtbroken.armory.data.WeaponData;
import com.builtbroken.mc.framework.json.imp.IJsonProcessor;
import com.builtbroken.mc.framework.json.loading.JsonProcessorData;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/26/2017.
 */
public class MeleeToolData extends WeaponData
{
    protected int enchantability = 0;
    protected int toolDamageLimit = Item.ToolMaterial.IRON.getMaxUses();
    protected int blockBreakDamage = 1;

    protected HashMap<Block, Float> blockToBreakSpeed = new HashMap();
    protected HashMap<Material, Float> materialToBreakSpeed = new HashMap();


    public MeleeToolData(IJsonProcessor processor, String id, String type, String name)
    {
        super(processor, id, type, name);
    }

    @JsonProcessorData(value = "enchantability", type = "int")
    public void setEnchantability(int value)
    {
        this.enchantability = value;
    }

    public int getEnchantability()
    {
        return enchantability;
    }

    public boolean doesWeaponTakeDamage()
    {
        return true; //TODO implement
    }

    public int getToolDamageLimit()
    {
        return toolDamageLimit;
    }

    public float getBreakSpeed(Block block)
    {
        if(block != null)
        {
            //Check block
            if (blockToBreakSpeed.containsKey(block))
            {
                return blockToBreakSpeed.get(block);
            }

            //Check against material
            Material material = block.getMaterial();
            if(materialToBreakSpeed.containsKey(material))
            {
                return materialToBreakSpeed.get(material);
            }
            //TODO add property and ore name checks
        }
        return 0;
    }

    /**
     * Damage taken when a tool breaks a block
     * @return
     */
    public int getDamageTakenBreakingBlocks()
    {
        return blockBreakDamage;
    }
}
