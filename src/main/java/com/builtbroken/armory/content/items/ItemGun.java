package com.builtbroken.armory.content.items;

import com.builtbroken.armory.Armory;
import com.builtbroken.armory.data.ranged.GunData;
import com.builtbroken.jlib.type.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/16/2016.
 */
public class ItemGun extends ItemWeapon
{
    /** Map of weapons to meta values for fast access */
    public static final HashMap<Integer, GunData> metaToGun = new HashMap();
    /** Cache of the last weapon the entity has out */
    public static final HashMap<Entity, Pair<GunData, ItemStack>> gunCache = new HashMap();
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
        this.setHasSubtypes(true);
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
    {
        if (!entityLiving.worldObj.isRemote)
        {
            if (entityLiving instanceof EntityPlayer)
            {
                ((EntityPlayer) entityLiving).addChatComponentMessage(new ChatComponentText("Bang!!!"));
            }
        }
        return true;
    }

    @Override
    public boolean onItemUse(ItemStack p_77648_1_, EntityPlayer p_77648_2_, World p_77648_3_, int p_77648_4_, int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
    {
        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack p_77659_1_, World p_77659_2_, EntityPlayer p_77659_3_)
    {
        return p_77659_1_;
    }

    public GunData getGun(ItemStack stack)
    {
        return getGun(stack.getItemDamage());
    }

    public GunData getGun(int meta)
    {
        return metaToGun.containsKey(meta) ? metaToGun.get(meta) : null;
    }
}
