package lock.brainsynder.utils;

import lock.brainsynder.Core;
import lock.brainsynder.api.IProtection;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;
import org.bukkit.scheduler.BukkitRunnable;
import simple.brainsynder.nms.IActionMessage;
import simple.brainsynder.utils.Reflection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utilities {
    private static IActionMessage actionMessage;
    private static final BlockFace[] ALL_FACES = {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    static final BlockFace[] FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    static {
        actionMessage = Reflection.getActionMessage();
    }

    public static boolean isDoubleChest(Block block) {
        BlockState state = block.getState();
        if (state instanceof Chest) {
            Chest chest = (Chest) state;
            Inventory inventory = chest.getInventory();
            return inventory instanceof DoubleChestInventory;
        }

        return false;
    }

    public static boolean canMoveBlocks(List<Block> blocks) {
        for (Block block : blocks) {
            if (ProtectionUtils.findProtectionSign(block) != null) return false;
        }
        return true;
    }

    public static boolean canPlaceBlock(Block block, Player player) {
        switch (block.getType()) {
            case CHEST:
            case TRAPPED_CHEST:
                for (BlockFace face : FACES) {
                    Block b = block.getRelative(face);
                    if ((b.getType() == block.getType())) {
                        Sign sign = ProtectionUtils.hasAttachedSign(b);
                        if (sign != null) {
                            IProtection data = ProtectionUtils.getProtectionInfo(sign);
                            if (!data.isOwner(player)) {
                                return false;
                            }
                        }
                    }
                }
                break;
        }
        return true;
    }

    public static List<Block> canExplode(List<Block> blocks) {
        List<Block> remove = new ArrayList<>();
        for (Block block : blocks) {
            if (ProtectionUtils.findProtectionSign(block) != null) remove.add(block);
        }
        return remove;
    }

    public static boolean isSimilar(ItemStack[] content1, ItemStack[] content2) {
        if (content1.length != content2.length) return false;
        for (int i = 0; i < content1.length; i++) {
            if ((content1[i] == null) || (content1[i].getType() == Material.AIR)) continue;
            if ((content2[i] == null) || (content2[i].getType() == Material.AIR)) continue;
            if (!content1[i].isSimilar(content2[i])) return false;
        }

        return true;
    }

    public static void placeSign(Block block, BlockFace facing, String... lines) {
        block.setType(Material.WALL_SIGN);
        byte data;
        // Why on Earth... does the setFacing not work...
        // (I hate doing the rotations this way...)
        switch (facing) {
            case NORTH:
                data = 2;
                break;
            case EAST:
                data = 5;
                break;
            case WEST:
                data = 4;
                break;
            case SOUTH:
                data = 3;
                break;
            default:
                return;
        }
        block.setData(data, true);
        block.getState().update();

        if (lines.length != 0) {
            Sign sign = (Sign) block.getState();
            for (int i = 0; i < lines.length; i++) {
                sign.setLine(i, lines[i]);
            }
            sign.update();
        }
    }

    public static void updateSign(Sign sign, String... lines) {
        if (lines.length != 0) {
            for (int i = 0; i < lines.length; i++) {
                sign.setLine(i, lines[i]);
            }
            sign.update();
        }
    }

    public static void sendActionMessage(Player player, String message) {
        if (actionMessage == null) actionMessage = Reflection.getActionMessage();
        if (player == null) return;
        if ((message == null) || (message.isEmpty())) return;

        actionMessage.sendMessage(player, ChatColor.translateAlternateColorCodes('&', message));
    }

    public static String blockLocToString(Location location) {
        String string = "";
        string += location.getWorld().getName() + ",";
        string += location.getBlockX() + ",";
        string += location.getBlockY() + ",";
        string += location.getBlockZ();
        return string;
    }

    public static BlockFace getBlockFace(Player player) {
        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, 100);
        Block targetBlock = lastTwoTargetBlocks.get(1);
        Block adjacentBlock = lastTwoTargetBlocks.get(0);
        return targetBlock.getFace(adjacentBlock);
    }

    public static Block getBlock(Player player) {
        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, 100);
        return lastTwoTargetBlocks.get(1);
    }

    // Searched the block(s) to check if it is protected
    public static Map<String, Sign> findSigns(Map<String, Sign> signs, Block block) {
        // Checks if the block is a protection sign
        BlockState state = block.getState();
        if (state instanceof Sign) {
            signs.put(blockLocToString(block.getLocation()), (Sign) state);
            org.bukkit.material.Sign sign = (org.bukkit.material.Sign) state.getData();
            return findSigns(signs, block.getRelative(sign.getAttachedFace()));
        }

        // Checks if the block has a protection sign on its BlockFaces
        for (BlockFace face : Utilities.FACES) {
            Block b = block.getRelative(face);
            if (b.getState() instanceof Sign) {
                org.bukkit.material.Sign sign = (org.bukkit.material.Sign) b.getState().getData();
                if (sign.getAttachedFace() == face.getOppositeFace())
                    if (!signs.containsKey(blockLocToString(b.getLocation()))) signs.put(blockLocToString(b.getLocation()), (Sign) b.getState());
            }
        }

        List<Location> locations = new ArrayList<>();

        // Checks if the block is part of a double chest, If so it will add the locations
        if (Utilities.isDoubleChest(block)) {
            DoubleChestInfo info = DoubleChestUtil.getChest(block);
            locations.add(info.getLocation(Side.LEFT));
            locations.add(info.getLocation(Side.RIGHT));
        }

        // Checks if the block is part of a door, If it is it will add the opposite location
        if (block.getType().name().contains("DOOR") && (!block.getType().name().contains("TRAP"))) {
            Door door = (Door) block.getState().getData();
            if (door.isTopHalf()) {
                locations.add(block.getRelative(BlockFace.DOWN).getLocation());
            } else {
                locations.add(block.getRelative(BlockFace.UP).getLocation());
            }
        }

        // Checks if the block has a door on top, If so it will add the locations
        Block bottom = block.getRelative(BlockFace.UP);
        if (bottom.getType().name().contains("DOOR") && (!bottom.getType().name().contains("TRAP"))) {
            locations.add(bottom.getLocation());
            locations.add(bottom.getRelative(BlockFace.UP).getLocation());
        }

        if (locations.isEmpty()) return signs;

        // Checks if the locations have a protection sign on them
        for (Location location : locations) {
            for (BlockFace face : Utilities.FACES) {
                Block b = location.getBlock().getRelative(face);
                if (b.getState() instanceof Sign) {
                    org.bukkit.material.Sign sign = (org.bukkit.material.Sign) b.getState().getData();
                    if (sign.getAttachedFace() == face.getOppositeFace())
                        if (!signs.containsKey(blockLocToString(b.getLocation()))) signs.put(blockLocToString(b.getLocation()), (Sign) b.getState());
                }
            }
        }

        return signs;
    }


    // This will be used when I add in code to open/close both doors
    // Need to code in the checks for double doors first
    public static void toggleDoor(Block block, boolean open) {
        BlockState doorstate = block.getState();
        Openable openablestate = (Openable) doorstate.getData();
        openablestate.setOpen(open);
        doorstate.setData((MaterialData) openablestate);
        doorstate.update();
        block.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
    }

    public static void sync (Runnable runnable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTask(Core.getPlugin(Core.class));
    }
}
