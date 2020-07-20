package com.anatawa12.mcWrapper.test;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestUtil {
    private static ScriptEngineManager manager = new ScriptEngineManager();

    public static Object eval(String script) throws ScriptException {
        return manager.getEngineByName("nashorn")
                .eval(script);
    }

    public static Set<?> set(Object... keys) {
        return new HashSet<Object>(Arrays.asList(keys));
    }
}
