package alpha.soft.weather.utils.extensions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols


fun ViewGroup.inflate(@LayoutRes resId: Int) =
    LayoutInflater.from(context).inflate(resId, this, false)!!


//fun Fragment.database() = Database.getInstance(context!!).musicDao()

//fun AppCompatActivity.database() = Database.getInstance(this).musicDao()

fun Context.inflate(@LayoutRes resId: Int) = LayoutInflater.from(this).inflate(resId, null, false)!!

fun String.showSnacbar(view: View) = Snackbar.make(view, this, Snackbar.LENGTH_SHORT).show()

fun String.showToast(context: Context?) = Toast.makeText(context, this, Toast.LENGTH_SHORT).show()

fun TextView.textError(@StringRes message: Int) {
    this.text = context.getString(message)
}

fun <T : Number> formatMoney(value: T, showDecimal: Boolean): String {
    val decimalFormat = DecimalFormat("###,###,###,###")
    decimalFormat.groupingSize = 3
    decimalFormat.minimumFractionDigits = 0

    val s = DecimalFormatSymbols()
    s.groupingSeparator = ' '
    val symbols = decimalFormat.decimalFormatSymbols
    s.decimalSeparator = symbols.decimalSeparator
    decimalFormat.decimalFormatSymbols = s

    decimalFormat.minimumFractionDigits = if (showDecimal) 2 else 0
    decimalFormat.maximumFractionDigits = if (showDecimal) 2 else 0

    return decimalFormat.format(value)
}