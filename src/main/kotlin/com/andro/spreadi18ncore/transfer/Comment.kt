package com.andro.spreadi18ncore.transfer

const val CommentIndicator = "//"
val String.indicatesComment get() = startsWith(CommentIndicator)
val String.commentText get() = replace("^\\Q$CommentIndicator\\E *".toRegex(), "")//escape all between \\Q and \\E
val String.withCommentIndicator get() = if (indicatesComment) this else "$CommentIndicator$this"
