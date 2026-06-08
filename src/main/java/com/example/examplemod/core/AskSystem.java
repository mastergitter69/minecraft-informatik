package com.example.examplemod.core;

import com.example.examplemod.data.DailyAskData;
import com.example.examplemod.question.QuestionManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Zentrale Logik ohne Bug-States
public class AskSystem {

    // nur RAM für aktuelle Frage
    private static final Map<UUID, QuestionManager.Question> active = new HashMap<>();

    // HELM: 1x pro Tag
    public static void giveHelmetQuestion(ServerPlayer player, ServerLevel level, DailyAskData data) {

        UUID uuid = player.getUUID();
        long day = level.getDayTime() / 24000L;

        // blockt nur Helm spam
        if (data.getLastHelmetAsk(uuid) == day) return;

        data.setLastHelmetAsk(uuid, day);

        QuestionManager.Question q = QuestionManager.getQuestion(data);

        active.put(uuid, q);
        data.setActiveQuestion(uuid, q.question());
        data.setWaiting(uuid, true);

        player.sendSystemMessage(Component.literal("§e[DailyAsk] " + q.question()));
    }

    // COMMAND / BUCH: unbegrenzt
    public static void giveUnlimitedQuestion(ServerPlayer player, ServerLevel level, DailyAskData data) {

        UUID uuid = player.getUUID();

        QuestionManager.Question q = QuestionManager.getQuestion(data);

        active.put(uuid, q);
        data.setActiveQuestion(uuid, q.question());
        data.setWaiting(uuid, true);

        player.sendSystemMessage(Component.literal("§e[Practice] " + q.question()));
    }

    public static QuestionManager.Question getActive(UUID uuid) {
        return active.get(uuid);
    }

    public static void clearActive(UUID uuid) {
        active.remove(uuid);
    }
}