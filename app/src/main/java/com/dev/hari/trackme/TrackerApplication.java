package com.dev.hari.trackme;

import android.app.Application;
import android.content.Context;

import com.dev.hari.trackme.utility.FontsOverride;


public class TrackerApplication extends Application {
    public static Context appContext;

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);
//    }
    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    @Override
    public void onCreate() {
//        if (Common.D && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
//        }
        super.onCreate();
        appContext = this;
//        FontsOverride.setDefaultFont(this, "DEFAULT", "Roboto-Medium.ttf");
//        FontsOverride.setDefaultFont(this, "MONOSPACE", "Roboto-Light.ttf");
//        FontsOverride.setDefaultFont(this, "SERIF", "Roboto-Thin.ttf");
//        FontsOverride.setDefaultFont(this, "SANS_SERIF", "Roboto-Thin.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/Calibri.ttf");

//        initImageLoader(appContext);
    }

//    public static void initImageLoader(Context context) {
//
//        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.img_loading)
//                .showImageForEmptyUri(R.drawable.img_loading)
//                .showImageOnFail(R.drawable.img_loading)
//                .cacheInMemory(true)
//                .cacheOnDisk(true)
//                .displayer(new FadeInBitmapDisplayer(500))
//                .imageScaleType(ImageScaleType.EXACTLY)
//                .resetViewBeforeLoading(true)
//                .considerExifParams(true)
//                .bitmapConfig(Bitmap.Config.RGB_565)
////                .displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
//                .build();
//
//        // This configuration tuning is custom. You can tune every option, you may tune some of them,
//        // or you can create default configuration by
//        //  ImageLoaderConfiguration.createDefault(this);
//        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
//        config.threadPriority(Thread.NORM_PRIORITY - 2);
//        config.denyCacheImageMultipleSizesInMemory();
//        config.defaultDisplayImageOptions(options);
//        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
//        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
//        config.tasksProcessingOrder(QueueProcessingType.LIFO);
//        if(Common.D) config.writeDebugLogs();
//
//        // Initialize ImageLoader with configuration.
//        ImageLoader.getInstance().init(config.build());
//    }
}
