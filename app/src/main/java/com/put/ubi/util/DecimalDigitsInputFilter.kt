package com.put.ubi.util

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Pattern

class DecimalDigitsInputFilter(digitsBeforeZero: Int, digitsAfterZero: Int) : InputFilter {

    private val pattern =
        Pattern.compile("(\\d{0,$digitsBeforeZero})|(\\d{0,$digitsBeforeZero}\\.\\d{0,$digitsAfterZero})")

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        return if (source.isEmpty()) {
            // When the source text is empty, we need to remove characters and check the result
            if (pattern.matcher(dest.removeRange(dstart, dend)).matches()) {
                // No changes to source
                null
            } else {
                // Don't delete characters, return the old subsequence
                dest.subSequence(dstart, dend)
            }
        } else {
            // Check the result
            if (pattern.matcher(dest.replaceRange(dstart, dend, source)).matches()) {
                // No changes to source
                null
            } else {
                // Return nothing
                ""
            }
        }
    }
}