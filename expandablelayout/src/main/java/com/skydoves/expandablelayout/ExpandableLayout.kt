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
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.annotation.Px
import androidx.core.widget.ImageViewCompat
import kotlinx.android.synthetic.main.expandable_layout_parent.view.arrow
import kotlinx.android.synthetic.main.expandable_layout_parent.view.cover

/** An expandable layout that shows a two-level layout with an indicator. */
class ExpandableLayout : FrameLayout {

  lateinit var parentLayout: View
  lateinit var secondLayout: View
  private lateinit var parentFrameLayout: RelativeLayout
  @LayoutRes var parentLayoutResource: Int = R.layout.expandable_layout_parent
    set(value) {
      field = value
      updateExpandableLayout()
    }
  @LayoutRes var secondLayoutResource: Int = R.layout.expandable_layout_child
    set(value) {
      field = value
      updateExpandableLayout()
    }
  var spinnerDrawable: Drawable? = null
    set(value) {
      field = value
      updateSpinner()
    }
  @Px var spinnerSize: Float = dp2Px(24)
    set(value) {
      field = value
      updateSpinner()
    }
  @Px var spinnerMargin: Float = dp2Px(8)
    set(value) {
      field = value
      updateSpinner()
    }
  @ColorInt var spinnerColor: Int = Color.WHITE
    set(value) {
      field = value
      updateSpinner()
    }
  var showSpinner: Boolean = true
    set(value) {
      field = value
      updateSpinner()
    }

  @Px private var secondLayoutHeight: Int = 0

