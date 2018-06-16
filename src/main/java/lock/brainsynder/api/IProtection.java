package lock.brainsynder.api;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import simple.brainsynder.nbt.StorageTagCompound;

import java.util.Map;

public interface IProtection {
    /**
     * Checks if the player is allowed
     *
     * @param player
     * @return
     *      true  | isOwner, isTrusted, isAdded, or isTemporary
     *      false | is not trusted or owner, of if owner is not online
     */
    boolean isPlayerAllowed (Player player);

    /**
     * Checks if the OfflinePlayer is the owner of the block
     */
    boolean isOwner (OfflinePlayer player);

    /**
     * Checks if the player is trusted
     */
    boolean isTrusted (OfflinePlayer player);

    /**
     * Checks if the player is added
     *      see {@link #isPlayerAllowed(Player)}
     */
    boolean isAdded (OfflinePlayer player);

    /**
     * Checks if the player is temporarily added
     *      see {@link #isPlayerAllowed(Player)}
     */
    boolean isTemporary (OfflinePlayer player);

    /**
     * Will remove the player from the role they are currently in
     */
    ReturnResult remove (OfflinePlayer player);

    ReturnResult addTrusted (OfflinePlayer player);
    ReturnResult addPlayer (OfflinePlayer player);
    ReturnResult addTemporary (OfflinePlayer player, int seconds);

    boolean canAllowFriends();
    boolean canAllowHoppers();
    boolean canAllowRedstone();

    Map<String, TimeInfo> getTempAdded();
    Map<String, String> getAdded();
    Map<String, String> getTrusted();

    String getOwnerName();
    String getOwnerUUID();

    void setAdded(Map<String, String> added);
    void setOwnerName(String ownerName);
    void setOwnerUUID(String ownerUUID);
    void setTrusted(Map<String, String> trusted);
    void setTempAdded(Map<String, TimeInfo> tempAdded);

    StorageTagCompound toCompound ();
    void loadCompound (StorageTagCompound compound);

    enum ReturnResult {
        ALREADY_EXISTING,
        SUCCEDED,
        MISSING,
        FAILED
    }
}
