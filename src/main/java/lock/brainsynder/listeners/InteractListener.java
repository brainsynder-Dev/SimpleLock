package lock.brainsynder.listeners;

import lock.brainsynder.Core;
import lock.brainsynder.api.IProtection;
import lock.brainsynder.utils.ProtectionUtils;
import lock.brainsynder.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class InteractListener implements Listener {
    private Core core;

    public InteractListener(Core core) {
        this.core = core;
    }

    @EventHandler
    public void addSign(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = e.getClickedBlock();
        if (block == null) return;
        if (block.getType() == Material.WALL_SIGN) return;
        if (!core.getProtectedTypes().contains(block.getType())) return; // Chests that the block can be protected
        BlockFace clickedFace = e.getBlockFace();
        if ((clickedFace != BlockFace.NORTH)
                && (clickedFace != BlockFace.SOUTH)
                && (clickedFace != BlockFace.EAST)
                && (clickedFace != BlockFace.WEST)) return;

        if (ProtectionUtils.findProtectionSign(block) == null) {
            // This block is not protected
            Player player = e.getPlayer();
            ItemStack item = player.getEquipment().getItemInMainHand().clone();
            if (item == null) return;
            if (item.getType() != Material.SIGN) return;

            Block relative = block.getRelative(clickedFace);
            if (relative == null) return;
            if (relative.getType() != Material.AIR) return;

            // Verifies the player can place a block here
            BlockPlaceEvent event = new BlockPlaceEvent(relative, relative.getState(), block, e.getItem(), player, true, EquipmentSlot.HAND);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) return;
            Utilities.placeSign(relative, clickedFace, "[Private]", player.getName());

            e.setCancelled(true);
            e.setUseInteractedBlock(Event.Result.DENY);
            e.setUseItemInHand(Event.Result.DENY);
            ProtectionUtils.registerProtection(player, relative, core);
            // Handles the item (decreases/removes it)
            if ((player.getGameMode() == GameMode.CREATIVE) || (player.getGameMode() == GameMode.SPECTATOR)) return;
            if (item.getAmount() == 1) {
                player.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
            } else {
                item.setAmount(item.getAmount() - 1);
                player.getEquipment().setItemInMainHand(item);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void tryClick (PlayerInteractEvent e) {
        if (e.getAction().name().contains("RIGHT")) return;

        if (e.getPlayer().isSneaking()){
            if (e.getItem() != null) {
                if (!e.getItem().getType().name().contains("SIGN")) return;
            }
        }
        Block block = e.getClickedBlock();
        if (block == null) return;
        Sign sign = ProtectionUtils.findProtectionSign(block);
        if (sign == null) return;
        IProtection data = ProtectionUtils.getProtectionInfo(sign);
        if (data == null) return;
        if (!data.isOwner(e.getPlayer())) {
            if (data.isPlayerAllowed(e.getPlayer())) return;
            e.setCancelled(true);
            e.setUseInteractedBlock(Event.Result.DENY);
            e.setUseItemInHand(Event.Result.DENY);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (block == null) return;
        Sign protect = ProtectionUtils.findProtectionSign(block);
        Sign sign = ProtectionUtils.hasAttachedSign(block);
        if (sign == null) { // No sign on the block
            if (protect == null) return; // Is not protected

            // block is protected
            IProtection data = ProtectionUtils.getProtectionInfo(protect);
            if (data == null) return;
            if (!data.isOwner(e.getPlayer())) {
                e.setCancelled(true);
            }
            return;
        }
        IProtection data = ProtectionUtils.getProtectionInfo(sign);
        if (data == null) return;
        if (!data.isOwner(e.getPlayer())) {
            e.setCancelled(true);
            return;
        }


        ProtectionUtils.removeProtection(sign);
        Utilities.sendActionMessage(e.getPlayer(), "&a&lBlock has been successfully unprotected");
    }

    // prevents the player from placing a chest next to an already claimed chest (when forming a double chest)
    @EventHandler
    public void onPlace (BlockPlaceEvent e) {
        if (!Utilities.canPlaceBlock(e.getBlockPlaced(), e.getPlayer())) {
            e.setBuild(false);
            e.setCancelled(true);
        }
    }
}
