import android.content.Context
import android.widget.Toast
import java.lang.Exception

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

class CustomException(message: String) : Exception(message)
