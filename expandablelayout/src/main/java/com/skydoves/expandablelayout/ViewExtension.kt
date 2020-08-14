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

import android.view.View
import android.view.ViewGroup

/** makes visible or invisible a View align the value parameter. */
internal fun View.visible(value: Boolean) {
  if (value) {
    this.visibility = View.VISIBLE
  } else {
    this.visibility = View.INVISIBLE
  }
}

/** dp size to px size. */
internal fun View.dp2Px(dp: Int): Float {
  val scale = resources.displayMetrics.density
  return dp * scale
}

/** dp size to px size. */
internal fun View.dp2Px(dp: Float): Float {
  val scale = resources.displayMetrics.density
  return dp * scale
}

/**
 * Executes [block] with the View's layoutParams and reassigns the layoutParams with the
 * updated version.
 *
 * @see View.getLayoutParams
 * @see View.setLayoutParams
 **/
internal inline fun View.updateLayoutParams(block: ViewGroup.LayoutParams.() -> Unit) {
  updateLayoutParam(this, block)
}

/**
 * Executes [block] with a typed version of the View's layoutParams and reassigns the
 * layoutParams with the updated version.
 *
 * @see View.getLayoutParams
 * @see View.setLayoutParams
 **/
private inline fun <reified T : ViewGroup.LayoutParams> updateLayoutParam(
  view: View,
  block: T.() -> Unit
) {
  val params = view.layoutParams as T
  block(params)
  view.layoutParams = params
}
