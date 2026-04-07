package com.example.examplemod;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment.Rarity;

public class MagnetEnchant extends Enchantment {
    public MagnetEnchant(){
        super(Rarity.RARE, EnchantmentCategory.WEAPON,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel(){
        return 3; // Stufen I, II, III
    }

    @Override
    public void doPostAttack(LivingEntity attacker,
                             net.minecraft.world.entity.Entity target,
                             int level) {
        if (!attacker.level().isClientSide) {
            // Ziel wird zu dir gezogen
            double dx = attacker.getX() - target.getX();
            double dy = attacker.getY() - target.getY();
            double dz = attacker.getZ() - target.getZ();

            // Je höher das Level desto stärker der Zug
            double strength = 0.5 *level;

            target.setDeltaMovement(dx * strength,
                    dy * strength,
                    dz * strength);
        }
    }
}
