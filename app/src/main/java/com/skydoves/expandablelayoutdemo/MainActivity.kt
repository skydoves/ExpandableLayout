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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_second.view.*

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    expandable.setOnExpandListener {
      if (it) {
        toast("expanded")
      } else {
        toast("collapse")
      }
    }
    expandable.parentLayout.setOnClickListener {
      if (expandable.isExpanded) {
        expandable.collapse()
      } else {
        expandable.expand()
      }
    }
    expandable.secondLayout.button0.setOnClickListener { toast("item0 clicked") }
    expandable.secondLayout.button1.setOnClickListener { toast("item1 clicked") }
    expandable.secondLayout.button2.setOnClickListener { toast("item2 clicked") }
    expandable.secondLayout.button3.setOnClickListener { toast("item3 clicked") }

    expandable1.setOnExpandListener {
      if (it) {
        toast("expanded")
      } else {
        toast("collapse")
      }
    }
    expandable1.parentLayout.setOnClickListener {
      if (expandable1.isExpanded) {
        expandable1.collapse()
      } else {
        expandable1.expand(370)
      }
    }
    expandable1.secondLayout.setOnClickListener { toast("clicked the second layout") }

    expandable2.setOnExpandListener {
      if (it) {
        toast("expanded")
      } else {
        toast("collapse")
      }
    }
    expandable2.parentLayout.setOnClickListener {
      if (expandable2.isExpanded) {
        expandable2.collapse()
      } else {
        expandable2.expand()
      }
    }
  }

  private fun toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
  }
}
