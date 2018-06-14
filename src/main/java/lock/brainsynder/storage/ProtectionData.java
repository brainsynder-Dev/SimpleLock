package lock.brainsynder.storage;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import simple.brainsynder.nbt.StorageTagCompound;
import simple.brainsynder.utils.Base64Wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Data that will be stored for a protected block
public class ProtectionData {
    private String ownerName;
    private String ownerUUID;
    private boolean allowHoppers = false;
    private boolean allowRedstone = false;
    private boolean allowFriends = false;

    // A collection of names/UUIDs of players that can
    // access the block when the owner is offline
    private Map<String, String> trusted = new HashMap<>();

    // A collection of names/UUIDs of players that can
    // only access the block when the owner is online
    private Map<String, String> added = new HashMap<>();

    // A collection of UUIDs that are temporarily able to access/use the block
    private Map<String, TimeInfo> tempAdded = new HashMap<> ();

    public boolean isPlayerAllowed (Player player) {
        if (player.getUniqueId().toString().equals(ownerUUID)) return true;
        if (isTrusted(player)) return true;
        if (isAdded(player) || isTemporary(player)) {
            Player owner = Bukkit.getPlayer(UUID.fromString(ownerUUID));
            return (owner != null) && (owner.isOnline());
        }

        return false;
    }

    // IS PLAYER ALLOWED
    public boolean isOwner (OfflinePlayer player) {
        return player.getUniqueId().toString().equals(ownerUUID);
    }
    public boolean isTrusted (Player player) {
        if (trusted.containsKey(player.getName())) return true;
        for (String uuid : trusted.values()) {
            if (uuid.equals(player.getUniqueId().toString())) return true;
        }

        return false;
    }
    public boolean isAdded (Player player) {
        if (added.containsKey(player.getName())) return true;
        for (String uuid : added.values()) {
            if (uuid.equals(player.getUniqueId().toString())) return true;
        }

        return false;
    }
    public boolean isTemporary (Player player) {
        if (tempAdded.containsKey(player.getUniqueId().toString())) {
            boolean remaining=tempAdded.get(player.getUniqueId().toString()).hasTimeRemaining();
            if (!remaining) tempAdded.remove(player.getUniqueId().toString());
            return remaining;
        }
        return false;
    }

    public boolean remove (OfflinePlayer player) {
        if (isOwner(player)) return false;
        if (added.containsKey(player.getName())) {
            added.remove(player.getName());
            return true;
        }
        if (trusted.containsKey(player.getName())) {
            trusted.remove(player.getName());
            return true;
        }
        if (tempAdded.containsKey(player.getUniqueId().toString())) {
            tempAdded.remove(player.getUniqueId().toString());
            return true;
        }
        return false;
    }

    // ADD PLAYERS
    public boolean addTrusted (OfflinePlayer player) {
        if (trusted.containsKey(player.getName())) return false;

        // Clean up user if they are already added
        added.remove(player.getName());
        tempAdded.remove(player.getUniqueId().toString());

        trusted.put(player.getName(), player.getUniqueId().toString());
        return true;
    }
    public boolean addPlayer (OfflinePlayer player) {
        if (added.containsKey(player.getName())) return false;

        // Clean up user if they are already added
        trusted.remove(player.getName());
        tempAdded.remove(player.getUniqueId().toString());

        added.put(player.getName(), player.getUniqueId().toString());
        return true;
    }
    public boolean addTemporary (OfflinePlayer player, int seconds) {
        if (tempAdded.containsKey(player.getUniqueId().toString())) return false;

        // Clean up user if they are already added
        added.remove(player.getName());
        trusted.remove(player.getName());

        TimeInfo info = new TimeInfo();
        info.setSeconds(seconds);
        info.setStart(System.currentTimeMillis());
        tempAdded.put(player.getUniqueId().toString(), info);
        return true;
    }


    /* GETTERS */
    public boolean canAllowFriends() {
        return allowFriends;
    }
    public boolean canAllowHoppers() {
        return allowHoppers;
    }
    public boolean canAllowRedstone() {
        return allowRedstone;
    }
    public Map<String, TimeInfo> getTempAdded() {
        return tempAdded;
    }
    public Map<String, String> getAdded() {
        return added;
    }
    public Map<String, String> getTrusted() {
        return trusted;
    }
    public String getOwnerName() {
        return ownerName;
    }
    public String getOwnerUUID() {
        return ownerUUID;
    }

