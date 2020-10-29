package alpha.soft.weather.utils

import alpha.soft.weather.R
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet

@SuppressLint("Recycle")
open class RoundRectCornerImageView : androidx.appcompat.widget.AppCompatImageView {
    private var radius = 8.0f
    private var path: Path? = null
    private var rect: RectF? = null

    constructor(context: Context) : super(context) {
        val a= context.obtainStyledAttributes(null, R.styleable.MyView, 0, 0)
        radius = a.getFloat(R.styleable.MyView_corner_radius, 8f)?:8f
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a= context.obtainStyledAttributes(attrs, R.styleable.MyView, 0, 0)
        radius = a.getFloat(R.styleable.MyView_corner_radius, 8f)?:8f
        init()
    }

    @SuppressLint("Recycle", "CustomViewStyleable")
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        val a= context.obtainStyledAttributes(attrs, R.styleable.MyView, defStyle, 0)
        radius = a.getFloat(R.styleable.MyView_corner_radius, 8f)?:8f
        init()
    }

    private fun init() {
        path = Path()
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        rect = RectF(0F, 0F, width.toFloat(), height.toFloat())
        path?.addRoundRect(rect!!, radius, radius, Path.Direction.CW)
        canvas.clipPath(path!!)
        super.onDraw(canvas)
    }
}