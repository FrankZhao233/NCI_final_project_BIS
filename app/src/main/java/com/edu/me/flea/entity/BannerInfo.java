package com.edu.me.flea.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BannerInfo {
    public String imageUrl;
    public String title;
    public int viewType;

    public BannerInfo(String imageUrl, String title, int viewType) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.viewType = viewType;
    }

    public static List<BannerInfo> getTestData() {
        List<BannerInfo> list = new ArrayList<>();
        list.add(new BannerInfo("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2813661608,4016128055&fm=26&gp=0.jpg", null, 1));
        list.add(new BannerInfo("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2308290721,812335025&fm=26&gp=0.jpg", null, 1));
        list.add(new BannerInfo("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1139733117,2865811610&fm=26&gp=0.jpg", null, 1));
        list.add(new BannerInfo("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1881469000,664628823&fm=26&gp=0.jpg", null, 1));
        list.add(new BannerInfo("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=858380709,3827212174&fm=26&gp=0.jpg", null, 1));
        return list;
    }

    public static List<String> getColors(int size) {
        List<String> list = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            list.add(getRandColor());
        }
        return list;
    }


    public static String getRandColor() {
        String R, G, B;
        Random random = new Random();
        R = Integer.toHexString(random.nextInt(256)).toUpperCase();
        G = Integer.toHexString(random.nextInt(256)).toUpperCase();
        B = Integer.toHexString(random.nextInt(256)).toUpperCase();

        R = R.length() == 1 ? "0" + R : R;
        G = G.length() == 1 ? "0" + G : G;
        B = B.length() == 1 ? "0" + B : B;

        return "#" + R + G + B;
    }
}
