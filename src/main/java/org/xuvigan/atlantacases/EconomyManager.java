package org.xuvigan.atlantacases;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyManager {

    private PluginMain plugin;
    private Economy economy;

    public EconomyManager(PluginMain plugin) {
        this.plugin = plugin;
        setupEconomy();
    }

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
        } else {
            plugin.getLogger().warning("Failed to get economy service provider. Economy-related features will not work.");
        }
    }

    public boolean hasEnoughMoney(Player player, double amount) {
        if (economy == null) {
            plugin.getLogger().warning("Economy service provider not available. Returning true for hasEnoughMoney.");
            return true;
        }
        return economy.getBalance(player) >= amount;
    }

    public void withdrawMoney(Player player, double amount) {
        if (economy == null) {
            plugin.getLogger().warning("Economy service provider not available. Cannot withdraw money.");
            return;
        }
        economy.withdrawPlayer(player, amount);
    }

    public void depositMoney(Player player, double amount) {
        if (economy == null) {
            plugin.getLogger().warning("Economy service provider not available. Cannot deposit money.");
            return;
        }
        economy.depositPlayer(player, amount);
    }
}