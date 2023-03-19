package org.xuvigan.atlantacases;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import ru.imine.shared.util.Discord;
public class PluginMain extends JavaPlugin implements Listener {
    public static Permission permission = null;
    private FileConfiguration config;
    public PluginMain() {
    }
    private void setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = this.getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = (Permission)permissionProvider.getProvider();
        }

    }
    public void onEnable() {
        this.saveDefaultConfig();
        this.getLogger().info("Im enabled");
        this.setupPermissions();
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "CasesListChanel");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "CasesShopChanel");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "CasesCurChanel");
        this.config = this.getConfig();
    }

    public void onDisable() {
        this.saveDefaultConfig();
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
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            try {
                dos.writeUTF("SetMotd," + this.config.getString("MpMotd"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ((Player)sender).getPlayer().sendPluginMessage(getPlugin(PluginMain.class), "CasesShopChanel", bos.toByteArray());
        }

        if (command.getName().equalsIgnoreCase("casesreload")) {
            if (sender instanceof Player) {
                if (sender.isOp()) {
                    this.reloadConfig();
                    this.config = this.getConfig();
                    if (sender.getName().equalsIgnoreCase("XuViGaN")) {
                        sender.sendMessage(ChatColor.AQUA + "Кейсы перезагружаны");
                    }
                }
            } else {
                this.reloadConfig();
                this.config = this.getConfig();
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
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://skala1.beget.tech:3306/skala1_atlanta?useSSL=false", "skala1_atlanta", "Alekcey2009@@");
            PreparedStatement statement = connection.prepareStatement("SELECT balance_real FROM users WHERE username = ?");
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            int balance = 0;
            if (resultSet.next()) {
                balance = resultSet.getInt("balance_real");
            }
            resultSet.close();
            statement.close();
            connection.close();
            return balance;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getCasePrice(int caseId) {
        ConfigurationSection caseSection = getConfig().getConfigurationSection("Cases");
        String caseName = getCaseNameById(caseId);
        if (caseSection.contains(caseName)) {
            return caseSection.getInt(caseName + ".price");
        } else {
            // Кейс не найден в конфиге, возвращаем цену по умолчанию
            return 0;
        }
    }

    private String getCaseNameById(int caseId) {
        ConfigurationSection caseSection = getConfig().getConfigurationSection("Cases");
        for (String caseName : caseSection.getKeys(false)) {
            if (caseSection.getInt(caseName + ".id") == caseId) {
                return caseName;
            }
        }
        return null;
    }

    private void rollCase(Player target, int caseId) {
        int casePrice = getCasePrice(caseId);
        int playerBalance = getPlayerBalanceFromDB(target.getName());
        if (playerBalance >= casePrice) {
            CaseItem ca = this.getRandomItemFromCase();
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
            target.sendMessage("У вас недостаточно денег на балансе для покупки кейса!");
        }
    }

    private CaseItem getRandomItemFromCase() {
        this.getCaseItems();
        List<CaseItem> itemsFin = new ArrayList<>();
        List<CaseItem> items1 = new ArrayList<>();
        List<CaseItem> items2 = new ArrayList<>();
        List<CaseItem> items3 = new ArrayList<>();
        List<CaseItem> items4 = new ArrayList<>();
        List<CaseItem> items5 = new ArrayList<>();

        int i;
        for(i = 0; i < this.getCaseItems().size(); ++i) {
            switch (((CaseItem)this.getCaseItems().get(i)).getRarity()) {
                case 1:
                    items1.add(this.getCaseItems().get(i));
                    break;
                case 2:
                    items2.add(this.getCaseItems().get(i));
                    break;
                case 3:
                    items3.add(this.getCaseItems().get(i));
                    break;
                case 4:
                    items4.add(this.getCaseItems().get(i));
                    break;
                case 5:
                    items5.add(this.getCaseItems().get(i));
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
        for(int i = 0; i < this.getCaseItems().size(); ++i) {
            List<CaseItem> items = this.getCaseItems();
            ByteArrayDataOutput list = ByteStreams.newDataOutput();
            list.writeUTF(((CaseItem)items.get(i)).getId() + "," + ((CaseItem)items.get(i)).getMeta() + "," + ((CaseItem)items.get(i)).getRarity());
            target.sendPluginMessage(getPlugin(PluginMain.class), "CasesCurChanel", list.toByteArray());
        }

        ByteArrayDataOutput open = ByteStreams.newDataOutput();
        open.writeUTF("Viev," + caseId);
        target.sendPluginMessage(getPlugin(PluginMain.class), "CasesShopChanel", open.toByteArray());
    }

    private void openCasesShop(Player target) {
        ByteArrayDataOutput clear = ByteStreams.newDataOutput();
        clear.writeUTF("Clear");
        target.sendPluginMessage(getPlugin(PluginMain.class), "CasesShopChanel", clear.toByteArray());
        for(int i = 0; i < this.getCasesList().size(); ++i) {
            ByteArrayDataOutput list = ByteStreams.newDataOutput();
            list.writeUTF(((Case)this.getCasesList().get(i)).getName() + "," + ((Case)this.getCasesList().get(i)).getPrice() + "," + ((Case)this.getCasesList().get(i)).getTexture());
            target.sendPluginMessage(getPlugin(PluginMain.class), "CasesListChanel", list.toByteArray());
        }
        ByteArrayDataOutput open = ByteStreams.newDataOutput();
        open.writeUTF("Open");
        target.sendPluginMessage(getPlugin(PluginMain.class), "CasesShopChanel", open.toByteArray());
    }

    private void sendCasesListToPlayer(Player player) {
        StringBuilder message = new StringBuilder();
        for(Case caseObj : this.getCasesList()) {
            message.append(caseObj.getName()).append(": ").append(caseObj.getPrice()).append("\n");
        }
        player.sendMessage("Available cases:\n" + message.toString());
    }

    private List<Case> getCasesList() {
        List<Case> cases = new ArrayList<>();
        List<String> list = this.config.getStringList("Cases");
        if (list != null && !list.isEmpty() && config.contains("Cases")) {
            for (String item : list) {
                String[] sumon = item.split(",");
                if (sumon.length != 3) {
                    // обработка ошибки
                    continue;
                }
                String name = sumon[0];
                int price;
                try {
                    price = Integer.parseInt(sumon[1]);
                } catch (NumberFormatException e) {
                    // обработка ошибки
                    continue;
                }
                String texture = sumon[2];
                cases.add(new Case(name, price, texture));
            }
        }
        return cases;
    }

    private List<CaseItem> getCaseItems() {
        List<ItemStack> items = new ArrayList<ItemStack>();

        for (String itemString : getConfig().getStringList("CaseItems")) {
            String[] itemParts = itemString.split(",");
            int id = Integer.parseInt(itemParts[0]);
            int data = Integer.parseInt(itemParts[1]);
            int amount = Integer.parseInt(itemParts[2]);
            int chance = Integer.parseInt(itemParts[3]);
            int maxPerCase = Integer.parseInt(itemParts[4]);
            int customData = Integer.parseInt(itemParts[5]);

            // If it's a custom item, get the custom data
            if (id == 422) {
                data = customData;
            }

            // Create the ItemStack and add it to the list
            ItemStack itemStack = new ItemStack(id, amount, (short) data);
            items.add(itemStack);
        }
    }

    public static int randInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
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