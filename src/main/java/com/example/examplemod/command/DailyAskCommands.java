package com.example.examplemod.command;

import com.example.examplemod.core.AskSystem;
import com.example.examplemod.data.DailyAskData;
import com.example.examplemod.question.QuestionManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

// Commands: Spieler kann Fragen manuell starten und beantworten
@Mod.EventBusSubscriber
public class DailyAskCommands {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {

        // /dailyask → manuell Frage bekommen
        event.getDispatcher().register(
                Commands.literal("dailyask")
                        .executes(ctx -> {

                            ServerPlayer player = ctx.getSource().getPlayer();
                            ServerLevel level = player.serverLevel();

                            DailyAskData data = DailyAskData.get(level);

                            AskSystem.giveHelmetQuestion(player, level, data);

                            return 1;
                        })
        );

        // /answer <text> → Antwort prüfen
        event.getDispatcher().register(
                Commands.literal("answer")
                        .then(Commands.argument("text", StringArgumentType.greedyString())
                                .executes(ctx -> {

                                    ServerPlayer player = ctx.getSource().getPlayer();
                                    ServerLevel level = player.serverLevel();

                                    String msg = StringArgumentType.getString(ctx, "text");

                                    UUID uuid = player.getUUID();

                                    QuestionManager.Question q =
                                            AskSystem.getActive(uuid);

                                    if (q == null) {
                                        player.sendSystemMessage(Component.literal("Keine Frage aktiv!"));
                                        return 1;
                                    }

                                    DailyAskData data = DailyAskData.get(level);
                                    if (QuestionManager.isCorrect(q, msg)) {
                                        player.sendSystemMessage(Component.literal("§aRichtig!"));
                                        data.setWaiting(uuid, false);
                                        AskSystem.clearActive(uuid);
                                    } else {
                                        player.sendSystemMessage(Component.literal("§cFalsch! " + q.answer()));
                                        player.hurt(player.damageSources().magic(), 16f);
                                    }

                                    return 1;
                                }))  // ← added ))
        );
    }
}