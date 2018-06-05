package lock.brainsynder.utils;

import lock.brainsynder.Core;
import lock.brainsynder.storage.ProtectionData;
import lock.brainsynder.storage.StorageMaker;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.material.Door;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import simple.brainsynder.nbt.StorageTagCompound;
import simple.brainsynder.utils.Base64Wrapper;

import java.util.*;

public class ProtectionUtils {
    private static final List<Material> DOORS = Arrays.asList(
            Material.ACACIA_DOOR, Material.BIRCH_DOOR, Material.DARK_OAK_DOOR,
            Material.IRON_DOOR, Material.JUNGLE_DOOR, Material.SPRUCE_DOOR,
            Material.WOOD_DOOR);
    private static Map<String, ProtectionData> protectionMap = new HashMap<>();

    // Caches the data from the file
    public static void loadProtection(Core core) {
        core.getStorage().getKeySet().forEach(loc -> {
            ProtectionData data = new ProtectionData();
            StorageTagCompound compound = core.getStorage().getCompound().getCompoundTag(loc);
            System.out.println(loc+"Owner UUID: " + compound.getString("uuid"));
            data.setOwnerName(compound.getString("name"));
            data.setOwnerUUID(compound.getString("uuid"));

            if (compound.hasKey("trusted")) {
                JSONArray array = StorageMaker.getJSONArray(compound, "trusted");
                Map<String, String> trusted = new HashMap<>();
                array.forEach(o -> {
                    JSONObject json = (JSONObject) o;
                    trusted.put(String.valueOf(json.get("name")), String.valueOf(json.get("uuid")));
                });

                if (!trusted.isEmpty()) data.setTrusted(trusted);
            }

            if (compound.hasKey("added")) {
                JSONArray array = StorageMaker.getJSONArray(compound, "added");
                Map<String, String> added = new HashMap<>();
                array.forEach(o -> {
                    JSONObject json = (JSONObject) o;
                    added.put(String.valueOf(json.get("name")), String.valueOf(json.get("uuid")));
                });

                if (!added.isEmpty()) data.setAdded(added);
            }

            if (compound.hasKey("temporary")) {
                JSONArray array = StorageMaker.getJSONArray(compound, "temporary");
                Map<String, ProtectionData.TimeInfo> temporary = new HashMap<>();
                array.forEach(o -> {
                    JSONObject json = (JSONObject) o;
                    String uuid = String.valueOf(json.get("name"));
                    ProtectionData.TimeInfo info = new ProtectionData.TimeInfo();
                    info.setStart(Long.parseLong(String.valueOf(json.get("start"))));
                    info.setSeconds(Integer.parseInt(String.valueOf(json.get("seconds"))));
                    if (info.hasTimeRemaining()) {
                        temporary.put(uuid, info);
                    }
                });

                if (!temporary.isEmpty()) data.setTempAdded(temporary);
            }

            protectionMap.put(loc, data);
        });
    }
    public static void saveProtections (Core core) {
        StorageTagCompound compound = new StorageTagCompound();
        protectionMap.forEach((loc, data) ->  {
            StorageTagCompound inner = new StorageTagCompound();

            inner.setString("name", data.getOwnerName());
            inner.setString("uuid", data.getOwnerUUID());


            if (data.getTrusted().isEmpty()) {
                inner.setString("trusted", Base64Wrapper.encodeString("[]"));
            }else{
                JSONArray array = new JSONArray();
                data.getTrusted().forEach((name, uuid) -> {
                    JSONObject json = new JSONObject();
                    json.put("name", name);
                    json.put("uuid", uuid);
                    array.add(json);
                });
                inner.setString("trusted", Base64Wrapper.encodeString(array.toJSONString()));
            }


            if (data.getAdded().isEmpty()) {
                inner.setString("added", Base64Wrapper.encodeString("[]"));
            }else{
                JSONArray array = new JSONArray();
                data.getAdded().forEach((name, uuid) -> {
                    JSONObject json = new JSONObject();
                    json.put("name", name);
                    json.put("uuid", uuid);
                    array.add(json);
                });
                inner.setString("added", Base64Wrapper.encodeString(array.toJSONString()));
            }


            if (data.getTempAdded().isEmpty()) {
                inner.setString("temporary", Base64Wrapper.encodeString("[]"));
            }else{
                JSONArray array = new JSONArray();
                data.getTempAdded().forEach((name, info) -> {
                    JSONObject json = new JSONObject();
                    json.put("name", name);
                    json.put("start", info.getStart());
                    json.put("seconds", info.getSeconds());
                    array.add(json);
                });
                inner.setString("temporary", Base64Wrapper.encodeString(array.toJSONString()));
            }
            compound.setTag(loc, inner);
        });

        core.getStorage().setCompound(compound);
        core.getStorage().save();
    }

    // Checks if a sign is a 'protection' sign
    public static boolean isProtectionSign(Block block) {
        if (block == null) return false;
        if (block.getType() == Material.AIR) return false;

        if (block.getType() == Material.WALL_SIGN) {
            Sign sign = (Sign) block.getState();
            // This will change once the storage of the signs is finished
            if (sign.getLines()[0].equals("[Private]"))
                return protectionMap.containsKey(Utilities.blockLocToString(block.getLocation()));
        }

        return false;
    }

