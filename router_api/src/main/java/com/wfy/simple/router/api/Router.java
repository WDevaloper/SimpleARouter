package com.wfy.simple.router.api;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wfy.simple.library.IRoute;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Router {
    private static Router mRouter;

    final static String GENERATE_ROUTE_TABLE_PACKAGE = "com.wfy.simple.router";
    final static String GENERATE_ROUTE_SUFFIX_NAME = "RouterMapTable$$Root_";
    final static String DOT = ".";

    private static Map<String, Class<?>> routes = new HashMap<>();

    private static Context mContext;

    private Router() {
    }

    public static Router getInstance() {
        if (mRouter == null) {
            synchronized (Router.class) {
                if (mRouter == null) {
                    mRouter = new Router();
                }
            }
        }
        return mRouter;
    }

    public void go(String path) {
        if (routes.containsKey(path)) {
            Class<?> aClass = routes.get(path);
            Intent intent = new Intent(mContext, aClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);

        }
    }


    public static void init(Application app) {
        mContext = app;
        Log.e("tag", "init");
        try {
            Set<String> routerMap = ClassUtils.getFileNameByPackageName(mContext, GENERATE_ROUTE_TABLE_PACKAGE);
            for (String className : routerMap) {
                Log.e("tag", "" + className);
                if (className.startsWith(GENERATE_ROUTE_TABLE_PACKAGE + DOT + GENERATE_ROUTE_SUFFIX_NAME)) {
                    ((IRoute) (Class.forName(className).getConstructor().newInstance())).loadInto(routes);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
