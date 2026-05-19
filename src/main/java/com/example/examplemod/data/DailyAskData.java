
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
 * Persistente Daten: Speichert pro Spieler den letzten Ingame-Tag
 * an dem die Frage gestellt wurde, sowie ob eine Antwort aussteht.
 */
// Erweitert SavedData, damit Minecraft die Daten automatisch in der Welt speichert
public class DailyAskData extends SavedData {

    private static final String DATA_NAME = "dailyask_data";

    // Speichert den letzten Ingame-Tag, an dem ein Spieler gefragt wurde (UUID -> Tag-Nummer)
    private final Map<UUID, Long> lastAskedDay = new HashMap<>();

    // Merkt sich, ob ein Spieler gerade auf eine Antwort wartet (UUID -> true/false)
    private final Map<UUID, Boolean> waitingForAnswer = new HashMap<>();

    // Set speichert alle gestellten Fragen, keine Duplikate möglich
    private final Set<String> askedQuestions = new HashSet<>();

    // ---- Getter & Setter ----

    // Gibt den letzten gespeicherten Tag zurück; -1 wenn noch kein Eintrag existiert
    public long getLastAskedDay(UUID uuid) {
        return lastAskedDay.getOrDefault(uuid, -1L);
    }

    // Setzt den aktuellen Tag für den Spieler und markiert die Daten als geändert
    public void setLastAskedDay(UUID uuid, long day) {
        lastAskedDay.put(uuid, day);
        setDirty();
    }

    // Gibt zurück, ob der Spieler aktuell auf eine Antwort wartet
    public boolean isWaiting(UUID uuid) {
        return waitingForAnswer.getOrDefault(uuid, false);
    }

    // Setzt den Wartestatus und markiert die Daten als geändert
    public void setWaiting(UUID uuid, boolean waiting) {
        waitingForAnswer.put(uuid, waiting);
        setDirty();
    }

    // Prüft ob eine Frage schon gestellt wurde
    public boolean wasAsked(String question) {
        return askedQuestions.contains(question);
    }

    // Fügt eine Frage zum Register hinzu und speichert
    public void addAskedQuestion(String question) {
        askedQuestions.add(question);
        setDirty();
    }

    // Leert das Register (wenn alle Fragen durch sind)
    public void clearAskedQuestions() {
        askedQuestions.clear();
        setDirty();
    }

    // ---- NBT Serialisierung ----

    // Speichert beide Maps in NBT-Tags, damit die Daten beim Weltladen erhalten bleiben
    @Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag daysTag = new CompoundTag();
        lastAskedDay.forEach((uuid, day) -> daysTag.putLong(uuid.toString(), day));
        tag.put("lastAskedDay", daysTag);

        CompoundTag waitingTag = new CompoundTag();
        waitingForAnswer.forEach((uuid, waiting) -> waitingTag.putBoolean(uuid.toString(), waiting));
        tag.put("waitingForAnswer", waitingTag);

        // Gestellte Fragen als Liste speichern
        ListTag questionsTag = new ListTag();
        askedQuestions.forEach(q -> questionsTag.add(StringTag.valueOf(q)));
        tag.put("askedQuestions", questionsTag);

        return tag;
    }

    // Liest die gespeicherten NBT-Daten und befüllt damit eine neue DailyAskData-Instanz
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

        // Gestellte Fragen laden
        ListTag questionsTag = tag.getList("askedQuestions", 8);
        questionsTag.forEach(t -> data.askedQuestions.add(t.getAsString()));

        return data;
    }

    // ---- Factory ----

    // Lädt die gespeicherten Daten aus dem DataStorage der Welt oder erstellt sie neu
    public static DailyAskData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                DailyAskData::load,
                DailyAskData::new,
                DATA_NAME
        );
    }
}

