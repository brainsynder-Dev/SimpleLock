package lock.brainsynder.listeners;

import lock.brainsynder.utils.ProtectionUtils;
import lock.brainsynder.utils.Utilities;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.CraftingInventory;

public class InventoryListener implements Listener {

    @EventHandler
    public void onOpen (InventoryOpenEvent e) {
        if (e.getPlayer() == null) return;
        if (!(e.getPlayer() instanceof Player)) return;
        Player player = (Player) e.getPlayer();

        if (e.getInventory() instanceof CraftingInventory) {
            Block block = Utilities.getBlock(player);
            if (ProtectionUtils.hasAttachedSign(block) == null) return;
            e.setCancelled(true);
            player.closeInventory();
        }
    }
}
