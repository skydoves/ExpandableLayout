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

@file:Suppress("unused")

package com.skydoves.expandablelayout

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.annotation.Px
import androidx.core.widget.ImageViewCompat
import com.skydoves.expandablelayout.databinding.ExpandableLayoutFrameBinding

/** An expandable layout that shows a two-level layout with an indicator. */
class ExpandableLayout @JvmOverloads constructor(
  context: Context,
  attributeSet: AttributeSet? = null,
  defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

  lateinit var parentLayout: View
  lateinit var secondLayout: View
  private val binding: ExpandableLayoutFrameBinding =
    ExpandableLayoutFrameBinding.inflate(LayoutInflater.from(context), null, false)

  private var _isExpanded: Boolean = false

  @LayoutRes
  private var _parentLayoutResource: Int = R.layout.expandable_layout_frame

  @LayoutRes
  private var _secondLayoutResource: Int = R.layout.expandable_layout_child

  private var _spinnerDrawable: Drawable? = null

  @Px
  private var _spinnerMargin: Float = dp2Px(14)

  @Px
  private var _spinnerSize: Float = dp2Px(12)

  @ColorInt
  private var _spinnerColor: Int = Color.WHITE

  private var _spinnerGravity: SpinnerGravity = SpinnerGravity.END

  private var _showSpinner: Boolean = true

  var isExpanded: Boolean
    get() = _isExpanded
    private set(value) {
      _isExpanded = value
    }

  var parentLayoutResource: Int
    @LayoutRes get() = _parentLayoutResource
    set(@LayoutRes value) {
      _parentLayoutResource = value
      updateExpandableLayout()
    }

  var secondLayoutResource: Int
    @LayoutRes get() = _secondLayoutResource
    set(@LayoutRes value) {
      _secondLayoutResource = value
      updateExpandableLayout()
    }

  var spinnerDrawable: Drawable?
    get() = _spinnerDrawable
    set(value) {
      _spinnerDrawable = value
      updateSpinner()
    }

  var spinnerSize: Float
    @Px get() = _spinnerSize
    set(@Dp value) {
      _spinnerSize = dp2Px(value)
      updateSpinner()
    }

  var spinnerMargin: Float
    @Px get() = _spinnerMargin
    set(@Dp value) {
      _spinnerMargin = dp2Px(value)
      updateSpinner()
    }

  var spinnerColor: Int
    @ColorInt get() = _spinnerColor
    set(@ColorInt value) {
      _spinnerColor = value
      updateSpinner()
    }

  var spinnerGravity: SpinnerGravity
    get() = _spinnerGravity
    set(value) {
      _spinnerGravity = value
      updateSpinner()
    }

  var showSpinner: Boolean
    get() = _showSpinner
    set(value) {
      _showSpinner = value
      updateSpinner()
    }

  @Px
  private var secondLayoutHeight: Int = 0
  var duration: Long = 250L
  var expandableAnimation: ExpandableAnimation = ExpandableAnimation.NORMAL
  var spinnerRotation: Int = -180
  var spinnerAnimate: Boolean = true

  @JvmField
  var onExpandListener: OnExpandListener? = null

  init {
    if (attributeSet != null) {
      getAttrs(attributeSet, defStyle)
    }
  }

  private fun getAttrs(attributeSet: AttributeSet?, defStyleAttr: Int) {
    val typedArray = context.obtainStyledAttributes(
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
    _isExpanded =
      a.getBoolean(R.styleable.ExpandableLayout_expandable_isExpanded, _isExpanded)
    _parentLayoutResource =
      a.getResourceId(R.styleable.ExpandableLayout_expandable_parentLayout,
        _parentLayoutResource)
    _secondLayoutResource =
      a.getResourceId(R.styleable.ExpandableLayout_expandable_secondLayout,
        _secondLayoutResource)
    _spinnerDrawable = a.getDrawable(R.styleable.ExpandableLayout_expandable_spinner)
    _showSpinner =
      a.getBoolean(R.styleable.ExpandableLayout_expandable_showSpinner, _showSpinner)
    _spinnerSize =
      a.getDimensionPixelSize(R.styleable.ExpandableLayout_expandable_spinner_size,
        _spinnerSize.toInt()).toFloat()
    _spinnerMargin =
      a.getDimensionPixelSize(R.styleable.ExpandableLayout_expandable_spinner_margin,
        _spinnerMargin.toInt()).toFloat()
    _spinnerColor =
      a.getColor(R.styleable.ExpandableLayout_expandable_spinner_color, _spinnerColor)
    val spinnerGravity = a.getInteger(R.styleable.ExpandableLayout_expandable_spinner_gravity,
      _spinnerGravity.value)
    when (spinnerGravity) {
      SpinnerGravity.START.value -> _spinnerGravity = SpinnerGravity.START
      SpinnerGravity.END.value -> _spinnerGravity = SpinnerGravity.END
    }
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
    this.spinnerAnimate =
      a.getBoolean(R.styleable.ExpandableLayout_expandable_spinner_animate, spinnerAnimate)
    this.spinnerRotation =
      a.getInt(R.styleable.ExpandableLayout_expandable_spinner_rotation, spinnerRotation)
  }

  override fun onFinishInflate() {
    super.onFinishInflate()
    updateExpandableLayout()
    if (isExpanded) {
      isExpanded = !isExpanded
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
    this.parentLayout = inflate(parentLayoutResource)
    this.parentLayout.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
    this.binding.cover.addView(parentLayout)
    this.binding.cover.updateLayoutParams { height = parentLayout.measuredHeight }
    addView(binding.root)
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
    with(binding.arrow) {
      visible(showSpinner)
      spinnerDrawable?.let { setImageDrawable(it) }
      ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(spinnerColor))
      parentLayout.post { y = (parentLayout.height / 2f) - (spinnerSize / 2) }
      with(layoutParams as LayoutParams) {
        width = spinnerSize.toInt()
        height = spinnerSize.toInt()
        leftMargin = spinnerMargin.toInt()
        rightMargin = spinnerMargin.toInt()
        gravity = when (spinnerGravity) {
          SpinnerGravity.START -> Gravity.START
          SpinnerGravity.END -> Gravity.END
        }
      }
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
      if (!isExpanded) {
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
              binding.arrow.rotation = spinnerRotation * value
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
      if (isExpanded) {
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
              binding.arrow.rotation = spinnerRotation * value
            }
          }
          isExpanded = false
          onExpandListener?.onExpand(isExpanded)
          start()
        }
      }
    }
  }

  /** sets an [OnExpandListener] to the [ExpandableLayout]. */
  fun setOnExpandListener(onExpandListener: OnExpandListener) {
    this.onExpandListener = onExpandListener
  }

  /** sets an [OnExpandListener] to the [ExpandableLayout] using a lambda. */
  fun setOnExpandListener(block: (Boolean) -> Unit) {
    this.onExpandListener = object : OnExpandListener {
      override fun onExpand(isExpanded: Boolean) {
        block(isExpanded)
      }
    }
  }

  private fun inflate(@LayoutRes resource: Int) =
    LayoutInflater.from(context).inflate(resource, this, false)

  /** Builder class for creating [ExpandableLayout]. */
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
