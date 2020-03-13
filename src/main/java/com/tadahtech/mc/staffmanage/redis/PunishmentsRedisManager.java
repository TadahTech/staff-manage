package com.tadahtech.mc.staffmanage.redis;

import com.tadahtech.mc.staffmanage.util.UtilConcurrency;
import com.tadahtechnologies.redis.Channels;
import com.tadahtechnologies.redis.RedisConfig;
import com.tadahtechnologies.redis.RedisManager;
import com.tadahtechnologies.redis.packets.punishments.PunishmentsRequestPacket;

public class PunishmentsRedisManager extends RedisManager {

    public PunishmentsRedisManager(RedisConfig config) {
        super(config, Channels.PUNISHMENTS);
        this.registerPacket(PunishmentsRequestPacket.class, new PunishmentsPacketListener());
    }

    @Override
    public void sync(Runnable runnable) {
        UtilConcurrency.runSync(runnable);
    }

    @Override
    public void async(Runnable runnable) {
        UtilConcurrency.runAsync(runnable);
    }
}
