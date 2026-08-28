package de.eferu.helix.recovery

enum class RecoveryReason {
    PLAYER_STUCK,
    PATH_INVALID,
    TARGET_MISSING,
    COLLISION,
    UNEXPECTED_POSITION,
    HARVEST_FAILED,
    REPEATED_PATH_FAILURE,
}
