package com.builtbroken.armory.content.items;

import com.builtbroken.armory.api.ArmoryAPI;
import com.builtbroken.armory.content.prefab.ItemMetaArmoryEntry;
import com.builtbroken.armory.data.meele.MeleeToolData;
import com.builtbroken.jlib.data.Colors;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.lib.helper.MaterialDict;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/26/2017.
 */
public class ItemTool<T extends MeleeToolData> extends ItemMetaArmoryEntry<T>
{
    public static final String NBT_TOOL_DAMAGE = "toolDamage";

    public ItemTool(String id, String name, String typeName)
    {
        super(id, name, typeName);
    }

    public ItemTool()
    {
        super("armoryMeleeTool", ArmoryAPI.MELEE_TOOL_ID, ArmoryAPI.MELEE_TOOL_ID);
        this.maxStackSize = 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack tool, EntityPlayer player, List list, boolean b)
    {
        try
        {
            super.addInformation(tool, player, list, b);
            if (Engine.runningAsDev && Engine.isShiftHeld())
            {
                MeleeToolData data = getData(tool);
                if (data != null)
                {
                    list.add("Harvesting Data:");
                    list.add("--Blocks:");
                    data.getBlockToBreakSpeed().forEach((k, v) -> list.add("----" + k + " > " + v));
                    list.add("--Materials:");
                    data.getMaterialToBreakSpeed().forEach((k, v) -> list.add("----" + MaterialDict.getName(k) + " > " + v));
                }
            }
        }
        catch (Exception e)
        {
            list.add(Colors.RED.code + "Error: " + e);
            if (Engine.runningAsDev)
            {
                e.printStackTrace();
            }
        }
    }

    //Break speed of block
    @Override
    public float func_150893_a(ItemStack tool, Block block)
    {
        //Get data
        MeleeToolData data = getData(tool);
        if (data != null)
        {
            return data.getBreakSpeed(block);
        }
        //Default
        return 1;
    }

    @Override
    public boolean hitEntity(ItemStack tool, EntityLivingBase hit, EntityLivingBase attacker)
    {
        boolean takeDamage = true;

        //Get data
        MeleeToolData data = getData(tool);
        if (data != null)
        {
            takeDamage = data.doesToolTakeDamage();
        }

        //Only damage if allowed
        if (takeDamage)
        {
            tool.damageItem(1, attacker);
        }
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack tool, World world, Block block, int x, int y, int z, EntityLivingBase entity)
    {
        int toolDamage = 1;

        //Get data
        MeleeToolData data = getData(tool);
        if (data != null)
        {
            toolDamage = data.getDamageTakenBreakingBlocks();
        }

        if ((double) block.getBlockHardness(world, x, y, z) > 0)
        {
            damageItem(tool, toolDamage, entity);
        }

        return true;
    }

    /**
     * Checks if the item can take damage
     *
     * @param tool
     * @return
     */
    protected boolean canTakeDamage(ItemStack tool)
    {
        MeleeToolData data = getData(tool);
        if (data != null)
        {
            return data.doesToolTakeDamage();
        }
        return false;
    }

    @Override
    public boolean isDamageable()
    {
        return false; //disable vanilla damage handling
    }

    @Override
    public boolean isRepairable()
    {
        return false; //Can't repair via vanilla methods
    }

    /**
     * Called to damage the item
     *
     * @param tool
     * @param amount
     * @param entity
     */
    public void damageItem(ItemStack tool, int amount, EntityLivingBase entity)
    {
        if (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).capabilities.isCreativeMode)
        {
            if (canTakeDamage(tool))
            {
                if (this.applyDamage(tool, amount))
                {
                    //Render tool breaking
                    entity.renderBrokenItemStack(tool);

                    //Decrease stack size
                    --tool.stackSize;
                    if (tool.stackSize < 0)
                    {
                        tool.stackSize = 0;
                    }

                    //Trigger break events
                    if (entity instanceof EntityPlayer)
                    {
                        EntityPlayer entityplayer = (EntityPlayer) entity;
                        entityplayer.addStat(StatList.objectBreakStats[Item.getIdFromItem(tool.getItem())], 1);
                    }

                    //Reset tool damage, fix for stack size > 1
                    setToolDamage(tool, 0);
                }
            }
        }
    }

    /**
     * Called to damage the item
     *
     * @param tool
     * @param damage - amount to add to damage counter, negative will heal the item
     * @return true if damage hit limit
     */
    protected boolean applyDamage(ItemStack tool, int damage)
    {
        //get damage
        int currentDamage = getToolDamage(tool);
        //Reduce
        currentDamage += damage;
        //Lower limit
        currentDamage = Math.max(0, currentDamage);
        //Upper limit
        currentDamage = Math.min(currentDamage, getToolDamageLimit(tool));
        //Update stack
        setToolDamage(tool, damage);
        //return true if damage is zero
        return currentDamage >= getToolDamageLimit(tool);
    }

    protected int getToolDamageLimit(ItemStack tool)
    {
        MeleeToolData data = getData(tool);
        if (data != null)
        {
            return data.getToolDamageLimit();
        }
        return Integer.MAX_VALUE;
    }

    /**
     * Sets the item's damage
     *
     * @param tool
     * @param damage
     */
    protected void setToolDamage(ItemStack tool, int damage)
    {
        if (tool.getTagCompound() == null)
        {
            tool.setTagCompound(new NBTTagCompound());
        }
        tool.getTagCompound().setInteger(NBT_TOOL_DAMAGE, damage);
    }

    /**
     * Gets the item's damage
     *
     * @param tool
     * @return
     */
    protected int getToolDamage(ItemStack tool)
    {
        if (tool.getTagCompound() != null)
        {
            return tool.getTagCompound().getInteger(NBT_TOOL_DAMAGE);
        }
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack tool, World world, EntityPlayer player)
    {
        player.setItemInUse(tool, this.getMaxItemUseDuration(tool));
        return tool;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        //Collect duration from super (listeners)
        int duration = super.getMaxItemUseDuration(stack);

        //Check if tool duration is higher, is yes user it instead
        MeleeToolData data = getData(stack);
        if (data != null && data.getUseDuration() > duration)
        {
            return data.getUseDuration();
        }
        return duration;
    }

    @Override
    public boolean canHarvestBlock(Block block, ItemStack tool)
    {
        //Get data
        MeleeToolData data = getData(tool);
        if (data != null)
        {
            return data.getBreakSpeed(block) > 0;
        }
        return func_150897_b(block);
    }

    //Can harvest block
    @Override
    public boolean func_150897_b(Block block)
    {
        return false; //no tool to check against, so default to false
    }

    @Override
    public int getItemEnchantability(ItemStack stack)
    {
        MeleeToolData data = getData(stack);
        if (data != null)
        {
            return data.getEnchantability();
        }
        return 0;
    }
}
