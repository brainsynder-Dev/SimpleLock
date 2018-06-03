package lock.brainsynder.listeners;

import lock.brainsynder.utils.Utilities;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;

public class BlockListener implements Listener {

    // Should prevent the blocks from being exploded
    @EventHandler(priority = EventPriority.HIGHEST)
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
}
