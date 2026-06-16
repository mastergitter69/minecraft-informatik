package com.example.examplemod.core;

import com.example.examplemod.data.DailyAskData;
import com.example.examplemod.question.QuestionManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Zentrale Steuerklasse des Fragesystems.
 *
 * Verantwortlich für:
 * - Vergabe von Helm-Fragen (1x pro Minecraft-Tag)
 * - Vergabe unbegrenzter Übungsfragen
 * - Verwaltung aktuell aktiver Fragen im Arbeitsspeicher
 *
 * Die aktive Frage wird zusätzlich in {@link DailyAskData}
 * gespeichert, damit sie nach Server-Neustarts wiederhergestellt
 * werden kann.
 */
public class AskSystem {

    /**
     * Aktuell aktive Fragen im RAM.
     *
     * Key   = Spieler UUID
     * Value = Aktive Frage
     *
     * Wird für schnelle Zugriffe verwendet.
     */
    private static final Map<UUID, QuestionManager.Question> active = new HashMap<>();


    // =======================================================
    // HELM-FRAGESYSTEM
    // =======================================================

    /**
     * Vergibt eine Helm-Frage.
     *
     * Ein Spieler kann maximal eine Helm-Frage pro Minecraft-Tag
     * erhalten. Die Sperre wird über {@link DailyAskData}
     * gespeichert.
     *
     * @param player Spieler
     * @param level  Aktuelle Welt
     * @param data   Persistente DailyAsk-Daten
     */
    public static void giveHelmetQuestion(ServerPlayer player,
                                          ServerLevel level,
                                          DailyAskData data) {

        UUID uuid = player.getUUID();

        // Aktuellen Minecraft-Tag berechnen
        long day = level.getDayTime() / 24000L;

        // Spieler hat heute bereits eine Helm-Frage erhalten
        if (data.getLastHelmetAsk(uuid) == day) {
            return;
        }

        // Heutigen Fragetag speichern
        data.setLastHelmetAsk(uuid, day);

        // Neue Frage auswählen
        QuestionManager.Question q =
                QuestionManager.getQuestion(data);

        // Frage aktiv setzen
        active.put(uuid, q);

        // Für Neustarts persistent speichern
        data.setActiveQuestion(uuid, q.question());

        // Spieler wartet nun auf eine Antwort
        data.setWaiting(uuid, true);

        // Frage im Chat anzeigen
        player.sendSystemMessage(
                Component.literal("§e[DailyAsk] " + q.question())
        );
    }


    // =======================================================
    // ÜBUNGS-/COMMAND-SYSTEM
    // =======================================================

    /**
     * Vergibt eine unbegrenzte Übungsfrage.
     *
     * Diese Methode besitzt keine Tagesbegrenzung und kann
     * beliebig oft über Commands, Bücher oder andere Systeme
     * aufgerufen werden.
     *
     * @param player Spieler
     * @param level  Aktuelle Welt
     * @param data   Persistente DailyAsk-Daten
     */
    public static void giveUnlimitedQuestion(ServerPlayer player,
                                             ServerLevel level,
                                             DailyAskData data) {

        UUID uuid = player.getUUID();

        // Neue Frage auswählen
        QuestionManager.Question q =
                QuestionManager.getQuestion(data);

        // Aktiv setzen
        active.put(uuid, q);

        // Für Neustarts speichern
        data.setActiveQuestion(uuid, q.question());

        // Antwortmodus aktivieren
        data.setWaiting(uuid, true);

        // Frage anzeigen
        player.sendSystemMessage(
                Component.literal("§e[Practice] " + q.question())
        );
    }


    // =======================================================
    // AKTIVE FRAGEN
    // =======================================================

    /**
     * Gibt die aktuell aktive Frage eines Spielers zurück.
     *
     * @param uuid Spieler UUID
     * @return Aktive Frage oder null
     */
    public static QuestionManager.Question getActive(UUID uuid) {
        return active.get(uuid);
    }

    /**
     * Entfernt die aktive Frage eines Spielers aus dem RAM.
     *
     * Wird normalerweise aufgerufen, nachdem eine Antwort
     * erfolgreich verarbeitet wurde.
     *
     * @param uuid Spieler UUID
     */
    public static void clearActive(UUID uuid) {
        active.remove(uuid);
    }
}