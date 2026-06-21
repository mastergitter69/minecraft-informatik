package com.example.examplemod;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
// Mein Magnet-Enchantment: Zieht getroffene Mobs oder Spieler nach einem Treffer zu mir heran.
public class MagnetEnchant extends Enchantment {
    public MagnetEnchant(){
        // Ich stufe das Enchantment als COMMON ein und erlaube es nur auf Waffen in der Haupthand.
        super(Rarity.COMMON, EnchantmentCategory.VANISHABLE,
                new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel(){
        return 3; // Stufen I, II, III
    }
    // Die Verzauberung hat maximal 3 Stufen (I, II, III).
    // Wird nach jedem Angriff automatisch ausgeführt.
    @Override
    public void doPostAttack(LivingEntity attacker,
                             net.minecraft.world.entity.Entity target,
                             int level) {
        // Ich führe die Logik nur auf dem Server aus, damit die Bewegungen für alle synchron bleiben.
        if (!attacker.level().isClientSide) {
// Ich berechne den Abstand und die Richtung vom Ziel zu mir auf allen 3 Achsen.
            double dx = attacker.getX() - target.getX();
            double dy = attacker.getY() - target.getY();
            double dz = attacker.getZ() - target.getZ();

            // Je höher das Level meiner Verzauberung, desto stärker wird der Zug.
            double strength = 0.4 *level;
// Hier setze ich die tatsächliche Bewegung des Ziels in meine Richtung.
            target.setDeltaMovement(dx * strength,
                    dy * strength,
                    dz * strength);
        }
    }
}

// Comment für en_us.json Hier lege ich den Namen fest, der im Spiel für mein neues Magnet-Enchantment angezeigt wird.