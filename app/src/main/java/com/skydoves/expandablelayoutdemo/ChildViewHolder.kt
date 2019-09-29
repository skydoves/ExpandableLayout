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

package com.skydoves.expandablelayoutdemo

import android.view.View
import com.skydoves.baserecyclerviewadapter.BaseViewHolder
import kotlinx.android.synthetic.main.item_row.view.*

class ChildViewHolder(
  private val delegate: Delegate,
  view: View
) : BaseViewHolder(view) {

  interface Delegate {
    fun onRowItemClick(position: Int, title: String)
  }

  private lateinit var title: String

  override fun bindData(data: Any) {
    if (data is String) {
      this.title = data
      drawItemUI()
    }
  }

  private fun drawItemUI() {
    itemView.run {
      row_title.text = title
    }
  }

  override fun onClick(p0: View?) = delegate.onRowItemClick(adapterPosition, this.title)

  override fun onLongClick(p0: View?) = false
}
