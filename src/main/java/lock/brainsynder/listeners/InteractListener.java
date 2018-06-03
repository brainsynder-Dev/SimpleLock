package lock.brainsynder.listeners;

import lock.brainsynder.Core;
import lock.brainsynder.utils.Utilities;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener {
    private Core core;

    public InteractListener (Core core) {
        this.core = core;
    }

    @EventHandler
    public void addSign (PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        Utilities.isDoubleChest(e.getClickedBlock());
    }

    // Should stop pistons from breaking doors ans such...
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