    /* SETTERS */
    public void setAdded(Map<String, String> added) {
        this.added = added;
    }
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    public void setOwnerUUID(String ownerUUID) {
        this.ownerUUID = ownerUUID;
    }
    public void setTrusted(Map<String, String> trusted) {
        this.trusted = trusted;
    }
    public void setTempAdded(Map<String, TimeInfo> tempAdded) {
        this.tempAdded = tempAdded;
    }

    public StorageTagCompound toCompound () {
        StorageTagCompound compound = new StorageTagCompound();

        compound.setString("name", ownerName);
        compound.setString("uuid", ownerUUID);


        if (trusted.isEmpty()) {
            compound.setString("trusted", Base64Wrapper.encodeString("[]"));
        }else{
            JSONArray array = new JSONArray();
            trusted.forEach((name, uuid) -> {
                JSONObject json = new JSONObject();
                json.put("name", name);
                json.put("uuid", uuid);
                array.add(json);
            });
            compound.setString("trusted", Base64Wrapper.encodeString(array.toJSONString()));
        }


        if (added.isEmpty()) {
            compound.setString("added", Base64Wrapper.encodeString("[]"));
        }else{
            JSONArray array = new JSONArray();
            added.forEach((name, uuid) -> {
                JSONObject json = new JSONObject();
                json.put("name", name);
                json.put("uuid", uuid);
                array.add(json);
            });
            compound.setString("added", Base64Wrapper.encodeString(array.toJSONString()));
        }


        if (tempAdded.isEmpty()) {
            compound.setString("temporary", Base64Wrapper.encodeString("[]"));
        }else{
            JSONArray array = new JSONArray();
            tempAdded.forEach((name, info) -> {
                JSONObject json = new JSONObject();
                json.put("name", name);
                json.put("start", info.getStart());
                json.put("seconds", info.getSeconds());
                array.add(json);
            });
            compound.setString("temporary", Base64Wrapper.encodeString(array.toJSONString()));
        }

        return compound;
    }

    public void loadCompound (StorageTagCompound compound) {
        ownerName = compound.getString("name");
        ownerUUID = compound.getString("uuid");

        if (compound.hasKey("trusted")) {
            JSONArray array = StorageMaker.getJSONArray(compound, "trusted");
            array.forEach(o -> {
                JSONObject json = (JSONObject) o;
                trusted.put(String.valueOf(json.get("name")), String.valueOf(json.get("uuid")));
            });
        }

        if (compound.hasKey("added")) {
            JSONArray array = StorageMaker.getJSONArray(compound, "added");
            array.forEach(o -> {
                JSONObject json = (JSONObject) o;
                added.put(String.valueOf(json.get("name")), String.valueOf(json.get("uuid")));
            });
        }

        if (compound.hasKey("temporary")) {
            JSONArray array = StorageMaker.getJSONArray(compound, "temporary");
            array.forEach(o -> {
                JSONObject json = (JSONObject) o;
                String uuid = String.valueOf(json.get("name"));
                ProtectionData.TimeInfo info = new ProtectionData.TimeInfo();
                info.setStart(Long.parseLong(String.valueOf(json.get("start"))));
                info.setSeconds(Integer.parseInt(String.valueOf(json.get("seconds"))));
                if (info.hasTimeRemaining()) {
                    tempAdded.put(uuid, info);
                }
            });
        }
    }

    public static class TimeInfo {
        private long start = 0;
        private int seconds = 0;

        public void setStart(long start) {
            this.start = start;
        }

        public void setSeconds(int seconds) {
            this.seconds = seconds;
        }

        public long getStart() {
            return start;
        }

        public int getSeconds() {
            return seconds;
        }

        public boolean hasTimeRemaining () {
            long seconds = start / 1000L;
            long secondsLeft = (seconds + this.seconds) - (System.currentTimeMillis() / 1000L);
            return (secondsLeft > 0L);
        }

        public int getRemainingTime () {
            long seconds = start / 1000L;
            return (int) ((seconds + this.seconds) - (System.currentTimeMillis() / 1000L));
        }
    }
}

