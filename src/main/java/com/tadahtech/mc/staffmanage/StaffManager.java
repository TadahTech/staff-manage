package com.tadahtech.mc.staffmanage;

import com.tadahtech.mc.staffmanage.commands.CommandManager;
import com.tadahtech.mc.staffmanage.database.SQLConfig;
import com.tadahtech.mc.staffmanage.punishments.PunishmentManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class StaffManager extends JavaPlugin {

    private static StaffManager instance;
    private SQLConfig sqlConfig;
    private CommandManager commandManager;
    private PunishmentManager punishmentManager;

    public static StaffManager getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.commandManager = new CommandManager(this);
        FileConfiguration config = this.getConfig();

        if (config == null) {
            this.saveDefaultConfig();
            config = this.getConfig();
        }

        loadSQL(config);
        this.punishmentManager = new PunishmentManager();

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

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public PunishmentManager getPunishmentManager() {
        return punishmentManager;
    }
}
