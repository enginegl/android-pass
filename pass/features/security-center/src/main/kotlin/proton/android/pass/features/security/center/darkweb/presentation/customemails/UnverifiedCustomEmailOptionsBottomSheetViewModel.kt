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

package proton.android.pass.features.security.center.darkweb.presentation.customemails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import proton.android.pass.commonui.api.SavedStateHandleProvider
import proton.android.pass.commonui.api.require
import proton.android.pass.domain.breach.BreachCustomEmailId
import proton.android.pass.features.security.center.darkweb.navigation.CustomEmailNavArgId
import proton.android.pass.features.security.center.shared.navigation.BreachEmailIdArgId
import proton.android.pass.navigation.api.NavParamEncoder
import javax.inject.Inject

@HiltViewModel
internal class UnverifiedCustomEmailOptionsBottomSheetViewModel @Inject constructor(
    savedStateHandleProvider: SavedStateHandleProvider
) : ViewModel() {

    private val breachCustomEmailId: BreachCustomEmailId = savedStateHandleProvider.get()
        .require<String>(BreachEmailIdArgId.key)
        .let(::BreachCustomEmailId)

    private val customEmail: String = savedStateHandleProvider.get()
        .require<String>(CustomEmailNavArgId.key)
        .let { NavParamEncoder.decode(it) }

    private val eventFlow: MutableStateFlow<UnverifiedCustomEmailOptionsEvent> =
        MutableStateFlow(UnverifiedCustomEmailOptionsEvent.Idle)

    private val loadingStateFlow: MutableStateFlow<UnverifiedCustomEmailOptionsLoadingState> =
        MutableStateFlow(UnverifiedCustomEmailOptionsLoadingState.Idle)

    val state: StateFlow<UnverifiedCustomEmailState> = combine(
        eventFlow,
        loadingStateFlow
    ) { event, loading ->
        UnverifiedCustomEmailState(event, loading)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = UnverifiedCustomEmailState.Initial
    )

    @Suppress("MagicNumber")
    fun onVerify() = viewModelScope.launch {
        loadingStateFlow.update { UnverifiedCustomEmailOptionsLoadingState.Verify }

        delay(2_000L)
        eventFlow.update { UnverifiedCustomEmailOptionsEvent.Verify(breachCustomEmailId, customEmail) }

        loadingStateFlow.update { UnverifiedCustomEmailOptionsLoadingState.Idle }
    }

    @Suppress("MagicNumber")
    fun onRemove() = viewModelScope.launch {
        loadingStateFlow.update { UnverifiedCustomEmailOptionsLoadingState.Remove }

        delay(2_000L)
        eventFlow.update { UnverifiedCustomEmailOptionsEvent.Close }

        loadingStateFlow.update { UnverifiedCustomEmailOptionsLoadingState.Idle }
    }

    internal fun consumeEvent(event: UnverifiedCustomEmailOptionsEvent) {
        eventFlow.compareAndSet(event, UnverifiedCustomEmailOptionsEvent.Idle)
    }

}