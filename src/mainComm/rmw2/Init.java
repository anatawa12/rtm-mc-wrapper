package rmw2;

import com.anatawa12.mcWrapper.internal.utils.JSUtil;
import rmw2.functions.EvalFunction;

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
            "    var JSUtil = Packages." + JSUtil.class.getName() + "\n" +
            "    var WEntity = Packages." + WEntity.class.getName() + "\n" +
            "    var WTileEntity = Packages." + WTileEntity.class.getName() + "\n" +
            "    JSUtil.setIsArray(function(array) { return Array.isArray(array); });\n" +
            "    JSUtil.setArrayGetter(function(array, index) { return array[index]; });\n" +
            "    JSUtil.setArraySetter(function(array, index, value) { array[index] = value; });\n" +
            "    JSUtil.setTypeof(function(object) { return typeof object; });\n" +
            "    JSUtil.setKeys(function(object) { return Object.keys(object); });\n" +
            "    JSUtil.setWEntityWrapFunc(function(func) {\n" +
            "        return function () {\n" +
            "            arguments[0] = WEntity.wrap(arguments[0])\n" +
            "            return func.call(this, arguments)\n" +
            "        }\n" +
            "    });\n" +
            "    JSUtil.setWTileEntityWrapFunc(function(func) {\n" +
            "        return function () {\n" +
            "            arguments[0] = WTileEntity.wrap(arguments[0])\n" +
            "            return func.call(this, arguments)\n" +
            "        }\n" +
            "    });\n" +
            "})();\n";
}
