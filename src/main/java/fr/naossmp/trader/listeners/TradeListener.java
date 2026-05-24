package fr.naossmp.trader.listeners;

import fr.naossmp.trader.NaosTrader;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityLoadEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class TradeListener implements Listener {

    private final NaosTrader plugin;

    public TradeListener(NaosTrader plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteractVillager(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Entity entity = event.getRightClicked();
        if (!(entity instanceof Villager villager)) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType().isAir() || !item.hasItemMeta()) return;
        if (!item.getItemMeta().getPersistentDataContainer()
                .has(plugin.getBlocMetierKey(), PersistentDataType.INTEGER)) return;

        event.setCancelled(true);

        // Consommer 1 exemplaire
        item.setAmount(item.getAmount() - 1);

        // Transformer le villageois en Maître Armurier
        villager.setProfession(Villager.Profession.WEAPONSMITH);
        villager.setVillagerLevel(5);
        villager.setRecipes(plugin.getTraderManager().buildTrades());

        // Marquer comme custom trader (survit aux restarts via NBT du monde)
        villager.getPersistentDataContainer().set(
                plugin.getCustomTraderKey(), PersistentDataType.INTEGER, 1);
        plugin.getTraderManager().addVillager(villager.getUniqueId());

        // Effets
        villager.getWorld().spawnParticle(
                Particle.HAPPY_VILLAGER,
                villager.getLocation().add(0, 1, 0),
                30, 0.5, 0.5, 0.5);
        villager.getWorld().playSound(
                villager.getLocation(),
                Sound.ENTITY_VILLAGER_CELEBRATE, 1f, 1f);

        player.sendMessage("§6Ce villageois est maintenant un §lMaître Marchand§6 !");
    }

    @EventHandler
    public void onEntityLoad(EntityLoadEvent event) {
        if (!(event.getEntity() instanceof Villager villager)) return;
        if (!villager.getPersistentDataContainer()
                .has(plugin.getCustomTraderKey(), PersistentDataType.INTEGER)) return;

        // Réappliquer les trades si le villageois a été réinitialisé au restart
        if (villager.getRecipes().isEmpty()) {
            villager.setRecipes(plugin.getTraderManager().buildTrades());
        }
        plugin.getTraderManager().addVillager(villager.getUniqueId());
    }
}
