
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

@Mod(Dailyask.MODID)
public class Dailyask {

    public static final String MODID = "examplemod";

    // ITEMS (optional, nur falls du Items später nutzt)
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // ENCHANTMENTS (hier wird deine Custom Verzauberung registriert)
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);

    // Custom Enchantment: wird später im Code abgefragt
    public static final RegistryObject<Enchantment> DAILY_QUESTION =
            ENCHANTMENTS.register("daily_question", () ->
                    new Enchantment(
                            Enchantment.Rarity.RARE,
                            net.minecraft.world.item.enchantment.EnchantmentCategory.ARMOR_HEAD,
                            new net.minecraft.world.entity.EquipmentSlot[]{EquipmentSlot.HEAD}
                    ) {
                        @Override
                        public int getMaxLevel() {
                            return 1;
                        }
                    });

    public Dailyask() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        // Registrierung wird beim Mod-Start geladen
        ITEMS.register(bus);
        ENCHANTMENTS.register(bus);
    }
}