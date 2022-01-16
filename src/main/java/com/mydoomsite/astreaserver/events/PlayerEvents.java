package com.mydoomsite.astreaserver.events;

import java.util.Collection;

import com.mydoomsite.astreaserver.helpers.PlayerHelper;
import com.mydoomsite.astreaserver.lib.Constants;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.NameFormat;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class PlayerEvents
{
    @SubscribeEvent
    public void OnNameFormat(NameFormat event)
    {
        if(PlayerHelper.IsSuperAdmin(event.getPlayer().getUUID()))
        {
            event.setDisplayname(new TextComponent("\u00A7a" + event.getDisplayname().getContents() + "\u00A7r"));
        }
        else if(PlayerHelper.IsOp(event.getPlayer()))
        {
            event.setDisplayname(new TextComponent("\u00A7e" + event.getDisplayname().getContents() + "\u00A7r"));
        }
    }
    
    @SubscribeEvent
    public void OnDeathDrop(LivingDropsEvent event)
    {
        if(!(event.getEntityLiving() instanceof Player))
            return;
        
        Collection<ItemEntity> drops = event.getDrops();
        for(ItemEntity item : drops)
        {
            item.lifespan = 10 * Constants.TICKS_MINUTE; // TODO: Add configuration file.
        }
    }
}
