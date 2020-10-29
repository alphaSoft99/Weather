package alpha.soft.weather.utils.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

fun View.gone(): View {
    visibility = View.GONE
    return this
}

@Suppress("SameParameterValue")
@NonNull
fun prepareFilePart(context: Context, partName: String, file: File, uri: Uri): MultipartBody.Part {
    val requestFile = file
        .asRequestBody(context.contentResolver.getType(uri)?.toMediaTypeOrNull())
    // MultipartBody.Part is used to send also the actual file name
    return MultipartBody.Part.createFormData(partName, file.name, requestFile)
}

fun View.invisible(): View {
    visibility = View.INVISIBLE
    return this
}

fun View.visible(): View {
    visibility = View.VISIBLE
    return this
}

fun <T> T?.or(default: T): T = this ?: default
fun <T> T?.or(compute: () -> T): T = this ?: compute()

/*
fun EditText.onTextChanged(textView: TextView){
    this.doOnTextChanged { _, _, _, _ ->
        textView.gone()
    }
}*/

fun EditText.to(): String {
    return this.text.toString()
}

fun EditText.clear() {
    this.setText("")
}

fun String.toRequestBody(): RequestBody = this.toRequestBody(MultipartBody.FORM)

fun File.toRequestData(keyName: String): MultipartBody.Part {
    val fileReqBody = this.asRequestBody("${toMimeType()}".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(keyName, name, fileReqBody)
}

private fun File.toMimeType(): String? {
    var type: String? = null
    val extension = MimeTypeMap.getFileExtensionFromUrl(path)
    if (extension != null) {
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    return type
}

fun doubleToStringNoDecimal(d: Double): String {
    val formatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
    formatter.applyPattern("#,###")
    return formatter.format(d)
}

typealias ButtonClick<T> = (T) -> Unit

fun String.showSnacber(view: View, text: String) = Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show()

fun drawableToBitmap(drawable: Drawable): Bitmap? {
    val bitmap: Bitmap? = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
        Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
        ) // Single color bitmap will be created of 1x1 pixel
    } else {
        Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
        )
    }

    if (drawable is BitmapDrawable) {
        if (drawable.bitmap != null) {
            return drawable.bitmap
        }
    }

    val canvas = bitmap?.let { Canvas(it) }
    drawable.setBounds(0, 0, canvas!!.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

fun log(message: Any?) = Log.d("TTT", message.toString())