  var isExpanded: Boolean = false
  var duration: Long = 250L
  var expandableAnimation: ExpandableAnimation = ExpandableAnimation.NORMAL
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
      a.getResourceId(R.styleable.ExpandableLayout_expandable_parentLayout,
        this.parentLayoutResource)
    this.secondLayoutResource =
      a.getResourceId(R.styleable.ExpandableLayout_expandable_secondLayout,
        this.secondLayoutResource)
    this.duration =
      a.getInteger(R.styleable.ExpandableLayout_expandable_duration, duration.toInt()).toLong()
    val animation =
      a.getInteger(R.styleable.ExpandableLayout_expandable_animation,
        this.expandableAnimation.value)
    when (animation) {
      ExpandableAnimation.NORMAL.value -> expandableAnimation = ExpandableAnimation.NORMAL
      ExpandableAnimation.ACCELERATE.value -> expandableAnimation = ExpandableAnimation.ACCELERATE
      ExpandableAnimation.BOUNCE.value -> expandableAnimation = ExpandableAnimation.BOUNCE
    }
    this.spinnerDrawable = a.getDrawable(R.styleable.ExpandableLayout_expandable_spinner)
    this.showSpinner =
      a.getBoolean(R.styleable.ExpandableLayout_expandable_showSpinner, showSpinner)
    this.spinnerAnimate =
      a.getBoolean(R.styleable.ExpandableLayout_expandable_spinner_animate, spinnerAnimate)
    this.spinnerRotation =
      a.getInt(R.styleable.ExpandableLayout_expandable_spinner_rotation, spinnerRotation)
    this.spinnerSize =
      a.getDimension(R.styleable.ExpandableLayout_expandable_spinner_size, spinnerSize)
    this.spinnerMargin =
      a.getDimension(R.styleable.ExpandableLayout_expandable_spinner_margin, spinnerMargin)
    this.spinnerColor =
      a.getColor(R.styleable.ExpandableLayout_expandable_spinner_color, spinnerColor)
    this.isExpanded =
      a.getBoolean(R.styleable.ExpandableLayout_expandable_isExpanded, isExpanded)
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
    this.parentFrameLayout.cover.addView(parentLayout)
    this.parentFrameLayout.cover.updateLayoutParams { height = parentLayout.measuredHeight }
    addView(parentFrameLayout)
  }

  private fun updateSecondLayout() {
    secondLayout = inflate(secondLayoutResource)
    secondLayout.visible(false)
    addView(secondLayout)
    secondLayout.post {
      secondLayoutHeight = getMeasuredHeight(secondLayout)
      secondLayout.visible(true)
      with(secondLayout) {
        updateLayoutParams { height = 0 }
        y = parentLayout.measuredHeight.toFloat()
      }
    }
  }

  private fun updateSpinner() {
    with(this.parentFrameLayout.arrow) {
      if (spinnerDrawable != null) {
        setImageDrawable(spinnerDrawable)
      }
      ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(spinnerColor))
      if (showSpinner) {
        visible(true)
      } else {
        visible(false)
      }

      if (layoutParams is MarginLayoutParams) {
        (layoutParams as MarginLayoutParams).rightMargin = spinnerMargin.toInt()
      }
      layoutParams.width = spinnerSize.toInt()
      layoutParams.height = spinnerSize.toInt()
    }
  }

  private fun getMeasuredHeight(view: View): Int {
    var height = view.height
    if (view is ViewGroup) {
      for (i in 0 until view.childCount) {
        val child = view.getChildAt(i)
        if (child is ExpandableLayout) {
          child.post {
            height += getMeasuredHeight(child)
          }
        }
      }
    }
    return height
  }

  /**
   * This function is for supporting Java language.
   * Expand the second layout with indicator animation.
   */
  fun expand() = expand(0)

  /** Expand the second layout with indicator animation. */
  fun expand(@Px expandableHeight: Int = 0) {
    post {
      if (!this.isExpanded) {
        ValueAnimator.ofFloat(0f, 1f).apply {
          duration = this@ExpandableLayout.duration
          applyInterpolator(expandableAnimation)
          addUpdateListener {
            val value = it.animatedValue as Float
            secondLayout.updateLayoutParams {
              height = if (expandableHeight != 0) {
                (expandableHeight * value).toInt() + parentLayout.height
              } else {
                (secondLayoutHeight * value).toInt() + parentLayout.height
              }
            }
            if (spinnerAnimate) {
              parentFrameLayout.arrow.rotation = spinnerRotation * value
            }
          }
          isExpanded = true
          onExpandListener?.onExpand(isExpanded)
          start()
        }
      }
    }
  }

  /** Collapse the second layout with indicator animation. */
  fun collapse() {
    post {
      if (this.isExpanded) {
        ValueAnimator.ofFloat(1f, 0f).apply {
          duration = this@ExpandableLayout.duration
          applyInterpolator(expandableAnimation)
          addUpdateListener {
            val value = it.animatedValue as Float
            secondLayout.updateLayoutParams {
              height =
                ((height - parentLayout.height) * value).toInt() + parentLayout.height
            }
            if (spinnerAnimate) {
              parentFrameLayout.arrow.rotation = spinnerRotation * value
            }
          }
          isExpanded = false
          onExpandListener?.onExpand(isExpanded)
          start()
        }
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

  private fun inflate(@LayoutRes resource: Int): View {
    val inflater: LayoutInflater =
      context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    return inflater.inflate(resource, this, false)
  }

  /** Builder class for creating [ExpandableLayout]. */
  @Suppress("unused")
  @ExpandableLayoutDsl
  class Builder(context: Context) {
    private val expandableLayout = ExpandableLayout(context)

    fun setParentLayoutResource(@LayoutRes value: Int) = apply {
      this.expandableLayout.parentLayoutResource = value
    }

    fun setSecondLayoutResource(@LayoutRes value: Int) = apply {
      this.expandableLayout.secondLayoutResource = value
    }

    fun setSpinnerDrawable(value: Drawable) = apply {
      this.expandableLayout.spinnerDrawable = value
    }

    fun setShowSpinner(value: Boolean) = apply { this.expandableLayout.showSpinner = value }
    fun setSpinnerRotation(value: Int) = apply { this.expandableLayout.spinnerRotation = value }
    fun setSpinnerAnimate(value: Boolean) = apply { this.expandableLayout.spinnerAnimate = value }
    fun setSpinnerSize(@Px value: Float) = apply { this.expandableLayout.spinnerSize = value }
    fun setSpinnerMargin(@Px value: Float) = apply { this.expandableLayout.spinnerMargin = value }
    fun setDuration(value: Long) = apply { this.expandableLayout.duration = value }
    fun setExpandableAnimation(value: ExpandableAnimation) = apply {
      this.expandableLayout.expandableAnimation = value
    }

    fun setOnExpandListener(value: OnExpandListener) = apply {
      this.expandableLayout.onExpandListener = value
    }

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
