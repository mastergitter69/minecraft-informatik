package com.example.examplemod.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Speichert alle persistenten Daten des DailyAsk-Systems pro Welt.
 *
 * Diese Daten werden automatisch in der Welt gespeichert und nach
 * einem Server-Neustart wieder geladen.
 *
 * Gespeichert werden:
 * - Letzter Fragetag eines Spielers
 * - Ob ein Spieler aktuell auf eine Antwort wartet
 * - Bereits gestellte Fragen (Vermeidung von Wiederholungen)
 * - Letzter Helm-Fragetag (1 Frage pro Tag)
 * - Aktive Frage eines Spielers
 */
public class DailyAskData extends SavedData {

    /** Name der gespeicherten Datendatei */
    private static final String DATA_NAME = "dailyask_data";

    /**
     * Speichert den letzten Tag, an dem ein Spieler eine normale Frage erhalten hat.
     * Key   = Spieler UUID
     * Value = Minecraft-Tag
     */
    private final Map<UUID, Long> lastAskedDay = new HashMap<>();

    /**
     * Merkt sich, ob ein Spieler aktuell auf eine Antwort reagieren muss.
     * true  = Frage offen
     * false = Keine aktive Frage
     */
    private final Map<UUID, Boolean> waitingForAnswer = new HashMap<>();

    /**
     * Enthält alle bereits verwendeten Fragen.
     * Dadurch werden doppelte Fragen vermieden.
     */
    private final Set<String> askedQuestions = new HashSet<>();

    /**
     * Speichert den letzten Tag, an dem ein Spieler über den Helm
     * eine Frage erhalten hat.
     *
     * Dadurch kann sichergestellt werden:
     * -> maximal eine Helm-Frage pro Tag.
     */
    private final Map<UUID, Long> lastHelmetAsk = new HashMap<>();

    /**
     * Speichert die aktuell aktive Frage eines Spielers.
     *
     * Key   = Spieler UUID
     * Value = Fragetext
     */
    private final Map<UUID, String> activeQuestion = new HashMap<>();


    // =======================================================
    // AKTIVE FRAGE VERWALTEN
    // =======================================================

    /**
     * Gibt die aktuell aktive Frage eines Spielers zurück.
     *
     * @param uuid Spieler UUID
     * @return Frage oder null falls keine aktiv ist
     */
    public String getActiveQuestion(UUID uuid) {
        return activeQuestion.getOrDefault(uuid, null);
    }

    /**
     * Setzt die aktuelle Frage eines Spielers.
     */
    public void setActiveQuestion(UUID uuid, String question) {
        activeQuestion.put(uuid, question);
        setDirty(); // Speichern vormerken
    }

    /**
     * Entfernt die aktive Frage eines Spielers.
     */
    public void clearActiveQuestion(UUID uuid) {
        activeQuestion.remove(uuid);
        setDirty();
    }


    // =======================================================
    // STANDARD FRAGESYSTEM
    // =======================================================

    /**
     * Letzten Fragetag abrufen.
     */
    public long getLastAskedDay(UUID uuid) {
        return lastAskedDay.getOrDefault(uuid, -1L);
    }

    /**
     * Letzten Fragetag setzen.
     */
    public void setLastAskedDay(UUID uuid, long day) {
        lastAskedDay.put(uuid, day);
        setDirty();
    }

    /**
     * Prüft ob ein Spieler aktuell auf eine Antwort wartet.
     */
    public boolean isWaiting(UUID uuid) {
        return waitingForAnswer.getOrDefault(uuid, false);
    }

    /**
     * Setzt den Antwortstatus eines Spielers.
     */
    public void setWaiting(UUID uuid, boolean waiting) {
        waitingForAnswer.put(uuid, waiting);
        setDirty();
    }

    /**
     * Fügt eine bereits gestellte Frage zur Historie hinzu.
     */
    public void addAskedQuestion(String question) {
        askedQuestions.add(question);
        setDirty();
    }

    /**
     * Prüft ob eine Frage bereits verwendet wurde.
     */
    public boolean wasAsked(String question) {
        return askedQuestions.contains(question);
    }

    /**
     * Löscht die Historie aller bereits gestellten Fragen.
     */
    public void clearAskedQuestions() {
        askedQuestions.clear();
        setDirty();
    }


