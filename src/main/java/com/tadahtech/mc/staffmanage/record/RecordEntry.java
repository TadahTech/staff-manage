package com.tadahtech.mc.staffmanage.record;

import com.tadahtech.mc.staffmanage.PunishmentMessage;
import com.tadahtech.mc.staffmanage.database.ColumnType;
import com.tadahtech.mc.staffmanage.database.Savable;
import com.tadahtech.mc.staffmanage.database.Saved;
import com.tadahtech.mc.staffmanage.lang.ColorFormatting;
import com.tadahtech.mc.staffmanage.lang.Messaging;
import com.tadahtech.mc.staffmanage.lang.types.RegularMessage;
import com.tadahtech.mc.staffmanage.player.PlayerPunishmentData;
import com.tadahtech.mc.staffmanage.util.Colors;
import com.tadahtech.mc.staffmanage.util.UtilText;
import com.tadahtech.mc.staffmanage.util.UtilTime;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

public class RecordEntry implements Savable {

    @Saved(primaryKey = true, columnType = ColumnType.INTEGER, exclude = true, attributes = {ColumnType.ColumnAttribute.AUTO_INCREMENT})
    private int entryId;

    @Saved(primaryKey = true, columnType = ColumnType.UUID)
    private UUID uuid;

    @Saved
    private String name;

    @Saved(primaryKey = true, columnType = ColumnType.ENUM)
    private RecordEntryType type;

    @Saved(size = 64)
    private String category;

    @Saved(size = 64)
    private String subType;

    @Saved
    private String initiatorName;

    @Saved(columnType = ColumnType.UUID)
    private UUID initiatorUUID;

    @Saved(columnType = ColumnType.DATE)
    private Date timestamp;

    @Saved(columnType = ColumnType.DATE)
    private Date expiry;

    @Saved(columnType = ColumnType.BOOLEAN)
    private boolean removed;

    @Deprecated
    public RecordEntry() {
    }

    public RecordEntry(PlayerPunishmentData data) {
        this(data.getUuid(),
          data.getName(),
          data.getType().getRecord(),
          data.getCategory(),
          data.getSubTypePretty(),
          data.getInitiatorName(),
          data.getInitiatorUUID(),
          new Date(),
          data.getExpiry());
    }

    public RecordEntry(UUID uuid, String name, RecordEntryType type, String category, String subType, String initiatorName, UUID initiatorUUID, Date timestamp, Date expiry) {
        this.uuid = uuid;
        this.name = name;
        this.type = type;
        this.category = category;
        this.subType = subType;
        this.initiatorName = initiatorName;
        this.initiatorUUID = initiatorUUID;
        this.timestamp = timestamp;
        this.expiry = expiry;
        this.removed = false;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public RecordEntryType getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public String getSubType() {
        return subType;
    }

    public String getInitiatorName() {
        return initiatorName;
    }

    public UUID getInitiatorUUID() {
        return initiatorUUID;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public void remove() {
        this.setRemoved(true);
    }

    public void print(Player player) {
        String initiator = this.getInitiatorName();

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

        Messaging.send(player,
          new RegularMessage(PunishmentMessage.PUNISHMENTS_HISTORY_TIMESTAMP)
            .withArg((this.isRemoved() ? Colors.BOLD + Colors.DARK_RED + "REMOVED" : "") + color + UtilTime.format(this.getTimestamp()))
            .withArg(String.valueOf(this.getEntryId()), ColorFormatting.COUNT),
          new RegularMessage(PunishmentMessage.PUNISHMENTS_HISTORY_TYPE).withArg(UtilText.format(this.getType().name()), ColorFormatting.ELEMENT),
          this.getExpiry() == null ? null : new RegularMessage(PunishmentMessage.PUNISHMENTS_HISTORY_LENGTH).withArg(UtilTime.toSentence(this.getLength()), ColorFormatting.ELEMENT),
          new RegularMessage(PunishmentMessage.PUNISHMENTS_HISTORY_REASON).withArg(this.getCategory(), ColorFormatting.ELEMENT).withArg(this.getSubType(), ColorFormatting.ELEMENT),
          new RegularMessage(PunishmentMessage.PUNISHMENTS_HISTORY_INITIATOR).withArg(initiator, ColorFormatting.NAME)
        );
    }


    public int getEntryId() {
        return entryId;
    }

    public Date getExpiry() {
        return expiry;
    }

    private long getLength() {
        return this.getExpiry().getTime() - this.getTimestamp().getTime();
    }
}
