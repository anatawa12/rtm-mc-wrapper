package rmw2;

import com.anatawa12.mcWrapper.internal.utils.JSUtil;
import com.anatawa12.mcWrapper.test.ScriptUtil;
import org.junit.jupiter.api.Test;

import javax.script.ScriptException;

class InitTest {

    @Test
    public void initOnNashorn() throws ScriptException {
        ScriptUtil.eval("Packages.rmw2.Init.init(eval)");
        JSUtil.checkInitialized();
    }

}
