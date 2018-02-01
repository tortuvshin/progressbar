# Progressbar

Android material progressbar library

How to include
---

With gradle: edit your `build.gradle`:
```groovy
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}

dependencies {
    compile 'com.github.techstar-cloud:progressbar:1.0.2'
}
```

Or declare it into your `pom.xml`:

```xml
<repositories>
  <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
    <groupId>com.github.techstar-cloud</groupId>
    <artifactId>progressbar</artifactId>
    <version>1.0.2</version>
</dependency>
```

How to use
---

#### Basic Usage

```js

@Override 
public void onCreate(Bundle savedInstanceState) {
  ...
  TSProgressBar prgLoading = (TSProgressBar) rootView.findViewById(R.id.prgLoading);
}
```

```xml

<cloud.techstar.progressbar.TSProgressBar
    android:layout_width="64dp"
    android:layout_height="64dp"
    app:thickness="@dimen/default_thickness"
    app:inner_padding="@dimen/default_inner_padding"
    app:inner_color="@color/colorPrimary"
    app:outer_color="@color/colorPrimaryDark"
    android:id="@+id/prgLoading"
    android:layout_centerInParent="true"/>

```

    
