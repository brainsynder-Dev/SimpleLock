package lock.brainsynder.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public class Utilities {
    private static final BlockFace[] ALL_FACES = { BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
    private static final BlockFace[] FACES = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };

    public static boolean isDoubleChest (Block block) {
        BlockState state = block.getState();
        if (state instanceof Chest) {
            Chest chest = (Chest) state;
            Inventory inventory = chest.getInventory();
            return inventory instanceof DoubleChestInventory;
        }

        return false;
    }

    public static boolean canMoveBlocks (List<Block> blocks) {
        List<Block> checked = new ArrayList<>();
        for (Block block : blocks) {
            if (!canMoveBlock(checked, block)) return false;
        }
        return true;
    }

    public static List<Block> canExplode (List<Block> blocks) {
        List<Block> remove = new ArrayList<>();
        List<Block> checked = new ArrayList<>();
        for (Block block : blocks) {
            if (isProtected(checked, block))remove.add(block);
        }
        return remove;
    }

    private static boolean isProtected (List<Block> checkedBlocks, Block block) {
        if (findSign(block.getLocation()) != null) return true;
        for (BlockFace face : ALL_FACES) {
            Block b = block.getRelative(face);
            if (b == null) continue;
            Material type = b.getType();
            if (type == Material.AIR) continue;
            if (checkedBlocks.contains(b)) continue;
            checkedBlocks.add(b);

            if (findSign(b.getLocation()) != null) return true;

            Block above = b.getRelative(BlockFace.UP);
            if (above == null) continue;
            if (above.getType() == Material.AIR) continue;
            if (findSign(above.getLocation()) != null) return true;
        }

        return false;
    }

    // Checks if a can be moved (If the block is protected it will return false).
    public static boolean canMoveBlock(List<Block> checkedBlocks, Block block) {
        if (findSign(block.getLocation()) != null) return false;
        for (BlockFace face : ALL_FACES) {
            Block b = block.getRelative(face);
            if (b == null) continue;
            Material type = b.getType();
            if (type == Material.AIR) continue;
            if (checkedBlocks.contains(b)) continue;
            checkedBlocks.add(b);

            if (type == Material.SLIME_BLOCK) return canMoveBlock(checkedBlocks, b);
            if (findSign(b.getLocation()) != null) return false;

            Block above = b.getRelative(BlockFace.UP);
            if (above == null) continue;
            if (above.getType() == Material.AIR) continue;
            if (above.getType() == Material.SLIME_BLOCK) return canMoveBlock(checkedBlocks, above);
            if (findSign(above.getLocation()) != null) return false;
        }
        return true;
    }

    public static Sign findSign (Location location) {
        List<Location> locations = new ArrayList<>();
        locations.add(location);
        Block block = location.getBlock();
        if (block == null) return null;
        if (block.getType() == Material.AIR) return null;

        MaterialData data = block.getState().getData();

        if (data instanceof Door) {
            Door door = (Door) data;
            if (door.isTopHalf()) {
                locations.add(location.clone().subtract(0,1,0));
            }else{
                locations.add(location.clone().add(0,1,0));
            }
        }

        for (Location loc : locations) {
            for (BlockFace face : FACES) {
                Block b = loc.getBlock().getRelative(face);
                if (b == null) continue;
                if (b.getType() != Material.WALL_SIGN) continue;
                Sign sign = (Sign) b.getState();
                if (sign.getLines()[0].equals("[Private]")) return sign;
            }
        }
        return null;
    }

    public static boolean isSimilar (ItemStack[] content1, ItemStack[] content2) {
        if (content1.length != content2.length) return false;
        for (int i = 0; i < content1.length; i++) {
            if ((content1[i] == null) || (content1[i].getType() == Material.AIR)) continue;
            if ((content2[i] == null) || (content2[i].getType() == Material.AIR)) continue;
            if (!content1[i].isSimilar(content2[i])) return false;
        }

        return true;
    }
}
