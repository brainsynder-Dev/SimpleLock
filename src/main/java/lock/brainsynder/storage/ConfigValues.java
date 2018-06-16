package lock.brainsynder.storage;

public enum ConfigValues {
    ALLOWED_BLOCKS ("allowed-blocks"),
    PLAYER_NOT_FOUND ("message.player-not-found"),
    NOT_PROTECTED ("message.not-protected"),
    TRANSFER_START ("message.transfer.start"),
    TRANSFER_SUCCESSFUL ("message.transfer.successfully"),
    TRANSFER_FAILED ("message.transfer.failed"),

    PLAYER_ADDED_SUCCESSFUL ("message.player-added.successfully"),
    PLAYER_ADDED_FAILED ("message.player-added.failed"),
    PLAYER_ADDED_EXISTING ("message.player-added.already-existing"),

    PLAYER_TRUSTED_SUCCESSFUL ("message.player-trusted.successfully"),
    PLAYER_TRUSTED_FAILED ("message.player-trusted.failed"),
    PLAYER_TRUSTED_EXISTING ("message.player-trusted.already-existing"),

    PLAYER_TEMP_SUCCESSFUL ("message.player-temp-added.successfully"),
    PLAYER_TEMP_FAILED ("message.player-temp-added.failed"),
    PLAYER_TEMP_EXISTING ("message.player-temp-added.already-existing"),

    PLAYER_REMOVE_SUCCESSFUL ("message.player-remove.successfully"),
    PLAYER_REMOVE_FAILED ("message.player-remove.failed"),
    PLAYER_REMOVE_MISSING ("message.player-remove.missing");

    private String path;
    ConfigValues (String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
