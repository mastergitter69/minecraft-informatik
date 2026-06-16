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

/**
 * Registriert alle Commands des DailyAsk-Systems.
 *
 * Verfügbare Commands:
 * - /dailyask         → Startet eine neue Frage
 * - /answer <text>   → Beantwortet die aktuell aktive Frage
 *
 * Die Commands werden automatisch beim Serverstart
 * über den Forge EventBus registriert.
 */
@Mod.EventBusSubscriber
public class DailyAskCommands {

    /**
     * Wird von Forge beim Registrieren aller Commands aufgerufen.
     *
     * @param event Command-Registrierungs-Event
     */
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {

        // =======================================================
        // /dailyask
        // =======================================================
        //
        // Vergibt dem Spieler eine neue Frage.
        // Nutzt dieselbe Logik wie das Helm-System.
        //
        event.getDispatcher().register(
                Commands.literal("dailyask")
                        .executes(ctx -> {

                            // Spieler abrufen
                            ServerPlayer player =
                                    ctx.getSource().getPlayer();

                            // Aktuelle Welt abrufen
                            ServerLevel level =
                                    player.serverLevel();

                            // Gespeicherte Mod-Daten laden
                            DailyAskData data =
                                    DailyAskData.get(level);

                            // Neue Frage vergeben
                            AskSystem.giveHelmetQuestion(
                                    player,
                                    level,
                                    data
                            );

                            return 1;
                        })
        );


        // =======================================================
        // /answer <text>
        // =======================================================
        //
        // Prüft die Antwort des Spielers auf die aktuell
        // aktive Frage.
        //
        event.getDispatcher().register(
                Commands.literal("answer")

                        // Gesamte Eingabe als Antwort übernehmen
                        .then(
                                Commands.argument(
                                                "text",
                                                StringArgumentType.greedyString()
                                        )
                                        .executes(ctx -> {

                                            // Spieler abrufen
                                            ServerPlayer player =
                                                    ctx.getSource().getPlayer();

                                            // Welt abrufen
                                            ServerLevel level =
                                                    player.serverLevel();

                                            // Eingegebene Antwort lesen
                                            String msg =
                                                    StringArgumentType.getString(
                                                            ctx,
                                                            "text"
                                                    );

                                            UUID uuid =
                                                    player.getUUID();

                                            // Aktive Frage des Spielers abrufen
                                            QuestionManager.Question q =
                                                    AskSystem.getActive(uuid);

                                            // Keine aktive Frage vorhanden
                                            if (q == null) {

                                                player.sendSystemMessage(
                                                        Component.literal(
                                                                "Keine Frage aktiv!"
                                                        )
                                                );

                                                return 1;
                                            }

                                            // Persistente Daten laden
                                            DailyAskData data =
                                                    DailyAskData.get(level);

                                            // Antwort überprüfen
                                            if (QuestionManager.isCorrect(q, msg)) {

                                                // Richtige Antwort
                                                player.sendSystemMessage(
                                                        Component.literal(
                                                                "§aRichtig!"
                                                        )
                                                );

                                                // Antwortstatus zurücksetzen
                                                data.setWaiting(uuid, false);

                                                // Aktive Frage entfernen
                                                AskSystem.clearActive(uuid);

                                            } else {

                                                // Falsche Antwort anzeigen
                                                player.sendSystemMessage(
                                                        Component.literal(
                                                                "§cFalsch! Richtige Antwort: "
                                                                        + q.answer()
                                                        )
                                                );

                                                // Spieler bestrafen
                                                player.hurt(
                                                        player.damageSources().magic(),
                                                        16f
                                                );
                                            }

                                            return 1;
                                        })
                        )
        );
    }
}