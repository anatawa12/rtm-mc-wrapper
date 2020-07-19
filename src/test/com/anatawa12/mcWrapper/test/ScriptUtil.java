package com.anatawa12.mcWrapper.test;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptUtil {
    private static ScriptEngineManager manager = new ScriptEngineManager();

    public static Object eval(String script) throws ScriptException {
        return manager.getEngineByName("nashorn")
                .eval(script);
    }
}
