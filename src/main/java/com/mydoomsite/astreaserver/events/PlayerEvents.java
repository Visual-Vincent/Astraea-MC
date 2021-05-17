package com.mydoomsite.astreaserver.events;

import java.util.Collection;

import com.mydoomsite.astreaserver.lib.Constants;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class PlayerEvents
{
	@SubscribeEvent
	public void OnDrop(LivingDropsEvent event)
	{
		if(!(event.getEntityLiving() instanceof PlayerEntity))
			return;
		
		Collection<ItemEntity> drops = event.getDrops();
		for(ItemEntity item : drops)
		{
			item.lifespan = 10 * Constants.TICKS_MINUTE; // TODO: Add configuration file.
		}
	}
}
