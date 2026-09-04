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

internal fun filterDrawerSections(
    sections: List<DrawerSection>,
    query: String,
): List<DrawerSection> {
    if (query.isBlank()) return sections

    return sections.mapNotNull { section ->
        val matchingEntries = section.entries.filter { entry ->
            drawerSearchMatchRanges(label = entry.label, query = query).isNotEmpty()
        }
        if (matchingEntries.isEmpty()) {
            null
        } else {
            section.copy(entries = matchingEntries)
        }
    }
}

internal fun drawerSearchMatchRanges(label: String, query: String): List<IntRange> {
    val normalizedQuery = normalizeDrawerSearchText(text = query.trim()).text
    if (normalizedQuery.isEmpty()) return emptyList()

    val normalizedLabel = normalizeDrawerSearchText(text = label)
    val ranges = mutableListOf<IntRange>()
    var searchStart = 0
    while (searchStart <= normalizedLabel.text.length - normalizedQuery.length) {
        val matchStart = normalizedLabel.text.indexOf(
            string = normalizedQuery,
            startIndex = searchStart,
        )
        if (matchStart < 0) break
        val matchEnd = matchStart + normalizedQuery.length - 1
        ranges += normalizedLabel.originalStarts[matchStart] until
            normalizedLabel.originalEnds[matchEnd]
        searchStart = matchStart + normalizedQuery.length
    }
    return ranges
}

private data class NormalizedDrawerSearchText(
    val text: String,
    val originalStarts: List<Int>,
    val originalEnds: List<Int>,
)

private fun normalizeDrawerSearchText(text: String): NormalizedDrawerSearchText {
    val normalized = StringBuilder()
    val starts = mutableListOf<Int>()
    val ends = mutableListOf<Int>()
    var originalIndex = 0
    while (originalIndex < text.length) {
        val codePoint = text.codePointAt(originalIndex)
        val originalLength = Character.charCount(codePoint)
        val folded = drawerSearchLatinNormalizer.get().transliterate(
            String(Character.toChars(codePoint)),
        )
        folded.forEach { character ->
            val type = Character.getType(character)
            if (type != Character.NON_SPACING_MARK.toInt() &&
                type != Character.COMBINING_SPACING_MARK.toInt() &&
                type != Character.ENCLOSING_MARK.toInt()
            ) {
                normalized.append(character)
                starts += originalIndex
                ends += originalIndex + originalLength
            }
        }
        originalIndex += originalLength
    }
    return NormalizedDrawerSearchText(
        text = normalized.toString(),
        originalStarts = starts,
        originalEnds = ends,
    )
}

private val drawerSearchLatinNormalizer = ThreadLocal.withInitial {
    Transliterator.getInstance("Latin-ASCII; Lower")
}

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
    val collator = Collator.getInstance(locale).apply {
        strength = Collator.PRIMARY
    }
    val normalizedEntries = entries.map { entry ->
        NormalizedDrawerEntry(
            entry = entry,
            normalizedLabel = normalizer.normalize(entry.label),
        )
    }.sortedWith(Comparator { left, right ->
        val labelOrder = collator.compare(left.normalizedLabel, right.normalizedLabel)
        if (labelOrder != 0) {
            labelOrder
        } else {
            compareValuesBy(
                left.entry,
                right.entry,
                { it.identity.profileSerialNumber },
                { it.identity.componentName.flattenToString() },
            )
        }
    })
    val entriesBySection = normalizedEntries.groupBy { normalized ->
        normalizer.sectionForNormalized(normalized.normalizedLabel)
    }

    return buildList {
        entriesBySection["#"]
            ?.takeIf { it.isNotEmpty() }
            ?.let { sectionEntries ->
                add(
                    DrawerSection(
                        label = "#",
                        entries = sectionEntries.map(NormalizedDrawerEntry::entry),
                    ),
                )
            }
        for (sectionCharacter in 'A'..'Z') {
            val label = sectionCharacter.toString()
            val sectionEntries = entriesBySection[label].orEmpty()
            if (sectionEntries.isNotEmpty()) {
                add(
                    DrawerSection(
                        label = label,
                        entries = sectionEntries.map(NormalizedDrawerEntry::entry),
                    ),
                )
            }
        }
    }
}

private data class NormalizedDrawerEntry(
    val entry: LaunchableEntry,
    val normalizedLabel: String,
)

private fun LaunchableLabelNormalizer.sectionForNormalized(normalizedLabel: String): String {
    val firstCharacter = normalizedLabel.firstOrNull()?.uppercaseChar()
    return if (firstCharacter != null && firstCharacter in 'A'..'Z') {
        firstCharacter.toString()
    } else {
        "#"
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

internal fun captureDrawerOrdinaryListPosition(
    sections: List<DrawerSection>,
    firstVisibleItemIndex: Int,
    firstVisibleItemScrollOffset: Int,
): DrawerListPosition? {
    val applicationPosition = captureDrawerListPosition(
        sections = sections,
        firstVisibleItemIndex = firstVisibleItemIndex,
        firstVisibleItemScrollOffset = firstVisibleItemScrollOffset,
    )
    if (applicationPosition != null) return applicationPosition

    val settingsHeaderIndex = sections.sumOf(selector = { section ->
        1 + section.entries.size
    })
    val settingsRelativeIndex = firstVisibleItemIndex - settingsHeaderIndex
    return if (settingsRelativeIndex in 0..1) {
        DrawerListPosition(
            sectionLabel = SETTINGS_SECTION_POSITION_LABEL,
            relativeItemIndex = settingsRelativeIndex,
            scrollOffset = firstVisibleItemScrollOffset,
        )
    } else {
        null
    }
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

internal fun resolveDrawerOrdinaryRestorationTarget(
    position: DrawerListPosition,
    sections: List<DrawerSection>,
): DrawerRestorationTarget? {
    if (position.sectionLabel != SETTINGS_SECTION_POSITION_LABEL) {
        return resolveDrawerRestorationTarget(position = position, sections = sections)
    }
    val settingsHeaderIndex = sections.sumOf(selector = { section ->
        1 + section.entries.size
    })
    return DrawerRestorationTarget(
        itemIndex = settingsHeaderIndex + position.relativeItemIndex.coerceIn(
            minimumValue = 0,
            maximumValue = 1,
        ),
        scrollOffset = position.scrollOffset,
    )
}

private fun drawerSectionRank(label: String): Int =
    if (label == "#") 0 else (label.firstOrNull()?.code ?: Int.MAX_VALUE)

private const val SETTINGS_SECTION_POSITION_LABEL = "\u0000settings"

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
