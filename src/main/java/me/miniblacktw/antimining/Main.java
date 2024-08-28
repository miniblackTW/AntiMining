package me.miniblacktw.antimining;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class Main extends JavaPlugin {

    private final Map<UUID, Long> playerStationaryTime = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getScheduler().runTaskTimer(this, this::checkPlayers, 20L, 20L);
    }

    @Override
    public void onDisable() {
        playerStationaryTime.clear();
    }

    @SuppressWarnings("deprecation")
    private void checkPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("antimining.bypass")) {
                continue;
            }

            Block targetBlock = player.getTargetBlock((HashSet<Byte>) null, 5);
            if (targetBlock == null || targetBlock.getType() != Material.GOLD_BLOCK) {
                playerStationaryTime.remove(player.getUniqueId());
                continue;
            }

            if (player.getInventory().getItemInHand().getType() != Material.DIAMOND_PICKAXE) {
                playerStationaryTime.remove(player.getUniqueId());
                continue;
            }

            UUID playerId = player.getUniqueId();
            if (!playerStationaryTime.containsKey(playerId)) {
                playerStationaryTime.put(playerId, System.currentTimeMillis());
            } else {
                long stationaryTime = System.currentTimeMillis() - playerStationaryTime.get(playerId);
                if (stationaryTime >= 60000) {
                    liftPlayerHead(player);
                    playerStationaryTime.remove(playerId);
                }
            }
        }
    }

    private void liftPlayerHead(Player player) {
        player.teleport(player.getLocation().setDirection(player.getLocation().getDirection().setY(1)));
        player.sendMessage("§cStop mining golds while being idle!!!");
    }
}