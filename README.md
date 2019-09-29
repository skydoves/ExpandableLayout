# ExpandableLayout

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=16"><img alt="API" src="https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat"/></a>
    <a href="https://travis-ci.org/skydoves/ExpandableLayout"><img alt="Travis" src="https://travis-ci.org/skydoves/ExpandableLayout.svg?branch=master"/></a>
  <a href="https://skydoves.github.io/libraries/expandablelayout/javadoc/expandablelayout/com.skydoves.expandablelayout/index.html"><img alt="Javadoc" src="https://img.shields.io/badge/Javadoc-ExpandableLayout-yellow"/></a>
</p>

<p align="center">
🦚 An expandable layout that shows a two-level layout with an indicator.
</p>

<p align="center">
<img src="https://user-images.githubusercontent.com/24237865/65829414-49c96d80-e2e0-11e9-8e92-45e1432dc26a.gif" width="32%"/>
<img src="https://user-images.githubusercontent.com/24237865/65830792-bd25ac00-e2ed-11e9-8ce5-890db409ea05.gif" width="32%"/>
</p>

## Including in your project
[![Download](https://api.bintray.com/packages/devmagician/maven/expandablelayout/images/download.svg) ](https://bintray.com/devmagician/maven/expandablelayout/_latestVersion)
[![Jitpack](https://jitpack.io/v/skydoves/ExpandableLayout.svg)](https://jitpack.io/#skydoves/ExpandableLayout)
### Gradle 
Add below codes to your **root** `build.gradle` file (not your module build.gradle file).
```gradle
allprojects {
    repositories {
        jcenter()
    }
}
```
And add a dependency code to your **module**'s `build.gradle` file.
```gradle
dependencies {
    implementation "com.github.skydoves:expandablelayout:1.0.0"
}
```

## Usage
Add following XML namespace inside your XML layout file.

```gradle
xmlns:app="http://schemas.android.com/apk/res-auto"
```


### ExpandableLayout
Here is a basic example of implementing `ExpandableLayout`.

```gradle
<com.skydoves.expandablelayout.ExpandableLayout
  android:id="@+id/expandable"
  android:layout_width="match_parent"
  android:layout_height="wrap_content"
  android:layout_marginTop="30dp"
  app:expandable_duration="300"
  app:expandable_isExpanded="false" // expand the second layout initially or not.
  app:expandable_parentLayout="@layout/layout_parent" // sets the parent layout. 
  app:expandable_secondLayout="@layout/layout_second" // sets the second layout.
  app:expandable_showSpinner="true" // shows the spinner or not.
  app:expandable_spinner="@drawable/ic_arrow_down" // sets the spinner's drawable.
  app:expandable_spinner_animate="true" // animates the spinner when expanding or collapse.
  app:expandable_spinner_margin="14dp" // sets the margin to the spinner.
  app:expandable_spinner_size="32dp" // sets the spinner size.
/>
```

### Create using builder class
We can create an instance of `ExpandableLayout` using the builder class.
```kotlin
val myExpandableLayout = expandableLayout(context) {
  setParentLayoutResource(R.layout.layout_parent)
  setSecondLayoutResource(R.layout.layout_second)
  setShowSpinner(true)
  setSpinnerAnimate(true)
  setSpinnerMargin(12f)
  setSpinnerRotation(90)
  setDuration(200)
  setOnExpandListener { toast("is expanded : $it") }
}
```

### Expand and Collapse
We can expand and collapse using the below methods.
```kotlin
expandablelayout.expand() // expand the second layout with indicator animation.
expandablelayout.collapse() // collapse the second layout with indicator animation.
```

### ParentLayout and SecondLayout
We can get the `parentLayout` and `secondLayout` of the `ExpandableLayout`.<br>
And we can access child views of them.
```kotlin
expandablelayout.parentLayout.setOnClickListener {
  toast("the parent layout is clicked!")
}
expandablelayout.secondLayout.setOnClickListener {
  toast("the second layout is clicked!")
}

// getting child view using findViewById.
expandablelayout.secondLayout.findViewById<Button>(R.id.button0).setOnClickListener { 
    toast("button0 clicked") 
}
// getting child view using android extension.
expandablelayout.secondLayout.button0.setOnClickListener { toast("button0 clicked") }
```

### OnExpandListener
We can listen to the `ExpandableLayout` is expanded or collapsed.
```kotlin
expandablelayout.onExpandListener = object : OnExpandListener {
  override fun onExpand(isExpanded: Boolean) {
    toast("Expanded : $it")
  }
}

// or we can listen using a lambda expression.
expandable.setOnExpandListener {
  if (it) {
    toast("expanded")
  } else {
    toast("collapse")
  }
}
```

## ExpandableLayout Attributes
Attributes | Type | Default | Description
--- | --- | --- | ---
isExpanded | Boolean | false | Expand the second layout initially or not.
parentLayout | layout | default layout | Sets the parent layout.
secondLayout | layout | default layout | Sets the second layout.
duration | Long | 250L | Sets the duration of the spinner animation.
spinner | Drawable | arrow_down | Sets the spinner's drawable.
showSpinner | Boolean | true | Shows the spinner or not.
Spinner_animate | Boolean | true | Animates the spinner when expanding or collapse.
Spinner_rotation | Integer | -180 | Sets the rotation of the spinner animation.
Spinner_size | Dimension | 36dp | Sets the size of the spinner.
Spinner_margin | Dimension | 8dp | Sets the margin of the spinner.

## Find this library useful? :heart:
Support it by joining __[stargazers](https://github.com/skydoves/ExpandableLayout/stargazers)__ for this repository. :star:<br>
And __[follow](https://github.com/skydoves)__ me for my next creations! 🤩

# License
```xml
Copyright 2019 skydoves (Jaewoong Eum)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
