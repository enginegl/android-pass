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

package proton.android.pass.autofill.ui.autofill

import android.os.Bundle
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import proton.android.pass.autofill.entities.AssistInfo
import proton.android.pass.autofill.entities.AutofillData
import proton.android.pass.autofill.extensions.toAutofillItem
import proton.android.pass.autofill.heuristics.NodeCluster
import proton.android.pass.common.api.None
import proton.android.pass.common.api.Option
import proton.android.pass.common.api.Some
import proton.android.pass.common.api.toOption
import proton.android.pass.domain.Item
import proton.android.pass.domain.entity.AppName
import proton.android.pass.domain.entity.PackageInfo
import proton.android.pass.domain.entity.PackageName

@Parcelize
data class AutofillExtras(
    val cluster: NodeCluster,
    val url: String?,
    val packageName: String?,
    val appName: String?,
) : Parcelable

fun AutofillData.toExtras() = AutofillExtras(
    cluster = assistInfo.cluster,
    url = assistInfo.url.map { it }.value(),
    packageName = packageInfo.map { it.packageName.value }.value(),
    appName = packageInfo.map { it.appName.value }.value(),
)

fun AutofillExtras.toData() = AutofillData(
    assistInfo = AssistInfo(
        cluster = cluster,
        url = url.toOption(),
    ),
    packageInfo = packageName?.let { pName ->
        appName?.let { aName ->
            PackageInfo(
                packageName = PackageName(pName),
                appName = AppName(aName),
            )
        }
    }.toOption()
)

object AutofillIntentExtras {

    const val ARG_INLINE_SUGGESTION_AUTOFILL_ITEM = "arg_inline_suggestion_autofill_item"
    const val ARG_AUTOFILL_DATA = "arg_autofill_data"

    fun toExtras(data: AutofillData, itemOption: Option<Item> = None): Bundle {
        val extras = Bundle()
        val asExtras = data.toExtras()
        extras.putParcelable(ARG_AUTOFILL_DATA, asExtras)

        if (itemOption is Some) {
            val autofillItem = itemOption.value.toAutofillItem()
            if (autofillItem is Some) {
                extras.putParcelable(
                    ARG_INLINE_SUGGESTION_AUTOFILL_ITEM,
                    autofillItem.value
                )
            }
        }

        return extras
    }
}
