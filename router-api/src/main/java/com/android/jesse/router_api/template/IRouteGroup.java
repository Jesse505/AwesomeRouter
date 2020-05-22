package com.android.jesse.router_api.template;

import com.android.jesse.router_annotation.model.RouteMeta;
import java.util.Map;

public interface IRouteGroup {
    void loadInto(Map<String, RouteMeta> atlas);
}
