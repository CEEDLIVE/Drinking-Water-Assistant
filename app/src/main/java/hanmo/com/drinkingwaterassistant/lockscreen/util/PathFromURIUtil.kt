package hanmo.com.drinkingwaterassistant.lockscreen.util

import android.net.Uri
import android.provider.MediaStore
import hanmo.com.drinkingwaterassistant.DWApplication

/**
 * Created by hanmo on 2018. 9. 12..
 */
object PathFromURIUtil {

    fun getRealPathFromURI(contentURI: Uri?): String? {
        val cursor = DWApplication.applicationContext()?.run {
            contentResolver.query(contentURI, null, null, null, null)
        }

        // Source is Dropbox or other similar local file path
        cursor?.run {
            moveToFirst()
            val idx = getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            return getString(idx)
        } ?: kotlin.run {
            return contentURI?.path
        }
    }
}