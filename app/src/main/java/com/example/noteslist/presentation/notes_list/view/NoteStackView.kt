package com.example.noteslist.presentation.notes_list.view

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.animation.PathInterpolator
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import androidx.core.view.isEmpty
import com.example.noteslist.R

class NoteStackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private companion object {
        private const val BASE_DURATION_MS = 200L
        private const val STEP_DURATION_MS = 40L
        private const val MAX_DURATION_MS = 800L
        private const val ITEM_START_DELAY_MS = 20L
        private const val BUTTON_SHOW_DELAY_MS = 100L
        private const val BUTTON_ANIM_DURATION_MS = 200L
    }

    private var stackSpacing = 10f.dpToPx(context)
    private var stackMaxVisible = 3
    private var isExpanded = false
    private var isExpandAnimationRunning = false
    private var onExpandedStateChanged: ((Boolean) -> Unit)? = null

    private var expandedSpacing = 0f
    private var finalizeExpandRunnable: Runnable? = null
    private var showButtonRunnable: Runnable? = null

    private val motionInterpolator = PathInterpolator(0.4f, 0.1f, 0.2f, 1f)

    private val collapseButton = TextView(context).apply {
        text = context.getString(R.string.collapse)
        textSize = 14f
        setTypeface(null, Typeface.BOLD)
        setTextColor(Color.DKGRAY)
        setBackgroundColor(Color.LTGRAY)
        gravity = Gravity.CENTER
        val vertPad = 12f.dpToPx(context).toInt()
        val horPad = 16f.dpToPx(context).toInt()
        setPadding(horPad, vertPad, horPad, vertPad)
        visibility = GONE
        setOnClickListener { toggleExpanded() }
    }

    init {
        clipChildren = false
        clipToPadding = false

        context.withStyledAttributes(attrs, R.styleable.NoteStackView, defStyleAttr, 0) {
            stackSpacing = getDimension(R.styleable.NoteStackView_stackSpacing, 20f.dpToPx(context))
            stackMaxVisible = getInteger(R.styleable.NoteStackView_stackMaxVisible, 3)
        }

        addView(collapseButton)
    }

    private fun noteChildren(): Sequence<View> =
        children.filter { it !== collapseButton }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val availableWidth = width - paddingLeft - paddingRight

        val childWidthSpec = MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.EXACTLY)
        val childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)

        var maxChildElevation = 0f
        noteChildren().forEach { child ->
            child.measure(childWidthSpec, childHeightSpec)
            if (child is NoteView) {
                maxChildElevation = maxOf(maxChildElevation, child.elevation)
            }
        }

        val shadowSize = (maxChildElevation * 1.5f)
        expandedSpacing = maxOf(stackSpacing, shadowSize * 2)
        val bottomShadowPadding = shadowSize.toInt()

        var totalHeight: Int

        val canExpand = canExpandStack()

        if ((isExpanded || isExpandAnimationRunning) && canExpand) {
            totalHeight = 0
            noteChildren().forEach { child ->
                totalHeight += child.measuredHeight + expandedSpacing.toInt()
            }
            collapseButton.measure(childWidthSpec, childHeightSpec)
            totalHeight += collapseButton.measuredHeight
        } else {
            val visibleNotes = noteChildren().toList().takeLast(stackMaxVisible)
            totalHeight = 0
            visibleNotes.forEachIndexed { index, child ->
                totalHeight = maxOf(
                    totalHeight,
                    child.measuredHeight + (index * stackSpacing).toInt()
                )
            }
        }

        totalHeight += bottomShadowPadding

        val height = resolveSize(totalHeight + paddingTop + paddingBottom, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (isExpandAnimationRunning) {
            return
        }

        val allNotes = noteChildren().toList()
        val canExpand = canExpandStack()

        if (isExpanded && canExpand) {
            collapseButton.visibility = VISIBLE
            var currentTop = paddingTop

            for (child in allNotes) {
                child.visibility = VISIBLE
                child.scaleX = 1f
                child.scaleY = 1f
                child.alpha = 1f
                child.translationZ = 0f

                val childLeft = paddingLeft
                val childRight = childLeft + child.measuredWidth
                val childBottom = currentTop + child.measuredHeight

                child.layout(childLeft, currentTop, childRight, childBottom)
                currentTop = childBottom + expandedSpacing.toInt()
            }

            val btnLeft = paddingLeft
            val btnRight = btnLeft + collapseButton.measuredWidth
            val btnBottom = currentTop + collapseButton.measuredHeight
            collapseButton.layout(btnLeft, currentTop, btnRight, btnBottom)

        } else {
            collapseButton.visibility = GONE
            val visibleNotes = allNotes.takeLast(stackMaxVisible)
            val visibleSet = visibleNotes.toSet()

            for (child in allNotes) {
                if (child !in visibleSet) {
                    child.visibility = GONE
                }
            }

            visibleNotes.forEachIndexed { index, child ->
                child.visibility = VISIBLE
                val offset = (index * stackSpacing).toInt()

                val childLeft = paddingLeft
                val childTop = paddingTop + offset
                val childRight = childLeft + child.measuredWidth
                val childBottom = childTop + child.measuredHeight

                child.layout(childLeft, childTop, childRight, childBottom)

                val depth = visibleNotes.size - 1 - index
                child.scaleX = 1f - depth * 0.05f
                child.scaleY = 1f - depth * 0.05f
                child.alpha = 1f - depth * 0.15f
                child.translationZ = index.toFloat()
            }
        }

        updateTopClickListener()
    }

    private fun updateTopClickListener() {
        if (isEmpty()) return

        val canExpand = canExpandStack()
        if (!canExpand) {
            setOnClickListener(null)
            noteChildren().filterIsInstance<NoteView>().forEach {
                it.setContainerClickHandlingEnabled(true)
            }
            return
        }

        if (isExpandAnimationRunning) {
            setOnClickListener(null)
            noteChildren().filterIsInstance<NoteView>().forEach {
                it.setContainerClickHandlingEnabled(false)
            }
            return
        }

        if (!isExpanded) {
            setOnClickListener { toggleExpanded() }
            noteChildren().filterIsInstance<NoteView>().forEach {
                it.setContainerClickHandlingEnabled(false)
            }
        } else {
            setOnClickListener(null)
            noteChildren().filterIsInstance<NoteView>().forEach {
                it.setContainerClickHandlingEnabled(true)
            }
        }
    }

    private fun toggleExpanded() {
        if (!canExpandStack()) return

        if (isExpanded) {
            cancelExpandAnimationState()
            isExpanded = false
            onExpandedStateChanged?.invoke(false)
            requestLayout()
            return
        }

        startExpandAnimation()
    }

    private fun startExpandAnimation() {
        val allNotes = noteChildren().toList()
        if (allNotes.isEmpty()) return
        if (isExpandAnimationRunning) return
        if (width == 0 || height == 0) {
            post { startExpandAnimation() }
            return
        }

        cancelExpandAnimationState()
        isExpandAnimationRunning = true
        // Expand container height immediately so surrounding list items move out of the way.
        requestLayout()

        post {
            if (!isExpandAnimationRunning) return@post
            runExpandAnimation(allNotes)
        }

        updateTopClickListener()
    }

    private fun runExpandAnimation(allNotes: List<View>) {
        if (allNotes.isEmpty()) return

        collapseButton.apply {
            alpha = 0f
            scaleX = 0.7f
            scaleY = 0.7f
            visibility = GONE
            animate().cancel()
        }

        val childLeft = paddingLeft
        val expandedSpacingInt = expandedSpacing.toInt()
        val collapsedAnchorOffset = ((stackMaxVisible - 1).coerceAtLeast(0) * stackSpacing).toInt()

        val targets = LinkedHashMap<View, Int>(allNotes.size)
        var currentTop = paddingTop
        allNotes.forEach { child ->
            targets[child] = currentTop
            currentTop += child.measuredHeight + expandedSpacingInt
        }

        allNotes.forEachIndexed { index, child ->
            val collapsedTop = paddingTop + minOf(index, stackMaxVisible - 1).coerceAtLeast(0) * stackSpacing.toInt()
            val anchorTop = if (index >= stackMaxVisible) {
                paddingTop + collapsedAnchorOffset
            } else {
                collapsedTop
            }
            val childRight = childLeft + child.measuredWidth
            val childBottom = anchorTop + child.measuredHeight

            child.visibility = VISIBLE
            child.layout(childLeft, anchorTop, childRight, childBottom)
            child.translationY = 0f
            child.scaleX = 1f
            child.scaleY = 1f
            child.alpha = 1f
            child.translationZ = index.toFloat()
            child.animate().cancel()
        }

        val orderForWave = allNotes.asReversed()
        val itemDuration = minOf(MAX_DURATION_MS, BASE_DURATION_MS + allNotes.size * STEP_DURATION_MS)

        orderForWave.forEachIndexed { i, child ->
            val targetTop = targets[child] ?: child.top
            val deltaY = (targetTop - child.top).toFloat()
            child.animate()
                .translationY(deltaY)
                .setStartDelay(i * ITEM_START_DELAY_MS)
                .setDuration(itemDuration)
                .setInterpolator(motionInterpolator)
                .start()
        }

        val lastElementFinishMs = (orderForWave.size - 1) * ITEM_START_DELAY_MS + itemDuration

        showButtonRunnable = Runnable {
            if (!isExpandAnimationRunning) return@Runnable
            collapseButton.visibility = VISIBLE
            collapseButton.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(BUTTON_ANIM_DURATION_MS)
                .setInterpolator(motionInterpolator)
                .start()
        }.also { postDelayed(it, lastElementFinishMs + BUTTON_SHOW_DELAY_MS) }

        finalizeExpandRunnable = Runnable {
            allNotes.forEach { child ->
                child.animate().cancel()
                child.translationY = 0f
            }
            isExpandAnimationRunning = false
            isExpanded = true
            onExpandedStateChanged?.invoke(true)
            requestLayout()
        }.also {
            postDelayed(
                it,
                lastElementFinishMs + BUTTON_SHOW_DELAY_MS + BUTTON_ANIM_DURATION_MS
            )
        }
    }

    private fun cancelExpandAnimationState() {
        showButtonRunnable?.let { removeCallbacks(it) }
        finalizeExpandRunnable?.let { removeCallbacks(it) }
        showButtonRunnable = null
        finalizeExpandRunnable = null

        noteChildren().forEach { child ->
            child.animate().cancel()
            child.translationY = 0f
        }
        collapseButton.animate().cancel()
        isExpandAnimationRunning = false
    }

    fun isExpandedState(): Boolean = isExpanded

    fun setExpandedState(expanded: Boolean) {
        cancelExpandAnimationState()
        isExpanded = expanded && canExpandStack()
        onExpandedStateChanged?.invoke(isExpanded)
        requestLayout()
    }

    fun setOnExpandedStateChangedListener(listener: ((Boolean) -> Unit)? = { _ -> }) {
        onExpandedStateChanged = listener
    }

    fun addNote(note: NoteView) {
        val index = indexOfChild(collapseButton)
        addView(note, if (index >= 0) index else childCount)
        sortNotesByDate()
        cancelExpandAnimationState()
        requestLayout()
    }

    fun removeNote(note: NoteView) {
        cancelExpandAnimationState()
        removeView(note)
        requestLayout()
    }

    private fun sortNotesByDate() {
        val notesByDate = noteChildren()
            .filterIsInstance<NoteView>()
            .sortedBy { it.timestamp }
            .toList()

        notesByDate.forEach { removeViewInLayout(it) }
        removeViewInLayout(collapseButton)

        notesByDate.forEach { addViewInLayout(it, -1, it.layoutParams ?: generateDefaultLayoutParams()) }
        addViewInLayout(collapseButton, -1, collapseButton.layoutParams ?: generateDefaultLayoutParams())
    }

    private fun canExpandStack(): Boolean {
        return noteChildren().count() > 1
    }

    private fun Float.dpToPx(context: Context): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            context.resources.displayMetrics
        )
    }

    fun setStackSpacingDp(value: Float) {
        stackSpacing = value.dpToPx(context)
        cancelExpandAnimationState()
        requestLayout()
    }

    fun setStackMaxVisible(value: Int) {
        stackMaxVisible = value.coerceAtLeast(1)
        cancelExpandAnimationState()
        requestLayout()
    }
}