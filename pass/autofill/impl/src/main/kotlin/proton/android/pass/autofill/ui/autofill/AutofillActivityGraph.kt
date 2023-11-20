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

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavGraphBuilder
import proton.android.pass.autofill.Utils
import proton.android.pass.autofill.entities.AutofillAppState
import proton.android.pass.autofill.entities.AutofillItem
import proton.android.pass.autofill.extensions.CreatedAlias
import proton.android.pass.autofill.extensions.toAutoFillItem
import proton.android.pass.autofill.extensions.toAutofillItem
import proton.android.pass.autofill.ui.autofill.navigation.SelectItem
import proton.android.pass.autofill.ui.autofill.navigation.SelectItemNavigation
import proton.android.pass.autofill.ui.autofill.navigation.selectItemGraph
import proton.android.pass.autofill.ui.bottomsheet.itemoptions.AutofillItemOptionsBottomSheet
import proton.android.pass.autofill.ui.bottomsheet.itemoptions.AutofillItemOptionsNavigation
import proton.android.pass.autofill.ui.bottomsheet.itemoptions.autofillItemOptionsGraph
import proton.android.pass.common.api.None
import proton.android.pass.common.api.Some
import proton.android.pass.commonuimodels.api.PackageInfoUi
import proton.android.pass.featureauth.impl.AuthNavigation
import proton.android.pass.featureauth.impl.EnterPin
import proton.android.pass.featureauth.impl.authGraph
import proton.android.pass.featureitemcreate.impl.alias.CreateAlias
import proton.android.pass.featureitemcreate.impl.alias.CreateAliasBottomSheet
import proton.android.pass.featureitemcreate.impl.alias.CreateAliasNavigation
import proton.android.pass.featureitemcreate.impl.alias.createAliasGraph
import proton.android.pass.featureitemcreate.impl.bottomsheets.createitem.CreateItemBottomSheetMode
import proton.android.pass.featureitemcreate.impl.bottomsheets.createitem.CreateItemBottomsheet
import proton.android.pass.featureitemcreate.impl.bottomsheets.createitem.CreateItemBottomsheetNavigation
import proton.android.pass.featureitemcreate.impl.bottomsheets.createitem.bottomsheetCreateItemGraph
import proton.android.pass.featureitemcreate.impl.bottomsheets.customfield.AddCustomFieldBottomSheet
import proton.android.pass.featureitemcreate.impl.bottomsheets.customfield.CustomFieldOptionsBottomSheet
import proton.android.pass.featureitemcreate.impl.common.KEY_VAULT_SELECTED
import proton.android.pass.featureitemcreate.impl.dialogs.CustomFieldNameDialog
import proton.android.pass.featureitemcreate.impl.dialogs.EditCustomFieldNameDialog
import proton.android.pass.featureitemcreate.impl.login.BaseLoginNavigation
import proton.android.pass.featureitemcreate.impl.login.CreateLogin
import proton.android.pass.featureitemcreate.impl.login.CreateLoginNavigation
import proton.android.pass.featureitemcreate.impl.login.InitialCreateLoginUiState
import proton.android.pass.featureitemcreate.impl.login.bottomsheet.aliasoptions.AliasOptionsBottomSheet
import proton.android.pass.featureitemcreate.impl.login.bottomsheet.aliasoptions.CLEAR_ALIAS_NAV_PARAMETER_KEY
import proton.android.pass.featureitemcreate.impl.login.createUpdateLoginGraph
import proton.android.pass.featureitemcreate.impl.totp.CameraTotp
import proton.android.pass.featureitemcreate.impl.totp.PhotoPickerTotp
import proton.android.pass.featurepassword.impl.GeneratePasswordBottomsheet
import proton.android.pass.featurepassword.impl.GeneratePasswordBottomsheetModeValue
import proton.android.pass.featurepassword.impl.GeneratePasswordNavigation
import proton.android.pass.featurepassword.impl.dialog.mode.PasswordModeDialog
import proton.android.pass.featurepassword.impl.dialog.separator.WordSeparatorDialog
import proton.android.pass.featurepassword.impl.generatePasswordBottomsheetGraph
import proton.android.pass.featuresearchoptions.impl.SortingBottomsheet
import proton.android.pass.featuresearchoptions.impl.SortingLocation
import proton.android.pass.featuresearchoptions.impl.sortingGraph
import proton.android.pass.featurevault.impl.VaultNavigation
import proton.android.pass.featurevault.impl.bottomsheet.select.SelectVaultBottomsheet
import proton.android.pass.featurevault.impl.vaultGraph
import proton.android.pass.navigation.api.AppNavigator

