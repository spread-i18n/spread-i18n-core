package com.andro.spreadi18ncore.localization

import com.andro.spreadi18ncore.localization.LanguageTag
import java.nio.file.Path

internal data class LocalizationFile(val path: Path, val languageTag: LanguageTag) {
}
