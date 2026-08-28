package de.eferu.helix.notification

import java.util.ArrayDeque

data class HelixNotification(
    val title: String,
    val message: String,
    val createdAt: Long = System.currentTimeMillis(),
    var alpha: Float = 1f,
)

object NotificationManager {
    private val queue = ArrayDeque<HelixNotification>()
    private const val MAX = 6
    private const val DURATION_MS = 3500L

    fun initialize() = Unit

    fun show(title: String, message: String = "") {
        queue.addFirst(HelixNotification(title, message))
        while (queue.size > MAX) queue.removeLast()
    }

    fun active(): List<HelixNotification> {
        val now = System.currentTimeMillis()
        queue.removeIf { now - it.createdAt > DURATION_MS }
        queue.forEach { notification ->
            val age = now - notification.createdAt
            notification.alpha = 1f - (age / DURATION_MS.toFloat()).coerceIn(0f, 1f)
        }
        return queue.toList()
    }
}
