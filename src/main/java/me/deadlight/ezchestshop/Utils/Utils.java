package me.deadlight.ezchestshop.Utils;
import me.deadlight.ezchestshop.Commands.Ecsadmin;
import me.deadlight.ezchestshop.Commands.MainCommands;
import me.deadlight.ezchestshop.EzChestShop;
import me.deadlight.ezchestshop.LanguageManager;
import me.deadlight.ezchestshop.Listeners.ChatListener;
import me.deadlight.ezchestshop.Listeners.PlayerLookingAtChestShop;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Utils {

    public static List<Object> onlinePackets = new ArrayList<>();

    public static String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static void storeItem(ItemStack item, PersistentDataContainer data) throws IOException {

        try {
            ByteArrayOutputStream io = new ByteArrayOutputStream();
            BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);

            os.writeObject(item);

            os.flush();
            byte[] rawData = io.toByteArray();

            String encodedData = Base64.getEncoder().encodeToString(rawData);

            data.set(new NamespacedKey(EzChestShop.getPlugin(), "item"), PersistentDataType.STRING, encodedData);
            os.close();

        } catch (IOException ex) {
            System.out.println(ex);
        }


    }

    public static ItemStack getItem(String encodedItem) {

        byte[] rawData = Base64.getDecoder().decode(encodedItem);

        try {

            ByteArrayInputStream io = new ByteArrayInputStream(rawData);
            BukkitObjectInputStream in = new BukkitObjectInputStream(io);

            ItemStack thatItem = (ItemStack) in.readObject();

            in.close();

            return thatItem;

        } catch (IOException | ClassNotFoundException ex) {
            System.out.println(ex);
            return null;
        }

    }


    public static void reloadConfigs() {

        //reloading config.yml

        EzChestShop.getPlugin().reloadConfig();
        FileConfiguration config = EzChestShop.getPlugin().getConfig();
        PlayerLookingAtChestShop.showholo = config.getBoolean("show-holograms");
        PlayerLookingAtChestShop.firstLine = config.getString("hologram-first-line");
        PlayerLookingAtChestShop.secondLine = config.getString("hologram-second-line");
        PlayerLookingAtChestShop.holodelay = config.getInt("hologram-disappearance-delay");

    }

    public static void reloadLanguages() {
        FileConfiguration fc = YamlConfiguration.loadConfiguration(new File(EzChestShop.getPlugin().getDataFolder(), "languages.yml"));
        EzChestShop.setLanguages(fc);
        LanguageManager newLanguage = new LanguageManager();
        MainCommands.updateLM(newLanguage);
        ChatListener.updateLM(newLanguage);
        Ecsadmin.updateLM(newLanguage);
    }

    //this one checks for the config.yml ima make one for language.yml
    public static void checkForConfigYMLupdate() throws IOException {

        //update 1.2.4 config.yml
        boolean result = YamlConfiguration.loadConfiguration(new File(EzChestShop.getPlugin().getDataFolder(), "config.yml")).isInt("hologram-disappearance-delay");
        if (!result) {
            FileConfiguration fc = YamlConfiguration.loadConfiguration(new File(EzChestShop.getPlugin().getDataFolder(), "config.yml"));
            fc.set("hologram-disappearance-delay", 10);
            fc.save(new File(EzChestShop.getPlugin().getDataFolder(), "config.yml"));

            Utils.reloadConfigs();
        }
    }

    public static void checkForLanguagesYMLupdate() throws IOException {

        //update 1.2.8 Languages
        boolean result = YamlConfiguration.loadConfiguration(new File(EzChestShop.getPlugin().getDataFolder(), "languages.yml")).isString("commandmsg-negativeprice");
        boolean update1_3_0 = YamlConfiguration.loadConfiguration(new File(EzChestShop.getPlugin().getDataFolder(), "languages.yml")).isString("settingsButton");
        if (!result) {
            FileConfiguration fc = YamlConfiguration.loadConfiguration(new File(EzChestShop.getPlugin().getDataFolder(), "languages.yml"));
            //new values that were added in update 1.2.8
            fc.set("commandmsg-negativeprice", "&cNegative price? but you have to use positive price...");
            fc.set("commandmsg-notenoughargs", "&cYou haven't provided enough arguments! \\n &cCorrect usage: /ecs create (Buy price) (Sell price)");
            fc.set("commandmsg-consolenotallowed", "&cYou are not allowed to execute any command from console.");
            fc.set("commandmsg-help", "&7- &c/ecs create (Buy Price) (Sell Price) &7| Create a chest shop by looking at a chest and having the item that you want to sell in your hand. \n &7- &c/ecs remove &7| Removes the chest shop that you are looking at \n &7Eazy right? :)");
            fc.set("commandmsg-alreadyashop", "&cThis chest is already a shop!");
            fc.set("commandmsg-shopcreated", "&aYou have successfully created a chest shop!");
            fc.set("commandmsg-holdsomething", "&cPlease hold something in your main hand!");
            fc.set("commandmsg-notallowdtocreate", "&cYou are not allowed to create/remove a chest shop in this location.");
            fc.set("commandmsg-notchest", "&cThe block that you are looking at is not supported type of chest/is not a chest.");
            fc.set("commandmsg-lookatchest", "&cPlease look at a chest.");
            fc.set("commandmsg-csremoved", "&eThis chest shop successfully removed.");
            fc.set("commandmsg-notowner", "&aYou are not the owner of this chest shop!");
            fc.set("commandmsg-notachestorcs", "&cThe block that you are looking at is not a chest/or this is not a chest shop.");
            fc.save(new File(EzChestShop.getPlugin().getDataFolder(), "languages.yml"));

            reloadLanguages();
            EzChestShop.getPlugin().logConsole("&c[&eEzChestShop&c]&r &bNew languages.yml generated...");
        }

        if (!update1_3_0) {
            FileConfiguration fc = YamlConfiguration.loadConfiguration(new File(EzChestShop.getPlugin().getDataFolder(), "languages.yml"));
            //for update 1.3.0
            fc.set("settingsButton", "&b&lSettings");
            fc.set("disabledButtonTitle", "&cDisabled");
            fc.set("disabledButtonLore", "&7This option is disabled by \n &7the shop owner.");
            fc.set("transactionButtonTitle", "&aTransaction logs");
            fc.set("backToSettingsButton", "&eBack to settings");
            fc.set("transactionPaperTitleBuy", "&aBuy | %player%");
            fc.set("transactionPaperTitleSell", "&cSell | %player%");
            fc.set("transactionPaperLoreBuy", "&7Total Price: %price% \n &7Quantity: %count% \n &7Transaction Type: &aBought from you \n &e%time%");
            fc.set("transactionPaperLoreSell", "&7Total Price: %price% \n &7Quantity: %count% \n &7Transaction Type: &cSold to you \n &e%time%");
            fc.set("lessthanminute", "&eless than a minute ago");
            fc.set("minutesago", "&e%minutes% minute(s) ago");
            fc.set("hoursago", "&e%hours% hour(s) ago");
            fc.set("daysago", "&e%days% days ago");
            fc.set("adminshopguititle", "&cAdmin shop");
            fc.set("settingsGuiTitle", "&b&lSettings");
            fc.set("latestTransactionsButton", "&aLatest Transactions");
            fc.set("toggleTransactionMessageButton", "&eToggle Transaction Message");
            fc.set("statusOn", "&aOn");
            fc.set("statusOff", "&cOff");
            fc.set("toggleTransactionMessageButtonLore", "&7Current status: %status% \n &7If you keep this option on, \n &7you will recieve transaction \n &7messages in chat whenever someone \n &7buy/sell something from this shop");
            fc.set("toggleTransactionMessageOnInChat", "&7Toggle Transaction Messages: &aON");
            fc.set("toggleTransactionMessageOffInChat", "&7Toggle Transaction Messages: &cOFF");
            fc.set("disableBuyingButtonTitle", "&eDisable Buying");
            fc.set("disableBuyingButtonLore", "&7Current status: %status% \n &7If you keep this option on, \n &7the shop won't let anyone buy \n &7from your chest shop.");
            fc.set("disableBuyingOnInChat", "&7Disable Buying: &aON");
            fc.set("disableBuyingOffInChat", "&7Disable Buying: &cOFF");
            fc.set("disableSellingButtonTitle", "&eDisable Selling");
            fc.set("disableSellingButtonLore", "&7Current status: %status% \n &7If you keep this option on, \n &7the shop won't let anyone sell \n &7anything to the shop.");
            fc.set("disableSellingOnInChat", "&7Disable Selling: &aON");
            fc.set("disableSellingOffInChat", "&7Disable Selling: &cOFF");
            fc.set("shopAdminsButtonTitle", "&eShop admins");
            fc.set("nobodyStatusAdmins", "&aNobody");
            fc.set("shopAdminsButtonLore", "&7You can add/remove admins to \n &7your chest shop. Admins are able to \n &7access the shop storage & access certain \n &7settings (everything except share income \n &7and add/remove-ing admins). \n &aLeft Click &7to add an admin \n &cRight Click &7to remove an admin \n &7Current admins: %admins%");
            fc.set("addingAdminWaiting", "&ePlease enter the name of the person you want to add to the list of admins.");
            fc.set("removingAdminWaiting", "&ePlease enter the name of the person you want to remove from the list of admins.");
            fc.set("shareIncomeButtonTitle", "&eShared income");
            fc.set("shareIncomeButtonLore", "&7Current status: %status% \n &7If you keep this option on, \n &7the profit of ONLY sales, will be \n &7shared with admins as well.");
            fc.set("sharedIncomeOnInChat", "&7Shared income: &aON");
            fc.set("sharedIncomeOffInChat", "&7Shared income: &cOFF");
            fc.set("backToShopGuiButton", "&eBack to shop");
            fc.set("selfAdmin", "&cYou can't add or remove yourself in the admins list!");
            fc.set("noPlayer", "&cThis player doesn't exist or haven't played here before.");
            fc.set("sucAdminAdded", "&e%player% &asuccessfully added to the admins list.");
            fc.set("alreadyAdmin", "&cThis player is already in the admins list!");
            fc.set("sucAdminRemoved", "&e%player% &asuccessfully removed from the admins list.");
            fc.set("notInAdminList", "&cThis player is not in the admins list!");
            fc.save(new File(EzChestShop.getPlugin().getDataFolder(), "languages.yml"));
            reloadLanguages();
            EzChestShop.getPlugin().logConsole("&c[&eEzChestShop&c]&r &bNew languages.yml generated... (1.3.0V)");
        }
    }

    public static HashMap<String, Block> blockBreakMap = new HashMap<>();

    public static LanguageManager lm;

    public static List<UUID> getAdminsList(PersistentDataContainer data) {

        String adminsString = data.get(new NamespacedKey(EzChestShop.getPlugin(), "admins"), PersistentDataType.STRING);
        //UUID@UUID@UUID
        if (adminsString.equalsIgnoreCase("none")) {
            return new ArrayList<>();
        } else {
            String[] stringUUIDS = adminsString.split("@");
            List<UUID> finalList = new ArrayList<>();
            for (String uuidInString : stringUUIDS) {
                finalList.add(UUID.fromString(uuidInString));
            }
            return finalList;
        }
    }


    public static List<TransactionLogObject> getListOfTransactions(PersistentDataContainer data) {
        String wholeString = data.get(new NamespacedKey(EzChestShop.getPlugin(), "trans"), PersistentDataType.STRING);
        if (wholeString.equalsIgnoreCase("none")) {
            return new ArrayList<>();
        } else {
            List<TransactionLogObject> logObjectList = new ArrayList<>();
            String[] logs = wholeString.split("#");
            for (String log : logs) {
                String[] datas = log.split("@");
                String pname = datas[0];
                String type = datas[1];
                String price = datas[2];
                String time = datas[3];
                int count = Integer.parseInt(datas[4]);
                logObjectList.add(new TransactionLogObject(type, pname, price, time, count));

            }
            return logObjectList;

        }
    }

    public static Location getSpawnLocation(Chest chest) {
        return chest.getLocation().clone().add(0.5, 1, 0.5);
    }


    public static boolean is1_17 = false;

    //







}