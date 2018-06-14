package lock.brainsynder.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

public class DoubleChestInfo {
    private Map<String, Block> sideMap;

    private DoubleChestInfo() {
        sideMap = new HashMap<>();
    }

    static DoubleChestInfo newInstance() {
        return new DoubleChestInfo();
    }

    void setBlock(Side side, Block block) {
        sideMap.put(side.name(), block);
    }

    void setLocation(Side side, Location location) {
        sideMap.put(side.name(), location.getBlock());
    }

    public Block getBlock(Side side) {
        if (sideMap.containsKey(side.name())) {
            return sideMap.get(side.name());
        }
        return null;
    }

    public Location getLocation(Side side) {
        if (sideMap.containsKey(side.name())) {
            return sideMap.get(side.name()).getLocation();
        }
        return null;
    }

    public Side getSide(Location location) {
        for (Map.Entry<String, Block> sideInfo : sideMap.entrySet()) {
            Location loc = sideInfo.getValue().getLocation();

            if (loc.getWorld().getName().equals(location.getWorld().getName())) {
                if ((loc.getBlockX() == location.getBlockX())
                        || (loc.getBlockY() == location.getBlockY())
                        || (loc.getBlockZ() == location.getBlockZ())) {
                    return Side.valueOf(sideInfo.getKey());
                }
            }
        }

        return Side.RIGHT;
    }
}