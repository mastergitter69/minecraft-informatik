package com.example.examplemod.events;

import com.example.examplemod.Dailyask;
import com.example.examplemod.data.DailyAskData;
import com.example.examplemod.question.QuestionManager;
import com.example.examplemod.question.QuestionManager.Question;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Dailyask.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    // Schaden bei falscher Antwort (in Halben Herzen, 4 = 2 Herzen)
    private static final float WRONG_ANSWER_DAMAGE = 4.0f;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        // Prüfe ob Helm mit Daily Ask Enchantment getragen wird
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        boolean hasEnchantment = EnchantmentHelper.getItemEnchantmentLevel(
                Dailyask.DAILY_QUESTION.get(), helmet) > 0;

        if (!hasEnchantment) return;

        ServerLevel level = player.serverLevel();
        DailyAskData data = DailyAskData.get(level);

        // Aktueller Ingame-Tag (ein Tag = 24000 Ticks, Sonnenaufgang = Tag-Wechsel)
        long currentDay = level.getDayTime() / 24000L;

        // Bereits heute gefragt?
        if (data.getLastAskedDay(player.getUUID()) == currentDay) return;

        // Warte noch auf Antwort von heute? Nicht nochmal fragen
        if (data.isWaiting(player.getUUID())) return;

        // Frage stellen
        data.setLastAskedDay(player.getUUID(), currentDay);
        data.setWaiting(player.getUUID(), true);

        Question question = QuestionManager.getQuestion(currentDay);

        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("§6§l╔══════════════════════════╗"));
        player.sendSystemMessage(Component.literal("§6§l║        §eDailyAsk        ║  §6§l"));
        player.sendSystemMessage(Component.literal("§6§l╚══════════════════════════╝"));
        player.sendSystemMessage(Component.literal("§e❓ §f" + question.question()));
        player.sendSystemMessage(Component.literal("§7Antworte im Chat! §c(Falsche Antwort = Schaden)"));
        player.sendSystemMessage(Component.literal(""));
    }

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        ServerLevel level = player.serverLevel();
        DailyAskData data = DailyAskData.get(level);

        // Wartet dieser Spieler auf eine Antwort?
        if (!data.isWaiting(player.getUUID())) return;

        // Antwort auslesen
        String answer = event.getMessage().getString();
        data.setWaiting(player.getUUID(), false);

        // Aktuelle Frage ermitteln
        long currentDay = level.getDayTime() / 24000L;
        Question question = QuestionManager.getQuestion(currentDay);

        if (QuestionManager.isCorrect(question, answer)) {
            // Richtige Antwort
            player.sendSystemMessage(Component.literal("§6[DailyAsk] §a✔ Richtig! Gut gemacht!"));
        } else {
            // Falsche Antwort → Schaden
            player.sendSystemMessage(Component.literal(
                    "§6[DailyAsk] §c✘ Falsch! Die richtige Antwort war: §f" + question.answer()
            ));
            player.hurt(
                    player.damageSources().magic(),
                    WRONG_ANSWER_DAMAGE
            );
            player.sendSystemMessage(Component.literal(
                    "§c Du hast §l" + (int)(WRONG_ANSWER_DAMAGE / 2) + " Herzen §r§c Schaden erhalten!"
            ));
        }
    }
}
