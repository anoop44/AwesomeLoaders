package ss.anoop.awesomeloaders.utils

import android.content.res.Resources
import android.util.TypedValue

internal fun dpToPx(dp: Float, resources: Resources) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)