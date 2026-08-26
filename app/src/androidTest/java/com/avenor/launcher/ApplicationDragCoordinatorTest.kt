package com.avenor.launcher

import android.content.ComponentName
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ApplicationDragCoordinatorTest {
    @Test
    fun targetBodyAndBoundariesRemainMutuallyExclusive() {
        val sourceIdentity = identity("source")
        val targetIdentities = listOf(identity("first"), identity("second"), identity("third"))
        val targetKey = "vertical-list:target"
        val descriptors = mapOf(
            targetKey to ApplicationDragContainerDescriptor(
                key = targetKey,
                type = FavoriteContainerType.VerticalList,
                axis = ApplicationDragAxis.Vertical,
                bounds = Rect(0f, 0f, 100f, 300f),
                identities = targetIdentities,
            ),
        )
        val itemBounds = targetIdentities.mapIndexed { index, identity ->
            "$targetKey:${identity.testStableKey()}" to
                Rect(0f, index * 100f, 100f, (index + 1) * 100f)
        }.toMap()
        val session = session(sourceIdentity, Offset(50f, 150f))

        val exchange = session.advanced(Offset.Zero, descriptors, itemBounds)
        assertEquals(ApplicationDragTargetMode.Exchange, exchange.targetMode)
        assertEquals(targetIdentities[1], exchange.targetIdentity)
        assertEquals(1, exchange.targetIndex)

        val beforeFirst = session.copy(touchStartInWindow = Offset(50f, 5f))
            .advanced(Offset.Zero, descriptors, itemBounds)
        assertEquals(ApplicationDragTargetMode.Insertion, beforeFirst.targetMode)
        assertNull(beforeFirst.targetIdentity)
        assertEquals(0, beforeFirst.targetIndex)

        val betweenItems = session.copy(touchStartInWindow = Offset(50f, 105f))
            .advanced(Offset.Zero, descriptors, itemBounds)
        assertEquals(ApplicationDragTargetMode.Insertion, betweenItems.targetMode)
        assertNull(betweenItems.targetIdentity)
        assertEquals(1, betweenItems.targetIndex)

        val afterLast = session.copy(touchStartInWindow = Offset(50f, 295f))
            .advanced(Offset.Zero, descriptors, itemBounds)
        assertEquals(ApplicationDragTargetMode.Insertion, afterLast.targetMode)
        assertNull(afterLast.targetIdentity)
        assertEquals(3, afterLast.targetIndex)
    }

    @Test
    fun provisionalEmptyContainerAcceptsOnlyItsFirstInsertionBoundary() {
        val sourceIdentity = identity("source")
        val provisionalKey = "vertical-list:provisional:0"
        val descriptors = mapOf(
            provisionalKey to ApplicationDragContainerDescriptor(
                key = provisionalKey,
                type = FavoriteContainerType.VerticalList,
                axis = ApplicationDragAxis.Vertical,
                bounds = Rect(0f, 0f, 100f, 200f),
            ),
        )

        val target = session(sourceIdentity, Offset(50f, 100f))
            .advanced(Offset.Zero, descriptors, emptyMap())

        assertEquals(provisionalKey, target.targetContainerKey)
        assertEquals(ApplicationDragTargetMode.Insertion, target.targetMode)
        assertNull(target.targetIdentity)
        assertEquals(0, target.targetIndex)
    }

    @Test
    fun sourceContainerAndInvalidSpaceProduceNoCrossContainerTarget() {
        val sourceIdentity = identity("source")
        val sourceKey = "vertical-list:source"
        val descriptors = mapOf(
            sourceKey to ApplicationDragContainerDescriptor(
                key = sourceKey,
                type = FavoriteContainerType.VerticalList,
                axis = ApplicationDragAxis.Vertical,
                bounds = Rect(0f, 0f, 100f, 200f),
                identities = listOf(sourceIdentity),
            ),
        )

        val inSource = session(sourceIdentity, Offset(50f, 50f))
            .advanced(Offset.Zero, descriptors, emptyMap())
        assertNull(inSource.targetContainerKey)
        assertNull(inSource.targetMode)

        val outside = session(sourceIdentity, Offset(150f, 250f))
            .advanced(Offset.Zero, descriptors, emptyMap())
        assertNull(outside.targetContainerKey)
        assertNull(outside.targetMode)
    }

    @Test
    fun edgeScrollCandidateUsesOnlyTheActiveContainerAxisAndBand() {
        val sourceIdentity = identity("source")
        val sourceKey = "vertical-list:source"
        val targetKey = "favorite-bar:target"
        val descriptors = mapOf(
            sourceKey to ApplicationDragContainerDescriptor(
                key = sourceKey,
                type = FavoriteContainerType.VerticalList,
                axis = ApplicationDragAxis.Vertical,
                bounds = Rect(0f, 0f, 100f, 300f),
            ),
            targetKey to ApplicationDragContainerDescriptor(
                key = targetKey,
                type = FavoriteContainerType.FavoriteBar,
                axis = ApplicationDragAxis.Horizontal,
                bounds = Rect(200f, 0f, 500f, 100f),
            ),
        )

        val sourceLeading = session(sourceIdentity, Offset(50f, 5f))
            .edgeScrollCandidate(descriptors, bandPx = 48f)
        assertEquals(sourceKey, sourceLeading?.containerKey)
        assertEquals(ApplicationDragAxis.Vertical, sourceLeading?.axis)
        assertEquals(false, sourceLeading?.forward)

        val sourceCenter = session(sourceIdentity, Offset(50f, 150f))
            .edgeScrollCandidate(descriptors, bandPx = 48f)
        assertNull(sourceCenter)

        val targetTrailing = session(sourceIdentity, Offset(495f, 50f)).copy(
            targetContainerKey = targetKey,
            targetContainerType = FavoriteContainerType.FavoriteBar,
            targetAxis = ApplicationDragAxis.Horizontal,
        ).edgeScrollCandidate(descriptors, bandPx = 48f)
        assertEquals(targetKey, targetTrailing?.containerKey)
        assertEquals(ApplicationDragAxis.Horizontal, targetTrailing?.axis)
        assertEquals(true, targetTrailing?.forward)

        val outsideTarget = session(sourceIdentity, Offset(150f, 50f)).copy(
            targetContainerKey = targetKey,
            targetContainerType = FavoriteContainerType.FavoriteBar,
            targetAxis = ApplicationDragAxis.Horizontal,
        ).edgeScrollCandidate(descriptors, bandPx = 48f)
        assertNull(outsideTarget)
    }

    private fun session(
        sourceIdentity: LaunchableIdentity,
        touch: Offset,
    ) = ApplicationDragTargetSession(
        sourceContainerKey = "vertical-list:source",
        sourceIdentity = sourceIdentity,
        sourceContainerType = FavoriteContainerType.VerticalList,
        sourceAxis = ApplicationDragAxis.Vertical,
        touchStartInWindow = touch,
    )

    private fun identity(name: String) = LaunchableIdentity(
        profileSerialNumber = 1,
        componentName = ComponentName("com.example.$name", "MainActivity"),
    )

    private fun LaunchableIdentity.testStableKey(): String =
        "$profileSerialNumber:${componentName.flattenToString()}"
}
