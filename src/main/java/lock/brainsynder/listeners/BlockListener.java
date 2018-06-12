package lock.brainsynder.listeners;

import lock.brainsynder.utils.ProtectionUtils;
import lock.brainsynder.utils.Utilities;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.List;

public class BlockListener implements Listener {

    // Should prevent zombies from breaking the door
    @EventHandler
    public void onZombieBreak (EntityBreakDoorEvent e) {
        if (!(e.getEntity() instanceof Zombie)) return;
        if (ProtectionUtils.hasAttachedSign(e.getBlock()) != null) e.setCancelled(true);
    }

    @EventHandler
    public void onPhysics (BlockPhysicsEvent e) {
        if (ProtectionUtils.hasAttachedSign(e.getBlock()) != null) e.setCancelled(true);
    }

    // Should prevent the blocks from being exploded
    @EventHandler
    public void onExplode (EntityExplodeEvent e) {
        if (e.blockList().isEmpty()) return;

        List<Block> removeList = Utilities.canExplode(e.blockList());
        e.blockList().removeIf(removeList::contains);
    }

    // Should stop pistons from breaking doors and such...
    @EventHandler
    public void onBlockPistonEvent(BlockPistonExtendEvent e) {
        if (e.getBlocks().isEmpty()) return;
        if (!Utilities.canMoveBlocks(e.getBlocks())) e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPistonEvent(BlockPistonRetractEvent e) {
        if (e.getBlocks().isEmpty()) return;
        if (!Utilities.canMoveBlocks(e.getBlocks())) e.setCancelled(true);
    }

    @EventHandler
    public void onHopperRemoveItem(InventoryMoveItemEvent e) {
        if (e.getSource().getType() != InventoryType.CHEST) return;
        Location loc = e.getSource().getLocation();
        if (ProtectionUtils.hasAttachedSign(loc.getWorld().getBlockAt(loc)) != null) {
            e.setCancelled(true);
        }
    }
}
