# Helix Foraging Client

Fabric client mod for Minecraft **26.1.2** — modular foraging automation framework with Helix route engine, HUD, and configuration GUI.

**Intended use:** private development and testing environments only. Automation is gated by `EnvironmentGuard`.

## Requirements

- Java 25
- Minecraft 26.1.2
- Fabric Loader 0.19.3+
- Fabric API 0.155.2+26.1.2
- Fabric Language Kotlin 1.13.13+kotlin.2.4.10

## Build

```bash
./gradlew build
```

## Run client

```bash
./gradlew runClient
```

## Architecture

See the `src/client/kotlin/de/eferu/helix/` package tree for modular subsystems (core, macro, world, route, pathfinding, hud, gui, config, etc.).
