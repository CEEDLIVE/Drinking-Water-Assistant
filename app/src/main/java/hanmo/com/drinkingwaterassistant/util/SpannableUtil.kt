package hanmo.com.drinkingwaterassistant.util

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan

/**
 * Created by hanmo on 2018. 9. 20..
 */
object SpannableUtil {

    fun getColorText(string: String, targetString: String, color: Int): SpannableString {
        val spannableString = SpannableString(string)
        val targetStartIndex = string.indexOf(targetString)
        val targetEndIndex = targetStartIndex + targetString.length
        spannableString.setSpan(ForegroundColorSpan(color), targetStartIndex, targetEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spannableString
    }

    fun getBoldText(string: String, targetString: String): SpannableString {
        val spannableString = SpannableString(string)
        val targetStartIndex = string.indexOf(targetString)
        val targetEndIndex = targetStartIndex + targetString.length
        spannableString.setSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD), targetStartIndex, targetEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spannableString
    }

    fun getTextSize(string: String, targetString: String): SpannableString {
        val spannableString = SpannableString(string)
        val targetStartIndex = string.indexOf(targetString)
        val targetEndIndex = targetStartIndex + targetString.length
        spannableString.setSpan(android.text.style.RelativeSizeSpan(12f), targetStartIndex, targetEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spannableString
    }

}