/*
 * Copyright (C) 2019 skydoves
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skydoves.expandablelayout

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.expandable_layout_parent.view.*

/** An expandable layout that shows a two-level layout with an indicator. */
@Suppress("unused")
class ExpandableLayout : FrameLayout {

  lateinit var parentLayout: ViewGroup
  lateinit var secondLayout: ViewGroup
  private lateinit var parentFrameLayout: RelativeLayout
  var parentLayoutResource: Int = R.layout.expandable_layout_parent
    set(value) {
      field = value
      updateExpandableLayout()
    }
  var secondLayoutResource: Int = R.layout.expandable_layout_child
    set(value) {
      field = value
      updateExpandableLayout()
    }
  var spinnerDrawable: Drawable? = null
    set(value) {
      field = value
      updateExpandableLayout()
    }
  var spinnerSize: Float = dp2Px(24)
    set(value) {
      field = value
      updateExpandableLayout()
    }
  var spinnerMargin: Float = dp2Px(8)
    set(value) {
      field = value
      updateExpandableLayout()
    }
  var showSpinner: Boolean = true
    set(value) {
      field = value
      updateExpandableLayout()
    }

  var secondLayoutHeight: Int = 0
  var isExpanded: Boolean = false
  var duration: Long = 250L
  var spinnerRotation: Int = -180
  var spinnerAnimate: Boolean = true
  var onExpandListener: OnExpandListener? = null

  constructor(context: Context) : super(context)

  constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
    getAttrs(attributeSet)
  }

  constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(
    context,
    attributeSet, defStyle
  ) {
    getAttrs(attributeSet, defStyle)
  }

  private fun getAttrs(attributeSet: AttributeSet) {
    val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ExpandableLayout)
    try {
      setTypeArray(typedArray)
    } finally {
      typedArray.recycle()
    }
  }

  private fun getAttrs(attributeSet: AttributeSet, defStyleAttr: Int) {
    val typedArray =
      context.obtainStyledAttributes(
        attributeSet,
        R.styleable.ExpandableLayout,
        defStyleAttr,
        0
      )
    try {
      setTypeArray(typedArray)
    } finally {
      typedArray.recycle()
    }
  }

  private fun setTypeArray(a: TypedArray) {
    this.parentLayoutResource =
      a.getResourceId(R.styleable.ExpandableLayout_expandable_parentLayout, this.parentLayoutResource)
    this.secondLayoutResource =
      a.getResourceId(R.styleable.ExpandableLayout_expandable_secondLayout, this.secondLayoutResource)
    this.duration =
      a.getInteger(R.styleable.ExpandableLayout_expandable_duration, this.duration.toInt()).toLong()
    this.spinnerDrawable = a.getDrawable(R.styleable.ExpandableLayout_expandable_spinner)
    this.showSpinner =
      a.getBoolean(R.styleable.ExpandableLayout_expandable_showSpinner, this.showSpinner)
    this.spinnerAnimate =
      a.getBoolean(R.styleable.ExpandableLayout_expandable_spinner_animate, this.spinnerAnimate)
    this.spinnerRotation =
      a.getInt(R.styleable.ExpandableLayout_expandable_spinner_rotation, this.spinnerRotation)
    this.spinnerSize =
      a.getDimension(R.styleable.ExpandableLayout_expandable_spinner_size, this.spinnerSize)
    this.spinnerMargin =
      a.getDimension(R.styleable.ExpandableLayout_expandable_spinner_margin, this.spinnerMargin)
    this.isExpanded =
      a.getBoolean(R.styleable.ExpandableLayout_expandable_isExpanded, this.isExpanded)
  }

  override fun onFinishInflate() {
    super.onFinishInflate()
    updateExpandableLayout()
    if (this.isExpanded) {
      this.isExpanded = !this.isExpanded
      expand()
    }
  }

  private fun updateExpandableLayout() {
    removeAllViews()
    updateParentLayout()
    updateSecondLayout()
    updateSpinner()
  }

  private fun updateParentLayout() {
    this.parentFrameLayout = inflate(R.layout.expandable_layout_parent) as RelativeLayout
    this.parentLayout = inflate(parentLayoutResource)
    this.parentLayout.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
    this.parentFrameLayout.cover.addView(this.parentLayout)
    this.parentFrameLayout.cover.updateLayoutParams { height = parentLayout.measuredHeight }
    addView(this.parentFrameLayout)
  }

  private fun updateSecondLayout() {
    this.secondLayout = inflate(this.secondLayoutResource)
    with(this.secondLayout) {
      updateLayoutParams { height = 0 }
      y = parentLayout.measuredHeight.toFloat()
    }
    addView(this.secondLayout)
    setMeasureHeight(secondLayout)
  }

  private fun updateSpinner() {
    with(this.parentFrameLayout.arrow) {
      if (spinnerDrawable != null) {
        setImageDrawable(spinnerDrawable)
      }
      if (showSpinner) {
        visible(true)
      } else {
        visible(false)
      }

      if (layoutParams is MarginLayoutParams) {
        (layoutParams as MarginLayoutParams).setMargins(0, 0, spinnerMargin.toInt(), 0)
      }
      layoutParams.width = spinnerSize.toInt()
      layoutParams.height = spinnerSize.toInt()
    }
  }

  private fun setMeasureHeight(parent: ViewGroup): Int {
    parent.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
    var height = parent.height
    height += getTopBottomPaddingSize(parent)
    height += getTopBottomMarginSize(parent)
    for (i in 0 until parent.childCount) {
      val child = parent.getChildAt(i)
      child.post {
        child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        height += when (child) {
          is ExpandableLayout -> child.height + child.secondLayoutHeight
          is ViewGroup -> setMeasureHeight(child)
          else -> child.measuredHeight
        }
        height += getTopBottomPaddingSize(child)
        height += getTopBottomMarginSize(child)
        if (height > this.secondLayoutHeight) {
          this.secondLayoutHeight = height
        }
      }
    }
    return height
  }

  private fun getTopBottomPaddingSize(view: View): Int {
    return view.paddingTop + view.paddingBottom
  }

  private fun getTopBottomMarginSize(view: View): Int {
    var margin = 0
    if (view.layoutParams is MarginLayoutParams) {
      val marginLayoutParams = (view.layoutParams as MarginLayoutParams)
      margin += marginLayoutParams.topMargin + marginLayoutParams.bottomMargin
    }
    return margin
  }

  /** Expand the second layout with indicator animation. */
  fun expand() {
    post {
      if (!this.isExpanded) {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = this.duration
        animator.addUpdateListener {
          val value = it.animatedValue as Float
          this.secondLayout.updateLayoutParams {
            height = (secondLayoutHeight * value).toInt() + parentLayout.height
          }
          if (this.spinnerAnimate) {
            this.parentFrameLayout.arrow.rotation = this.spinnerRotation * value
          }
        }
        this.isExpanded = true
        this.onExpandListener?.onExpand(this.isExpanded)
        animator.start()
      }
    }
  }

  /** Collapse the second layout with indicator animation. */
  fun collapse() {
    post {
      if (this.isExpanded) {
        val animator = ValueAnimator.ofFloat(1f, 0f)
        animator.duration = this.duration
        animator.addUpdateListener {
          val value = it.animatedValue as Float
          this.secondLayout.updateLayoutParams {
            height =
              ((height - parentLayout.height) * value).toInt() + parentLayout.height
          }
          if (this.spinnerAnimate) {
            this.parentFrameLayout.arrow.rotation = this.spinnerRotation * value
          }
        }
        this.isExpanded = false
        this.onExpandListener?.onExpand(this.isExpanded)
        animator.start()
      }
    }
  }

  /** Sets the [OnExpandListener] using a lambda. */
  fun setOnExpandListener(block: (Boolean) -> Unit) {
    this.onExpandListener = object : OnExpandListener {
      override fun onExpand(isExpanded: Boolean) {
        block(isExpanded)
      }
    }
  }

  private fun inflate(resource: Int): ViewGroup {
    val inflater: LayoutInflater =
      context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val view = inflater.inflate(resource, this, false)
    if (view is ViewGroup) {
      return view
    } else {
      throw IllegalArgumentException("the layout resource should be wrapped a ViewGroup.")
    }
  }

  /** Builder class for creating [ExpandableLayout]. */
  @ExpandableLayoutDsl
  class Builder(context: Context) {
    private val expandableLayout = ExpandableLayout(context)

    fun setParentLayoutResource(value: Int) = apply { this.expandableLayout.parentLayoutResource = value }
    fun setSecondLayoutResource(value: Int) = apply { this.expandableLayout.secondLayoutResource = value }
    fun setSpinnerDrawable(value: Drawable) = apply { this.expandableLayout.spinnerDrawable = value }
    fun setShowSpinner(value: Boolean) = apply { this.expandableLayout.showSpinner = value }
    fun setSpinnerRotation(value: Int) = apply { this.expandableLayout.spinnerRotation = value }
    fun setSpinnerAnimate(value: Boolean) = apply { this.expandableLayout.spinnerAnimate = value }
    fun setSpinnerSize(value: Float) = apply { this.expandableLayout.spinnerSize = value }
    fun setSpinnerMargin(value: Float) = apply { this.expandableLayout.spinnerMargin = value }
    fun setDuration(value: Long) = apply { this.expandableLayout.duration = value }
    fun setOnExpandListener(value: OnExpandListener) = apply { this.expandableLayout.onExpandListener = value }
    fun setOnExpandListener(block: (Boolean) -> Unit) = apply {
      this.expandableLayout.onExpandListener = object : OnExpandListener {
        override fun onExpand(isExpanded: Boolean) {
          block(isExpanded)
        }
      }
    }

    fun build() = this.expandableLayout
  }
}