    // Registers a protection sign
    public static void registerProtection (Player player, Block sign) {
        ProtectionData data = new ProtectionData();
        data.setOwnerName(player.getName());
        data.setOwnerUUID(player.getUniqueId().toString());

        protectionMap.put(Utilities.blockLocToString(sign.getLocation()), data);
        Utilities.sendActionMessage(player, "&a&lBlock has been protected");

    }

    public static ProtectionData getProtectionInfo (Sign sign) {
        String loc = Utilities.blockLocToString(sign.getLocation());
        if (protectionMap.containsKey(loc)) return protectionMap.get(loc);
        return null;
    }

    // Removes a protection sign
    public static void removeProtection (Sign sign) {
        protectionMap.remove(Utilities.blockLocToString(sign.getLocation()));
    }

    // Checks if the specific block has a protection sign
    public static Sign hasAttachedSign (Block block) {
        // Checks if the block is a protection sign
        if (ProtectionUtils.isProtectionSign(block)) return (Sign) block.getState();

        // Checks if the block has a protection sign on its BlockFaces
        for (BlockFace face : Utilities.FACES) {
            Block b = block.getRelative(face);
            if (ProtectionUtils.isProtectionSign(b)) {
                org.bukkit.material.Sign sign = (org.bukkit.material.Sign) b.getState().getData();
                if (sign.getAttachedFace() == face.getOppositeFace())
                    return (Sign) b.getState();
            }
        }


        List<Location> locations = new ArrayList<>();

        // Checks if the block is part of a door, If it is it will add the opposite location
        if (block.getType().name().contains("DOOR") && (!block.getType().name().contains("TRAP"))) {
            Door door = (Door) block.getState().getData();
            if (door.isTopHalf()) {
                locations.add(block.getRelative(BlockFace.DOWN).getLocation());
            } else {
                locations.add(block.getRelative(BlockFace.UP).getLocation());
            }
        }

        // Checks if the block has a door on top, If so it will add the locations
        Block bottom = block.getRelative(BlockFace.UP);
        if (bottom.getType().name().contains("DOOR") && (!bottom.getType().name().contains("TRAP"))) {
            locations.add(bottom.getLocation());
            locations.add(bottom.getRelative(BlockFace.UP).getLocation());
        }

        // Checks if the locations have a protection sign on them
        for (Location location : locations) {
            for (BlockFace face : Utilities.FACES) {
                Block b = location.getBlock().getRelative(face);
                if (ProtectionUtils.isProtectionSign(b)) {
                    org.bukkit.material.Sign sign = (org.bukkit.material.Sign) b.getState().getData();
                    if (sign.getAttachedFace() == face.getOppositeFace())
                        return (Sign) b.getState();
                }
            }
        }

        return null;
    }

    // Searched the block(s) to check if it is protected
    public static Sign findProtectionSign (Block block) {
        // Checks if the block is a protection sign
        if (ProtectionUtils.isProtectionSign(block)) return (Sign) block.getState();

        // Checks if the block has a protection sign on its BlockFaces
        for (BlockFace face : Utilities.FACES) {
            Block b = block.getRelative(face);
            if (ProtectionUtils.isProtectionSign(b)) {
                org.bukkit.material.Sign sign = (org.bukkit.material.Sign) b.getState().getData();
                if (sign.getAttachedFace() == face.getOppositeFace())
                    return (Sign) b.getState();
            }
        }

        List<Location> locations = new ArrayList<>();

        // Checks if the block is part of a double chest, If so it will add the locations
        if (Utilities.isDoubleChest(block)) {
            DoubleChestInfo info = DoubleChestUtil.getChest(block);
            locations.add(info.getLocation(Side.LEFT));
            locations.add(info.getLocation(Side.RIGHT));
        }

        // Checks if the block is part of a door, If it is it will add the opposite location
        if (block.getType().name().contains("DOOR") && (!block.getType().name().contains("TRAP"))) {
            Door door = (Door) block.getState().getData();
            if (door.isTopHalf()) {
                locations.add(block.getRelative(BlockFace.DOWN).getLocation());
            } else {
                locations.add(block.getRelative(BlockFace.UP).getLocation());
            }
        }

        // Checks if the block has a door on top, If so it will add the locations
        Block bottom = block.getRelative(BlockFace.UP);
        if (bottom.getType().name().contains("DOOR") && (!bottom.getType().name().contains("TRAP"))) {
            locations.add(bottom.getLocation());
            locations.add(bottom.getRelative(BlockFace.UP).getLocation());
        }

        // Checks if the locations have a protection sign on them
        for (Location location : locations) {
            for (BlockFace face : Utilities.FACES) {
                Block b = location.getBlock().getRelative(face);
                if (ProtectionUtils.isProtectionSign(b)) {
                    org.bukkit.material.Sign sign = (org.bukkit.material.Sign) b.getState().getData();
                    if (sign.getAttachedFace() == face.getOppositeFace())
                        return (Sign) b.getState();
                }
            }
        }

        return null;
    }
}
