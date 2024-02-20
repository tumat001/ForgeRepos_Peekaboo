package net.evn.peekaboomod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.level.BlockEvent;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(PeekabooMod.MOD_ID)
public class PeekabooMod
{
    // Define mod id in a common place for everything to reference
    // only lower case char, number, underscore, or dash
    public static final String MOD_ID = "peekaboo_evn_mod";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final StoreOfGameProbabilities store_of_game_probabilities = new StoreOfGameProbabilities();

    public PeekabooMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);


        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        //modEventBus.addListener(this::HarvestDropsEvent);
        MinecraftForge.EVENT_BUS.addListener(this::HarvestDropsEvent);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {

    }


    public void HarvestDropsEvent(BlockEvent.BreakEvent event) {
        event.setExpToDrop(event.getExpToDrop() + 20);
        store_of_game_probabilities.rollChanceOnBreak_SummonPlaceOrDoEvent(event);
    }


}
