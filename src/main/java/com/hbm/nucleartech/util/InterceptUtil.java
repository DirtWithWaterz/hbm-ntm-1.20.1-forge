package com.hbm.nucleartech.util;

import com.hbm.nucleartech.HBM;
import com.hbm.nucleartech.block.RegisterBlocks;
import com.hbm.nucleartech.hazard.HazardItem;
import com.hbm.nucleartech.item.RegisterItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

@Mod.EventBusSubscriber(modid = HBM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class InterceptUtil {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {

        if (event.getLevel().isClientSide()) return; // server only

        BlockState state = event.getLevel().getBlockState(event.getPos());
        if (state.is(Tags.Blocks.ORES_COAL)) {
            ServerLevel server = (ServerLevel) event.getLevel();
            BlockPos pos = event.getPos();

            for(Direction d : Direction.values()) {

                var rel = pos.relative(d);

                if(server.getBlockState(rel).isAir())
                    server.setBlock(rel, RegisterBlocks.COAL_AIR.get().defaultBlockState(), 3);
            }

            server.setBlock(pos, RegisterBlocks.COAL_AIR.get().defaultBlockState(), 3);
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {

        if(event.getEntity().level().isClientSide) return;

        BlockState state = event.getState();
        if(state.is(RegisterBlocks.ASBESTOS_ORE.get()) || state.is(RegisterBlocks.DEEPSLATE_ASBESTOS_ORE.get())) {

            event.getPosition().ifPresent(pos -> {

                ServerLevel server = (ServerLevel) event.getEntity().level();
                if (server.random.nextInt(100) > 60) return;


                for (Direction d : Direction.values()) {

                    if (server.random.nextInt(100) > 50) continue;

                    var rel = pos.relative(d);

                    if (server.getBlockState(rel).isAir())
                        server.setBlock(rel, RegisterBlocks.ASBESTOS_AIR.get().defaultBlockState(), 3);
                }
            });
        }
    }

    private static final DeferredRegister<Item> VANILLA_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, "minecraft");

    public static final RegistryObject<Item> COAL_REPLACEMENT =
            VANILLA_ITEMS.register("coal", () ->
                    // instantiate your HazardItem here — copy properties you need
                    new HazardItem(0, 0, 0, 0, 0, 0, 1, new Item.Properties().stacksTo(64))
            );

    public static void registerVanillaItemIntercepts(IEventBus modBus) {
        VANILLA_ITEMS.register(modBus);
    }

    @SubscribeEvent
    public static void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        ItemStack stack = event.getItemStack();

        // if it's your custom item
        if (stack.getItem() == RegisterItems.BIOMASS.get()) {
            // burn time in ticks (20 ticks = 1 second)
            event.setBurnTime(800); // e.g. same as coal (80s)
            // returning here prevents later handlers clobbering it
            return;
        }

        // you can also test tags:
        // if (stack.is(YourTags.Items.MY_FUEL_TAG)) event.setBurnTime(...);
    }
}
