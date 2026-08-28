package de.eferu.helix.macro

enum class MacroState {
    IDLE,
    SCANNING,
    SELECTING_TARGET,
    CALCULATING_ROUTE,
    MOVING,
    ROTATING,
    HARVESTING,
    VERIFYING,
    ADVANCING_ROUTE,
    RECOVERY,
    PAUSED,
    STOPPED,
}
