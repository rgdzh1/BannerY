package com.yey.bannery;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.yey.library_banner.BannerY;
import com.yey.library_banner.IClickBanner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BannerY bannerY;
    private BannerY bannerY2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        banner1();
        banner2();
    }

    private void banner2() {
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
    }

    private void banner1() {
        bannerY = (BannerY) findViewById(R.id.banner);
        ArrayList<Integer> integers = new ArrayList<>();
        ArrayList<String> strings = new ArrayList<>();
        strings.add("111111111");
        strings.add("222222222");
        strings.add("333333333");
        integers.add(R.mipmap.home_banner_1);
        integers.add(R.mipmap.home_banner_2);
        integers.add(R.mipmap.home_banner_3);
        bannerY.setImagesRes(integers);
        bannerY.setDescList(strings);
        bannerY.setClickBanner(new IClickBanner() {
            @Override
            public void click(int i) {
                Toast.makeText(MainActivity.this, i + "bannerY 被点击", Toast.LENGTH_LONG).show();
            }
        });
    }
}
