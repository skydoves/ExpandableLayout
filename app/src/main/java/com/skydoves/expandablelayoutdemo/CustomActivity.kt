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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_custom.*

class CustomActivity : AppCompatActivity() {

  private val adapter by lazy { ParentAdapter() }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_custom)

    recyclerView.adapter = adapter
    adapter.addSectionItem(SectionItem("Title0", R.color.colorPrimary, arrayListOf("item0", "item1", "item2", "item3")))
    adapter.addSectionItem(SectionItem("Title1", R.color.md_yellow_100, arrayListOf("item0", "item1", "item2", "item3")))
    adapter.addSectionItem(SectionItem("Title2", R.color.md_amber_700, arrayListOf("item0", "item1", "item2", "item3")))
    adapter.addSectionItem(SectionItem("Title3", R.color.md_orange_200, arrayListOf("item0", "item1", "item2", "item3")))
    adapter.addSectionItem(SectionItem("Title4", R.color.md_green_200, arrayListOf("item0", "item1", "item2", "item3")))
    adapter.addSectionItem(SectionItem("Title5", R.color.md_blue_100, arrayListOf("item0", "item1", "item2", "item3")))
    adapter.addSectionItem(SectionItem("Title6", R.color.md_blue_200, arrayListOf("item0", "item1", "item2", "item3")))
    adapter.addSectionItem(SectionItem("Title7", R.color.md_purple_100, arrayListOf("item0", "item1", "item2", "item3")))
    adapter.addSectionItem(SectionItem("Title8", R.color.md_purple_200, arrayListOf("item0", "item1", "item2", "item3")))
  }
}
