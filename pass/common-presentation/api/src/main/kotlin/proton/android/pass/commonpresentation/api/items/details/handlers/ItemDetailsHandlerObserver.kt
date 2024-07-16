/*
 * Copyright (c) 2024 Proton AG
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

package proton.android.pass.commonpresentation.api.items.details.handlers

import kotlinx.coroutines.flow.Flow
import proton.android.pass.domain.ItemCustomFieldSection
import proton.android.pass.commonpresentation.api.items.details.domain.ItemDetailsFieldType
import proton.android.pass.commonuimodels.api.items.ItemDetailState
import proton.android.pass.crypto.api.context.EncryptionContext
import proton.android.pass.domain.CustomFieldContent
import proton.android.pass.domain.HiddenState
import proton.android.pass.domain.Item
import proton.android.pass.domain.ItemContents
import proton.android.pass.domain.ItemDiffType
import proton.android.pass.domain.ItemDiffs

abstract class ItemDetailsHandlerObserver<in ITEM_CONTENTS : ItemContents> {

    abstract fun observe(item: Item): Flow<ItemDetailState>

    abstract fun updateItemContents(
        itemContents: ITEM_CONTENTS,
        hiddenFieldType: ItemDetailsFieldType.Hidden,
        hiddenFieldSection: ItemCustomFieldSection,
        hiddenState: HiddenState
    ): ItemContents

    abstract fun calculateItemDiffs(baseItemDetailState: ITEM_CONTENTS, otherItemDetailState: ITEM_CONTENTS): ItemDiffs

    protected fun calculateItemDiffType(
        encryptionContext: EncryptionContext,
        baseItemFieldHiddenState: HiddenState,
        otherItemFieldHiddenState: HiddenState
    ): ItemDiffType = with(encryptionContext) {
        decrypt(baseItemFieldHiddenState.encrypted) to decrypt(otherItemFieldHiddenState.encrypted)
    }.let { (baseItemFieldValue, otherItemFieldValue) ->
        calculateItemDiffType(baseItemFieldValue, otherItemFieldValue)
    }

    protected fun calculateItemDiffType(baseItemFieldValue: String, otherItemFieldValue: String): ItemDiffType = when {
        baseItemFieldValue.isEmpty() && otherItemFieldValue.isEmpty() -> {
            ItemDiffType.None
        }

        baseItemFieldValue.isNotEmpty() && otherItemFieldValue.isNotEmpty() -> {
            if (baseItemFieldValue == otherItemFieldValue) {
                ItemDiffType.None
            } else {
                ItemDiffType.Content
            }
        }

        else -> {
            ItemDiffType.Field
        }
    }

    protected fun calculateItemDiffTypes(
        encryptionContext: EncryptionContext,
        baseItemCustomFieldsContent: List<CustomFieldContent>,
        otherItemCustomFieldsContent: List<CustomFieldContent>
    ): List<ItemDiffType> {
        val itemDiffTypes = mutableListOf<ItemDiffType>()
        val baseCustomFieldsMap = baseItemCustomFieldsContent.associateBy { it.label }
        val otherCustomFieldsMap = otherItemCustomFieldsContent.associateBy { it.label }

        val customFieldsLabels = if (baseItemCustomFieldsContent.size > otherItemCustomFieldsContent.size) {
            baseCustomFieldsMap.keys
        } else {
            otherCustomFieldsMap.keys
        }

        customFieldsLabels.forEach { customFieldsLabel ->
            val baseCustomField = baseCustomFieldsMap[customFieldsLabel]
            val otherCustomField = otherCustomFieldsMap[customFieldsLabel]

            when {
                baseCustomField == null && otherCustomField == null -> {
                    ItemDiffType.None
                }

                baseCustomField == null || otherCustomField == null -> {
                    ItemDiffType.Field
                }

                baseCustomField is CustomFieldContent.Text && otherCustomField is CustomFieldContent.Text -> {
                    calculateItemDiffType(
                        baseItemFieldValue = baseCustomField.value,
                        otherItemFieldValue = otherCustomField.value
                    )
                }

                baseCustomField is CustomFieldContent.Hidden && otherCustomField is CustomFieldContent.Hidden -> {
                    calculateItemDiffType(
                        encryptionContext = encryptionContext,
                        baseItemFieldHiddenState = baseCustomField.value,
                        otherItemFieldHiddenState = otherCustomField.value
                    )
                }

                baseCustomField is CustomFieldContent.Totp && otherCustomField is CustomFieldContent.Totp -> {
                    calculateItemDiffType(
                        encryptionContext = encryptionContext,
                        baseItemFieldHiddenState = baseCustomField.value,
                        otherItemFieldHiddenState = otherCustomField.value
                    )
                }

                else -> ItemDiffType.Content
            }.also(itemDiffTypes::add)
        }

        return itemDiffTypes
    }

    protected fun toggleHiddenCustomField(
        customFieldsContent: List<CustomFieldContent>,
        hiddenFieldType: ItemDetailsFieldType.Hidden.CustomField,
        hiddenState: HiddenState
    ): List<CustomFieldContent> = customFieldsContent.mapIndexed { index, customFieldContent ->
        if (index == hiddenFieldType.index && customFieldContent is CustomFieldContent.Hidden) {
            customFieldContent.copy(value = hiddenState)
        } else {
            customFieldContent
        }
    }

}
