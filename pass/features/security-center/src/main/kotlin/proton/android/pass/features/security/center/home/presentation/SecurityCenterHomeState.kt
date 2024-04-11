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

package proton.android.pass.features.security.center.home.presentation

import androidx.compose.runtime.Stable
import proton.android.pass.common.api.LoadingResult
import proton.android.pass.domain.Item
import proton.android.pass.domain.PlanType
import proton.android.pass.domain.breach.Breach
import proton.android.pass.domain.features.PaidFeature
import proton.android.pass.features.security.center.home.navigation.SecurityCenterHomeNavDestination
import proton.android.pass.securitycenter.api.InsecurePasswordsResult
import proton.android.pass.securitycenter.api.Missing2faResult
import proton.android.pass.securitycenter.api.ReusedPasswordsResult

@Stable
internal data class SecurityCenterHomeState(
    internal val isSentinelEnabled: Boolean,
    private val breachLoadingResult: LoadingResult<Breach>,
    private val insecurePasswordsLoadingResult: LoadingResult<InsecurePasswordsResult>,
    private val reusedPasswordsLoadingResult: LoadingResult<ReusedPasswordsResult>,
    private val missing2faResult: LoadingResult<Missing2faResult>,
    private val excludedLoginItemsLoadingResult: LoadingResult<List<Item>>,
    private val planType: PlanType
) {

    private val dataBreachesCount: Int = when (breachLoadingResult) {
        is LoadingResult.Error,
        LoadingResult.Loading -> 0

        is LoadingResult.Success -> breachLoadingResult.data.breachesCount
    }

    private val hasDataBreaches: Boolean = dataBreachesCount > 0

    internal val insecurePasswordsCount: Int? = when (insecurePasswordsLoadingResult) {
        is LoadingResult.Error,
        LoadingResult.Loading -> null

        is LoadingResult.Success -> insecurePasswordsLoadingResult.data.insecurePasswordsCount
    }

    internal val reusedPasswordsCount: Int? = when (reusedPasswordsLoadingResult) {
        is LoadingResult.Error,
        LoadingResult.Loading -> null

        is LoadingResult.Success -> reusedPasswordsLoadingResult.data.reusedPasswordsCount
    }

    internal val missing2faCount: Int? = when (missing2faResult) {
        is LoadingResult.Error,
        LoadingResult.Loading -> null

        is LoadingResult.Success -> missing2faResult.data.missing2faCount
    }

    internal val excludedItemsCount: Int? = when (excludedLoginItemsLoadingResult) {
        is LoadingResult.Error,
        LoadingResult.Loading -> null

        is LoadingResult.Success -> excludedLoginItemsLoadingResult.data.size
    }

    internal val missing2faDestination: SecurityCenterHomeNavDestination = when (planType) {
        is PlanType.Free,
        is PlanType.Unknown -> SecurityCenterHomeNavDestination.Upsell(PaidFeature.ViewMissing2fa)

        is PlanType.Paid,
        is PlanType.Trial -> SecurityCenterHomeNavDestination.MissingTFA
    }

    internal val isSentinelPaidFeature: Boolean = when (planType) {
        is PlanType.Free,
        is PlanType.Unknown -> true

        is PlanType.Paid,
        is PlanType.Trial -> false
    }

    internal val isMissing2faPaidFeature: Boolean = when (planType) {
        is PlanType.Free,
        is PlanType.Unknown -> true

        is PlanType.Paid,
        is PlanType.Trial -> false
    }

    internal val isExcludedItemsPaidFeature: Boolean = false

    internal val darkWebMonitoring: SecurityCenterHomeDarkWebMonitoring = when (planType) {
        is PlanType.Free,
        is PlanType.Unknown -> if (hasDataBreaches) {
            SecurityCenterHomeDarkWebMonitoring.FreeDataBreaches(
                dataBreachedSite = "", // implement when BE is ready
                dataBreachedTime = 0L, // implement when BE is ready
                dateBreachedEmail = DATA_BREACHED_EMAIL,
                dataBreachedPassword = DATA_BREACHED_PASSWORD
            )
        } else {
            SecurityCenterHomeDarkWebMonitoring.FreeNoDataBreaches
        }

        is PlanType.Trial,
        is PlanType.Paid -> if (hasDataBreaches) {
            SecurityCenterHomeDarkWebMonitoring.PaidDataBreaches(dataBreachesCount)
        } else {
            SecurityCenterHomeDarkWebMonitoring.PaidNoDataBreaches
        }
    }

    internal companion object {

        internal val Initial: SecurityCenterHomeState = SecurityCenterHomeState(
            isSentinelEnabled = false,
            breachLoadingResult = LoadingResult.Loading,
            insecurePasswordsLoadingResult = LoadingResult.Loading,
            reusedPasswordsLoadingResult = LoadingResult.Loading,
            missing2faResult = LoadingResult.Loading,
            excludedLoginItemsLoadingResult = LoadingResult.Loading,
            planType = PlanType.Unknown()
        )

        // This constants are arbitrary values since only will be use for blurry effect on the UI
        private const val DATA_BREACHED_EMAIL = "email@proton.me"
        private const val DATA_BREACHED_PASSWORD = "********"

    }

}
