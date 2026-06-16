package com.example.examplemod.event;

import com.example.examplemod.Dailyask;
import com.example.examplemod.core.AskSystem;
import com.example.examplemod.data.DailyAskData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// Registriert diese Klasse als Event-Listener für den Forge EventBus
@Mod.EventBusSubscriber(modid = Dailyask.MODID)
public class ModEvents {

    // Wird bei jedem Spieler-Tick aufgerufen
    @SubscribeEvent
    public static void tick(TickEvent.PlayerTickEvent event) {

        // Nur am Ende eines Ticks ausführen
        if (event.phase != TickEvent.Phase.END) return;

        // Sicherstellen, dass der Spieler ein ServerPlayer ist
        if (!(event.player instanceof ServerPlayer player)) return;

        // Die aktuelle Server-Welt des Spielers abrufen
        ServerLevel level = player.serverLevel();

        // Gespeicherte Mod-Daten für diese Welt laden
        DailyAskData data = DailyAskData.get(level);

        // Den Helm-Slot des Spielers auslesen
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);

        // Prüfen, ob der Helm die Verzauberung "DAILY_QUESTION" besitzt
        boolean hasEnch =
                EnchantmentHelper.getItemEnchantmentLevel(
                        Dailyask.DAILY_QUESTION.get(),
                        helmet
                ) > 0;

        // Falls die Verzauberung nicht vorhanden ist, nichts tun
        if (!hasEnch) return;

        // Fragesystem ausführen und ggf. eine Helm-Frage vergeben
        AskSystem.giveHelmetQuestion(player, level, data);
    }
}
