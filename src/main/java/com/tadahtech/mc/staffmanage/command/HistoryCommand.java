package com.tadahtech.mc.staffmanage.command;

import com.google.common.collect.Lists;
import com.tadahtech.mc.staffmanage.PunishmentMessage;
import com.tadahtech.mc.staffmanage.StaffManager;
import com.tadahtech.mc.staffmanage.lang.ColorFormatting;
import com.tadahtech.mc.staffmanage.lang.Message;
import com.tadahtech.mc.staffmanage.lang.types.RegularMessage;
import com.tadahtech.mc.staffmanage.player.PlayerRecordData;
import com.tadahtech.mc.staffmanage.record.RecordEntry;
import com.tadahtech.mc.staffmanage.util.Colors;
import com.tadahtech.mc.staffmanage.util.UtilConcurrency;
import com.tadahtech.mc.staffmanage.util.UtilText;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class HistoryCommand implements CommandExecutor {

    private static final int PER_PAGE = 5;
    private static final String COMMAND_FORMAT = "/record view %s";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sms.history")) {
            sender.sendMessage(Colors.RED + "You cannot perform this command!");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Colors.RED + "Please specify a player!");
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        UUID test = UUID.randomUUID();
        UUID targetUUID;

        if (target == null) {
            targetUUID = test;
        } else {
            targetUUID = target.getUniqueId();
        }

        UUID finalTargetUUID = targetUUID;

        UtilConcurrency.runAsync(() -> {
            UUID uuid = finalTargetUUID;

            if (uuid == test) {
                uuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
            }

            StaffManager.getInstance().getPunishmentManager().getRecordSQLManager().getRecord(uuid, sender.hasPermission("sms.view-removed"), record -> {
                if (record == null) {
                    sender.sendMessage(Colors.RED + "No records found for " + targetName);
                    return;
                }

                int page = 1;
                if (args.length > 1) {
                    String pageRaw = args[1];
                    try {
                        page = Integer.parseInt(pageRaw);
                    } catch (NumberFormatException ignored) {
                        sender.sendMessage(Colors.RED + "Please enter a valid page number");
                        return;
                    }
                }

                this.print(record, targetName, (Player) sender, page);
            });
        });
        return true;
    }


    private void print(PlayerRecordData record, String target, Player player, int tempPage) {
        UtilConcurrency.runAsync(() -> {
            int page = tempPage;

            if (record.getEntries().size() == 0) {
                Message header = new RegularMessage(PunishmentMessage.PUNISHMENTS_RECORD_HEADER)
                  .withArg(target, ColorFormatting.NAME)
                  .withArg("-/-", ColorFormatting.ELEMENT);
                player.sendMessage(UtilText.createLineCenteredMessage(Colors.DARK_GRAY, header.toString(player)));
                player.sendMessage(Colors.GOLD + "No entries!");
                player.sendMessage(UtilText.createLine(Colors.DARK_GRAY));
                return;
            }

            record.getEntries().sort(Comparator.comparingInt(RecordEntry::getEntryId).reversed());
            int pages = (int) Math.ceil((double) record.getEntries().size() / (double) PER_PAGE);
            page = Math.min(page, pages) - 1;

            List<RecordEntry> entries = record.getEntries().subList(page * PER_PAGE, Math.min(record.getEntries().size(), (page + 1) * PER_PAGE));

            Message header = new RegularMessage(PunishmentMessage.PUNISHMENTS_RECORD_HEADER)
              .withArg(target, ColorFormatting.NAME)
              .withArg((page + 1) + "/" + pages, ColorFormatting.ELEMENT);

            player.sendMessage(UtilText.createLineCenteredMessage(Colors.DARK_GRAY, header.toString(player)));
            for (int i = 0; i < entries.size(); i++) {
                if (i > 0) {
                    player.sendMessage("");
                }

                entries.get(i).print(player);
            }

            boolean hasNextPage = page < (pages - 1);
            boolean hasPrevPage = page > 0;

            String commandFormat = String.format(COMMAND_FORMAT, target) + " %s";
            player.spigot().sendMessage(createPageBar(player, page + 1, commandFormat, hasNextPage, hasPrevPage));
        });
    }

    private BaseComponent[] createPageBar(Player player, int currentPage, String commandFormat, boolean hasNextPage, boolean hasPrevPage) {
        String actionMsg = "";
        List<BaseComponent> components = Lists.newArrayList();

        if (hasPrevPage) {
            String prevPage = ChatColor.DARK_GRAY + " \u00ab " + new RegularMessage(PunishmentMessage.GENERAL_LIST_PREVIOUS_PAGE).toString(player) + " ";
            String prevCommand = String.format(commandFormat, currentPage - 1);
            String prevHover = new RegularMessage(PunishmentMessage.GENERAL_LIST_CLICK_TO_PREV).toString(player);

            actionMsg += prevPage;

            BaseComponent[] prevComponents = TextComponent.fromLegacyText(prevPage);
            HoverEvent hoverEvent = new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText(prevHover));
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, prevCommand);

            for (BaseComponent component : prevComponents) {
                component.setHoverEvent(hoverEvent);
                component.setClickEvent(clickEvent);
            }

            Collections.addAll(components, prevComponents);
        }

        if (hasNextPage) {
            if (hasPrevPage) {
                String spacer = ChatColor.DARK_GRAY + "||";
                actionMsg += spacer;
                Collections.addAll(components, TextComponent.fromLegacyText(spacer));
            }

            String nextPage = " " + new RegularMessage(PunishmentMessage.GENERAL_LIST_NEXT_PAGE).toString(player) + ChatColor.DARK_GRAY + " \u00bb ";
            String nextCommand = String.format(commandFormat, currentPage + 1);
            String nextHover = new RegularMessage(PunishmentMessage.GENERAL_LIST_CLICK_TO_NEXT).toString(player);

            actionMsg += nextPage;

            BaseComponent[] nextComponents = TextComponent.fromLegacyText(nextPage);
            HoverEvent hoverEvent = new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText(nextHover));
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, nextCommand);

            for (BaseComponent component : nextComponents) {
                component.setHoverEvent(hoverEvent);
                component.setClickEvent(clickEvent);
            }

            Collections.addAll(components, nextComponents);
        }

        String endingFill = ChatColor.DARK_GRAY + Colors.STRIKETHROUGH + UtilText.createEndingFill((actionMsg.isEmpty() ? " " : "") + actionMsg, " ");
        Collections.addAll(components, TextComponent.fromLegacyText((actionMsg.isEmpty() ? " " : "") + endingFill));
        return components.toArray(new BaseComponent[0]);
    }
}
