package fr.naossmp.trader.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class SmithListener implements Listener {

    private static final Set<Material> BLOCKED = Set.of(
        Material.NETHERITE_HELMET,
        Material.NETHERITE_CHESTPLATE,
        Material.NETHERITE_LEGGINGS,
        Material.NETHERITE_BOOTS,
        Material.NETHERITE_SWORD,
        Material.NETHERITE_PICKAXE,
        Material.NETHERITE_AXE,
        Material.NETHERITE_SHOVEL,
        Material.NETHERITE_HOE
    );

    private boolean isBlockedNetherite(ItemStack item) {
        if (item == null) return false;
        // Constantes connues à la compilation
        if (BLOCKED.contains(item.getType())) return true;
        // Rattrape les items néserite non inclus dans la compilation (ex: armure de cheval)
        String name = item.getType().name();
        return name.startsWith("NETHERITE_") && !name.equals("NETHERITE_INGOT") && !name.equals("NETHERITE_SCRAP");
    }

    // Efface le résultat dès que le joueur positionne les items
    @EventHandler
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        if (isBlockedNetherite(event.getResult())) {
            event.setResult(null);
        }
    }

    // Double sécurité : annule si le joueur clique quand même sur le slot résultat
    @EventHandler
    public void onSmith(SmithItemEvent event) {
        if (isBlockedNetherite(event.getCurrentItem())) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof Player player) {
                player.sendMessage("§cLes équipements en néserite sont désactivés sur ce serveur.");
            }
        }
    }
}