@Suppress("LongParameterList", "LongMethod", "ComplexMethod")
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
fun NavGraphBuilder.autofillActivityGraph(
    appNavigator: AppNavigator,
    autofillAppState: AutofillAppState,
    selectedAutofillItem: AutofillItem?,
    onNavigate: (AutofillNavigation) -> Unit,
    onAutofillItemReceived: (AutofillItem) -> Unit,
    dismissBottomSheet: (() -> Unit) -> Unit
) {
    authGraph(
        canLogout = false,
        navigation = {
            when (it) {
                AuthNavigation.Back -> onNavigate(AutofillNavigation.Cancel)
                AuthNavigation.Success -> if (selectedAutofillItem != null) {
                    onAutofillItemReceived(selectedAutofillItem)
                } else {
                    appNavigator.navigate(SelectItem)
                }

                AuthNavigation.Dismissed -> onNavigate(AutofillNavigation.Cancel)
                AuthNavigation.Failed -> onNavigate(AutofillNavigation.Cancel)
                AuthNavigation.SignOut -> {}
                AuthNavigation.ForceSignOut -> onNavigate(AutofillNavigation.ForceSignOut)
                AuthNavigation.EnterPin -> appNavigator.navigate(EnterPin)
            }
        }
    )
    selectItemGraph(
        state = autofillAppState,
        onNavigate = {
            when (it) {
                SelectItemNavigation.AddItem -> appNavigator.navigate(CreateItemBottomsheet)
                SelectItemNavigation.Cancel -> onNavigate(AutofillNavigation.Cancel)
                is SelectItemNavigation.ItemSelected -> onNavigate(AutofillNavigation.Selected(it.autofillMappings))
                is SelectItemNavigation.SortingBottomsheet ->
                    appNavigator.navigate(
                        SortingBottomsheet,
                        SortingBottomsheet.createNavRoute(
                            sortingType = it.searchSortingType,
                            location = SortingLocation.Autofill
                        )
                    )

                is SelectItemNavigation.ItemOptions -> appNavigator.navigate(
                    destination = AutofillItemOptionsBottomSheet,
                    route = AutofillItemOptionsBottomSheet.createRoute(it.shareId, it.itemId)
                )

                SelectItemNavigation.Upgrade -> onNavigate(AutofillNavigation.Upgrade)
            }
        }
    )
    sortingGraph { appNavigator.navigateBack() }

    createUpdateLoginGraph(
        initialCreateLoginUiState = InitialCreateLoginUiState(
            title = run {
                val url = autofillAppState.autofillData.assistInfo.url
                val appName = autofillAppState.autofillData.packageInfo.map { it.appName.value }
                Utils.getTitle(url, appName)
            },
            url = autofillAppState.autofillData.assistInfo.url.value(),
            aliasItemFormState = null,
            packageInfoUi = autofillAppState.autofillData.packageInfo.map(::PackageInfoUi).value(),
        ),
        onNavigate = {
            when (it) {
                BaseLoginNavigation.Close -> dismissBottomSheet {
                    appNavigator.navigateBack(comesFromBottomsheet = true)
                }

                is BaseLoginNavigation.CreateAlias -> appNavigator.navigate(
                    destination = CreateAliasBottomSheet,
                    route = CreateAliasBottomSheet.createNavRoute(
                        it.shareId,
                        it.showUpgrade,
                        it.title
                    )
                )

                BaseLoginNavigation.GeneratePassword -> appNavigator.navigate(
                    destination = GeneratePasswordBottomsheet,
                    route = GeneratePasswordBottomsheet.buildRoute(
                        mode = GeneratePasswordBottomsheetModeValue.CancelConfirm
                    )
                )

                is BaseLoginNavigation.OnCreateLoginEvent -> when (val event = it.event) {
                    is CreateLoginNavigation.LoginCreated -> {
                        when (val autofillItem = event.itemUiModel.toAutoFillItem()) {
                            None -> {}
                            is Some -> onAutofillItemReceived(autofillItem.value)
                        }
                    }

                    is CreateLoginNavigation.SelectVault -> {
                        appNavigator.navigate(
                            destination = SelectVaultBottomsheet,
                            route = SelectVaultBottomsheet.createNavRoute(event.shareId)
                        )
                    }
                }

                is BaseLoginNavigation.ScanTotp -> appNavigator.navigate(
                    destination = CameraTotp,
                    route = CameraTotp.createNavRoute(it.index)
                )

                BaseLoginNavigation.Upgrade -> onNavigate(AutofillNavigation.Upgrade)

                is BaseLoginNavigation.AliasOptions -> appNavigator.navigate(
                    destination = AliasOptionsBottomSheet,
                    route = AliasOptionsBottomSheet.createNavRoute(it.shareId, it.showUpgrade)
                )

                BaseLoginNavigation.DeleteAlias -> appNavigator.navigateBackWithResult(
                    key = CLEAR_ALIAS_NAV_PARAMETER_KEY,
                    value = true
                )

                is BaseLoginNavigation.EditAlias -> {
                    appNavigator.navigate(
                        destination = CreateAliasBottomSheet,
                        route = CreateAliasBottomSheet.createNavRoute(
                            it.shareId,
                            it.showUpgrade,
                            isEdit = true
                        )
                    )
                }

                BaseLoginNavigation.AddCustomField -> appNavigator.navigate(
                    destination = AddCustomFieldBottomSheet
                )

                is BaseLoginNavigation.CustomFieldTypeSelected -> dismissBottomSheet {
                    appNavigator.navigate(
                        destination = CustomFieldNameDialog,
                        route = CustomFieldNameDialog.buildRoute(it.type),
                        backDestination = CreateLogin
                    )
                }

                is BaseLoginNavigation.CustomFieldOptions -> appNavigator.navigate(
                    destination = CustomFieldOptionsBottomSheet,
                    route = CustomFieldOptionsBottomSheet.buildRoute(it.index, it.currentValue)
                )

                is BaseLoginNavigation.EditCustomField -> dismissBottomSheet {
                    appNavigator.navigate(
                        destination = EditCustomFieldNameDialog,
                        route = EditCustomFieldNameDialog.buildRoute(it.index, it.currentValue),
                        backDestination = CreateLogin
                    )
                }

                BaseLoginNavigation.RemovedCustomField -> dismissBottomSheet {
                    appNavigator.navigateBack(comesFromBottomsheet = true)
                }

                // Updates cannot happen
                is BaseLoginNavigation.OnUpdateLoginEvent -> {}
                is BaseLoginNavigation.OpenImagePicker -> appNavigator.navigate(
                    destination = PhotoPickerTotp,
                    route = PhotoPickerTotp.createNavRoute(it.index),
                    backDestination = CreateLogin
                )
                BaseLoginNavigation.TotpCancel -> appNavigator.navigateBack()
                is BaseLoginNavigation.TotpSuccess ->
                    appNavigator.navigateBackWithResult(it.results)
            }
        }
    )

    generatePasswordBottomsheetGraph(
        onNavigate = {
            when (it) {
                GeneratePasswordNavigation.CloseDialog -> appNavigator.navigateBack()
                GeneratePasswordNavigation.DismissBottomsheet -> dismissBottomSheet {
                    appNavigator.navigateBack(comesFromBottomsheet = true)
                }

                GeneratePasswordNavigation.OnSelectWordSeparator -> appNavigator.navigate(
                    destination = WordSeparatorDialog
                )

                GeneratePasswordNavigation.OnSelectPasswordMode -> appNavigator.navigate(
                    destination = PasswordModeDialog
                )
            }
        }
    )
    createAliasGraph(
        onNavigate = {
            when (it) {
                CreateAliasNavigation.Close -> appNavigator.navigateBack()
                CreateAliasNavigation.CloseBottomsheet -> dismissBottomSheet {
                    appNavigator.navigateBack(comesFromBottomsheet = true)
                }

                is CreateAliasNavigation.CreatedFromBottomsheet -> dismissBottomSheet {
                    appNavigator.navigateBack(comesFromBottomsheet = true)
                }


                is CreateAliasNavigation.Created -> {
                    val created = CreatedAlias(it.shareId, it.itemId, it.alias)
                    onAutofillItemReceived(created.toAutofillItem())
                }

                CreateAliasNavigation.Upgrade -> onNavigate(AutofillNavigation.Upgrade)
                is CreateAliasNavigation.SelectVault -> {
                    appNavigator.navigate(
                        destination = SelectVaultBottomsheet,
                        route = SelectVaultBottomsheet.createNavRoute(it.shareId)
                    )
                }
            }
        }
    )
    bottomsheetCreateItemGraph(
        mode = CreateItemBottomSheetMode.Autofill,
        onNavigate = {
            when (it) {
                is CreateItemBottomsheetNavigation.CreateAlias -> dismissBottomSheet {
                    appNavigator.navigate(
                        destination = CreateAlias,
                        route = CreateAlias.createNavRoute(it.shareId)
                    )
                }

                is CreateItemBottomsheetNavigation.CreateLogin -> dismissBottomSheet {
                    appNavigator.navigate(
                        destination = CreateLogin,
                        route = CreateLogin.createNavRoute(it.shareId)
                    )
                }

                else -> {}
            }
        }
    )

    vaultGraph(
        onNavigate = {
            when (it) {
                VaultNavigation.Close -> appNavigator.navigateBack()
                VaultNavigation.Upgrade -> onNavigate(AutofillNavigation.Upgrade)
                is VaultNavigation.VaultSelected -> dismissBottomSheet {
                    appNavigator.navigateBackWithResult(
                        key = KEY_VAULT_SELECTED,
                        value = it.shareId.id,
                        comesFromBottomsheet = true
                    )
                }

                is VaultNavigation.VaultEdit,
                is VaultNavigation.VaultMigrate,
                is VaultNavigation.VaultRemove,
                is VaultNavigation.VaultShare,
                is VaultNavigation.VaultLeave,
                is VaultNavigation.VaultAccess -> {
                }
            }
        }
    )

    autofillItemOptionsGraph {
        when (it) {
            AutofillItemOptionsNavigation.Close -> dismissBottomSheet {
                appNavigator.navigateBack(comesFromBottomsheet = true)
            }
        }
    }
}
