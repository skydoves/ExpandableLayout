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

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.expandablelayoutdemo.databinding.ItemSectionBinding

class ParentAdapter :
  RecyclerView.Adapter<ParentAdapter.ParentViewHolder>(), ChildAdapter.Delegate {

  private val items: ArrayList<SectionItem> = arrayListOf()
  private val childAdapter = ChildAdapter(this)

  fun addSectionItem(sectionItem: SectionItem) {
    items.add(sectionItem)
    notifyItemChanged(items.lastIndex)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
    val binding = ItemSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ParentViewHolder(binding).apply {
      with(binding.root) {
        setOnClickListener { toggleLayout() }
      }
    }
  }

  override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
    val sectionItem = items[position]
    with(holder.binding.expandableLayout) {
      parentLayoutResource = R.layout.item_section_parent
      secondLayoutResource = R.layout.item_section_child
      duration = 200L
      parentLayout.findViewById<TextView>(R.id.title).text = sectionItem.title
      parentLayout.setBackgroundColor(ContextCompat.getColor(context, sectionItem.color))
      secondLayout.findViewById<RecyclerView>(R.id.recyclerViewChild).adapter = childAdapter
      childAdapter.addItemList(sectionItem.itemList)
    }
  }

  override fun onRowItemClick(position: Int, title: String, context: Context) {
    Toast.makeText(context, "position : $position, title: $title", Toast.LENGTH_SHORT).show()
  }

  override fun getItemCount() = items.size

  class ParentViewHolder(val binding: ItemSectionBinding) :
    RecyclerView.ViewHolder(binding.root)
}
