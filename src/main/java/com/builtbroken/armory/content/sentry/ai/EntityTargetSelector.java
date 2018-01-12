package com.builtbroken.armory.content.sentry.ai;

import com.builtbroken.armory.content.sentry.Sentry;
import com.builtbroken.armory.content.sentry.TargetMode;
import com.builtbroken.mc.api.entity.IFoF;
import com.builtbroken.mc.framework.access.AccessProfile;
import com.builtbroken.mc.framework.access.perm.Permissions;
import com.builtbroken.mc.prefab.entity.type.EntityTypeCheck;
import com.builtbroken.mc.prefab.entity.type.EntityTypeCheckRegistry;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 4/23/2017.
 */
public class EntityTargetSelector implements IEntitySelector
{
    public final Sentry sentry;

    public EntityTargetSelector(Sentry sentry)
    {
        this.sentry = sentry;
    }

    @Override
    public boolean isEntityApplicable(Entity entity)
    {
        if (!entity.isDead && !entity.isEntityInvulnerable())
        {
            final AccessProfile profile = sentry.getAccessProfile();

            if (entity instanceof EntityPlayer)
            {
                final EntityPlayer player = (EntityPlayer) entity;
                final TargetMode mode = sentry.targetModes.get("players");

                //Prevents mistakes :P
                if(mode == TargetMode.NONE)
                {
                    return false;
                }

                //ignore owner to prevent user derps
                if (sentry.host.isOwner(player))
                {
                    return false;
                }

                //kill all humans
                if(mode == TargetMode.ALL)
                {
                    return true;
                }

                //Handling for access system, overrides all settings
                if (profile != null)
                {
                    boolean hostile = profile.hasNode(player, Permissions.targetHostile.toString());
                    boolean containsUser = !profile.containsUser(player);
                    if (mode == TargetMode.NOT_FRIEND && hostile)
                    {
                        return true;
                    }
                    else if (mode == TargetMode.NOT_FRIEND && (!containsUser || hostile))
                    {
                        return true;
                    }
                    else if (mode == TargetMode.NEUTRAL && !containsUser)
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
            }

            List<String> keys = new ArrayList();
            keys.add(entity.getClass().getName().replace("class", "").trim());
            keys.add(EntityList.getEntityString(entity));
            if (entity instanceof EntityFlying)
            {
                keys.add("flying");
            }
            if (entity instanceof EntityLivingBase)
            {
                keys.add("living");
            }
            if (entity instanceof EntityPlayer)
            {
                keys.add("players");
            }
            if (entity instanceof INpc)
            {
                keys.add("npcs");
            }
            if (entity instanceof EntityTameable)
            {
                keys.add("tameable");
            }
            if (entity instanceof EntityAnimal)
            {
                keys.add("animals");
            }
            if (entity instanceof IProjectile)
            {
                keys.add("projectile");
            }

            for (EntityTypeCheck checker : EntityTypeCheckRegistry.typeCheckers.values())
            {
                if (checker.isEntityApplicable(entity))
                {
                    keys.add(checker.key);
                }
            }

            for (String key : keys)
            {
                if (sentry.targetModes.containsKey(key))
                {
                    TargetMode mode = sentry.targetModes.get(key);
                    if (mode == TargetMode.ALL)
                    {
                        return true;
                    }
                    else if (mode == TargetMode.HOSTILE || mode == TargetMode.NOT_FRIEND)
                    {
                        return !(entity instanceof IFoF) || sentry.getFoFStation() == null || !sentry.getFoFStation().isFriendly(entity);
                    }
                    else if (mode == TargetMode.NEUTRAL)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
