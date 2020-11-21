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
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.expandablelayoutdemo.databinding.ItemRowBinding

class ChildAdapter(private val delegate: Delegate) : RecyclerView.Adapter<ChildAdapter.ChildViewHolder>() {

  private val items: ArrayList<String> = arrayListOf()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
    val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ChildViewHolder(binding)
  }

  override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
    with(holder.binding) {
      rowTitle.text = items[position]
      root.setOnClickListener {
        delegate.onRowItemClick(position, items[position], root.context)
      }
    }
  }

  fun addItemList(itemList: List<String>) {
    items.clear()
    items.addAll(itemList)
    notifyDataSetChanged()
  }

  override fun getItemCount() = items.size

  class ChildViewHolder(val binding: ItemRowBinding) : RecyclerView.ViewHolder(binding.root)

  interface Delegate {
    fun onRowItemClick(position: Int, title: String, context: Context)
  }
}
