package org.example.shopdemo.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JSPHelper {
    private final static String PATH = "WEB-INF/jsp/%s.jsp";

    public String getPath(String jsp){
        return PATH.formatted(jsp);
    }
}
