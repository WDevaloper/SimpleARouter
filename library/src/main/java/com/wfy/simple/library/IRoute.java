package com.wfy.simple.library;

import java.util.Map;

public interface IRoute {
    void loadInto(Map<String, Class<?>> routes);
}
