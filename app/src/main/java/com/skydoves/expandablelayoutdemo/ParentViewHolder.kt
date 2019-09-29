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
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.skydoves.baserecyclerviewadapter.BaseViewHolder
import kotlinx.android.synthetic.main.item_section.view.*
import kotlinx.android.synthetic.main.item_section_child.view.*
import kotlinx.android.synthetic.main.item_section_parent.view.*

class ParentViewHolder(view: View) : BaseViewHolder(view), ChildViewHolder.Delegate {

  private lateinit var sectionItem: SectionItem
  private val childAdapter = ChildAdapter(this)

  override fun bindData(data: Any) {
    if (data is SectionItem) {
      this.sectionItem = data
      drawItemUI()
    }
  }

  private fun drawItemUI() {
    if (!sectionItem.initailized) {
      itemView.run {
        with(expandableLayout) {
          parentLayoutResource = R.layout.item_section_parent
          secondLayoutResource = R.layout.item_section_child
          duration = 200L
          parentLayout.title.text = sectionItem.title
          parentLayout.setBackgroundColor(ContextCompat.getColor(context, sectionItem.color))
          secondLayout.recyclerViewChild.adapter = childAdapter
          childAdapter.addItemList(sectionItem.itemList)
          sectionItem.initailized = true
        }
      }
    }
  }

  override fun onClick(p0: View?) {
    with(itemView.expandableLayout) {
      if (isExpanded) {
        collapse()
      } else {
        expand()
      }
    }
  }

  override fun onLongClick(p0: View?) = false

  override fun onRowItemClick(position: Int, title: String) =
    Toast.makeText(context(), "position : $position, title: $title", Toast.LENGTH_SHORT).show()
}
