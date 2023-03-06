package proton.android.pass.presentation.navigation.drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.proton.core.accountmanager.presentation.compose.AccountPrimaryState
import me.proton.core.accountmanager.presentation.compose.rememberAccountPrimaryState
import me.proton.core.compose.theme.ProtonDimens
import me.proton.core.compose.theme.ProtonTheme
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.presentation.navigation.CoreNavigation

private const val SHOW_NEW_DRAWER_UI = false

@Composable
fun NavigationDrawerContent(
    modifier: Modifier = Modifier,
    drawerUiState: DrawerUiState,
    accountPrimaryState: AccountPrimaryState = rememberAccountPrimaryState(false),
    navDrawerNavigation: NavDrawerNavigation,
    coreNavigation: CoreNavigation,
    onSignOutClick: () -> Unit = {},
    onCloseDrawer: () -> Unit
) {
    val sidebarColors = requireNotNull(ProtonTheme.colors.sidebarColors).copy(
        backgroundNorm = PassTheme.colors.backgroundNorm
    )
    PassTheme(protonColors = sidebarColors) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = PassTheme.colors.backgroundNorm
        ) {
            Column {
                NavigationDrawerHeader(
                    currentUser = drawerUiState.currentUser,
                    sidebarColors = sidebarColors,
                    coreNavigation = coreNavigation,
                    accountPrimaryState = accountPrimaryState
                )
                if (SHOW_NEW_DRAWER_UI) {
                    NavigationDrawerVaultSection(
                        modifier = Modifier
                            .padding(top = ProtonDimens.DefaultSpacing)
                            .weight(1f, fill = true),
                        drawerUiState = drawerUiState,
                        navDrawerNavigation = navDrawerNavigation,
                        onVaultOptionsClick = {}, // To be implemented
                        onCloseDrawer = onCloseDrawer
                    )
                } else {
                    NavigationDrawerBody(
                        modifier = Modifier
                            .padding(top = ProtonDimens.DefaultSpacing)
                            .weight(1f, fill = false),
                        drawerUiState = drawerUiState,
                        navDrawerNavigation = navDrawerNavigation,
                        onSignOutClick = onSignOutClick,
                        onCloseDrawer = onCloseDrawer
                    )
                }
            }
        }
    }
}
