package org.xuvigan.atlantacases;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.List;

public class CaseItem {

    public static ItemStack createCaseItem(String caseName) {
        ItemStack caseItem = new ItemStack(Material.CHEST);
        ItemMeta caseMeta = caseItem.getItemMeta();
        caseMeta.setDisplayName(caseName);
        List<String> lore = new ArrayList<>();
        lore.add("A case containing various items.");
        caseMeta.setLore(lore);
        caseItem.setItemMeta(caseMeta);
        return caseItem;
    }

    private int id;
    private int meta;
    private int rarity;
    private int minStackSize;
    private int maxStackSize;
    private int caseId;

    public CaseItem(int id, int meta, int rarity, int minStackSize, int maxStackSize, int caseId) {
        this.id = id;
        this.meta = meta;
        this.rarity = rarity;
        this.minStackSize = minStackSize;
        this.maxStackSize = maxStackSize;
        this.caseId = caseId;
    }

    public int getId() {
        return this.id;
    }

    public int getMeta() {
        return this.meta;
    }

    public int getRarity() {
        return this.rarity;
    }

    public int getMinStackSize() {
        return this.minStackSize;
    }

    public int getMaxStackSize() {
        return this.maxStackSize;
    }

    public int getCaseId() {
        return this.caseId;
    }

    private void giveCaseToPlayer(Player player, String caseName) {
        List<Case> cases = new ArrayList<>();
        cases.add(new Case("case1", 10, "texture1"));
        cases.add(new Case("case2", 20, "texture2"));
        for (Case caseObj : cases) {
            System.out.println(caseObj.getName());
        }
        double price = cases.stream()
                .filter(c -> c.getName().equals(caseName))
                .findFirst()
                .orElseThrow(NoSuchElementException::new)
                .getPrice();
        JavaPlugin plugin = JavaPlugin.getPlugin(PluginMain.class);
        EconomyManager economyManager = new EconomyManager((PluginMain) plugin);
        if (!economyManager.hasEnoughMoney(player, price)) {
            player.sendMessage("You do not have enough money to buy this case.");
            return;
        }
        economyManager.withdrawMoney(player, price);
        player.getInventory().addItem(CaseItem.createCaseItem(caseName));
        player.sendMessage("You have purchased a " + caseName + " case for " + price + " dollars.");
    }
}