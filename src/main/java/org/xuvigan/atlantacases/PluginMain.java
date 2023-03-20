package org.xuvigan.atlantacases;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import ru.imine.shared.util.Discord;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class PluginMain extends JavaPlugin implements Listener {

    private void setBalanceInDB(String name, int balance) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://skala1.beget.tech:3306/skala1_atlanta?useSSL=false", "skala1_atlanta", "Alekcey2009@@");
            PreparedStatement statement = connection.prepareStatement("UPDATE users SET balance_real = ? WHERE username = ?");
            statement.setInt(1, balance);
            statement.setString(2, name);
            statement.executeUpdate();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getPlayerBalanceFromDB(String name) {
        int balance = 0;
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://skala1.beget.tech:3306/skala1_atlanta?useSSL=false", "skala1_atlanta", "Alekcey2009@@");
            PreparedStatement statement = connection.prepareStatement("SELECT balance_real FROM users WHERE username = ?");
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                balance = resultSet.getInt("balance_real");
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }

    public static Permission permission = null;
    private FileConfiguration config;

    public PluginMain() {
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = this.getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = (Permission)permissionProvider.getProvider();
        }

        return permission != null;
    }

    public void onEnable() {
        this.saveDefaultConfig();
        this.getLogger().info("Im enabled");
        this.setupPermissions();
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "CasesListChanel");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "CasesShopChanel");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "CasesCurChanel");

        // Load configuration from JSON file
        ObjectMapper mapper = new ObjectMapper();
        Config config;
        try {
            config = mapper.readValue(new File(getDataFolder(), "config.json"), Config.class);
        } catch (IOException e) {
            // If the file doesn't exist or can't be read, use default values
            config = new Config();
            config.setDatabaseUrl("jdbc:mysql://localhost:3306/mydatabase");
            config.setDatabaseUsername("myusername");
            config.setDatabasePassword("mypassword");
            config.setDebug(false);
            config.setLogLevel("INFO");
            config.setMaxConnections(10);
        }

        // Write default configuration to file if it doesn't exist
        if (!new File(getDataFolder(), "config.json").exists()) {
            try {
                mapper.writerWithDefaultPrettyPrinter().writeValue(new File(getDataFolder(), "config.json"), config);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDisable() {
        this.saveDefaultConfig();
        this.getLogger().info("Im disabled");
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
            ByteArrayDataOutput setWon = ByteStreams.newDataOutput();
            setWon.writeUTF("SetMotd," + this.config.getString("MpMotd"));
            ((Player)sender).getPlayer().sendPluginMessage(getPlugin(PluginMain.class), "CasesShopChanel", setWon.toByteArray());
        }

        if (command.getName().equalsIgnoreCase("casesreload")) {
            if (sender instanceof Player) {
                if (sender.isOp()) {
                    this.reloadConfig();
                    this.config = this.getConfig();
                    if (sender.getName().equalsIgnoreCase("Noire")) {
                        sender.sendMessage(ChatColor.AQUA + "Кейсы перезагружаны. Глебка, шо ты там придумал? Крусаучег!");
                    } else if (sender.getName().equalsIgnoreCase("Admin")) {
                        sender.sendMessage(ChatColor.AQUA + "Кейсы перезагружаны. Саня, красава! Пили еще. Ты великолепен!)))");
                    } else {
                        sender.sendMessage(ChatColor.AQUA + "Кейсы перезагружаны.");
                    }
                }
            } else {
                this.reloadConfig();
                this.config = this.getConfig();
                sender.sendMessage("Привет, владелец консоли. Кейсы я, конечно, перезагружу, но я бы ");
                sender.sendMessage("не отказался стать владельцем пасса от фтп(((9 ");
            }
        }

        return false;
    }

    private Case getCaseById(int caseId) {
        for(int i = 0; i < this.getCasesList().size(); ++i) {
            if (i == caseId) {
                return (Case)this.getCasesList().get(i);
            }
        }
        return null;
    }

    private void rollCase(Player target, int caseId) {
        int casePrice = CasePriceManager.getCasePrice(caseId);
        int playerBalance = getPlayerBalanceFromDB(target.getName());
        if (playerBalance >= casePrice) {
            CaseItem ca = this.getRandomItemFromCase(caseId);
            int rand = randInt(ca.getMinStackSize(), ca.getMaxStackSize());
            ItemStack is = new ItemStack(Material.getMaterial(ca.getId()), rand, (short)((byte)ca.getMeta()));
            (new AddItemDelay(target, is)).start();
            ByteArrayDataOutput setWon = ByteStreams.newDataOutput();
            setWon.writeUTF("SetWon," + ca.getId() + "," + ca.getMeta() + "," + rand + "," + ca.getRarity());
            this.getLogger().info(ca.getRarity() + "");
            target.sendPluginMessage(getPlugin(PluginMain.class), "CasesShopChanel", setWon.toByteArray());
            ByteArrayDataOutput roll = ByteStreams.newDataOutput();
            roll.writeUTF("RollCase");
            target.sendPluginMessage(getPlugin(PluginMain.class), "CasesShopChanel", roll.toByteArray());
            setBalanceInDB(target.getName(), playerBalance - casePrice);
        } else {
            target.sendMessage("You don't have enough money to buy this case!");
        }
    }

    private CaseItem getRandomItemFromCase(int caseId) {
        this.getCaseItems(caseId);
        List<CaseItem> itemsFin = new ArrayList();
        List<CaseItem> items1 = new ArrayList();
        List<CaseItem> items2 = new ArrayList();
        List<CaseItem> items3 = new ArrayList();
        List<CaseItem> items4 = new ArrayList();
        List<CaseItem> items5 = new ArrayList();

        int i;
        for(i = 0; i < this.getCaseItems(caseId).size(); ++i) {
            switch (((CaseItem)this.getCaseItems(caseId).get(i)).getRarity()) {
                case 1:
                    items1.add(this.getCaseItems(caseId).get(i));
                    break;
                case 2:
                    items2.add(this.getCaseItems(caseId).get(i));
                    break;
                case 3:
                    items3.add(this.getCaseItems(caseId).get(i));
                    break;
                case 4:
                    items4.add(this.getCaseItems(caseId).get(i));
                    break;
                case 5:
                    items5.add(this.getCaseItems(caseId).get(i));
            }
        }

        int col;
        for(i = 0; i < items1.size(); ++i) {
            for(col = 0; col < this.config.getInt("Chance1"); ++col) {
                itemsFin.add(items1.get(i));
            }
        }

        for(i = 0; i < items2.size(); ++i) {
            for(col = 0; col < this.config.getInt("Chance2"); ++col) {
                itemsFin.add(items2.get(i));
            }
        }

        for(i = 0; i < items3.size(); ++i) {
            for(col = 0; col < this.config.getInt("Chance3"); ++col) {
                itemsFin.add(items3.get(i));
            }
        }

        for(i = 0; i < items4.size(); ++i) {
            for(col = 0; col < this.config.getInt("Chance4"); ++col) {
                itemsFin.add(items4.get(i));
            }
        }

        for(i = 0; i < items5.size(); ++i) {
            for(col = 0; col < this.config.getInt("Chance5"); ++col) {
                itemsFin.add(items5.get(i));
            }
        }

        Collections.shuffle(itemsFin);
        this.getLogger().info(itemsFin.size() + "s");
        return (CaseItem)itemsFin.get(randInt(0, itemsFin.size() - 1));
    }

    private void openCaseView(Player target, int caseId) {
        ByteArrayDataOutput clear = ByteStreams.newDataOutput();
        clear.writeUTF("ClearLast");
        target.sendPluginMessage(getPlugin(PluginMain.class), "CasesShopChanel", clear.toByteArray());
        this.getLogger().warning("PENIS? 253");

        for(int i = 0; i < this.getCaseItems(caseId).size(); ++i) {
            List<CaseItem> items = this.getCaseItems(caseId);
            this.getLogger().info(i + "Point");
            ByteArrayDataOutput list = ByteStreams.newDataOutput();
            this.getLogger().info(((CaseItem)items.get(i)).getId() + "," + ((CaseItem)items.get(i)).getMeta() + "," + ((CaseItem)items.get(i)).getRarity());
            list.writeUTF(((CaseItem)items.get(i)).getId() + "," + ((CaseItem)items.get(i)).getMeta() + "," + ((CaseItem)items.get(i)).getRarity());
            target.sendPluginMessage(getPlugin(PluginMain.class), "CasesCurChanel", list.toByteArray());
            this.getLogger().warning("PENIS? 262");
        }

        ByteArrayDataOutput open = ByteStreams.newDataOutput();
        open.writeUTF("Viev," + caseId);
        target.sendPluginMessage(getPlugin(PluginMain.class), "CasesShopChanel", open.toByteArray());
        this.getLogger().warning("PENIS? 268");
    }

    private void openCasesShop(Player target) {
        ByteArrayDataOutput clear = ByteStreams.newDataOutput();
        clear.writeUTF("Clear");
        target.sendPluginMessage(getPlugin(PluginMain.class), "CasesShopChanel", clear.toByteArray());
        this.getLogger().warning("PENIS? 275");

        for(int i = 0; i < this.getCasesList().size(); ++i) {
            ByteArrayDataOutput list = ByteStreams.newDataOutput();
            list.writeUTF(((Case)this.getCasesList().get(i)).getName() + "," + ((Case)this.getCasesList().get(i)).getPrice() + "," + ((Case)this.getCasesList().get(i)).getTexture());
            target.sendPluginMessage(getPlugin(PluginMain.class), "CasesListChanel", list.toByteArray());
            this.getLogger().warning("PENIS? 281");
        }

        ByteArrayDataOutput open = ByteStreams.newDataOutput();
        open.writeUTF("Open");
        target.sendPluginMessage(getPlugin(PluginMain.class), "CasesShopChanel", open.toByteArray());
        this.getLogger().warning("PENIS? 287");
    }

    private void sendCasesListToPlayer(Player player) {
        player.sendMessage("privet");
    }

    private List<Case> getCasesList() {
        List<Case> cases = new ArrayList();
        List<String> list = this.config.getStringList("Cases");
        if (list.size() != 0) {
            for(int i = 0; i < list.size(); ++i) {
                String[] sumon = ((String)list.get(i)).split(",");
                String name = sumon[0];
                int price = Integer.parseInt(sumon[1]);
                String texture = sumon[2];
                cases.add(new Case(name, price, texture));
            }
        }

        return cases;
    }

    private List<CaseItem> getCaseItems(int caseIdToget) {
        List<CaseItem> items = new ArrayList();
        List<String> list = this.config.getStringList("CaseItems");
        if (list.size() != 0) {
            for(int i = 0; i < list.size(); ++i) {
                String[] sumon = ((String)list.get(i)).split(",");
                int id = Integer.parseInt(sumon[0]);
                int meta = Integer.parseInt(sumon[1]);
                int rarity = Integer.parseInt(sumon[2]);
                int minStackSize = Integer.parseInt(sumon[3]);
                int maxStackSize = Integer.parseInt(sumon[4]);
                int caseId = Integer.parseInt(sumon[5]);
                if (caseId == caseIdToget) {
                    items.add(new CaseItem(id, meta, rarity, minStackSize, maxStackSize, caseId));
                }
            }
        }

        return items;
    }

    public static int randInt(int min, int max) {
        int randomNum = (new Random()).nextInt(max - min + 1) + min;
        return randomNum;
    }

    static class AddItemDelay extends Thread {
        Player player;
        ItemStack is;

        public AddItemDelay(Player player, ItemStack is) {
            this.player = player;
            this.is = is;
        }

        public void run() {
            String playerName = player.getPlayerListName();
            try {
                Thread.sleep(12000L);
            } catch (InterruptedException var2) {
                var2.printStackTrace();
            }
            this.player.getInventory().addItem(this.is);
            String message = playerName + " При открытии кейса получил: " + this.is.getType().toString();
            Bukkit.getServer().broadcastMessage(message); //Отправляем сообщение в общий чат при получении предмета
            Discord.instance.sendErrorLog("Открытие кейса", "Игрок " + playerName + " получил предмет: " + this.is.getType().toString()); //Отправляет информацию на закрытый канал для сбора информации
        }
    }
}