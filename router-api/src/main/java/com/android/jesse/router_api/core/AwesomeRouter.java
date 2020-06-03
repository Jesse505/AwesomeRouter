package com.android.jesse.router_api.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import com.android.jesse.router_annotation.model.RouteMeta;
import com.android.jesse.router_api.template.IProvider;
import com.android.jesse.router_api.template.IRouteGroup;
import com.android.jesse.router_api.utils.Consts;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

/**
 * 路由操作类
 */
public final class AwesomeRouter {

    // 路由组名
    private String group;
    // 路由详细路径
    private String path;

    // Lru缓存，key:组名, value:路由组Group加载接口
    private LruCache<String, IRouteGroup> groupCache;
    // Lru缓存，key:路径, value:RouteMeta
    private LruCache<String, RouteMeta> pathCache;
    //Lru缓存，key:路径，value:IProvider
    private LruCache<String, IProvider> iProviderCache;

    private volatile static AwesomeRouter instance;

    private AwesomeRouter() {
        groupCache = new LruCache<>(20);
        pathCache = new LruCache<>(200);
        iProviderCache = new LruCache<>(200);
    }

    public static AwesomeRouter getInstance() {
        if (null == instance) {
            synchronized (AwesomeRouter.class) {
                if (null == instance) {
                    instance = new AwesomeRouter();
                }
            }
        }
        return instance;
    }


    public PostCard build(String path) {
        // @ARouter注解中的path值，必须要以 / 开头（模仿阿里Arouter规范）
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new IllegalArgumentException("未按规范配置，如：/app/MainActivity");
        }

        group = subFromPath2Group(path);

        // 检查后再赋值
        this.path = path;

        return new PostCard();
    }

    Object navigation(@NonNull final Context context, final PostCard postCard) {

        //根据path地址从缓存里拿RouteMeta对象
        RouteMeta routeMeta = pathCache.get(path);

        //如果缓存里没有对应的RouteMeta对象
        if (null == routeMeta) {
            //对应的group类的全路径
            String groupClassName = Consts.PACKAGE_OF_GENERATE_FILE + Consts.GROUP_FILE_PREFIX_NAME + group;
            //根据group从缓存里拿IRouteGroup对象
            IRouteGroup routeGroup = groupCache.get(group);
            //如果缓存中没有拿到IRouteGroup对象，就通过group的全路径去加载类，并将IRouteGroup对象加入到缓存中
            if (routeGroup == null) {
                Class<?> clazz = null;
                try {
                    clazz = Class.forName(groupClassName);
                    routeGroup = (IRouteGroup) clazz.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("路由路径加载失败" + e.getMessage());
                }
                groupCache.put(group, routeGroup);
            }
            //通过IRouteGroup对象的loadPath方法拿到path地址和RouteMeta对象的映射map
            Map<String, RouteMeta> routeMetaMap = routeGroup.loadPath();
            if (routeMetaMap.isEmpty() || routeMetaMap.get(path) == null) {
                throw new RuntimeException("路由路径加载失败,没有找到对应的class文件");
            } else {
                //根据path地址，从map取出RouteMeta对象
                routeMeta = routeMetaMap.get(path);
                //加入缓存
                for (RouteMeta meta : routeMetaMap.values()) {
                    pathCache.put(meta.getPath(), meta);
                }
            }
        }

        if (routeMeta != null) {
            switch (routeMeta.getType()) {
                case ACTIVITY:
                    final Intent intent = new Intent(context, routeMeta.getClazz());
                    intent.putExtras(postCard.getBundle());
                    runInMainThread(new Runnable() {
                        @Override
                        public void run() {
                            ActivityCompat.startActivity(context, intent, null);
                            if ((-1 != postCard.getEnterAnim() && -1 != postCard.getExitAnim()) && context instanceof Activity) {    // Old version.
                                ((Activity) context).overridePendingTransition(postCard.getEnterAnim(), postCard.getExitAnim());
                            }
                        }
                    });
                    break;
                case PROVIDER:
                    //根据path地址从provider缓存中拿到IProvider对象
                    IProvider instance = iProviderCache.get(routeMeta.getPath());
                    //如果从缓存中没有拿到IProvider对象，就通过newInstance的方式获取对象，并加入缓存
                    if (instance == null) {
                        try {
                            instance = (IProvider) routeMeta.getClazz().newInstance();
                            instance.init(context);
                            iProviderCache.put(routeMeta.getPath(), instance);
                        } catch (Exception e) {
                            throw new RuntimeException("Init provider failed! " + e.getMessage());
                        }
                    }
                    return instance;
            }
        }

        return null;
    }

    /**
     * Be sure execute in main thread.
     *
     * @param runnable code
     */
    private void runInMainThread(Runnable runnable) {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            new Handler(Looper.getMainLooper()).post(runnable);
        } else {
            runnable.run();
        }
    }


    private String subFromPath2Group(String path) {
        // 比如开发者代码为：path = "/MainActivity"，最后一个 / 符号必然在字符串第1位
        if (path.lastIndexOf("/") == 0) {
            // 架构师定义规范，让开发者遵循
            throw new IllegalArgumentException("@ARouter注解未按规范配置，如：/app/MainActivity");
        }

        // 从第一个 / 到第二个 / 中间截取，如：/app/MainActivity 截取出 app 作为group
        String finalGroup = path.substring(1, path.indexOf("/", 1));

        if (TextUtils.isEmpty(finalGroup)) {
            // 架构师定义规范，让开发者遵循
            throw new IllegalArgumentException("@ARouter注解未按规范配置，如：/app/MainActivity");
        }

        // 最终组名：app
        return finalGroup;
    }

}
