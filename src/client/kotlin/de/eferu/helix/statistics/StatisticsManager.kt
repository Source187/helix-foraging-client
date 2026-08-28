package de.eferu.helix.statistics

import com.google.gson.GsonBuilder
import de.eferu.helix.config.ConfigManager
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

object StatisticsManager {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val historyFile = FabricLoader.getInstance().configDir.resolve("helixforaging/statistics_history.json")

    var current: SessionStatistics = SessionStatistics()
        private set
    var history: MutableList<SessionStatistics> = mutableListOf()
        private set

    fun initialize() {
        if (historyFile.exists()) {
            runCatching {
                history = gson.fromJson(historyFile.readText(), Array<SessionStatistics>::class.java).toMutableList()
            }
        }
    }

    fun startSession(routeName: String, pointCount: Int) {
        current = SessionStatistics(routeName = routeName, routePointCount = pointCount)
    }

    fun recordHarvest(success: Boolean, distance: Double) {
        if (success) {
            current.blocksHarvested++
            current.successfulHarvests++
            current.totalTargetDistance += distance
            current.xpGained += 48.0
        } else {
            current.failedHarvests++
        }
    }

    fun recordRecovery() {
        current.recoveries++
    }

    fun updateRoute(point: Int, total: Int) {
        current.routePoint = point
        current.routePointCount = total
    }

    fun endSession() {
        if (!ConfigManager.statistics.persistSessions) return
        history.add(0, current)
        if (history.size > ConfigManager.statistics.maxHistoryEntries) {
            history = history.take(ConfigManager.statistics.maxHistoryEntries).toMutableList()
        }
        Files.createDirectories(historyFile.parent)
        historyFile.writeText(gson.toJson(history))
    }
}
