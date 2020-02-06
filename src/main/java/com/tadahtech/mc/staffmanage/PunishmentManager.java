package com.tadahtech.mc.staffmanage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tadahtech.mc.staffmanage.length.LengthManager;
import com.tadahtech.mc.staffmanage.length.PunishmentLength;
import com.tadahtech.mc.staffmanage.mute.MuteManager;
import com.tadahtech.mc.staffmanage.player.PlayerPunishmentData;
import com.tadahtech.mc.staffmanage.punishments.PunishmentCategory;
import com.tadahtech.mc.staffmanage.punishments.PunishmentData;
import com.tadahtech.mc.staffmanage.punishments.PunishmentSQLManager;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import com.tadahtech.mc.staffmanage.punishments.builder.PunishmentBuilderManager;
import com.tadahtech.mc.staffmanage.record.RecordSQLManager;
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

    public PunishmentManager(StaffManager staffManager) {
        this.sqlManager = new PunishmentSQLManager();
        this.recordSQLManager = new RecordSQLManager();
        this.builderManager = new PunishmentBuilderManager();
        this.muteManager = new MuteManager();
        this.lengthManager = new LengthManager(this);
        this.categoryMap = Maps.newHashMap();

        setupData(staffManager.getConfig());
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

        PunishmentLength length = this.lengthManager.getLength(data);

        if (length == null) {
            switch (data.getType()) {
                case TEMP_BAN:
                    data.setType(PunishmentType.BAN);
                    break;
                case TEMP_MUTE:
                    data.setType(PunishmentType.MUTE);
                    break;
            }
        }

        if (length != null) {
            this.lengthManager.incrementLength(data);
            data.setExpiry(length.toDate());
        }

        String message = getMessage(data);

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
                player.sendMessage(message);
        }

        broadcast(data);
        this.sqlManager.save(data);
        this.builderManager.cleanup(data.getInitiatorUUID());

        Bukkit.getPlayer(data.getInitiatorUUID()).sendMessage(getStaff(data));
    }

    public String getMessage(PlayerPunishmentData data) {
        String base = StaffManager.getInstance().getMessagesSection().getString(data.getType().name().toLowerCase());

        base = base.replace("%player%", data.getName())
          .replace("%staff%", data.getInitiatorName())
          .replace("%category%", data.getCategory())
          .replace("%subCat%", data.getSubType())
          .replace("%type%", data.getType().getMessageVersion())
          .replace("%timeRemaining%", data.getTimeRemaining())
          .replace("%time%", data.getTime())
          .replaceAll("\\n", "\n");

        base = ChatColor.translateAlternateColorCodes('&', base);

        return base;
    }

    private String getStaff(PlayerPunishmentData data) {
        String base = StaffManager.getInstance().getMessagesSection().getString("staff");

        base = base.replace("%player%", data.getName())
          .replace("%staff%", data.getInitiatorName())
          .replace("%category%", data.getCategory())
          .replace("%subCat%", data.getSubType())
          .replace("%type%", data.getType().getMessageVersion())
          .replace("%timeRemaining%", data.getTimeRemaining())
          .replace("%time%", data.getTime())
          .replaceAll("\\n", "\n");

        base = ChatColor.translateAlternateColorCodes('&', base);

        return base;
    }

    private void broadcast(PlayerPunishmentData data) {
        if (!data.getType().shouldBroadcast()) {
            return;
        }

        String base = StaffManager.getInstance().getMessagesSection().getString("ingame");

        base = base.replace("%player%", data.getName())
          .replace("%category%", data.getCategory())
          .replace("%type%", data.getType().getMessageVersion())
          .replace("%subCat%", data.getSubType())
          .replaceAll("\\n", "\n");

        base = ChatColor.translateAlternateColorCodes('&', base);

        Bukkit.broadcastMessage(base);
    }

    private void setupData(FileConfiguration config) {
        ConfigurationSection categories = config.getConfigurationSection("categories");
        for (String s : categories.getKeys(false)) {
            ConfigurationSection section = categories.getConfigurationSection(s);

            Material catIcon = Material.getMaterial(section.getString("icon"));
            String guiName = ChatColor.translateAlternateColorCodes('&', section.getString("gui-name"));

            PunishmentCategory category = new PunishmentCategory(guiName, catIcon);
            ConfigurationSection subTypes = section.getConfigurationSection("subCat");

            for (String sub : subTypes.getKeys(false)) {
                generateSubTypes(category, subTypes, sub);
            }

            categoryMap.put(s.toLowerCase(), category);

            StaffManager.getInstance().getLogger().info("Added new punishment Category: " + category.getName() + ". Contains " + category.getPunishments().size() + " sub subCat");
        }
    }

    private void generateSubTypes(PunishmentCategory category, ConfigurationSection subTypes, String sub) {
        ConfigurationSection section;
        section = subTypes.getConfigurationSection(sub);

        Material icon = Material.getMaterial(section.getString("icon"));
        String uiName = ChatColor.translateAlternateColorCodes('&', section.getString("gui-name"));
        boolean allowBan = section.getBoolean("allow-ban");
        boolean allowPermMute = section.getBoolean("allow-perm-mute");
        boolean allowIpBan = section.getBoolean("allow-ip-ban");

        ConfigurationSection length = section.getConfigurationSection("lengths");
        Map<PunishmentType, LinkedList<PunishmentLength>> lengths = Maps.newHashMap();

        for (String l : length.getKeys(false)) {
            getLengths(subTypes, lengths, l);
        }

        PunishmentData data = new PunishmentData(uiName, icon, allowBan, allowPermMute, allowIpBan, lengths);
        category.add(data);
    }

    private void getLengths(ConfigurationSection subTypes, Map<PunishmentType, LinkedList<PunishmentLength>> lengths, String l) {
        PunishmentType type = PunishmentType.getByName(l);

        if (type == null) {
            StaffManager.getInstance().getLogger().severe("No such punishment type: " + l);
            return;
        }

        List<String> times = subTypes.getStringList(l);

        if (times == null) {
            StaffManager.getInstance().getLogger().severe("No list found. What type is it?");
            return;
        }

        LinkedList<PunishmentLength> linkedList = Lists.newLinkedList(times.stream().map(PunishmentLength::new).collect(Collectors.toList()));
        lengths.put(type, linkedList);
    }


}
