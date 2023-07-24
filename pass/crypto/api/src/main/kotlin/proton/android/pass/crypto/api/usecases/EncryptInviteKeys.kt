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

package proton.android.pass.crypto.api.usecases

import me.proton.core.key.domain.entity.key.PrivateKey
import me.proton.core.key.domain.entity.key.PublicKey
import proton.pass.domain.key.ShareKey

@JvmInline
value class EncryptedInviteShareKeyList(val keys: List<EncryptedInviteKey>)

data class EncryptedInviteKey(
    val keyRotation: Long,
    val key: String
)

interface EncryptInviteKeys {

    operator fun invoke(
        inviterAddressKey: PrivateKey,
        shareKeys: List<ShareKey>,
        targetAddressKey: PublicKey
    ): EncryptedInviteShareKeyList

}