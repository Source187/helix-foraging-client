package de.eferu.helix.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.eferu.helix.HelixClient
import de.eferu.helix.route.RouteProfile
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

object ConfigManager {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val configDir: Path = FabricLoader.getInstance().configDir.resolve("helixforaging")

    var client: ClientConfig = ClientConfig()
        private set
    var hud: HudConfig = HudConfig()
        private set
    var helix: HelixConfig = HelixConfig()
        private set
    var pathfinding: PathfindingConfig = PathfindingConfig()
        private set
    var rotation: RotationConfig = RotationConfig()
        private set
    var statistics: StatisticsConfig = StatisticsConfig()
        private set
    var scanner: ScannerConfig = ScannerConfig()
        private set
    var timing: TimingConfig = TimingConfig()
        private set
    var routeProfiles: MutableList<RouteProfile> = mutableListOf()
        private set

    fun initialize() {
        Files.createDirectories(configDir)
        loadAll()
        if (routeProfiles.isEmpty()) {
            routeProfiles.add(RouteProfile.torrhusStyle())
            saveRouteProfiles()
        }
    }

    fun loadAll() {
        client = load("client.json", ClientConfig::class.java, ClientConfig())
        hud = load("hud.json", HudConfig::class.java, HudConfig())
        helix = load("helix.json", HelixConfig::class.java, HelixConfig())
        pathfinding = load("pathfinding.json", PathfindingConfig::class.java, PathfindingConfig())
        rotation = load("rotation.json", RotationConfig::class.java, RotationConfig())
        statistics = load("statistics.json", StatisticsConfig::class.java, StatisticsConfig())
        scanner = load("scanner.json", ScannerConfig::class.java, ScannerConfig())
        timing = load("timing.json", TimingConfig::class.java, TimingConfig())
        routeProfiles = loadList("route_profiles.json", RouteProfile::class.java, mutableListOf(RouteProfile.torrhusStyle()))
    }

    fun saveAll() {
        save("client.json", client)
        save("hud.json", hud)
        save("helix.json", helix)
        save("pathfinding.json", pathfinding)
        save("rotation.json", rotation)
        save("statistics.json", statistics)
        save("scanner.json", scanner)
        save("timing.json", timing)
        saveRouteProfiles()
    }

    fun saveRouteProfiles() = save("route_profiles.json", routeProfiles)

    fun resetHudLayout() {
        hud = HudConfig()
        saveAll()
    }

    fun resetAll() {
        client = ClientConfig()
        hud = HudConfig()
        helix = HelixConfig()
        pathfinding = PathfindingConfig()
        rotation = RotationConfig()
        statistics = StatisticsConfig()
        scanner = ScannerConfig()
        timing = TimingConfig()
        routeProfiles = mutableListOf(RouteProfile.torrhusStyle())
        saveAll()
    }

    fun exportTo(path: Path) {
        Files.createDirectories(path)
        save(path.resolve("client.json"), client)
        save(path.resolve("hud.json"), hud)
        save(path.resolve("helix.json"), helix)
        save(path.resolve("pathfinding.json"), pathfinding)
        save(path.resolve("rotation.json"), rotation)
        save(path.resolve("statistics.json"), statistics)
        save(path.resolve("scanner.json"), scanner)
        save(path.resolve("timing.json"), timing)
        save(path.resolve("route_profiles.json"), routeProfiles)
    }

    fun importFrom(path: Path) {
        client = load(path.resolve("client.json"), ClientConfig::class.java, client)
        hud = load(path.resolve("hud.json"), HudConfig::class.java, hud)
        helix = load(path.resolve("helix.json"), HelixConfig::class.java, helix)
        pathfinding = load(path.resolve("pathfinding.json"), PathfindingConfig::class.java, pathfinding)
        rotation = load(path.resolve("rotation.json"), RotationConfig::class.java, rotation)
        statistics = load(path.resolve("statistics.json"), StatisticsConfig::class.java, statistics)
        scanner = load(path.resolve("scanner.json"), ScannerConfig::class.java, scanner)
        timing = load(path.resolve("timing.json"), TimingConfig::class.java, timing)
        routeProfiles = loadList(path.resolve("route_profiles.json"), RouteProfile::class.java, routeProfiles)
        saveAll()
    }

    private fun <T> load(file: String, type: Class<T>, default: T): T =
        load(configDir.resolve(file), type, default)

    private fun <T> load(path: Path, type: Class<T>, default: T): T {
        if (!path.exists()) {
            save(path, default as Any)
            return default
        }
        return runCatching { gson.fromJson(path.readText(), type) }
            .onFailure { HelixClient.logger.error("Failed to load config $path", it) }
            .getOrElse { default }
    }

    private fun <T> loadList(file: String, type: Class<T>, default: MutableList<T>): MutableList<T> =
        loadList(configDir.resolve(file), type, default)

    private fun <T> loadList(path: Path, type: Class<T>, default: MutableList<T>): MutableList<T> {
        if (!path.exists()) {
            save(path, default as Any)
            return default
        }
        return runCatching {
            val arrayType = com.google.gson.reflect.TypeToken.getParameterized(List::class.java, type).type
            gson.fromJson<List<T>>(path.readText(), arrayType).toMutableList()
        }.getOrElse {
            HelixClient.logger.error("Failed to load list config $path", it)
            default
        }
    }

    private fun save(file: String, value: Any) = save(configDir.resolve(file), value)

    private fun save(path: Path, value: Any) {
        path.writeText(gson.toJson(value))
    }
}
