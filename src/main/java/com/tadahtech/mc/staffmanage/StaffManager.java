package com.tadahtech.mc.staffmanage;

import com.tadahtech.mc.staffmanage.command.CheckCommand;
import com.tadahtech.mc.staffmanage.command.DupeIPCommand;
import com.tadahtech.mc.staffmanage.command.FreezeCommand;
import com.tadahtech.mc.staffmanage.command.HelpCommand;
import com.tadahtech.mc.staffmanage.command.HistoryCommand;
import com.tadahtech.mc.staffmanage.command.PardonCommand;
import com.tadahtech.mc.staffmanage.command.PunishCommand;
import com.tadahtech.mc.staffmanage.command.PunishServerCommand;
import com.tadahtech.mc.staffmanage.database.SQLConfig;
import com.tadahtech.mc.staffmanage.dupeip.DupeIPManager;
import com.tadahtech.mc.staffmanage.listener.StaffChatListener;
import com.tadahtech.mc.staffmanage.menu.listeners.MenuListener;
import com.tadahtech.mc.staffmanage.redis.PunishmentsRedisManager;
import com.tadahtech.mc.staffmanage.util.UtilFreeze;
import com.tadahtechnologies.redis.RedisConfig;
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
    private PunishmentsRedisManager redisManager;
    private DupeIPManager dupeIPManager;
    private UtilFreeze utilFreeze;
    private String chatPrefix;
    private boolean debug;

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

        this.messagesSection = config.getConfigurationSection("messages");
        this.debug = config.getBoolean("debug", true);

        if (debug) {
            getLogger().info("Enabled debugging. This will output a lot of shit, to turn it off, add \"debug: false\" to the config.");
        }

        loadSQL(config);
        loadRedis(config);
        this.punishmentManager = new PunishmentManager(this);
        this.dupeIPManager = new DupeIPManager();

        new MenuListener();
        new StaffChatListener();
        this.utilFreeze = new UtilFreeze();

        getCommand("punish").setExecutor(new PunishCommand());
        getCommand("pardon").setExecutor(new PardonCommand());
        getCommand("freeze").setExecutor(new FreezeCommand());
        getCommand("phistory").setExecutor(new HistoryCommand());
        getCommand("pcheck").setExecutor(new CheckCommand());
        getCommand("phelp").setExecutor(new HelpCommand());
        getCommand("dupeip").setExecutor(new DupeIPCommand());
        getCommand("serverpunish").setExecutor(new PunishServerCommand());

        getLogger().info("Started Staff Manager...");
    }

    private void loadRedis(FileConfiguration config) {
        ConfigurationSection redis = config.getConfigurationSection("redis");

        if (redis == null) {
            return;
        }

        String host = redis.getString("host");
        int port = redis.getInt("port");

        RedisConfig redisConfig = new RedisConfig() {
            @Override
            public String getHost() {
                return host;
            }

            @Override
            public int getPort() {
                return port;
            }

            @Override
            public String getPassword() {
                return null;
            }

        };

        this.redisManager = new PunishmentsRedisManager(redisConfig);
        this.redisManager.subscribe();
        debug("Enabled redis!");
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

    public void debug(String message) {
        if (!debug) {
            return;
        }

        getLogger().info(message);
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

    public PunishmentsRedisManager getRedisManager() {
        return this.redisManager;
    }

    public DupeIPManager getDupeIPManager() {
        return dupeIPManager;
    }

    public UtilFreeze getUtilFreeze() {
        return utilFreeze;
    }
}
