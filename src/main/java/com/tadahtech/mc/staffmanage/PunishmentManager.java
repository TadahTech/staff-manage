package com.tadahtech.mc.staffmanage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tadahtech.mc.staffmanage.length.LengthManager;
import com.tadahtech.mc.staffmanage.length.PunishmentLength;
import com.tadahtech.mc.staffmanage.listener.BanListener;
import com.tadahtech.mc.staffmanage.listener.MuteListener;
import com.tadahtech.mc.staffmanage.mute.MuteManager;
import com.tadahtech.mc.staffmanage.player.PlayerPunishmentData;
import com.tadahtech.mc.staffmanage.punishments.PunishmentCategory;
import com.tadahtech.mc.staffmanage.punishments.PunishmentData;
import com.tadahtech.mc.staffmanage.punishments.PunishmentSQLManager;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import com.tadahtech.mc.staffmanage.punishments.builder.PunishmentBuilderManager;
import com.tadahtech.mc.staffmanage.record.RecordEntry;
import com.tadahtech.mc.staffmanage.record.RecordEntryType;
import com.tadahtech.mc.staffmanage.record.RecordSQLManager;
import com.tadahtech.mc.staffmanage.util.Colors;
import com.tadahtech.mc.staffmanage.util.UtilTime;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PunishmentManager {

    private PunishmentSQLManager sqlManager;
    private RecordSQLManager recordSQLManager;
    private PunishmentBuilderManager builderManager;
    private LengthManager lengthManager;
    private MuteManager muteManager;
    private Map<String, PunishmentCategory> categoryMap;
    private long recordExpireTime;

    public PunishmentManager(StaffManager staffManager) {
        this.sqlManager = new PunishmentSQLManager();
        this.recordSQLManager = new RecordSQLManager();
        this.builderManager = new PunishmentBuilderManager();
        this.muteManager = new MuteManager();
        this.lengthManager = new LengthManager(this);
        this.categoryMap = Maps.newHashMap();

        new BanListener(this);
        new MuteListener(this);

        FileConfiguration config = staffManager.getConfig();

        this.recordExpireTime = UtilTime.toMillis(config.getString("record-expire-time"));

        setupData(config);
    }

    public Set<PunishmentCategory> getAll() {
        return Sets.newHashSet(this.categoryMap.values());
    }

    public PunishmentCategory getCategory(String name) {
        return this.categoryMap.get(name.toLowerCase());
    }

    public PunishmentSQLManager getSQLManager() {
        return sqlManager;
    }

    public RecordSQLManager getRecordSQLManager() {
        return recordSQLManager;
    }

    public PunishmentBuilderManager getBuilderManager() {
        return builderManager;
    }

    public MuteManager getMuteManager() {
        return muteManager;
    }

    public void punish(PlayerPunishmentData data) {
        Player player = Bukkit.getPlayer(data.getUuid());

        if (player == null) {
            throw new IllegalArgumentException("that player is not online");
        }

        Player sender = Bukkit.getPlayer(data.getInitiatorUUID());

        if (sender == null) {
            throw new IllegalArgumentException("that sender is not online");
        }

        PunishmentLength length = this.lengthManager.getLength(data);

        if (!sender.hasPermission("staffmanager." + data.getType().getLabel())) {
            sender.sendMessage(Colors.RED + "You cannot execute a " + data.getType() + "! Please get a higher ranked staff member.");
            return;
        }

        if (length != null) {
            data.setExpiry(length.toDate());
        }

        String message = getMessage(data);
        message = message.replace("%next%", this.lengthManager.getNext(data));

        switch (data.getType()) {
            case BAN:
            case TEMP_BAN:
            case KICK:
            case IP_BAN:
                player.kickPlayer(message);
                break;
            case MUTE:
            case TEMP_MUTE:
            case IP_MUTE:
                this.muteManager.mute(data);
                player.sendMessage(message);
                break;
            default:
                player.sendMessage(StaffManager.getInstance().getChatPrefix() + message);
        }

        broadcast(data);
        this.sqlManager.save(data);
        this.builderManager.cleanup(data.getInitiatorUUID());

        if (sender.getUniqueId() == player.getUniqueId()) {
            return;
        }

        sender.sendMessage(getStaff(data));
    }

    public void removePunishment(PlayerPunishmentData punishment) {
        this.getSQLManager().deletePunishment(punishment.getUuid(), punishment.getType());

        RecordEntry entry = new RecordEntry(RecordEntryType.REMOVE_EXPIRED, punishment);
        this.getRecordSQLManager().saveEntry(entry);
    }

    public String getMessage(PlayerPunishmentData data) {
        String base = StaffManager.getInstance().getMessagesSection().getString(data.getType().getLabel().toLowerCase());
        base = replacePlaceHolders(data, base);
        return base;
    }

    private String getStaff(PlayerPunishmentData data) {
        String base = StaffManager.getInstance().getMessagesSection().getString("staff");
        base = replacePlaceHolders(data, base);
        return base;
    }

    private void broadcast(PlayerPunishmentData data) {
        if (!data.getType().shouldBroadcast()) {
            return;
        }

        String base = StaffManager.getInstance().getMessagesSection().getString("ingame");
        base = replacePlaceHolders(data, base);
        Bukkit.broadcastMessage(StaffManager.getInstance().getChatPrefix() + base);
    }

    private String replacePlaceHolders(PlayerPunishmentData data, String base) {
        base = base.replace("%player%", data.getName())
          .replace("%staff%", data.getInitiatorName())
          .replace("%category%", data.getCategory())
          .replace("%subCat%", data.getSubType())
          .replace("%type%", data.getType().getMessageVersion())
          .replace("%timeRemaining%", data.getTimeRemaining())
          .replace("%time%", data.getExpiry() == null ? "" : data.getTime())
          .replaceAll("\\n", Colors.RESET + "\n");
        return ChatColor.translateAlternateColorCodes('&', base);
    }

    private void setupData(FileConfiguration config) {
        ConfigurationSection categories = config.getConfigurationSection("categories");
        for (String s : categories.getKeys(false)) {
            ConfigurationSection section = categories.getConfigurationSection(s);

            Material catIcon = Material.getMaterial(section.getString("icon"));

            String guiName = ChatColor.translateAlternateColorCodes('&', section.getString("gui-name"));

            PunishmentCategory category = new PunishmentCategory(guiName, catIcon);
            ConfigurationSection subTypes = section.getConfigurationSection("types");

            for (String sub : subTypes.getKeys(false)) {
                generateSubTypes(category, subTypes, sub);
            }

            categoryMap.put(category.getName().toLowerCase(), category);

            StaffManager.getInstance().getLogger().info("Added new punishment Category: " + category.getName() + ". Contains " + category.getPunishments().size() + " subCats");
        }
    }

    private void generateSubTypes(PunishmentCategory category, ConfigurationSection subTypes, String sub) {
        ConfigurationSection section = subTypes.getConfigurationSection(sub);

        Material icon = Material.getMaterial(section.getString("icon"));
        String uiName = ChatColor.translateAlternateColorCodes('&', section.getString("gui-name"));

        List<String> punishments = section.getStringList("punishments");

        LinkedList<PunishmentType> types = Lists.newLinkedList(punishments.stream().map(PunishmentType::getByName).collect(Collectors.toList()));

        ConfigurationSection length = section.getConfigurationSection("lengths");
        Map<PunishmentType, LinkedList<PunishmentLength>> lengths = Maps.newHashMap();

        for (String l : length.getKeys(false)) {
            getLengths(length, lengths, l);
        }

        PunishmentData data = new PunishmentData(sub, uiName, icon, types, lengths);

        StaffManager.getInstance().getLogger().info("Added new PunishmentData " + data.getName());

        category.add(data);
    }

    private void getLengths(ConfigurationSection section, Map<PunishmentType, LinkedList<PunishmentLength>> lengths, String l) {
        PunishmentType type = PunishmentType.getByName(l);

        if (type == null) {
            return;
        }

        List<String> times = section.getStringList(l);

        if (times == null || times.isEmpty()) {
            return;
        }

        LinkedList<PunishmentLength> linkedList = Lists.newLinkedList(times.stream().map(PunishmentLength::new).collect(Collectors.toList()));
        lengths.put(type, linkedList);
    }


    public LengthManager getLengthManager() {
        return this.lengthManager;
    }

    public long getRecordExpireTime() {
        return recordExpireTime;
    }
}
