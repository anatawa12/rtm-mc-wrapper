package com.anatawa12.mcWrapper.v2;

public final class Init {
    public static void init(EvalFunction eval) {
        if (inited) return;
        synchronized (lock) {
            if (inited) return;
            eval.eval(javaScript);
            inited = true;
        }
    }
    
    private static final Object lock = new Object();
    private static volatile boolean inited = false;

    private static final String javaScript = "" +
            "(function () {\n" +
            "    var JSUtil = Packages.com.anatawa12.mcWrapper.utils.JSUtil\n" +
            "    JSUtil.setIsArray(function(array) { return Array.isArray(array); });\n" +
            "    JSUtil.setArrayGetter(function(array, index) { return array[index]; });\n" +
            "    JSUtil.setArraySetter(function(array, index, value) { array[index] = value; });\n" +
            "    JSUtil.setTypeof(function(object) { return typeof object; });\n" +
            "})();\n";
}
