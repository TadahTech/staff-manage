package com.tadahtech.mc.staffmanage.punishments.record;

import com.tadahtech.mc.staffmanage.StaffManager;
import com.tadahtech.mc.staffmanage.database.ColumnType;
import com.tadahtech.mc.staffmanage.database.Savable;
import com.tadahtech.mc.staffmanage.database.Saved;
import com.tadahtech.mc.staffmanage.punishments.Punishment;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import com.tadahtech.mc.staffmanage.util.Colors;
import com.tadahtech.mc.staffmanage.util.UtilTime;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

public class RecordEntry implements Savable {

    @Saved(primaryKey = true, columnType = ColumnType.INTEGER, exclude = true, attributes = {ColumnType.ColumnAttribute.AUTO_INCREMENT})
    private int entryId;

    @Saved(primaryKey = true, columnType = ColumnType.UUID)
    private UUID accountId;

    @Saved(primaryKey = true, columnType = ColumnType.STRING)
    private String accountName;

    @Saved(columnType = ColumnType.ENUM)
    private PunishmentType type;

    @Saved(columnType = ColumnType.DATE)
    private Date expiry;

    @Saved(columnType = ColumnType.LONG_STRING)
    private String reason;

    @Saved(columnType = ColumnType.STRING)
    private String initiator;

    @Saved(columnType = ColumnType.UUID)
    private UUID initiatorUUID;

    @Saved(columnType = ColumnType.DATE)
    private Date timestamp;

    @Saved(columnType = ColumnType.BOOLEAN)
    private boolean removed;

    @Deprecated
    public RecordEntry() {
    }

    public RecordEntry(UUID accountId, String accountName, Punishment punishment) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.type = punishment.getType();
        this.expiry = punishment.getExpiry();
        this.reason = punishment.getReason();
        this.initiator = punishment.getInitiator();
        this.timestamp = punishment.getTimestamp();
        this.removed = false;
    }

    public RecordEntry(UUID accountId, String accountName, PunishmentType type, Date expiry, String reason, String initiator, UUID initiatorUUID, Date timestamp) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.type = type;
        this.expiry = expiry;
        this.reason = reason;
        this.initiator = initiator;
        this.initiatorUUID = initiatorUUID;
        this.timestamp = timestamp;
        this.removed = false;
    }

    public int getEntryId() {
        return entryId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public PunishmentType getType() {
        return type;
    }

    public Date getExpiry() {
        return expiry;
    }

    public String getReason() {
        return reason;
    }

    public String getInitiator() {
        return initiator;
    }

    public UUID getInitiatorUUID() {
        return initiatorUUID;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    private long getLength() {
        return this.getExpiry().getTime() - this.getTimestamp().getTime();
    }

    public boolean isRemoved() {
        return removed;
    }

    public void remove() {
        this.removed = true;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void print(Player player) {
        String initiator = this.getInitiator();

        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(this.getTimestamp());
        Calendar endCalendar = Calendar.getInstance();

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int diffMonth = (diffYear * 12) + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);

        String color;
        if (diffMonth <= 1) {
            color = Colors.GREEN;
        } else if (diffMonth <= 6) {
            color = Colors.GOLD;
        } else {
            color = Colors.RED;
        }

        String base = StaffManager.getInstance().getMessagesSection().getString("record-entry-each");
        base = base.replace("%type%", getType().name())
          .replace("%date%", UtilTime.format(getTimestamp()))
          .replace("%by%", initiator)
          .replace("%reason%", getReason())
          .replace("%dateDiff%", color + UtilTime.formatTime(diffMonth) + (diffMonth == 1 ? "month" : "months"));

        base = ChatColor.translateAlternateColorCodes('&', base);

        player.sendMessage(base);
    }
}
