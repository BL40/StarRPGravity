package dev.bronzylobster.starrpgravity.managers;

import dev.bronzylobster.starrpgravity.StarRPGravity;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GravityDataManager {
    private final JavaPlugin plugin;
    private final File dataFile;
    private final YamlConfiguration dataConfig;

    public GravityDataManager() {
        this.plugin = StarRPGravity.getInstance();
        this.dataFile = new File(plugin.getDataFolder(), "gravity_data.yml");
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void saveData(Map<UUID, Double> customGravityPlayers, Map<UUID, Long> gravityExpiryTimes) {
        for (Map.Entry<UUID, Double> entry : customGravityPlayers.entrySet()) {
            UUID entityId = entry.getKey();
            double gravityMultiplier = entry.getValue();
            long expiryTime = gravityExpiryTimes.getOrDefault(entityId, -1L);

            dataConfig.set("gravity." + entityId + ".multiplier", gravityMultiplier);
            dataConfig.set("gravity." + entityId + ".expiry", expiryTime);
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить данные о гравитации: " + e.getMessage());
        }
    }

    public Map<UUID, Double> loadData(Map<UUID, Long> gravityExpiryTimes) {
        Map<UUID, Double> customGravityPlayers = new HashMap<>();

        if (dataConfig.contains("gravity")) {
            for (String key : dataConfig.getConfigurationSection("gravity").getKeys(false)) {
                UUID entityId = UUID.fromString(key);
                double gravityMultiplier = dataConfig.getDouble("gravity." + key + ".multiplier");
                long expiryTime = dataConfig.getLong("gravity." + key + ".expiry", -1L);

                if (expiryTime == -1 || expiryTime > System.currentTimeMillis()) {
                    customGravityPlayers.put(entityId, gravityMultiplier);
                    gravityExpiryTimes.put(entityId, expiryTime);
                }
            }
        }

        return customGravityPlayers;
    }
}
