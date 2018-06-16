package lock.brainsynder.listeners;

import lock.brainsynder.api.IProtection;
import lock.brainsynder.utils.ProtectionUtils;
import lock.brainsynder.utils.Utilities;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
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

    @EventHandler // Extract Items
    public void onHopperRemoveItem(InventoryMoveItemEvent e) {
        if (e.getSource().getType() != InventoryType.CHEST) return;
        Location loc = e.getSource().getLocation();
        if (ProtectionUtils.hasAttachedSign(loc.getWorld().getBlockAt(loc)) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler // Insert Items
    public void onHopperInsertItem(InventoryMoveItemEvent e) {
        if (e.getDestination().getType() != InventoryType.CHEST) return;
        Location loc = e.getSource().getLocation();
        Sign sign = ProtectionUtils.findProtectionSign(loc.getWorld().getBlockAt(loc));
        if (sign != null) {
            IProtection data = ProtectionUtils.getProtectionInfo(sign);
            if (!data.canAllowHoppers()) e.setCancelled(true);
        }
    }
}
