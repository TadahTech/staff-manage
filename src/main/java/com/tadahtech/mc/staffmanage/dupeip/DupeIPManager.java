package com.tadahtech.mc.staffmanage.dupeip;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.tadahtech.mc.staffmanage.database.Callback;
import com.tadahtech.mc.staffmanage.database.GenericSQLManager;
import com.tadahtech.mc.staffmanage.database.SavedFieldValue;
import com.tadahtech.mc.staffmanage.listener.PunishmentListener;
import com.tadahtech.mc.staffmanage.util.UtilConcurrency;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class DupeIPManager extends GenericSQLManager<PlayerConnectionInfo> implements PunishmentListener {

    private static final Comparator<PlayerConnectionInfo[]> ARRAY_COMPARATOR = Comparator.<PlayerConnectionInfo[]>comparingInt(array -> array.length).reversed();
    private static final Map<UUID, PlayerConnectionInfo> CONNECTION_INFO = Maps.newHashMap();

    public DupeIPManager() {
        super("player_connection_info", PlayerConnectionInfo.class);
        this.startListening();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String ip = player.getAddress().getAddress().getHostAddress();
        String name = player.getName();
        UUID uuid = player.getUniqueId();

        UtilConcurrency.runAsync(() -> {
            PlayerConnectionInfo connectionInfo = this.get(new SavedFieldValue<>(this.getField("uuid"), uuid));

            if (connectionInfo == null) {
                connectionInfo = new PlayerConnectionInfo(uuid, name, ip);
            } else {
                connectionInfo.setIp(ip);
            }

            CONNECTION_INFO.put(uuid, connectionInfo);
            this.saveSync(connectionInfo);
        });
    }

    public PlayerConnectionInfo getOnlineInfo(UUID uuid) {
        return CONNECTION_INFO.get(uuid);
    }

    public void getDuplicateIPs(Callback<Map<String, PlayerConnectionInfo[]>> callback) {
        UtilConcurrency.runAsync(() -> {
            // First we make a multimap with IP as key
            Multimap<String, PlayerConnectionInfo> byIpClone = ArrayListMultimap.create();
            for (PlayerConnectionInfo info : getAll()) {
                String data = info.getIp();

                if (data == null) {
                    continue; // These players haven't connected properly yet
                }

                byIpClone.put(data, info);
            }

            // Now we extract the ones that have dupes
            Map<String, PlayerConnectionInfo[]> unsorted = Maps.newHashMap();
            byIpClone.asMap().forEach((ip, collection) -> {
                if (collection.size() <= 1) { // 1 is really the only thing that will happen here
                    return;
                }

                unsorted.put(ip, collection.toArray(new PlayerConnectionInfo[0]));
            });

            // Gotta do a separate map just because lambda
            Map<String, PlayerConnectionInfo[]> sorted = sortByValue(unsorted);
            unsorted.clear(); // Clear it, because why not
            UtilConcurrency.runSync(() -> callback.call(sorted));
        });
    }

    private <K, V> Map<K, V> sortByValue(Map<K, V> map) {
        Map<K, V> linked = Maps.newLinkedHashMap();
        map.entrySet().stream().sorted(Entry.comparingByValue((Comparator<V>) DupeIPManager.ARRAY_COMPARATOR))
          .forEach(entry -> linked.put(entry.getKey(), entry.getValue()));
        return linked;
    }

}
