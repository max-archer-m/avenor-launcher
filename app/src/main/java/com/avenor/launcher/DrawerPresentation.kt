package com.avenor.launcher

import android.icu.text.Transliterator
import java.text.Collator
import java.util.Locale

internal data class DrawerSection(
    val label: String,
    val entries: List<LaunchableEntry>,
)

internal data class DrawerListPosition(
    val sectionLabel: String,
    val relativeItemIndex: Int,
    val scrollOffset: Int,
)

internal data class DrawerRestorationTarget(
    val itemIndex: Int,
    val scrollOffset: Int,
)

internal class LaunchableLabelNormalizer {
    private val transliterator = Transliterator.getInstance("Han-Latin; Latin-ASCII; Lower")

    fun normalize(label: String): String = transliterator.transliterate(label)

    fun sectionFor(label: String): String {
        val firstCharacter = normalize(label).firstOrNull()?.uppercaseChar()
        return if (firstCharacter != null && firstCharacter in 'A'..'Z') {
            firstCharacter.toString()
        } else {
            NUMBER_SECTION_LABEL
        }
    }

    private companion object {
        const val NUMBER_SECTION_LABEL = "#"
    }
}

internal fun buildDrawerSections(
    entries: List<LaunchableEntry>,
    locale: Locale,
): List<DrawerSection> {
    val normalizer = LaunchableLabelNormalizer()
    val sortedEntries = entries.sortedWith(LaunchableEntryComparator(locale, normalizer))
    val entriesBySection = sortedEntries.groupBy { entry ->
        normalizer.sectionFor(entry.label)
    }

    return buildList {
        add(DrawerSection(label = "#", entries = entriesBySection["#"].orEmpty()))
        for (sectionCharacter in 'A'..'Z') {
            val label = sectionCharacter.toString()
            val sectionEntries = entriesBySection[label].orEmpty()
            if (sectionEntries.isNotEmpty()) {
                add(DrawerSection(label = label, entries = sectionEntries))
            }
        }
    }
}

internal fun captureDrawerListPosition(
    sections: List<DrawerSection>,
    firstVisibleItemIndex: Int,
    firstVisibleItemScrollOffset: Int,
): DrawerListPosition? {
    var sectionHeaderIndex = 0
    sections.forEach { section ->
        val nextSectionHeaderIndex = sectionHeaderIndex + 1 + section.entries.size
        if (firstVisibleItemIndex < nextSectionHeaderIndex) {
            return DrawerListPosition(
                sectionLabel = section.label,
                relativeItemIndex = (firstVisibleItemIndex - sectionHeaderIndex).coerceAtLeast(0),
                scrollOffset = firstVisibleItemScrollOffset,
            )
        }
        sectionHeaderIndex = nextSectionHeaderIndex
    }
    return null
}

internal fun resolveDrawerRestorationTarget(
    position: DrawerListPosition,
    sections: List<DrawerSection>,
): DrawerRestorationTarget? {
    var sectionHeaderIndex = 0
    sections.forEach { section ->
        if (section.label == position.sectionLabel) {
            val restoredRelativeIndex =
                position.relativeItemIndex.coerceAtMost(section.entries.size)
            return DrawerRestorationTarget(
                itemIndex = sectionHeaderIndex + restoredRelativeIndex,
                scrollOffset = if (restoredRelativeIndex == position.relativeItemIndex) {
                    position.scrollOffset
                } else {
                    0
                },
            )
        }
        sectionHeaderIndex += 1 + section.entries.size
    }

    sectionHeaderIndex = 0
    sections.forEach { section ->
        if (drawerSectionRank(section.label) > drawerSectionRank(position.sectionLabel)) {
            return DrawerRestorationTarget(
                itemIndex = sectionHeaderIndex,
                scrollOffset = 0,
            )
        }
        sectionHeaderIndex += 1 + section.entries.size
    }

    val itemCount = sections.sumOf { section -> 1 + section.entries.size }
    return if (itemCount > 0) {
        DrawerRestorationTarget(itemIndex = itemCount - 1, scrollOffset = 0)
    } else {
        null
    }
}

private fun drawerSectionRank(label: String): Int =
    if (label == "#") 0 else (label.firstOrNull()?.code ?: Int.MAX_VALUE)

internal class LaunchableEntryComparator(
    locale: Locale,
    private val normalizer: LaunchableLabelNormalizer = LaunchableLabelNormalizer(),
) : Comparator<LaunchableEntry> {
    private val collator = Collator.getInstance(locale).apply {
        strength = Collator.PRIMARY
    }

    override fun compare(left: LaunchableEntry, right: LaunchableEntry): Int {
        val labelOrder = collator.compare(
            normalizer.normalize(left.label),
            normalizer.normalize(right.label),
        )
        if (labelOrder != 0) return labelOrder

        return compareValuesBy(
            left,
            right,
            { it.identity.profileSerialNumber },
            { it.identity.componentName.flattenToString() },
        )
    }
}
