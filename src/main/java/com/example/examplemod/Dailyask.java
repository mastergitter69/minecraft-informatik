
package com.example.examplemod;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// Markiert diese Klasse als Forge-Mod mit der ID "examplemod"
@Mod(Dailyask.MODID)
public class Dailyask {

    // Eindeutige Mod-ID – muss mit mods.toml übereinstimmen
    public static final String MODID = "examplemod";

    // Register für Items – hält alle zukünftigen Custom-Items dieser Mod
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // Register für Verzauberungen – verwaltet alle Custom-Enchantments dieser Mod
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);

    // Registriert die neue Verzauberung "daily_question" mit ID examplemod:daily_question
    public static final RegistryObject<Enchantment> DAILY_QUESTION =
            ENCHANTMENTS.register("daily_question", () ->
                    new Enchantment(
                            Enchantment.Rarity.RARE,                                        // Seltenheit: taucht selten in Bibliotheken auf
                            net.minecraft.world.item.enchantment.EnchantmentCategory.ARMOR_HEAD, // Nur auf Helme anwendbar
                            new net.minecraft.world.entity.EquipmentSlot[]{EquipmentSlot.HEAD}   // Wirkt nur wenn im Helm-Slot getragen
                    ) {
                        @Override
                        public int getMaxLevel() {
                            return 1; // Maximalstufe ist 1 (kein "daily_question II" möglich)
                        }
                    });

    public Dailyask() {
        // Holt den Event-Bus des Mods – über ihn läuft die gesamte Registrierung
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        // Meldet das Items-Register am Bus an → Items werden beim Start geladen
        ITEMS.register(bus);

        // Meldet das Enchantments-Register an → Verzauberung wird beim Start geladen
        ENCHANTMENTS.register(bus);
    }
}