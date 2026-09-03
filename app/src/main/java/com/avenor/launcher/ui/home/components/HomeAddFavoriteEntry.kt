package com.avenor.launcher.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.avenor.launcher.FavoriteListSize
import com.avenor.launcher.FavoriteNamePlacement
import com.avenor.launcher.OrderedFavoriteModule
import com.avenor.launcher.OrderedFavoriteModuleType
import com.avenor.launcher.R
import com.avenor.launcher.belowItemHeightResource
import com.avenor.launcher.iconSizeResource
import com.avenor.launcher.lineHeightResource
import com.avenor.launcher.rowHeightResource
import com.avenor.launcher.textSizeResource

@Composable
internal fun HomeMainListAddFavoriteEntry(
    modifier: Modifier,
    label: String,
    testTag: String,
    enabled: Boolean,
    onClick: () -> Unit,
    contentEnabled: Boolean = enabled,
) {
    Row(
        modifier = modifier
            .heightIn(min = dimensionResource(id = R.dimen.home_add_favorite_entry_min_height))
            .alpha(alpha = addEntryAlpha(enabled = contentEnabled))
            .addEntrySurface(backgroundResource = R.color.home_edit_surface)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = dimensionResource(id = R.dimen.home_favorite_bar_item_inset))
            .testTag(tag = testTag),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HomeAddFavoriteIconSlot(size = dimensionResource(id = R.dimen.home_add_favorite_icon_size))
        Spacer(modifier = Modifier.width(width = dimensionResource(id = R.dimen.home_add_favorite_entry_gap)))
        HomeAddFavoriteLabel(label = label, size = FavoriteListSize.Medium)
    }
}

@Composable
internal fun HomeModuleAddFavoriteEntry(
    modifier: Modifier,
    module: OrderedFavoriteModule,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val ribbon = module.type == OrderedFavoriteModuleType.Ribbon
    val below = !ribbon && module.namePlacement == FavoriteNamePlacement.Below
    val size = if (ribbon) FavoriteListSize.Medium else module.applicationSize
    val iconSlotSize = dimensionResource(id = size.iconSizeResource())
    val heightResource = when {
        ribbon -> R.dimen.home_favorite_bar_height
        below -> size.belowItemHeightResource()
        else -> size.rowHeightResource()
    }
    val surfaceModifier = modifier
        .height(height = dimensionResource(id = heightResource))
        .alpha(alpha = addEntryAlpha(enabled = enabled))
        .then(
            other = if (ribbon) {
                Modifier.addEntrySurface(backgroundResource = R.color.home_add_favorite_surface)
            } else {
                // Keep the parent's edit background visible; only the press Ripple is bounded here.
                Modifier.clipToBounds()
            },
        )
        .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
        .testTag(tag = "home_add_favorite_${module.id}")
    val label = stringResource(id = R.string.home_add_favorite)
    if (below) {
        Column(
            modifier = surfaceModifier.padding(
                start = dimensionResource(id = R.dimen.home_favorite_below_horizontal_inset),
                top = dimensionResource(id = R.dimen.home_favorite_below_top_inset),
                end = dimensionResource(id = R.dimen.home_favorite_below_horizontal_inset),
                bottom = dimensionResource(id = R.dimen.home_favorite_below_bottom_inset),
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HomeAddFavoriteIconSlot(size = iconSlotSize)
            Spacer(modifier = Modifier.height(height = dimensionResource(id = R.dimen.home_favorite_below_icon_label_gap)))
            HomeAddFavoriteLabel(
                label = label, size = size, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
            )
        }
    } else {
        Row(
            modifier = surfaceModifier.padding(horizontal = dimensionResource(id = R.dimen.home_favorite_bar_item_inset)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HomeAddFavoriteIconSlot(size = iconSlotSize)
            Spacer(
                modifier = Modifier.width(
                    width = dimensionResource(
                        id = if (ribbon) R.dimen.home_favorite_bar_icon_label_gap else R.dimen.home_favorite_icon_label_gap,
                    ),
                ),
            )
            HomeAddFavoriteLabel(label = label, size = size, modifier = Modifier.weight(weight = 1f))
        }
    }
}

@Composable
private fun Modifier.addEntrySurface(backgroundResource: Int): Modifier {
    val shape = RoundedCornerShape(size = dimensionResource(id = R.dimen.home_favorite_bar_corner_radius))
    return clip(shape = shape)
        .background(color = colorResource(id = backgroundResource))
        .border(
            width = dimensionResource(id = R.dimen.home_favorite_bar_border_width),
            color = colorResource(id = R.color.home_add_favorite_border),
            shape = shape,
        )
}

@Composable
private fun addEntryAlpha(enabled: Boolean): Float =
    if (enabled) 1f else integerResource(id = R.integer.disabled_content_alpha_percent) / 100f

@Composable
private fun HomeAddFavoriteIconSlot(size: Dp) {
    Box(modifier = Modifier.size(size = size), contentAlignment = Alignment.Center) {
        Icon(
            painter = painterResource(id = R.drawable.ic_add),
            contentDescription = null,
            modifier = Modifier.size(size = dimensionResource(id = R.dimen.home_add_favorite_icon_size)),
            tint = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun HomeAddFavoriteLabel(
    label: String,
    size: FavoriteListSize,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
) {
    Text(
        text = label,
        modifier = modifier,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = textAlign,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Normal,
        fontSize = dimensionResource(id = size.textSizeResource()).value.sp,
        lineHeight = dimensionResource(id = size.lineHeightResource()).value.sp,
    )
}
