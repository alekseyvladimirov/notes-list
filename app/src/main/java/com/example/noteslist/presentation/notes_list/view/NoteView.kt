package com.example.noteslist.presentation.notes_list.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.withTranslation
import com.example.noteslist.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint()

    private val titleTextPaint = TextPaint().apply {
        textSize = 16f.spToPx(context)
        typeface = Typeface.DEFAULT_BOLD
        setShadowLayer(
            2f,
            1f,
            1f,
            Color.LTGRAY
        )
    }
    private val noteTextPaint = TextPaint().apply {
        textSize = 14f.spToPx(context)
    }
    private val dateTextPaint = TextPaint().apply {
        textSize = 12f.spToPx(context)
    }

    private val overlayPaint = Paint().apply {
        color = Color.LTGRAY
        alpha = 0
    }

    private var outlineCornerRadius = 0f
    private var noteElevation = 0f
    private val corners = FloatArray(8)

    private val path = Path()

    private val starDrawable: Drawable? =
        AppCompatResources.getDrawable(context, R.drawable.ic_star)
    private val checkDrawable: Drawable? =
        AppCompatResources.getDrawable(context, R.drawable.ic_check)

    private val padding by lazy { 16f.dpToPx(context) }
    private val smallPadding by lazy { 8f.dpToPx(context) }

    private var containerClickHandlingEnabled = true

    var onNoteClick: ((NoteView) -> Unit)? = null
        set(value) {
            field = value
            updateClickability()
        }

    var onNoteLongClick: ((NoteView) -> Unit)? = null
        set(value) {
            field = value
            updateClickability()
        }

    init {
        context.withStyledAttributes(
            attrs,
            R.styleable.NoteView,
            defStyleAttr,
            R.style.NoteStyle
        ) {
            applyResolvedAppearance(
                bgColor = getColor(R.styleable.NoteView_noteBackgroundColor, Color.WHITE),
                textColor = getColor(R.styleable.NoteView_noteTextColor, Color.BLACK),
                cornerRadius = getDimension(R.styleable.NoteView_noteCornerRadius, 0f),
                elevation = getDimension(R.styleable.NoteView_noteElevation, 0f)
            )

            this@NoteView.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(0, 0, view.width, view.height, outlineCornerRadius)
                }
            }
            clipToOutline = true
        }

        updateClickability()
    }

    var title: String = ""
        set(value) {
            if (field == value) return
            field = value
            markContentChanged()
        }

    var noteText: String = ""
        set(value) {
            if (field == value) return
            field = value
            markContentChanged()
        }

    var timestamp: Long = 0L
        set(value) {
            if (field == value) return
            field = value
            date = dateFormatter.format(Date(value))
            markContentChanged()
        }
    private var date: String = ""

    var isImportant: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            markContentChanged()
        }

    var isRead: Boolean = false
        set(value) {
            field = value

            overlayPaint.alpha = if (value) 120 else 0

            applyStyle(
                if (value) R.style.NoteStyle_Read
                else R.style.NoteStyle_NotRead
            )

            invalidate()
        }

    private var starBounds = RectF()

    private var titleLayout: StaticLayout? = null
    private var titleTop = 0f
    private var titleX = 0f

    private var noteLayout: StaticLayout? = null
    private var noteTop = 0f

    private var dateX = 0f
    private var dateBaseline = 0f

    private var checkX = 0f
    private var checkY = 0f
    private var contentLayoutDirty = true

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var width = 0
        var height = 0

        val minContentWidth = calculateMinContentWidth()
        val minContentHeight = calculateMinContentHeight()

        when (widthMode) {
            MeasureSpec.EXACTLY -> width = widthSize
            MeasureSpec.AT_MOST -> width = minOf(widthSize, minContentWidth.toInt())
            MeasureSpec.UNSPECIFIED -> width = minContentWidth.toInt()
        }

        when (heightMode) {
            MeasureSpec.EXACTLY -> height = heightSize
            MeasureSpec.AT_MOST -> height = minOf(heightSize, minContentHeight.toInt())
            MeasureSpec.UNSPECIFIED -> height = minContentHeight.toInt()
        }

        setMeasuredDimension(width, height)
    }

    private fun calculateMinContentWidth(): Float {
        val textWidth = maxOf(
            titleTextPaint.measureText(title),
            noteTextPaint.measureText(noteText)
        )
        return (padding * 2) + textWidth +
                if (isImportant) (starDrawable?.intrinsicWidth?.toFloat() ?: 0f) + smallPadding else 0f
    }

    private fun calculateMinContentHeight(): Float {
        val titleHeight = titleTextPaint.fontMetrics.let { it.bottom - it.top }
        val noteHeight = noteTextPaint.fontMetrics.let { it.bottom - it.top } * 2
        val dateHeight = dateTextPaint.fontMetrics.let { it.bottom - it.top }

        return padding * 2 + titleHeight + smallPadding * 2 + noteHeight + dateHeight +
                if (isImportant) maxOf(starDrawable?.intrinsicHeight?.toFloat() ?: 0f, titleHeight) - titleHeight else 0f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        contentLayoutDirty = true
        invalidateOutline()
        rebuildRoundedPath(w, h)
    }

    private fun calculatePositions(width: Int, height: Int) {

        titleTop = padding
        val titleWidth = width - padding * 2 -
                if (isImportant) starBounds.width() + padding * 2 else 0f

        titleLayout = StaticLayout.Builder.obtain(
            title,
            0,
            title.length,
            titleTextPaint,
            titleWidth.toInt()
        )
            .setMaxLines(1)
            .setEllipsize(TextUtils.TruncateAt.END)
            .build()

        val titleHeight = titleLayout?.height ?: 0
        val starSize = titleHeight.toFloat()

        val titleCenterY = titleTop + titleHeight / 2f
        val starTop = titleCenterY - starSize / 2f

        starBounds.set(
            padding,
            starTop,
            padding + starSize,
            starTop + starSize
        )

        titleX = if (isImportant) {
            starBounds.right + smallPadding
        } else {
            padding
        }

        val noteWidth = width - 2 * padding
        noteLayout = StaticLayout.Builder.obtain(
            noteText,
            0,
            noteText.length,
            noteTextPaint,
            noteWidth.toInt()
        )
            .setMaxLines(2)
            .setEllipsize(TextUtils.TruncateAt.END)
            .build()

        val headerBottom = titleHeight + titleTop + smallPadding
        noteTop = headerBottom + smallPadding

        dateX = padding
        dateBaseline = height - padding - dateTextPaint.descent()

        val checkSize = 24f.dpToPx(context)
        checkX = width - padding - checkSize
        checkY = height - padding - checkSize

        if (isImportant) {
            starDrawable?.setBounds(
                starBounds.left.toInt(),
                starBounds.top.toInt(),
                starBounds.right.toInt(),
                starBounds.bottom.toInt()
            )
        }

        checkDrawable?.setBounds(
            checkX.toInt(),
            checkY.toInt(),
            (checkX + checkSize).toInt(),
            (checkY + checkSize).toInt()
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        ensureContentLayout()
        canvas.drawPath(path, backgroundPaint)

        if (isImportant) {
            starDrawable?.draw(canvas)
        }

        canvas.withTranslation(titleX, titleTop) {
            titleLayout?.draw(this)
        }

        canvas.withTranslation(padding, noteTop) {
            noteLayout?.draw(this)
        }

        canvas.drawText(date, dateX, dateBaseline, dateTextPaint)

        if (overlayPaint.alpha > 0) {
            canvas.drawPath(path, overlayPaint)
        }

        if (isRead) {
            checkDrawable?.draw(canvas)
        }
    }

    private fun applyStyle(styleRes: Int) {
        context.withStyledAttributes(styleRes, R.styleable.NoteView) {
            applyResolvedAppearance(
                bgColor = getColor(R.styleable.NoteView_noteBackgroundColor, Color.WHITE),
                textColor = getColor(R.styleable.NoteView_noteTextColor, Color.BLACK),
                cornerRadius = getDimension(R.styleable.NoteView_noteCornerRadius, 0f),
                elevation = getDimension(R.styleable.NoteView_noteElevation, 0f)
            )
        }
    }

    private fun applyResolvedAppearance(
        bgColor: Int,
        textColor: Int,
        cornerRadius: Float,
        elevation: Float
    ) {
        backgroundPaint.color = bgColor

        noteTextPaint.color = textColor
        titleTextPaint.color = textColor
        dateTextPaint.color = textColor

        outlineCornerRadius = cornerRadius
        corners.fill(outlineCornerRadius)

        noteElevation = elevation
        this.elevation = noteElevation

        contentLayoutDirty = true
        rebuildRoundedPath(width, height)
        invalidateOutline()
        invalidate()
    }

    private fun ensureContentLayout() {
        if (!contentLayoutDirty || width <= 0 || height <= 0) return
        calculatePositions(width, height)
        contentLayoutDirty = false
    }

    private fun markContentChanged() {
        contentLayoutDirty = true
        requestLayout()
        invalidate()
    }

    private fun rebuildRoundedPath(w: Int, h: Int) {
        path.reset()
        if (w > 0 && h > 0) {
            path.addRoundRect(
                0f,
                0f,
                w.toFloat(),
                h.toFloat(),
                corners,
                Path.Direction.CW
            )
        }
    }

    companion object {
        private val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    }

    private fun Float.spToPx(context: Context): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            this,
            context.resources.displayMetrics
        )
    }

    private fun Float.dpToPx(context: Context): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            context.resources.displayMetrics
        )
    }

    internal fun setContainerClickHandlingEnabled(enabled: Boolean) {
        containerClickHandlingEnabled = enabled
        updateClickability()
    }

    override fun performClick(): Boolean {
        val clickHandler = onNoteClick
        if (clickHandler != null && containerClickHandlingEnabled) {
            clickHandler(this)
            return true
        }
        return super.performClick()
    }

    override fun performLongClick(): Boolean {
        val longClickHandler = onNoteLongClick
        if (longClickHandler != null && containerClickHandlingEnabled) {
            longClickHandler(this)
            return true
        }
        return super.performLongClick()
    }

    private fun updateClickability() {
        val clickEnabled = containerClickHandlingEnabled && onNoteClick != null
        val longClickEnabled = containerClickHandlingEnabled && onNoteLongClick != null
        isClickable = clickEnabled
        isLongClickable = longClickEnabled
        isFocusable = clickEnabled || longClickEnabled
    }
}