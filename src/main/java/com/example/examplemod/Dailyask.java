package com.example.examplemod;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.item.ArmorItem;

// Registriert den Mod bei Forge mit der angegebenen MODID
@Mod(Dailyask.MODID)
public class Dailyask {

    public static final String MODID = "examplemod";

    // DeferredRegister verzögert die Item-Registrierung bis Forge bereit ist
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // Registriert das question_book als EnchantedBookItem (nicht stapelbar)
    public static final RegistryObject<Item> QUESTION_BOOK =
            ITEMS.register("question_book", () -> new EnchantedBookItem(new Item.Properties().stacksTo(1)));

    // DeferredRegister für Verzauberungen
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);

    // Registriert eine eigene RARE-Verzauberung nur für Helme; handelbar und zufällig findbar
    public static final RegistryObject<Enchantment> DAILY_QUESTION =
            ENCHANTMENTS.register("daily_question", () -> new Enchantment(
                    Enchantment.Rarity.RARE,
                    EnchantmentCategory.ARMOR_HEAD,
                    new EquipmentSlot[]{EquipmentSlot.HEAD}
            ) {
                @Override
                public int getMaxLevel() { return 1; }

                @Override
                public boolean isTradeable() { return true; }

                @Override
                public boolean isDiscoverable() { return true; }

                public boolean canApplyAtEnchantingTable(ItemStack stack) {
                    return stack.canApplyAtEnchantingTable(this);
                }


            });

    // Konstruktor: Registriert alle Register und den Creative-Tab-Listener am Mod-Eventbus
    public Dailyask(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ITEMS.register(modEventBus);
        ENCHANTMENTS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
    }

    // Fügt das verzauberte Buch in den COMBAT-Tab des Kreativmenüs ein
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {  // oder TOOLS
            ItemStack book = new ItemStack(QUESTION_BOOK.get());
            EnchantedBookItem.addEnchantment(book,
                    new EnchantmentInstance(DAILY_QUESTION.get(), 1));
            event.accept(book);
        }
    }
}
