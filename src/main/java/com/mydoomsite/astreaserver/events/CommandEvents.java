package com.mydoomsite.astreaserver.events;

import com.mojang.brigadier.CommandDispatcher;
import com.mydoomsite.astreaserver.commands.*;

import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class CommandEvents
{
    @SubscribeEvent
    public void RegisterCommands(final RegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        
        ProtectRegionCommand.register(dispatcher);
        EndProtectCommand.register(dispatcher);
        CancelProtectCommand.register(dispatcher);
        UnprotectRegionCommand.register(dispatcher);
        RegionInfoCommand.register(dispatcher);
        RegionPlayersCommand.register(dispatcher);
        ReloadProtectedRegionsCommand.register(dispatcher);
        RegionTrustCommand.register(dispatcher);
        RegionDistrustCommand.register(dispatcher);
        MakeSuperAdminCommand.register(dispatcher);
        RemoveSuperAdminCommand.register(dispatcher);
        RegionProtectionLevelCommand.register(dispatcher);
    }
}
