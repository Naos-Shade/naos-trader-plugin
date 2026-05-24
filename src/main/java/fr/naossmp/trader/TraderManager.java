package fr.naossmp.trader;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TraderManager {

    private final NaosTrader plugin;
    private final Set<UUID> transformedVillagers = new HashSet<>();
    private final File dataFile;

    public TraderManager(NaosTrader plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "traders.yml");
    }

    public void load() {
        if (!dataFile.exists()) return;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        for (String uuid : config.getStringList("villagers")) {
            try { transformedVillagers.add(UUID.fromString(uuid)); }
            catch (IllegalArgumentException ignored) {}
        }
    }

    public void save() {
        YamlConfiguration config = new YamlConfiguration();
        List<String> uuids = new ArrayList<>();
        for (UUID uuid : transformedVillagers) uuids.add(uuid.toString());
        config.set("villagers", uuids);
        try { config.save(dataFile); }
        catch (IOException e) {
            plugin.getLogger().severe("Erreur sauvegarde traders.yml : " + e.getMessage());
        }
    }

    public void addVillager(UUID uuid) {
        transformedVillagers.add(uuid);
    }

    public List<MerchantRecipe> buildTrades() {
        List<MerchantRecipe> trades = new ArrayList<>();

        // 5 cobblestone → 1 émeraude
        MerchantRecipe stone = new MerchantRecipe(new ItemStack(Material.EMERALD), 0, Integer.MAX_VALUE, false, 0, 0f);
        stone.addIngredient(new ItemStack(Material.COBBLESTONE, 5));
        trades.add(stone);

        // casque diamant + Protection III
        trades.add(armorTrade(Material.DIAMOND_HELMET, Enchantment.PROTECTION, 3, 12));
        // plastron diamant + Protection III
        trades.add(armorTrade(Material.DIAMOND_CHESTPLATE, Enchantment.PROTECTION, 3, 15));
        // jambières diamant + Protection III
        trades.add(armorTrade(Material.DIAMOND_LEGGINGS, Enchantment.PROTECTION, 3, 14));
        // bottes diamant + Protection III
        trades.add(armorTrade(Material.DIAMOND_BOOTS, Enchantment.PROTECTION, 3, 11));

        // 13 émeraudes → épée diamant + Tranchant II
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addEnchantment(Enchantment.SHARPNESS, 2);
        MerchantRecipe swordTrade = new MerchantRecipe(sword, 0, Integer.MAX_VALUE, false, 0, 0f);
        swordTrade.addIngredient(new ItemStack(Material.EMERALD, 13));
        trades.add(swordTrade);

        return trades;
    }

    private MerchantRecipe armorTrade(Material material, Enchantment enchant, int level, int price) {
        ItemStack item = new ItemStack(material);
        item.addEnchantment(enchant, level);
        MerchantRecipe trade = new MerchantRecipe(item, 0, Integer.MAX_VALUE, false, 0, 0f);
        trade.addIngredient(new ItemStack(Material.EMERALD, price));
        return trade;
    }
}
