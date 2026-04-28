package com.example.examplemod;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
// Magnet Enchant: Zieht Getroffene Mobs oder Spieler nach einem Treffer zum Spieler hin. und mann kann es auf fast alles packen (enchanten)
public class MagnetEnchant extends Enchantment {
    public MagnetEnchant(){
        super(Rarity.COMMON, EnchantmentCategory.WEAPON,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
// Enchant wird als COMMON eingestuft und kann nur auf Waffen in der Haupthand verwendet werden.
    @Override
    public int getMaxLevel(){
        return 3; // Stufen I, II, III
    }
// Maximale Stufe des Enchantments ist 3
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
            double strength = 0.4 *level;

            target.setDeltaMovement(dx * strength,
                    dy * strength,
                    dz * strength);
        }
    }
}
/**
 * Wird nach jedem Angriff ausgeführt
 * Berechnet die Richtung vom Ziel zum Angreifer und zieht das Ziel heran
 * Wird nur auf dem Server oder lokalem Server ausgeführt und nicht auf dem Client
 * Berechnet den Abstand zwischen Angreifer und Ziel auf allen 3 Achsen
 * Je höher das Level desto stärker wird das Ziel herangezogen
 * Setzt die Bewegung des Ziels in Richtung des Angreifers
 */