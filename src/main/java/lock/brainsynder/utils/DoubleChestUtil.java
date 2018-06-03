package lock.brainsynder.utils;

import org.bukkit.Location;
import org.bukkit.block.*;

import static org.bukkit.block.BlockFace.*;

class DoubleChestUtil {
    public static DoubleChestInfo getChest(Block block) {
        Chest chest = (Chest) block.getState();
        if (chest.getInventory().getHolder() instanceof DoubleChest) {
            DoubleChestInfo info = DoubleChestInfo.newInstance();
            info.setLocation(Side.RIGHT, getTopInventory(block));
            info.setLocation(Side.LEFT, getBottomInventory(block));
            return info;
        }
        return null;
    }

    private static Location getTopInventory(Block block) {
        Chest chest = (Chest) block.getState();
        org.bukkit.material.Chest chestMat = (org.bukkit.material.Chest) chest.getData();
        BlockFace facing = chestMat.getFacing();
        if (chest.getInventory().getHolder() instanceof DoubleChest) {
            DoubleChest doubleChest = (DoubleChest) chest.getInventory().getHolder();

            if (facing == NORTH) { // NORTH
                BlockState otherBlock = block.getRelative(WEST).getState();
                if (otherBlock instanceof Chest) {
                    Chest otherChest = (Chest) otherBlock;
                    if (otherChest.getInventory().getHolder() instanceof DoubleChest
                            && doubleChest.getLocation().equals(((DoubleChest) otherChest.getInventory().getHolder()).getLocation())) {
                        return otherBlock.getLocation();
                    }
                }
                return block.getLocation();
            } else if (facing == EAST) { // EAST
                BlockState otherBlock = block.getRelative(NORTH).getState();
                if (otherBlock instanceof Chest) {
                    Chest otherChest = (Chest) otherBlock;
                    if (otherChest.getInventory().getHolder() instanceof DoubleChest
                            && doubleChest.getLocation().equals(((DoubleChest) otherChest.getInventory().getHolder()).getLocation())) {
                        return otherBlock.getLocation();
                    }
                }
                return block.getLocation();
            } else if (facing == SOUTH) { // SOUTH
                BlockState otherBlock = block.getRelative(EAST).getState();
                if (otherBlock instanceof Chest) {
                    Chest otherChest = (Chest) otherBlock;
                    if (otherChest.getInventory().getHolder() instanceof DoubleChest
                            && doubleChest.getLocation().equals(((DoubleChest) otherChest.getInventory().getHolder()).getLocation())) {
                        return block.getLocation();
                    }
                }
                return block.getRelative(WEST).getLocation();
            } else if (facing == WEST) { // WEST
                BlockState otherBlock = block.getRelative(SOUTH).getState();
                if (otherBlock instanceof Chest) {
                    Chest otherChest = (Chest) otherBlock;
                    if (otherChest.getInventory().getHolder() instanceof DoubleChest
                            && doubleChest.getLocation().equals(((DoubleChest) otherChest.getInventory().getHolder()).getLocation())) {
                        return block.getLocation();
                    }
                }
                return block.getRelative(NORTH).getLocation();
            }
        }
        return block.getLocation();
    }

    private static Location getBottomInventory(Block block) {
        Chest chest = (Chest) block.getState();
        org.bukkit.material.Chest chestMat = (org.bukkit.material.Chest) chest.getData();
        BlockFace facing = chestMat.getFacing();
        if (chest.getInventory().getHolder() instanceof DoubleChest) {
            DoubleChest doubleChest = (DoubleChest) chest.getInventory().getHolder();

            if (facing == NORTH) { // NORTH
                BlockState otherBlock = block.getRelative(WEST).getState();
                if (otherBlock instanceof Chest) {
                    Chest otherChest = (Chest) otherBlock;
                    if (otherChest.getInventory().getHolder() instanceof DoubleChest
                            && doubleChest.getLocation().equals(((DoubleChest) otherChest.getInventory().getHolder()).getLocation())) {
                        return block.getLocation();
                    }
                }
                return block.getRelative(EAST).getLocation();
            } else if (facing == EAST) { // EAST
                BlockState otherBlock = block.getRelative(NORTH).getState();
                if (otherBlock instanceof Chest) {
                    Chest otherChest = (Chest) otherBlock;
                    if (otherChest.getInventory().getHolder() instanceof DoubleChest
                            && doubleChest.getLocation().equals(((DoubleChest) otherChest.getInventory().getHolder()).getLocation())) {
                        return block.getLocation();
                    }
                }
                return block.getRelative(SOUTH).getLocation();
            } else if (facing == SOUTH) { // SOUTH
                BlockState otherBlock = block.getRelative(EAST).getState();
                if (otherBlock instanceof Chest) {
                    Chest otherChest = (Chest) otherBlock;
                    if (otherChest.getInventory().getHolder() instanceof DoubleChest
                            && doubleChest.getLocation().equals(((DoubleChest) otherChest.getInventory().getHolder()).getLocation())) {
                        return otherBlock.getLocation();
                    }
                }
                return block.getLocation();
            } else if (facing == WEST) { // WEST
                BlockState otherBlock = block.getRelative(SOUTH).getState();
                if (otherBlock instanceof Chest) {
                    Chest otherChest = (Chest) otherBlock;
                    if (otherChest.getInventory().getHolder() instanceof DoubleChest
                            && doubleChest.getLocation().equals(((DoubleChest) otherChest.getInventory().getHolder()).getLocation())) {
                        return otherBlock.getLocation();
                    }
                }
                return block.getLocation();
            }
        }
        return null;
    }
}