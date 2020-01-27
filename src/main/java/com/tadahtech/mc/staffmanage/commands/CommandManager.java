package com.tadahtech.mc.staffmanage.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tadahtech.mc.staffmanage.StaffManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CommandManager implements Listener {

    private static final Map<String, SubCommand> COMMAND_MAP = Maps.newHashMap();

    public CommandManager(StaffManager plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().replace("/", "");
        String command;
        List<String> args = Lists.newArrayList();

        if (message.contains(" ")) {
            String[] split = message.split(" ");
            command = split[0];
            String[] rest = new String[split.length - 2];

            System.arraycopy(split, 1, rest, 0, rest.length);

            args.addAll(Arrays.asList(rest));
        } else {
            command = message;
        }

        SubCommand subCommand = COMMAND_MAP.get(command);

        if (subCommand == null) {
            return;
        }

        subCommand.run(event.getPlayer(), args);
        event.setCancelled(true);
    }

    public void register(SubCommand command) {
        COMMAND_MAP.put(command.getName(), command);
    }
}
