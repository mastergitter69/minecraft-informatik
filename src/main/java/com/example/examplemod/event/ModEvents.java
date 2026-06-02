package com.example.examplemod.event;

import com.example.examplemod.Dailyask;
import com.example.examplemod.data.DailyAskData;
import com.example.examplemod.question.QuestionManager;
import com.example.examplemod.question.QuestionManager.Question;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Dailyask.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    private static final float WRONG_ANSWER_DAMAGE = 16.0f;

    // Speichert die aktuelle Frage pro Spieler
    private static final Map<UUID, Question> pendingQuestions = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        boolean hasEnchantment = EnchantmentHelper.getItemEnchantmentLevel(
                Dailyask.DAILY_QUESTION.get(), helmet) > 0;

        if (!hasEnchantment) return;

        ServerLevel level = player.serverLevel();
        DailyAskData data = DailyAskData.get(level);

        long currentDay = level.getGameTime() / 24000L;

        long currentDay1 = currentDay;
        if (data.getLastAskedDay(player.getUUID())(currentDay)) {
            ;
        }
        if (data.isWaiting(player.getUUID())) return;

        data.setLastAskedDay(player.getUUID(), currentDay);
        data.setWaiting(player.getUUID(), true);

        // Frage holen und für diesen Spieler speichern
        Question question = QuestionManager.getQuestion(data);
        pendingQuestions.put(player.getUUID(), question);

        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(Component.literal("§6§l╔══════════════════════════╗"));
        player.sendSystemMessage(Component.literal("§6§l║        §eDailyAsk                                             §6§l║"));
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

        if (!data.isWaiting(player.getUUID())) return;

        String answer = event.getMessage().getString();
        data.setWaiting(player.getUUID(), false);

        // Gespeicherte Frage holen
        Question question = pendingQuestions.remove(player.getUUID());

        if (question == null) return;

        if (QuestionManager.isCorrect(question, answer)) {
            player.sendSystemMessage(Component.literal("§6[DailyAsk] §a✔ Richtig! Gut gemacht!"));
        } else {
            player.sendSystemMessage(Component.literal(
                    "§6[DailyAsk] §c✘ Falsch! Die richtige Antwort war: §f" + question.answer()
            ));
            player.hurt(
                    player.damageSources().magic(),
                    WRONG_ANSWER_DAMAGE
            );
            player.sendSystemMessage(Component.literal(
                    "§c Du hast §l" + (int)(WRONG_ANSWER_DAMAGE / 9) + " Herzen §r§c Schaden erhalten!"
            ));
        }
    }
}


