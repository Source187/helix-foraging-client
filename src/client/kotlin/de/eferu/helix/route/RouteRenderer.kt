package de.eferu.helix.route

import com.mojang.blaze3d.vertex.PoseStack
import de.eferu.helix.config.ConfigManager
import de.eferu.helix.modules.RouteVisualsModule
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.rendertype.RenderTypes
import org.joml.Matrix4f

object RouteRenderer {
    fun render(context: LevelRenderContext) {
        if (!RouteVisualsModule.routeVisible || !ConfigManager.helix.routeVisible) return
        val route = HelixRouteManager.route ?: return
        val camera = Minecraft.getInstance().gameRenderer.mainCamera.position()
        val poseStack: PoseStack = context.poseStack()
        val matrix: Matrix4f = poseStack.last().pose()
        val buffer = context.bufferSource().getBuffer(RenderTypes.lines())
        val color = ConfigManager.helix.routeColor

        for (i in 0 until route.points.lastIndex) {
            val a = route.points[i]
            val b = route.points[i + 1]
            drawLine(buffer, matrix, camera, a.x, a.y, a.z, b.x, b.y, b.z, color)
        }

        route.currentPoint()?.let { point ->
            drawPoint(buffer, matrix, camera, point.x, point.y, point.z, ConfigManager.helix.currentPointColor)
        }
        route.nextPoint()?.let { point ->
            drawPoint(buffer, matrix, camera, point.x, point.y, point.z, ConfigManager.helix.nextPointColor)
        }
    }

    private fun drawLine(
        buffer: com.mojang.blaze3d.vertex.VertexConsumer,
        matrix: Matrix4f,
        camera: net.minecraft.world.phys.Vec3,
        x1: Double, y1: Double, z1: Double,
        x2: Double, y2: Double, z2: Double,
        color: Int,
    ) {
        val r = ((color shr 16) and 0xFF) / 255f
        val g = ((color shr 8) and 0xFF) / 255f
        val b = (color and 0xFF) / 255f
        val a = ((color ushr 24) and 0xFF) / 255f
        buffer.addVertex(matrix, (x1 - camera.x).toFloat(), (y1 - camera.y).toFloat(), (z1 - camera.z).toFloat())
            .setColor(r, g, b, a)
            .setNormal(0f, 1f, 0f)
        buffer.addVertex(matrix, (x2 - camera.x).toFloat(), (y2 - camera.y).toFloat(), (z2 - camera.z).toFloat())
            .setColor(r, g, b, a)
            .setNormal(0f, 1f, 0f)
    }

    private fun drawPoint(
        buffer: com.mojang.blaze3d.vertex.VertexConsumer,
        matrix: Matrix4f,
        camera: net.minecraft.world.phys.Vec3,
        x: Double, y: Double, z: Double,
        color: Int,
    ) {
        drawLine(buffer, matrix, camera, x - 0.2, y, z, x + 0.2, y, z, color)
        drawLine(buffer, matrix, camera, x, y - 0.2, z, x, y + 0.2, z, color)
        drawLine(buffer, matrix, camera, x, y, z - 0.2, x, y, z + 0.2, color)
    }
}
