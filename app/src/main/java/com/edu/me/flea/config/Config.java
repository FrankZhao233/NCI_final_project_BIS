package com.edu.me.flea.config;


public final class Config {
    public static final String TAG = "flea";
    public static final String FIREBASE_STORAGE_URL = "gs://flea-market-acade.appspot.com";
    public static final String GOODS_FULL_REF_PATH_FMT = "gs://flea-market-acade.appspot.com/goods/%s";
    public static final String AVATAR_FULL_REF_PATH_FMT = "gs://flea-market-acade.appspot.com/avatars/%s";

    public static final class StorageSpace
    {
        public static final String GOODS = "goods/";
        public static final String AVATAR = "avatars/";
    }

    public static final class Page
    {
        public static final String MAIN = "/app/main";
        public static final String LOGIN = "/app/login";
        public static final String REGISTER = "/app/register";
        public static final String CHAT = "/app/chat";
        public static final String PUBLISH = "/app/publish";
        public static final String RESET_PWD = "/app/reset_pwd";
        public static final String GOODS_DETAIL = "/app/goods_detail";
        public static final String CROP_PHOTO = "/app/crop_photo";
        public static final String MY_GOODS_LIST = "/app/my_goods_list";
        public static final String GOODS_AUCTION = "/app/goods_auction";
        public static final String WELFARE_DETAIL = "/app/welfare_detail";
        public static final String MY_WELFARE = "/app/my_welfare";
        public static final String PROFILE_EDITOR = "/app/profile_editor";
        public static final String LANGUAGE_LIST = "/app/language_list";
    }
}
