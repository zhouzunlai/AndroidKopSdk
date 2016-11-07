package cst.kop;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import platform.cston.httplib.Cston;


/**
 * Created by zhou-pc on 2016/4/18.
 */
public class CstApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);//初始化百度地图SDK
        initImageLoader(this);//初始化第三方图片加载类
        Cston.Auth.init(this);//初始化CstSdk
        Cston.Auth.setDebug(true);
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.memoryCacheSize(2 * 1024 * 1024);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.defaultDisplayImageOptions(DisplayImageOptions.createSimple());
        config.imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000));
        ImageLoader.getInstance().init(config.build());
    }

}


