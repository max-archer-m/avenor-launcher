package com.avenor.launcher.ui.home.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.avenor.launcher.R

@Composable
internal fun HomeFavoriteAddControl(
    onClick: () -> Unit,
    testTag: String,
    @StringRes labelRes: Int = R.string.add_apps,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = dimensionResource(id = R.dimen.home_favorite_list_control_bar_height))
            .clickable(
                role = Role.Button,
                onClick = onClick,
            )
            .testTag(tag = testTag),
        contentAlignment = Alignment.Center,
        content = {
            Text(
                text = stringResource(id = labelRes),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge,
            )
        },
    )
}
