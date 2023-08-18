package net.devscape.project.woolwars.managers;

import net.devscape.project.woolwars.handlers.LocalData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LocalStatsManager {

    private final Map<UUID, LocalData> localDataMap = new HashMap<>();

    public LocalStatsManager() {}

    public LocalData getData(UUID uuid) {
        return localDataMap.get(uuid);
    }

    public void reset(UUID uuid) {
        localDataMap.clear();
        LocalData data = new LocalData(0, 0, 0);

        localDataMap.put(uuid, data);
    }
}