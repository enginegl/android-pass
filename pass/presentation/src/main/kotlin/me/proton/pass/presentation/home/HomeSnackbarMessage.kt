package me.proton.pass.presentation.home

import androidx.annotation.StringRes
import me.proton.android.pass.notifications.api.SnackbarMessage
import me.proton.android.pass.notifications.api.SnackbarType
import me.proton.pass.presentation.R

enum class HomeSnackbarMessage(
    @StringRes override val id: Int,
    override val type: SnackbarType,
    override val isClipboard: Boolean = false
) : SnackbarMessage {
    ObserveItemsError(R.string.error_observing_items, SnackbarType.ERROR),
    ObserveShareError(R.string.error_observing_share, SnackbarType.ERROR),
    RefreshError(R.string.error_refreshing, SnackbarType.ERROR),
    LoginMovedToTrash(R.string.home_snackbar_login_moved_to_trash, SnackbarType.SUCCESS),
    AliasMovedToTrash(R.string.home_snackbar_alias_moved_to_trash, SnackbarType.SUCCESS),
    NoteMovedToTrash(R.string.home_snackbar_note_moved_to_trash, SnackbarType.SUCCESS),
    AliasCopied(R.string.alias_copied_to_clipboard, SnackbarType.NORM, true),
    NoteCopied(R.string.note_copied_to_clipboard, SnackbarType.NORM, true),
    PasswordCopied(R.string.password_copied_to_clipboard, SnackbarType.NORM, true),
    UsernameCopied(R.string.username_copied_to_clipboard, SnackbarType.NORM, true),
}

