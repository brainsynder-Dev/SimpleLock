package lock.brainsynder.utils;

import org.bukkit.block.*;

class DoubleChestUtil {
    public static DoubleChestInfo getChest(Block block) {
        Chest chest = (Chest) block.getState();
        org.bukkit.material.Chest chestMat = (org.bukkit.material.Chest) chest.getData();
        BlockFace facing = chestMat.getFacing();
        if (chest.getInventory().getHolder() instanceof DoubleChest) {
            DoubleChest doubleChest = (DoubleChest) chest.getInventory().getHolder();

            DoubleChestInfo info = DoubleChestInfo.newInstance();
            BlockState otherBlock = block.getRelative(BlockFace.WEST).getState();
            switch (facing) {
                case NORTH:
                    if (otherBlock instanceof Chest) {
                        Chest otherChest = (Chest) otherBlock;
                        if (otherChest.getInventory().getHolder() instanceof DoubleChest && doubleChest.getLocation().equals(((DoubleChest) otherChest.getInventory().getHolder()).getLocation())) {
                            info.setBlock(Side.LEFT, block);
                        }
                    }
                    info.setBlock(block.getRelative(BlockFace.EAST));
                    break;


                case EAST:
                    otherBlock = block.getRelative(BlockFace.NORTH).getState();
                    if (otherBlock instanceof Chest) {
                        Chest otherChest = (Chest) otherBlock;
                        if (otherChest.getInventory().getHolder() instanceof DoubleChest && doubleChest.getLocation().equals(((DoubleChest) otherChest.getInventory().getHolder()).getLocation())) {
                            info.setBlock(Side.LEFT, block);
                        }
                    }
                    info.setBlock(block.getRelative(BlockFace.SOUTH));
                    break;


                case SOUTH:
                    otherBlock = block.getRelative(BlockFace.EAST).getState();
                    if (otherBlock instanceof Chest) {
                        Chest otherChest = (Chest) otherBlock;
                        if (otherChest.getInventory().getHolder() instanceof DoubleChest && doubleChest.getLocation().equals(((DoubleChest) otherChest.getInventory().getHolder()).getLocation())) {
                            info.setLocation(Side.LEFT, otherBlock.getLocation());
                        }
                    }
                    info.setBlock(block);
                    break;


                case WEST:
                    otherBlock = block.getRelative(BlockFace.SOUTH).getState();
                    if (otherBlock instanceof Chest) {
                        Chest otherChest = (Chest) otherBlock;
                        if (otherChest.getInventory().getHolder() instanceof DoubleChest && doubleChest.getLocation().equals(((DoubleChest) otherChest.getInventory().getHolder()).getLocation())) {
                            info.setLocation(Side.LEFT, otherBlock.getLocation());
                        }
                    }
                    info.setBlock(block);
                    break;
            }
            return info;
        }
        return null;
    }
}