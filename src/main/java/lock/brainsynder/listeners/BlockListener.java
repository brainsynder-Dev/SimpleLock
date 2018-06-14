package lock.brainsynder.listeners;

import lock.brainsynder.storage.ProtectionData;
import lock.brainsynder.utils.ProtectionUtils;
import lock.brainsynder.utils.Utilities;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

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
    public void onBlockRedstoneChange(BlockRedstoneEvent e) {
        Block block = e.getBlock();
        Sign sign = ProtectionUtils.findProtectionSign(block);
        if (sign != null) {
            ProtectionData data = ProtectionUtils.getProtectionInfo(sign);
            if (!data.canAllowRedstone()) e.setNewCurrent(e.getOldCurrent());
        }
    }
}
