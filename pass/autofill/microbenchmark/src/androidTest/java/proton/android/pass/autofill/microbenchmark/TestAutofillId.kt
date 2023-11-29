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

package proton.android.pass.autofill.microbenchmark

import android.os.Parcel
import android.os.Parcelable
import proton.android.pass.autofill.entities.AutofillFieldId
import kotlin.random.Random

data class TestAutofillId(val id: Int) : AutofillFieldId, Parcelable {
    override fun describeContents(): Int {
        throw IllegalStateException("Not implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        throw IllegalStateException("Not implemented")
    }

    override fun value() = id
}

fun newAutofillFieldId() = TestAutofillId(Random.nextInt())
