package com.builtbroken.armory.content.items;

import com.builtbroken.armory.api.ArmoryAPI;
import com.builtbroken.armory.data.damage.DamageData;
import com.builtbroken.armory.data.meele.MeleeWeaponData;
import com.google.common.collect.Multimap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/26/2017.
 */
public class ItemMeleeWeapon extends ItemTool<MeleeWeaponData>
{
    public ItemMeleeWeapon()
    {
        super("armoryMeleeWeapon", ArmoryAPI.MELEE_WEAPON_ID, ArmoryAPI.MELEE_WEAPON_ID);
        this.maxStackSize = 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack tool, EntityPlayer player, List list, boolean b)
    {
        super.addInformation(tool, player, list, b);
        MeleeWeaponData data = getData(tool);
        if (data != null)
        {
            //Apply extra damage
            for (DamageData damageData : data.getExtraDamageToApply())
            {
                if (damageData != null)
                {
                    String string = damageData.getDisplayString(); //handles translations itself
                    if(string != null && !string.isEmpty())
                    {
                        list.add(string);
                    }
                }
            }
        }
    }

    @Override
    public boolean hitEntity(ItemStack tool, EntityLivingBase hit, EntityLivingBase attacker)
    {
        MeleeWeaponData data = getData(tool);
        if (data != null)
        {
            //Apply extra damage
            for (DamageData damageData : data.getExtraDamageToApply())
            {
                if (damageData != null)
                {
                    damageData.onImpact(attacker, hit, hit.posX, hit.posY + (hit.height / 2), hit.posZ, 1, 1); //TODO ray trace hit pos
                }
            }
        }
        return super.hitEntity(tool, hit, attacker);
    }

    @Override
    public Multimap getAttributeModifiers(ItemStack stack)
    {
        Multimap multimap = super.getAttributeModifiers(stack);
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", (double) getDamageVsEntity(stack), 0));
        return multimap;
    }

    public float getDamageVsEntity(ItemStack stack)
    {
        MeleeWeaponData data = getData(stack);
        if (data != null)
        {
            return data.getDamageVsEntity();
        }
        return 1;
    }
}
