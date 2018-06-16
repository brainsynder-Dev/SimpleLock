package lock.brainsynder.storage;

import lock.brainsynder.api.IProtection;
import lock.brainsynder.api.TimeInfo;
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

public class ProtectionData implements IProtection {
    private String ownerName;
    private String ownerUUID;
    private boolean allowHoppers = false;
    private boolean allowRedstone = false;
    private boolean allowFriends = false;
    private Map<String, String> trusted = new HashMap<>();
    private Map<String, String> added = new HashMap<>();
    private Map<String, TimeInfo> tempAdded = new HashMap<> ();


    @Override
    public boolean isPlayerAllowed(Player player) {
        if (player.getUniqueId().toString().equals(ownerUUID)) return true;
        if (isTrusted(player)) return true;
        if (isAdded(player) || isTemporary(player)) {
            Player owner = Bukkit.getPlayer(UUID.fromString(ownerUUID));
            return (owner != null) && (owner.isOnline());
        }

        return false;
    }

    @Override
    public boolean isOwner(OfflinePlayer player) {
        return player.getUniqueId().toString().equals(ownerUUID);
    }

    @Override
    public boolean isTrusted(OfflinePlayer player) {
        if (trusted.containsKey(player.getName())) return true;
        for (String uuid : trusted.values()) {
            if (uuid.equals(player.getUniqueId().toString())) return true;
        }

        return false;
    }

    @Override
    public boolean isAdded(OfflinePlayer player) {
        if (added.containsKey(player.getName())) return true;
        for (String uuid : added.values()) {
            if (uuid.equals(player.getUniqueId().toString())) return true;
        }

        return false;
    }

    @Override
    public boolean isTemporary(OfflinePlayer player) {
        if (tempAdded.containsKey(player.getUniqueId().toString())) {
            boolean remaining=tempAdded.get(player.getUniqueId().toString()).hasTimeRemaining();
            if (!remaining) tempAdded.remove(player.getUniqueId().toString());
            return remaining;
        }
        return false;
    }

    @Override
    public ReturnResult remove(OfflinePlayer player) {
        if (isOwner(player)) return ReturnResult.FAILED;
        if (added.containsKey(player.getName())) {
            added.remove(player.getName());
            return ReturnResult.SUCCEDED;
        }
        if (trusted.containsKey(player.getName())) {
            trusted.remove(player.getName());
            return ReturnResult.SUCCEDED;
        }
        if (tempAdded.containsKey(player.getUniqueId().toString())) {
            tempAdded.remove(player.getUniqueId().toString());
            return ReturnResult.SUCCEDED;
        }
        return ReturnResult.MISSING;
    }

    @Override
    public ReturnResult addTrusted(OfflinePlayer player) {
        if (isOwner(player)) return ReturnResult.FAILED;
        if (trusted.containsKey(player.getName())) return ReturnResult.ALREADY_EXISTING;

        // Clean up user if they are already added
        added.remove(player.getName());
        tempAdded.remove(player.getUniqueId().toString());

        trusted.put(player.getName(), player.getUniqueId().toString());
        return ReturnResult.SUCCEDED;
    }

    @Override
    public ReturnResult addPlayer(OfflinePlayer player) {
        if (isOwner(player)) return ReturnResult.FAILED;
        if (added.containsKey(player.getName())) return ReturnResult.ALREADY_EXISTING;

        // Clean up user if they are already added
        trusted.remove(player.getName());
        tempAdded.remove(player.getUniqueId().toString());

        added.put(player.getName(), player.getUniqueId().toString());
        return ReturnResult.SUCCEDED;
    }

    @Override
    public ReturnResult addTemporary(OfflinePlayer player, int seconds) {
        if (isOwner(player)) return ReturnResult.FAILED;
        String uuid = player.getUniqueId().toString();
        if (isTemporary(player)) return ReturnResult.ALREADY_EXISTING;

        // Clean up user if they are already added
        added.remove(player.getName());
        trusted.remove(player.getName());

        TimeInfo info = new TimeInfo();
        info.setSeconds(seconds);
        info.setStart(System.currentTimeMillis());
        tempAdded.put(uuid, info);
        return ReturnResult.SUCCEDED;
    }

    @Override
    public boolean canAllowFriends() {
        return allowFriends;
    }

    @Override
    public boolean canAllowHoppers() {
        return allowHoppers;
    }

    @Override
    public boolean canAllowRedstone() {
        return allowRedstone;
    }

    @Override
    public Map<String, TimeInfo> getTempAdded() {
        return tempAdded;
    }

    @Override
    public Map<String, String> getAdded() {
        return added;
    }

    @Override
    public Map<String, String> getTrusted() {
        return trusted;
    }

    @Override
    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public String getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public void setAdded(Map<String, String> added) {
        this.added = added;
    }

    @Override
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @Override
    public void setOwnerUUID(String ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    @Override
    public void setTrusted(Map<String, String> trusted) {
        this.trusted = trusted;
    }

    @Override
    public void setTempAdded(Map<String, TimeInfo> tempAdded) {
        this.tempAdded = tempAdded;
    }

    @Override
    public StorageTagCompound toCompound() {
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

    @Override
    public void loadCompound(StorageTagCompound compound) {
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
                TimeInfo info = new TimeInfo();
                info.setStart(Long.parseLong(String.valueOf(json.get("start"))));
                info.setSeconds(Integer.parseInt(String.valueOf(json.get("seconds"))));
                if (info.hasTimeRemaining()) {
                    tempAdded.put(uuid, info);
                }
            });
        }
    }
}
