/*
 * Copyright (c) 2023 Proton AG
 * This file is part of Proton AG and Proton Pass.
 *
 * Proton Pass is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Proton Pass is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Proton Pass.  If not, see <https://www.gnu.org/licenses/>.
 */

package proton.android.pass.composecomponents.impl.item.icon

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.ThemePreviewProvider
import proton.android.pass.composecomponents.impl.container.BoxedIcon

@Composable
fun AliasIcon(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Int = 40,
    shape: Shape = PassTheme.shapes.squircleMediumShape,
    backgroundColor: Color = if (enabled) {
        PassTheme.colors.aliasInteractionNormMinor1
    } else {
        PassTheme.colors.aliasInteractionNormMinor2
    },
    foregroundColor: Color = if (enabled) {
        PassTheme.colors.aliasInteractionNormMajor2
    } else {
        PassTheme.colors.aliasInteractionNormMinor1
    }
) {
    BoxedIcon(
        modifier = modifier,
        backgroundColor = backgroundColor,
        size = size,
        shape = shape
    ) {
        Icon(
            modifier = Modifier.padding(4.dp),
            painter = painterResource(me.proton.core.presentation.R.drawable.ic_proton_alias),
            contentDescription = null,
            tint = foregroundColor
        )
    }
}

@Preview
@Composable
fun AliasIconPreview(@PreviewParameter(ThemePreviewProvider::class) isDark: Boolean) {
    PassTheme(isDark = isDark) {
        Surface {
            AliasIcon()
        }
    }
}