    // =======================================================
    // HELM-FRAGESYSTEM
    // =======================================================

    /**
     * Gibt den letzten Helm-Fragetag eines Spielers zurück.
     */
    public long getLastHelmetAsk(UUID uuid) {
        return lastHelmetAsk.getOrDefault(uuid, -1L);
    }

    /**
     * Speichert den aktuellen Helm-Fragetag.
     */
    public void setLastHelmetAsk(UUID uuid, long day) {
        lastHelmetAsk.put(uuid, day);
        setDirty();
    }


    // =======================================================
    // SPEICHERN
    // =======================================================

    /**
     * Schreibt alle Daten in die Weltdatei.
     *
     * Wird von Minecraft automatisch aufgerufen,
     * sobald die Daten gespeichert werden müssen.
     */
    @Override
    public CompoundTag save(CompoundTag tag) {

        // Letzte Fragetage speichern
        CompoundTag daysTag = new CompoundTag();
        lastAskedDay.forEach((uuid, day) ->
                daysTag.putLong(uuid.toString(), day));
        tag.put("lastAskedDay", daysTag);

        // Antwortstatus speichern
        CompoundTag waitingTag = new CompoundTag();
        waitingForAnswer.forEach((uuid, waiting) ->
                waitingTag.putBoolean(uuid.toString(), waiting));
        tag.put("waitingForAnswer", waitingTag);

        // Helm-Fragetage speichern
        CompoundTag helmetTag = new CompoundTag();
        lastHelmetAsk.forEach((uuid, day) ->
                helmetTag.putLong(uuid.toString(), day));
        tag.put("lastHelmetAsk", helmetTag);

        // Bereits verwendete Fragen speichern
        ListTag questionsTag = new ListTag();
        askedQuestions.forEach(q ->
                questionsTag.add(StringTag.valueOf(q)));
        tag.put("askedQuestions", questionsTag);

        // Aktive Fragen speichern
        CompoundTag activeTag = new CompoundTag();
        activeQuestion.forEach((uuid, q) ->
                activeTag.putString(uuid.toString(), q));
        tag.put("activeQuestion", activeTag);

        return tag;
    }


    // =======================================================
    // LADEN
    // =======================================================

    /**
     * Lädt alle gespeicherten Daten aus der Weltdatei.
     */
    public static DailyAskData load(CompoundTag tag) {

        DailyAskData data = new DailyAskData();

        // Letzte Fragetage laden
        CompoundTag daysTag = tag.getCompound("lastAskedDay");
        for (String key : daysTag.getAllKeys()) {
            data.lastAskedDay.put(
                    UUID.fromString(key),
                    daysTag.getLong(key)
            );
        }

        // Antwortstatus laden
        CompoundTag waitingTag = tag.getCompound("waitingForAnswer");
        for (String key : waitingTag.getAllKeys()) {
            data.waitingForAnswer.put(
                    UUID.fromString(key),
                    waitingTag.getBoolean(key)
            );
        }

        // Helm-Fragetage laden
        CompoundTag helmetTag = tag.getCompound("lastHelmetAsk");
        for (String key : helmetTag.getAllKeys()) {
            data.lastHelmetAsk.put(
                    UUID.fromString(key),
                    helmetTag.getLong(key)
            );
        }

        // Fragenhistorie laden
        ListTag questionsTag = tag.getList("askedQuestions", 8);
        for (int i = 0; i < questionsTag.size(); i++) {
            data.askedQuestions.add(
                    questionsTag.getString(i)
            );
        }

        // Aktive Fragen laden
        CompoundTag activeTag = tag.getCompound("activeQuestion");
        for (String key : activeTag.getAllKeys()) {
            data.activeQuestion.put(
                    UUID.fromString(key),
                    activeTag.getString(key)
            );
        }

        return data;
    }


    // =======================================================
    // WELT-INSTANZ ABRUFEN
    // =======================================================

    /**
     * Liefert die gespeicherte DailyAskData-Instanz der Welt.
     *
     * Falls noch keine existiert, wird automatisch eine neue erstellt.
     */
    public static DailyAskData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                DailyAskData::load,
                DailyAskData::new,
                DATA_NAME
        );
    }
}
