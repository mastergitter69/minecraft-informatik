package com.example.examplemod.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Persistente Daten: Speichert pro Spieler den letzten Ingame-Tag
 * an dem die Frage gestellt wurde, sowie ob eine Antwort aussteht.
 */
public class DailyAskData extends SavedData {

    private static final String DATA_NAME = "dailyask_data";

    // Letzter Ingame-Tag pro Spieler
    private final Map<UUID, Long> lastAskedDay = new HashMap<>();

    // Spieler warten auf Antwort
    private final Map<UUID, Boolean> waitingForAnswer = new HashMap<>();

    // ---- Getter & Setter ----

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

    // ---- NBT Serialisierung ----

    @Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag daysTag = new CompoundTag();
        lastAskedDay.forEach((uuid, day) -> daysTag.putLong(uuid.toString(), day));
        tag.put("lastAskedDay", daysTag);

        CompoundTag waitingTag = new CompoundTag();
        waitingForAnswer.forEach((uuid, waiting) -> waitingTag.putBoolean(uuid.toString(), waiting));
        tag.put("waitingForAnswer", waitingTag);

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

        return data;
    }

    // ---- Factory ----

    public static DailyAskData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                DailyAskData::load,
                DailyAskData::new,
                DATA_NAME
        );
    }
}
