package org.xuvigan.atlantacases;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
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

public class PluginMain extends JavaPlugin implements Listener {
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

    class AddItemDelay extends Thread {
        Player player;
        ItemStack is;

        public AddItemDelay(Player player, ItemStack is) {
            this.player = player;
            this.is = is;
        }

        public void run() {
            try {
                Thread.sleep(12000L);
            } catch (InterruptedException var2) {
                var2.printStackTrace();
            }

            this.player.getInventory().addItem(new ItemStack[]{this.is});
        }
    }
}