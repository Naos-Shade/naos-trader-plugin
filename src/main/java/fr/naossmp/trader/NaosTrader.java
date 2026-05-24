package fr.naossmp.trader;

import fr.naossmp.trader.listeners.SmithListener;
import fr.naossmp.trader.listeners.TradeListener;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class NaosTrader extends JavaPlugin {

    private NamespacedKey blocMetierKey;
    private NamespacedKey customTraderKey;
    private TraderManager traderManager;

    @Override
    public void onEnable() {
        blocMetierKey = new NamespacedKey(this, "bloc_metier");
        customTraderKey = new NamespacedKey(this, "custom_trader");

        traderManager = new TraderManager(this);
        traderManager.load();

        registerRecipe();
        getServer().getPluginManager().registerEvents(new TradeListener(this), this);
        getServer().getPluginManager().registerEvents(new SmithListener(), this);

        getLogger().info("NaosTrader activé !");
    }

    @Override
    public void onDisable() {
        if (traderManager != null) traderManager.save();
        getLogger().info("NaosTrader désactivé.");
    }

    private void registerRecipe() {
        NamespacedKey recipeKey = new NamespacedKey(this, "bloc_metier_recipe");
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, createBlocMetierItem());
        recipe.shape("DDD", "DND", "DDD");
        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        getServer().addRecipe(recipe);
    }

    public ItemStack createBlocMetierItem() {
        ItemStack item = new ItemStack(Material.LODESTONE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Bloc de Métier");
        meta.setLore(List.of("§7Offrez-le à un villageois pour en faire un maître marchand."));
        meta.getPersistentDataContainer().set(blocMetierKey, PersistentDataType.INTEGER, 1);
        item.setItemMeta(meta);
        return item;
    }

    public NamespacedKey getBlocMetierKey() { return blocMetierKey; }
    public NamespacedKey getCustomTraderKey() { return customTraderKey; }
    public TraderManager getTraderManager() { return traderManager; }
}
