package com.android.jesse.router_compiler.utils;

public class Consts {

    public static final String PROJECT = "AwesomeRouter";
    public static final String ACTIVITY = "android.app.Activity";
    public static final String IPROVIDER = "com.android.jesse.router_api.template.IProvider";
    public static final String AROUTE_GROUP = "com.android.jesse.router_api.template.IRouteGroup";
    public static final String METHOD_LOAD_PATH = "loadPath";
    public static final String PACKAGE_OF_GENERATE_FILE = "com.android.jesse.awesomerouter.routes";
    public static final String GROUP_FILE_NAME = "AwesomeRouter$$Group$$";
    public static final Object PATH_PARAMETER_NAME = "pathMap";

    static final String PREFIX_OF_LOGGER = PROJECT + "::Compiler ";
    public static final String KEY_MODULE_NAME = "moduleName";

    // 注解处理器中支持的注解类型
    public static final String ANNOTATION_TYPE_ROUTER = "com.android.jesse.router_annotation.Router";
}
