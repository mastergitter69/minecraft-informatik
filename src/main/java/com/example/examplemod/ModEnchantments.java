package com.example.examplemod;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
//Registiert alle Enchantments der Mod
public class ModEnchantments {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "examplemod");
//DefferedRegister speichert alle Enchantments und registiert sie später beim Laden der Mod
    public static final RegistryObject<Enchantment> MAGNET =
            ENCHANTMENTS.register("magnet", MagnetEnchant::new);
//Registiert das Magnet Enchant unter dem namen magnet
}

