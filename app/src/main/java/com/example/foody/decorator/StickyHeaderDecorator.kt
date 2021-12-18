package com.example.foody.decorator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foody.R
import com.example.foody.extension.toDp
import com.example.foody.extension.toSp
import kotlin.math.min

class StickyHeaderDecorator(context: Context) : RecyclerView.ItemDecoration() {

    var hideCategoryHeader: ((isHide: Boolean) -> Unit)? = null

    var updateCategoryHeader: ((categoryName: String) -> Unit)? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val colorBg = context.resources.getColor(R.color.mediumGray)
    private val colorText = context.resources.getColor(R.color.itemColor)

    private val categoryList = mutableListOf<String>()
    private val categorySet = mutableSetOf<String>()//recording how many sets of subtitles we have
    val categoryHeaderMap = mutableMapOf<String, Int>()//Record the start position of each set of subtitles
    private var categoryName = ""

    fun setCategoryList(value: List<String>) {
        categoryList.clear()
        categoryList.addAll(value)
        categorySet.clear()
        categorySet.addAll(value)

        //If there is only one set, the sticky title is hidden
        if (categorySet.size > 1) {
            hideCategoryHeader?.invoke(false)
        } else {
            hideCategoryHeader?.invoke(true)
        }
    }

    //Set text attributes
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorText
        textSize = 18.toSp()
    }

    private val headerMarginStart = 36.toDp() //The distance between the subtitle content and the left
    private val headerSpaceHeight = 15.toDp() //Add a gap height for each subtitle corresponding to the last item
    private val headerBackgroundHeight = 40.toDp()
    private val headerBackgroundRadius = 10.toDp()

    
    // set item layout（Leave room for the draw method to draw）
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (isHideInventoryHeader()) return
        val adapterPosition = parent.getChildAdapterPosition(view)
        if (adapterPosition == RecyclerView.NO_POSITION) {
            return
        }
        //Top 
        if (isFirstOfGroup(adapterPosition)) {
            outRect.top = headerBackgroundHeight.toInt()
            categoryHeaderMap[categoryList[adapterPosition]] = adapterPosition
        }
        //Bottom 
        if (isEndOfGroup(adapterPosition)) {
            outRect.bottom = headerSpaceHeight.toInt()
        }
    }

    //The background can be drawn in this method
    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (isHideInventoryHeader()) return
        val count = parent.childCount
        if (count == 0) {
            return
        }
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val adapterPosition = parent.getChildAdapterPosition(child)
            if (isFirstOfGroup(adapterPosition)) {
                val left = child.left.toFloat()
                val right = child.right.toFloat()
                val top = child.top.toFloat() - headerBackgroundHeight
                val bottom = child.top.toFloat()
                val radius = headerBackgroundRadius
                paint.color = colorBg
                //draw background
                canvas.drawRoundRect(
                    left, top, right, bottom, radius,
                    radius, paint
                )
            }
        }
    }

    //Leave space for the draw method to draw content, Sticky titles are also set here
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (isHideInventoryHeader()) return
        val count = parent.childCount
        if (count == 0) {
            return
        }
        //Draw text on each background
        drawHeaderTextIndex(canvas, parent)

        //draw sticky title
        drawStickyTimestampIndex(canvas, parent)
    }

    private fun drawHeaderTextIndex(canvas: Canvas, parent: RecyclerView) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val adapterPosition = parent.getChildAdapterPosition(child)
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return
            }
            if (isFirstOfGroup(adapterPosition)) {
                val categoryName = categoryList[adapterPosition]
                val start = child.left + headerMarginStart
                val fontMetrics = textPaint.fontMetrics
                //Calculate the height of the text itself
                val fontHeight = fontMetrics.bottom - fontMetrics.top
                val baseline =
                    child.top.toFloat() - (headerBackgroundHeight - fontHeight) / 2 - fontMetrics.bottom
                if (categoryName != null) {
                    canvas.drawText(categoryName.toUpperCase(), start, baseline, textPaint)
                }
            }
        }
    }

    private fun drawStickyTimestampIndex(canvas: Canvas, parent: RecyclerView) {
        val layoutManager = parent.layoutManager as LinearLayoutManager
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        if (firstVisiblePosition != RecyclerView.NO_POSITION) {
            val firstVisibleChildView =
                parent.findViewHolderForAdapterPosition(firstVisiblePosition)?.itemView
            firstVisibleChildView?.let { child ->
                val firstChild = parent.getChildAt(0)
                val left = firstChild.left.toFloat()
                val right = firstChild.right.toFloat()
                val top = 0.toFloat()
                val bottom = headerBackgroundHeight
                val radius = headerBackgroundRadius
                paint.color = colorBg

                val name = categoryList[firstVisiblePosition]
                if (categoryName != name) {
                    categoryName = name
                    // monitor the title currently scrolled to
                    categoryName?.let { name ->
                        updateCategoryHeader?.invoke(name)
                    }
                }
                val start = child.left + headerMarginStart
                //Calculate the height of the text itself
                val fontMetrics = textPaint.fontMetrics
                val fontHeight = fontMetrics.bottom - fontMetrics.top
                val baseline =
                    headerBackgroundHeight - (headerBackgroundHeight - fontHeight) / 2 - fontMetrics.bottom

                var upwardBottom = bottom
                var upwardBaseline = baseline
                // The next group will reach the top immediately
                if (isFirstOfGroup(firstVisiblePosition + 1)) {
                    upwardBottom = min(child.bottom.toFloat() + headerSpaceHeight, bottom)
                    if (child.bottom.toFloat() + headerSpaceHeight < headerBackgroundHeight) {
                        upwardBaseline = baseline * (child.bottom.toFloat() + headerSpaceHeight)/headerBackgroundHeight
                    }
                }
                //Draw a sticky title background
                canvas.drawRoundRect(left, top, right, upwardBottom, radius, radius, paint)
                //Draw a sticky title
                if (categoryName != null) {
                    canvas.drawText(categoryName.toUpperCase(), start, upwardBaseline, textPaint)
                }
            }
        }
    }

    //check if it is the first item in each group
    private fun isFirstOfGroup(adapterPosition: Int): Boolean {
        return adapterPosition == 0 || categoryList[adapterPosition] != categoryList[adapterPosition - 1]
    }

    //check if it is the last item in each group
    private fun isEndOfGroup(adapterPosition: Int): Boolean {
        if (adapterPosition + 1 == categoryList.size) return true
        return categoryList[adapterPosition] != categoryList[adapterPosition + 1]
    }

    //If there is only one group, the title is hidden
    private fun isHideInventoryHeader(): Boolean {
        return categorySet.size <= 1 || categoryList.isNullOrEmpty()
    }
}
