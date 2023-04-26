package proton.android.pass.data.api.usecases

import kotlinx.coroutines.flow.Flow
import me.proton.core.domain.entity.UserId
import proton.pass.domain.Share

interface ObserveAllShares {
    operator fun invoke(userId: UserId? = null): Flow<List<Share>>
}
