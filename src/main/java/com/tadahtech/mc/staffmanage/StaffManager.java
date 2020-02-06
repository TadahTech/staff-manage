package com.tadahtech.mc.staffmanage;

import com.tadahtech.mc.staffmanage.command.PunishCommand;
import com.tadahtech.mc.staffmanage.database.SQLConfig;
import com.tadahtech.mc.staffmanage.menu.listeners.MenuListener;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class StaffManager extends JavaPlugin {

    public static final UUID CONSOLE_UUID = UUID.fromString("f78a4d8d-d51b-4b39-98a3-230f2de0c670");

    private static StaffManager instance;
    private SQLConfig sqlConfig;
    private PunishmentManager punishmentManager;
    private ConfigurationSection messagesSection;
    private String chatPrefix;

    public static StaffManager getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        getLogger().info("Starting Staff Manager...");
        instance = this;
        this.saveDefaultConfig();

        FileConfiguration config = this.getConfig();

        this.chatPrefix = ChatColor.translateAlternateColorCodes('&', config.getString("chat-prefix"));

        getCommand("punish").setExecutor(new PunishCommand());

        this.messagesSection = config.getConfigurationSection("messages");

        loadSQL(config);
        this.punishmentManager = new PunishmentManager(this);

        new MenuListener();

        getLogger().info("Started Staff Manager...");
    }

    @Override
    public void onDisable() {
    }

    private void loadSQL(FileConfiguration config) {
        ConfigurationSection sql = config.getConfigurationSection("sql");

        this.sqlConfig = new SQLConfig() {
            @Override
            public String getHost() {
                return sql.getString("host");
            }

            @Override
            public String getDatabase() {
                return sql.getString("database");
            }

            @Override
            public String getUser() {
                return sql.getString("user");
            }

            @Override
            public String getPassword() {
                return sql.getString("password");
            }

            @Override
            public int getPort() {
                return sql.getInt("port");
            }
        };
    }

    public SQLConfig getSqlConfig() {
        return sqlConfig;
    }


    public PunishmentManager getPunishmentManager() {
        return punishmentManager;
    }

    public ConfigurationSection getMessagesSection() {
        return messagesSection;
    }

    public String getChatPrefix() {
        return chatPrefix;
    }
}
