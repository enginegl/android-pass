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

package proton.android.pass.data.impl.usecases.defaultvault

import proton.android.pass.data.api.usecases.defaultvault.SetDefaultVault
import proton.android.pass.domain.ShareId
import proton.android.pass.preferences.UserPreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetDefaultVaultImpl @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : SetDefaultVault {

    override suspend fun invoke(shareId: ShareId): Result<Unit> =
        userPreferencesRepository.setDefaultVault(shareId.id)

}