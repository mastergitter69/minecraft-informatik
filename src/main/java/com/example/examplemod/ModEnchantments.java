package com.example.examplemod;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantments {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "examplemod");

    public static final RegistryObject<Enchantment> MAGNET =
            ENCHANTMENTS.register("magnet", MagnetEnchant::new);

}

