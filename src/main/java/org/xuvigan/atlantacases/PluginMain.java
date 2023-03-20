//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\XuViGaN\Desktop\1.12 stable mappings"!

//Decompiled by Procyon!

package com.mayakplay.doge;

import org.bukkit.plugin.java.*;
import org.bukkit.event.*;
import org.bukkit.permissions.*;
import org.bukkit.configuration.file.*;
import org.bukkit.plugin.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import com.google.common.io.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import java.util.*;

public class PluginMain extends JavaPlugin implements Listener
{
    public static Permission permission;
    private FileConfiguration config;
    
    private boolean setupPermissions() {
        final RegisteredServiceProvider<Permission> permissionProvider = (RegisteredServiceProvider<Permission>)this.getServer().getServicesManager().getRegistration((Class)Permission.class);
        if (permissionProvider != null) {
            PluginMain.permission = (Permission)permissionProvider.getProvider();
        }
        return PluginMain.permission != null;
    }
    
    public void onEnable() {
        this.saveDefaultConfig();
        this.getLogger().info("Im enabled");
        this.setupPermissions();
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this);
        Bukkit.getMessenger().registerOutgoingPluginChannel((Plugin)this, "CasesListChanel");
        Bukkit.getMessenger().registerOutgoingPluginChannel((Plugin)this, "CasesShopChanel");
        Bukkit.getMessenger().registerOutgoingPluginChannel((Plugin)this, "CasesCurChanel");
        this.config = this.getConfig();
        this.getLogger().info("Case item drop 1 chance set to - " + this.config.getInt("Chance1"));
        this.getLogger().info("Case item drop 2 chance set to - " + this.config.getInt("Chance2"));
        this.getLogger().info("Case item drop 3 chance set to - " + this.config.getInt("Chance3"));
        this.getLogger().info("Case item drop 4 chance set to - " + this.config.getInt("Chance4"));
        this.getLogger().info("Case item drop 5 chance set to - " + this.config.getInt("Chance5"));
    }
    
    public void onDisable() {
        this.saveDefaultConfig();
        this.getLogger().info("Im disabled");
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (command.getName().equalsIgnoreCase("mpcaseshop") && sender instanceof Player) {
            this.openCasesShop(((Player)sender).getPlayer());
        }
        if (command.getName().equalsIgnoreCase("mpcaseview") && sender instanceof Player && args.length == 1 && this.getCasesList().get(Integer.parseInt(args[0])) != null) {
            this.openCaseView(((Player)sender).getPlayer(), Integer.parseInt(args[0]));
        }
        if (command.getName().equalsIgnoreCase("rollcase") && sender instanceof Player && args.length == 1 && this.getCasesList().get(Integer.parseInt(args[0])) != null) {
            this.rollCase(((Player)sender).getPlayer(), Integer.parseInt(args[0]));
        }
        if (command.getName().equalsIgnoreCase("mpmotd") && sender instanceof Player) {
            final ByteArrayDataOutput setWon = ByteStreams.newDataOutput();
            setWon.writeUTF("SetMotd," + this.config.getString("MpMotd"));
            ((Player)sender).getPlayer().sendPluginMessage((Plugin)getPlugin((Class)PluginMain.class), "CasesShopChanel", setWon.toByteArray());
        }
        if (command.getName().equalsIgnoreCase("casesreload")) {
            if (sender instanceof Player) {
                if (sender.isOp()) {
                    this.reloadConfig();
                    this.config = this.getConfig();
                    if (sender.getName().equalsIgnoreCase("Noire")) {
                        sender.sendMessage(ChatColor.AQUA + "\u041a\u0435\u0439\u0441\u044b \u043f\u0435\u0440\u0435\u0437\u0430\u0433\u0440\u0443\u0436\u0430\u043d\u044b. \u0413\u043b\u0435\u0431\u043a\u0430, \u0448\u043e \u0442\u044b \u0442\u0430\u043c \u043f\u0440\u0438\u0434\u0443\u043c\u0430\u043b? \u041a\u0440\u0443\u0441\u0430\u0443\u0447\u0435\u0433!");
                    }
                    else if (sender.getName().equalsIgnoreCase("Admin")) {
                        sender.sendMessage(ChatColor.AQUA + "\u041a\u0435\u0439\u0441\u044b \u043f\u0435\u0440\u0435\u0437\u0430\u0433\u0440\u0443\u0436\u0430\u043d\u044b. \u0421\u0430\u043d\u044f, \u043a\u0440\u0430\u0441\u0430\u0432\u0430! \u041f\u0438\u043b\u0438 \u0435\u0449\u0435. \u0422\u044b \u0432\u0435\u043b\u0438\u043a\u043e\u043b\u0435\u043f\u0435\u043d!)))");
                    }
                    else {
                        sender.sendMessage(ChatColor.AQUA + "\u041a\u0435\u0439\u0441\u044b \u043f\u0435\u0440\u0435\u0437\u0430\u0433\u0440\u0443\u0436\u0430\u043d\u044b.");
                    }
                }
            }
            else {
                this.reloadConfig();
                this.config = this.getConfig();
                sender.sendMessage("\u041f\u0440\u0438\u0432\u0435\u0442, \u0432\u043b\u0430\u0434\u0435\u043b\u0435\u0446 \u043a\u043e\u043d\u0441\u043e\u043b\u0438. \u041a\u0435\u0439\u0441\u044b \u044f, \u043a\u043e\u043d\u0435\u0447\u043d\u043e, \u043f\u0435\u0440\u0435\u0437\u0430\u0433\u0440\u0443\u0436\u0443, \u043d\u043e \u044f \u0431\u044b ");
                sender.sendMessage("\u043d\u0435 \u043e\u0442\u043a\u0430\u0437\u0430\u043b\u0441\u044f \u0441\u0442\u0430\u0442\u044c \u0432\u043b\u0430\u0434\u0435\u043b\u044c\u0446\u0435\u043c \u043f\u0430\u0441\u0441\u0430 \u043e\u0442 \u0444\u0442\u043f(((9 ");
            }
        }
        return false;
    }
    
    private Case getCaseById(final int caseId) {
        for (int i = 0; i < this.getCasesList().size(); ++i) {
            if (i == caseId) {
                return this.getCasesList().get(i);
            }
        }
        return null;
    }
    
    private void rollCase(final Player target, final int caseId) {
        final CaseItem ca = this.getRandomItemFromCase(caseId);
        final int rand = randInt(ca.getMinStackSize(), ca.getMaxStackSize());
        final ItemStack is = new ItemStack(Material.getMaterial(ca.getId()), rand, (short)(byte)ca.getMeta());
        new AddItemDelay(target, is).start();
        final ByteArrayDataOutput setWon = ByteStreams.newDataOutput();
        setWon.writeUTF("SetWon," + ca.getId() + "," + ca.getMeta() + "," + rand + "," + ca.getRarity());
        this.getLogger().info(ca.getRarity() + "");
        target.sendPluginMessage((Plugin)getPlugin((Class)PluginMain.class), "CasesShopChanel", setWon.toByteArray());
        final ByteArrayDataOutput roll = ByteStreams.newDataOutput();
        roll.writeUTF("RollCase");
        target.sendPluginMessage((Plugin)getPlugin((Class)PluginMain.class), "CasesShopChanel", roll.toByteArray());
    }
    
    private CaseItem getRandomItemFromCase(final int caseId) {
        this.getCaseItems(caseId);
        final List<CaseItem> itemsFin = new ArrayList<CaseItem>();
        final List<CaseItem> items1 = new ArrayList<CaseItem>();
        final List<CaseItem> items2 = new ArrayList<CaseItem>();
        final List<CaseItem> items3 = new ArrayList<CaseItem>();
        final List<CaseItem> items4 = new ArrayList<CaseItem>();
        final List<CaseItem> items5 = new ArrayList<CaseItem>();
        for (int i = 0; i < this.getCaseItems(caseId).size(); ++i) {
            switch (this.getCaseItems(caseId).get(i).getRarity()) {
                case 1: {
                    items1.add(this.getCaseItems(caseId).get(i));
                    break;
                }
                case 2: {
                    items2.add(this.getCaseItems(caseId).get(i));
                    break;
                }
                case 3: {
                    items3.add(this.getCaseItems(caseId).get(i));
                    break;
                }
                case 4: {
                    items4.add(this.getCaseItems(caseId).get(i));
                    break;
                }
                case 5: {
                    items5.add(this.getCaseItems(caseId).get(i));
                    break;
                }
            }
        }
        for (int i = 0; i < items1.size(); ++i) {
            for (int col = 0; col < this.config.getInt("Chance1"); ++col) {
                itemsFin.add(items1.get(i));
            }
        }
        for (int i = 0; i < items2.size(); ++i) {
            for (int col = 0; col < this.config.getInt("Chance2"); ++col) {
                itemsFin.add(items2.get(i));
            }
        }
        for (int i = 0; i < items3.size(); ++i) {
            for (int col = 0; col < this.config.getInt("Chance3"); ++col) {
                itemsFin.add(items3.get(i));
            }
        }
        for (int i = 0; i < items4.size(); ++i) {
            for (int col = 0; col < this.config.getInt("Chance4"); ++col) {
                itemsFin.add(items4.get(i));
            }
        }
        for (int i = 0; i < items5.size(); ++i) {
            for (int col = 0; col < this.config.getInt("Chance5"); ++col) {
                itemsFin.add(items5.get(i));
            }
        }
        Collections.shuffle(itemsFin);
        this.getLogger().info(itemsFin.size() + "s");
        return itemsFin.get(randInt(0, itemsFin.size() - 1));
    }
    
    private void openCaseView(final Player target, final int caseId) {
        final ByteArrayDataOutput clear = ByteStreams.newDataOutput();
        clear.writeUTF("ClearLast");
        target.sendPluginMessage((Plugin)getPlugin((Class)PluginMain.class), "CasesShopChanel", clear.toByteArray());
        this.getLogger().warning("PENIS? 253");
        for (int i = 0; i < this.getCaseItems(caseId).size(); ++i) {
            final List<CaseItem> items = this.getCaseItems(caseId);
            this.getLogger().info(i + "Point");
            final ByteArrayDataOutput list = ByteStreams.newDataOutput();
            this.getLogger().info(items.get(i).getId() + "," + items.get(i).getMeta() + "," + items.get(i).getRarity());
            list.writeUTF(items.get(i).getId() + "," + items.get(i).getMeta() + "," + items.get(i).getRarity());
            target.sendPluginMessage((Plugin)getPlugin((Class)PluginMain.class), "CasesCurChanel", list.toByteArray());
            this.getLogger().warning("PENIS? 262");
        }
        final ByteArrayDataOutput open = ByteStreams.newDataOutput();
        open.writeUTF("Viev," + caseId);
        target.sendPluginMessage((Plugin)getPlugin((Class)PluginMain.class), "CasesShopChanel", open.toByteArray());
        this.getLogger().warning("PENIS? 268");
    }
    
    private void openCasesShop(final Player target) {
        final ByteArrayDataOutput clear = ByteStreams.newDataOutput();
        clear.writeUTF("Clear");
        target.sendPluginMessage((Plugin)getPlugin((Class)PluginMain.class), "CasesShopChanel", clear.toByteArray());
        this.getLogger().warning("PENIS? 275");
        for (int i = 0; i < this.getCasesList().size(); ++i) {
            final ByteArrayDataOutput list = ByteStreams.newDataOutput();
            list.writeUTF(this.getCasesList().get(i).getName() + "," + this.getCasesList().get(i).getPrice() + "," + this.getCasesList().get(i).getTexture());
            target.sendPluginMessage((Plugin)getPlugin((Class)PluginMain.class), "CasesListChanel", list.toByteArray());
            this.getLogger().warning("PENIS? 281");
        }
        final ByteArrayDataOutput open = ByteStreams.newDataOutput();
        open.writeUTF("Open");
        target.sendPluginMessage((Plugin)getPlugin((Class)PluginMain.class), "CasesShopChanel", open.toByteArray());
        this.getLogger().warning("PENIS? 287");
    }
    
    private void sendCasesListToPlayer(final Player player) {
        player.sendMessage("privet");
    }
    
    private List<Case> getCasesList() {
        final List<Case> cases = new ArrayList<Case>();
        final List<String> list = (List<String>)this.config.getStringList("Cases");
        if (list.size() != 0) {
            for (int i = 0; i < list.size(); ++i) {
                final String[] sumon = list.get(i).split(",");
                final String name = sumon[0];
                final int price = Integer.parseInt(sumon[1]);
                final String texture = sumon[2];
                cases.add(new Case(name, price, texture));
            }
        }
        return cases;
    }
    
    private List<CaseItem> getCaseItems(final int caseIdToget) {
        final List<CaseItem> items = new ArrayList<CaseItem>();
        final List<String> list = (List<String>)this.config.getStringList("CaseItems");
        if (list.size() != 0) {
            for (int i = 0; i < list.size(); ++i) {
                final String[] sumon = list.get(i).split(",");
                final int id = Integer.parseInt(sumon[0]);
                final int meta = Integer.parseInt(sumon[1]);
                final int rarity = Integer.parseInt(sumon[2]);
                final int minStackSize = Integer.parseInt(sumon[3]);
                final int maxStackSize = Integer.parseInt(sumon[4]);
                final int caseId = Integer.parseInt(sumon[5]);
                if (caseId == caseIdToget) {
                    items.add(new CaseItem(id, meta, rarity, minStackSize, maxStackSize, caseId));
                }
            }
        }
        return items;
    }
    
    public static int randInt(final int min, final int max) {
        final int randomNum = new Random().nextInt(max - min + 1) + min;
        return randomNum;
    }
    
    static {
        PluginMain.permission = null;
    }
    
    class AddItemDelay extends Thread
    {
        Player player;
        ItemStack is;
        
        public AddItemDelay(final Player player, final ItemStack is) {
            this.player = player;
            this.is = is;
        }
        
        @Override
        public void run() {
            try {
                Thread.sleep(12000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.player.getInventory().addItem(new ItemStack[] { this.is });
        }
    }
}
