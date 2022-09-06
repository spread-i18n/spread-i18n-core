package com.andro.spreadi18ncore.transfer

const val NonTranslatableIndicator = "*"
val String.indicatesNonTranslatable get() = startsWith(NonTranslatableIndicator)
val String.translatable get() = replace("^\\Q$NonTranslatableIndicator\\E".toRegex(), "")//escape all between \\Q and \\E
val String.withNonTranslatableIndicator get() = if (indicatesNonTranslatable) this else "$NonTranslatableIndicator$this"
