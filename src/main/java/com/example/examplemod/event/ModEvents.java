package com.example.examplemod.event;

import com.example.examplemod.Dailyask;
import com.example.examplemod.core.AskSystem;
import com.example.examplemod.data.DailyAskData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Dailyask.MODID)
public class ModEvents {

    @SubscribeEvent
    public static void tick(TickEvent.PlayerTickEvent event) {

        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        ServerLevel level = player.serverLevel();
        DailyAskData data = DailyAskData.get(level);

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);

        boolean hasEnch =
                EnchantmentHelper.getItemEnchantmentLevel(
                        Dailyask.DAILY_QUESTION.get(),
                        helmet
                ) > 0;

        if (!hasEnch) return;

        AskSystem.giveHelmetQuestion(player, level, data);
    }
}