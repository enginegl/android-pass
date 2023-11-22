package proton.android.pass.composecomponents.impl.item

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import kotlinx.collections.immutable.toPersistentList
import proton.android.pass.commonui.api.PassTheme
import proton.android.pass.commonui.api.StringUtils.maskCreditCardNumber
import proton.android.pass.commonui.api.ThemePairPreviewProvider
import proton.android.pass.commonuimodels.api.ItemUiModel
import proton.android.pass.composecomponents.impl.item.icon.CreditCardIcon
import proton.android.pass.domain.ItemContents

private const val MAX_PREVIEW_LENGTH = 128

@Composable
fun CreditCardRow(
    modifier: Modifier = Modifier,
    item: ItemUiModel,
    highlight: String = "",
    vaultIcon: Int? = null
) {
    val content = item.contents as ItemContents.CreditCard
    val maskedNumber = remember(content.number) {
        if (content.number.isBlank()) {
            null
        } else {
            AnnotatedString(maskCreditCardNumber(content.number))
        }
    }

    val highlightColor = PassTheme.colors.interactionNorm
    val fields = remember(content.title, content.note, content.cardHolder, highlight) {
        getHighlightedFields(
            title = content.title,
            note = content.note,
            cardHolder = content.cardHolder,
            highlight = highlight,
            highlightColor = highlightColor
        )
    }

    ItemRow(
        modifier = modifier,
        icon = { CreditCardIcon() },
        title = fields.title,
        subtitles = listOfNotNull(maskedNumber, fields.note, fields.cardHolder).toPersistentList(),
        vaultIcon = vaultIcon
    )
}

private fun getHighlightedFields(
    title: String,
    note: String,
    cardHolder: String,
    highlight: String,
    highlightColor: Color
): CreditCardHighlightFields {
    var annotatedTitle = AnnotatedString(title.take(MAX_PREVIEW_LENGTH))
    var annotatedNote: AnnotatedString? = null
    var annotatedCardHolder: AnnotatedString? = null
    if (highlight.isNotBlank()) {
        title.highlight(highlight, highlightColor)?.let {
            annotatedTitle = it
        }
        note.replace("\n", " ").highlight(highlight, highlightColor)?.let {
            annotatedNote = it
        }
        cardHolder.highlight(highlight, highlightColor)?.let {
            annotatedCardHolder = it
        }
    }

    return CreditCardHighlightFields(
        title = annotatedTitle,
        note = annotatedNote,
        cardHolder = annotatedCardHolder
    )
}

@Stable
private data class CreditCardHighlightFields(
    val title: AnnotatedString,
    val note: AnnotatedString?,
    val cardHolder: AnnotatedString?
)

class ThemedCreditCardPreviewProvider : ThemePairPreviewProvider<CreditCardRowParameter>(
    CreditCardRowPreviewProvider()
)

@Preview
@Composable
fun CreditCardRowPreview(
    @PreviewParameter(ThemedCreditCardPreviewProvider::class) input: Pair<Boolean, CreditCardRowParameter>
) {
    PassTheme(isDark = input.first) {
        Surface {
            CreditCardRow(
                item = input.second.model,
                highlight = input.second.highlight,
            )
        }
    }
}
