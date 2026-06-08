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
 * Speichert alle persistenten Daten pro Welt:
 * - wann ein Spieler zuletzt eine Helm-Frage bekommen hat
 * - welche Fragen schon gestellt wurden
 * - ob ein Spieler gerade auf eine Antwort wartet
 */
public class DailyAskData extends SavedData {

    private static final String DATA_NAME = "dailyask_data";

    // 🔵 normale Tageslogik (für alte/optional Systeme)
    private final Map<UUID, Long> lastAskedDay = new HashMap<>();

    // 🟡 merkt ob Spieler gerade antworten muss
    private final Map<UUID, Boolean> waitingForAnswer = new HashMap<>();

    // 🟣 speichert alle bereits gestellten Fragen (keine Doppelungen)
    private final Set<String> askedQuestions = new HashSet<>();

    // 🟢 NEU: eigene Tages-Sperre nur für HELM-System
    private final Map<UUID, Long> lastHelmetAsk = new HashMap<>();

    // 🔴 NEU: speichert die aktuell aktive Frage pro Spieler
    private final Map<UUID, String> activeQuestion = new HashMap<>();

    // -------------------------------------------------------
    // 🔴 AKTIVE FRAGE PRO SPIELER
    // -------------------------------------------------------

    public String getActiveQuestion(UUID uuid) {
        return activeQuestion.getOrDefault(uuid, null);
    }

    public void setActiveQuestion(UUID uuid, String question) {
        activeQuestion.put(uuid, question);
        setDirty();
    }

    public void clearActiveQuestion(UUID uuid) {
        activeQuestion.remove(uuid);
        setDirty();
    }

    // -------------------------------------------------------
    // GETTER / SETTER (bestehendes System)
    // -------------------------------------------------------

    public long getLastAskedDay(UUID uuid) {
        return lastAskedDay.getOrDefault(uuid, -1L);
    }

    public void setLastAskedDay(UUID uuid, long day) {
        lastAskedDay.put(uuid, day);
        setDirty();
    }

    public boolean isWaiting(UUID uuid) {
        return waitingForAnswer.getOrDefault(uuid, false);
    }

    public void setWaiting(UUID uuid, boolean waiting) {
        waitingForAnswer.put(uuid, waiting);
        setDirty();
    }

    public void addAskedQuestion(String question) {
        askedQuestions.add(question);
        setDirty();
    }

    public boolean wasAsked(String question) {
        return askedQuestions.contains(question);
    }

    public void clearAskedQuestions() {
        askedQuestions.clear();
        setDirty();
    }

    // -------------------------------------------------------
    // 🟢 NEU: HELM SYSTEM (1x pro Tag)
    // -------------------------------------------------------

    public long getLastHelmetAsk(UUID uuid) {
        return lastHelmetAsk.getOrDefault(uuid, -1L);
    }

    public void setLastHelmetAsk(UUID uuid, long day) {
        lastHelmetAsk.put(uuid, day);
        setDirty();
    }

    // -------------------------------------------------------
    // SAVE / LOAD (WELT SPEICHERUNG)
    // -------------------------------------------------------

    @Override
    public CompoundTag save(CompoundTag tag) {

        CompoundTag daysTag = new CompoundTag();
        lastAskedDay.forEach((uuid, day) -> daysTag.putLong(uuid.toString(), day));
        tag.put("lastAskedDay", daysTag);

        CompoundTag waitingTag = new CompoundTag();
        waitingForAnswer.forEach((uuid, waiting) -> waitingTag.putBoolean(uuid.toString(), waiting));
        tag.put("waitingForAnswer", waitingTag);

        CompoundTag helmetTag = new CompoundTag();
        lastHelmetAsk.forEach((uuid, day) -> helmetTag.putLong(uuid.toString(), day));
        tag.put("lastHelmetAsk", helmetTag);

        ListTag questionsTag = new ListTag();
        askedQuestions.forEach(q -> questionsTag.add(StringTag.valueOf(q)));
        tag.put("askedQuestions", questionsTag);

        CompoundTag activeTag = new CompoundTag();
        activeQuestion.forEach((uuid, q) -> activeTag.putString(uuid.toString(), q));
        tag.put("activeQuestion", activeTag);

        return tag;
    }

    public static DailyAskData load(CompoundTag tag) {

        DailyAskData data = new DailyAskData();

        CompoundTag daysTag = tag.getCompound("lastAskedDay");
        for (String key : daysTag.getAllKeys()) {
            data.lastAskedDay.put(UUID.fromString(key), daysTag.getLong(key));
        }

        CompoundTag waitingTag = tag.getCompound("waitingForAnswer");
        for (String key : waitingTag.getAllKeys()) {
            data.waitingForAnswer.put(UUID.fromString(key), waitingTag.getBoolean(key));
        }

        CompoundTag helmetTag = tag.getCompound("lastHelmetAsk");
        for (String key : helmetTag.getAllKeys()) {
            data.lastHelmetAsk.put(UUID.fromString(key), helmetTag.getLong(key));
        }

        ListTag questionsTag = tag.getList("askedQuestions", 8);
        for (int i = 0; i < questionsTag.size(); i++) {
            data.askedQuestions.add(questionsTag.getString(i));
        }

        // activeQuestion laden
        CompoundTag activeTag = tag.getCompound("activeQuestion");
        for (String key : activeTag.getAllKeys()) {
            data.activeQuestion.put(UUID.fromString(key), activeTag.getString(key));
        }

        return data;
    }

    // -------------------------------------------------------
    // FACTORY (WELT INSTANZ)
    // -------------------------------------------------------

    public static DailyAskData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                DailyAskData::load,
                DailyAskData::new,
                DATA_NAME
        );
    }
}