package com.tadahtech.mc.staffmanage.redis;

import com.google.gson.JsonObject;
import com.tadahtech.mc.staffmanage.StaffManager;
import com.tadahtech.mc.staffmanage.punishments.PunishmentSQLManager;
import com.tadahtech.mc.staffmanage.punishments.PunishmentType;
import com.tadahtech.mc.staffmanage.util.Common;
import com.tadahtech.mc.staffmanage.util.UtilConcurrency;
import com.tadahtechnologies.redis.packet.PacketListener;
import com.tadahtechnologies.redis.packets.punishments.PunishmentsRequestPacket;
import com.tadahtechnologies.redis.packets.punishments.PunishmentsResponsePacket;

import java.util.Map;

public class PunishmentsPacketListener implements PacketListener<PunishmentsRequestPacket> {

    @Override
    public void handlePacket(PunishmentsRequestPacket packet) {
        PunishmentSQLManager manager = StaffManager.getInstance().getPunishmentManager().getSQLManager();
        PunishmentsRedisManager redisManager = StaffManager.getInstance().getRedisManager();

        UtilConcurrency.runAsync(() -> {
            PunishmentsResponsePacket responsePacket = new PunishmentsResponsePacket();
            JsonObject object = new JsonObject();
            PunishmentType punishmentType = packet.getPunishmentType() == PunishmentsRequestPacket.PunishmentType.NONE ? null : PunishmentType.getByName(packet.getPunishmentType().name());

            switch (packet.getType()) {
                case PLAYER:
                    if (packet.getPunishmentType() != PunishmentsRequestPacket.PunishmentType.NONE) {
                        manager.getTotalOfTypeForUser(packet.getPlayer(), punishmentType, total -> {
                            object.addProperty("total-punishments", total);
                        });
                        break;
                    }
                    manager.getTotalForUser(packet.getPlayer(), counts -> {
                        countsToJson(object, counts);
                    });
                    break;
                case SERVER:
                    if (packet.getPunishmentType() != PunishmentsRequestPacket.PunishmentType.NONE) {
                        manager.getTotalOfType(punishmentType, total -> {
                            object.addProperty("total-punishments", total);
                        });
                        break;
                    }
                    manager.getTotal(counts -> {
                        countsToJson(object, counts);
                    });
                    break;
            }

            String json = Common.GSON.toJson(object);
            StaffManager.getInstance().debug(json);
            responsePacket.setJson(json);
            responsePacket.setId(packet.getId());
            redisManager.sendPacket(responsePacket, packet.getChannelFrom());
        });

    }

    private void countsToJson(JsonObject object, Map<PunishmentType, Integer> counts) {
        int total = 0;
        JsonObject types = new JsonObject();
        for (PunishmentType type : PunishmentType.values()) {
            int amount = counts.getOrDefault(type, 0);
            types.addProperty(type.getLabel(), amount);
            total += amount;
        }
        object.add("types", types);
        object.addProperty("total-punishments", total);
    }
}
