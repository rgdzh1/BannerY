# BannerY
[![](https://jitpack.io/v/rgdzh1/BannerY.svg)](https://jitpack.io/#rgdzh1/BannerY)
> [原理介绍](https://blog.csdn.net/MoLiao2046/article/details/105918542)
#### 属性介绍
| 属性 |   解释   |
| --- | -------- |
|point_size|指示器大小|
|point_bg|指示器背景|
|point_bottom_margin|指示器距离底边距离|
|desc_bottom_margin|文字描述距离底边距离|
|desc_color|文字描述的文字颜色|
|desc_size|文字描述的文字大小|
|banner_interval|切换图片间隔时间|
|banner_im_scaletype|图片缩放类型|
|banner_size_sclae|图片两边增加Padding,一个页面可以看到3张Banner图|
#### 依赖
```groovy
// 1.
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
// 2.
dependencies {
    implementation 'com.github.rgdzh1:BannerY:0.2.8'
}
```
#### XML布局使用
```xml
<com.yey.library_banner.BannerY
    android:id="@+id/banner"
    android:layout_width="match_parent"
    android:layout_height="200dp"
    app:banner_interval="2000"
    app:banner_scaletype="centerInside"
    app:desc_bottom_margin="10dp"
    app:desc_color="@color/colorAccent"
    app:desc_size="18sp"
    app:point_bottom_margin="10dp"
    app:point_bg="@drawable/point_bg"
    app:point_size="8dp" />

#### 指示器背景资源 point_bg.xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_enabled="false" android:drawable="@drawable/point_normal"/>
    <item android:state_enabled="true" android:drawable="@drawable/point_press"/>
</selector>

#### point_press.xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval">
    <size
        android:width="8dp"
        android:height="8dp" />
    <solid android:color="@color/colorAccent"/>
</shape>

#### point_normal.xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval">
    <size
        android:width="8dp"
        android:height="8dp" />
    <solid android:color="@color/colorPrimary"/>
</shape>
```
#### 代码设置
```java
// 从链接获取图片
bannerY2 = (BannerY) findViewById(R.id.banner2);
ArrayList<String> urlList = new ArrayList<>();
ArrayList<String> descList = new ArrayList<>();
descList.add("第1链接");
descList.add("第2链接");
descList.add("第3链接");
descList.add("第4链接");
urlList.add("https://ww4.sinaimg.cn/bmiddle/005QVDzfly1g9bmtlo07jj30go0gp43w.jpg");
urlList.add("https://ww4.sinaimg.cn/bmiddle/005QVDzfly1g9bmtmaglhj30go0gp77x.jpg");
urlList.add("https://ww4.sinaimg.cn/bmiddle/005QVDzfly1g9bmtmmrofj30go0gpaen.jpg");
urlList.add("https://ww1.sinaimg.cn/bmiddle/005QVDzfly1g9bmtk204sj30go0gpjva.jpg");
bannerY2.setImagesRes(urlList);
bannerY2.setDescList(descList);
bannerY2.setClickBanner(new IClickBanner() {
    @Override
    public void click(int i) {
        Toast.makeText(MainActivity.this, i + "bannerY2 被点击", Toast.LENGTH_LONG).show();
    }
});
// 从资源id获取图片
bannerY = (BannerY) findViewById(R.id.banner);
ArrayList<Integer> integers = new ArrayList<>();
ArrayList<String> strings = new ArrayList<>();
strings.add("111111111");
strings.add("222222222");
strings.add("333333333");
strings.add("444444444");
integers.add(R.mipmap.banner_1);
integers.add(R.mipmap.banner_2);
integers.add(R.mipmap.banner_3);
integers.add(R.mipmap.banner_4);
bannerY.setImagesRes(integers);
bannerY.setDescList(strings);
bannerY.setClickBanner(new IClickBanner() {
    @Override
    public void click(int i) {
        Toast.makeText(MainActivity.this, i + "bannerY 被点击", Toast.LENGTH_LONG).show();
    }
});
```
#### DEMO下载
<img src="下载.png" style="zoom:50%">
