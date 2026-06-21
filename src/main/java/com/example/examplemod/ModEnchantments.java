package com.example.examplemod;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
// Hier registriere ich alle eigenen Verzauberungen meiner Mod.
public class ModEnchantments {

    // Dieses DeferredRegister sammelt meine Enchantments und lädt sie später sicher ins Spiel.
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "examplemod");

    // Hier füge ich mein Magnet-Enchantment unter dem internen Namen "magnet" zur Liste hinzu.
    public static final RegistryObject<Enchantment> MAGNET =
            ENCHANTMENTS.register("magnet", MagnetEnchant::new);
}

