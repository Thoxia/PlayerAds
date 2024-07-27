package com.thoxia.playerads.economy.impl;

import com.thoxia.playerads.economy.EconomyManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomyManager implements EconomyManager {

    private Economy economy = null;

    public void init() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return;

        economy = rsp.getProvider();
    }

    public void remove(OfflinePlayer player, double amount) {
        economy.withdrawPlayer(player, amount);
    }

    public void add(OfflinePlayer player, double amount) {
        economy.depositPlayer(player, amount);
    }

    public double getMoney(OfflinePlayer player) {
        return economy.getBalance(player);
    }

    public void setMoney(OfflinePlayer player, double amount) {
        this.remove(player, this.getMoney(player));
        this.add(player, amount);
    }

    @Override
    public String getName() {
        return "Vault";
    }

}
