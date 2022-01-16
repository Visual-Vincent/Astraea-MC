package com.mydoomsite.astreaserver.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mydoomsite.astreaserver.events.BlockEvents;
import com.mydoomsite.astreaserver.events.CommandEvents;
import com.mydoomsite.astreaserver.events.EntityEvents;
import com.mydoomsite.astreaserver.events.ItemEvents;
import com.mydoomsite.astreaserver.events.PlayerEvents;
import com.mydoomsite.astreaserver.events.ServerEvents;
import com.mydoomsite.astreaserver.helpers.BlockHelper;
import com.mydoomsite.astreaserver.helpers.EntityHelper;
import com.mydoomsite.astreaserver.helpers.PlayerHelper;
import com.mydoomsite.astreaserver.lib.ReferenceStrings;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;

@Mod(ReferenceStrings.MODID)
public final class MainRegistry
{
    public static final Logger Logger = LogManager.getLogger(ReferenceStrings.MODID);
    
    public MainRegistry()
    {
        // Make sure that the client doesn't display the server as "Incompatible" since this mod is server-side only.
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, 
            () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true)
        );
        
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::SetupDedicatedServer);
        });
    }
    
    private void SetupDedicatedServer(final FMLDedicatedServerSetupEvent event)
    {
        BlockHelper.Init();
        EntityHelper.Init();
        
        MinecraftForge.EVENT_BUS.register(new ServerEvents());
        MinecraftForge.EVENT_BUS.register(new CommandEvents());
        MinecraftForge.EVENT_BUS.register(new PlayerHelper());
        MinecraftForge.EVENT_BUS.register(new PlayerEvents());
        MinecraftForge.EVENT_BUS.register(new BlockEvents());
        MinecraftForge.EVENT_BUS.register(new EntityEvents());
        MinecraftForge.EVENT_BUS.register(new ItemEvents());
    }
}
