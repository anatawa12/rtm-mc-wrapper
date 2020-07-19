package rmw2;

import com.anatawa12.mcWrapper.internal.utils.JSUtil;
import com.anatawa12.mcWrapper.test.TestUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InitTest {
    @Test
    public void initOnNashorn() throws Exception {
        init(TestUtil::eval);
    }

    public void init(EvalFunction eval) throws Exception {
        eval.eval("Packages.rmw2.Init.init(eval)");
        JSUtil.checkInitialized();

        Object obj1 = eval.eval("({" +
                "key1: 'value_key1', " +
                "1: 'value_index_1', " +
                "})");

        Object ary1 = eval.eval("([" +
                "'value_1', " +
                "'value_2', " +
                "])");

        assertEquals("value_key1", JSUtil.get(obj1, "key1"));

        assertEquals("value_index_1", JSUtil.get(obj1, "1"));

        assertEquals("value_index_1", JSUtil.get(obj1, 1));

        JSUtil.set(obj1, "key2", "value_key2");
        assertEquals("value_key2", JSUtil.get(obj1, "key2"));

        assertFalse(JSUtil.isArray(obj1));
        assertTrue(JSUtil.isArray(ary1));

        assertEquals("object", JSUtil.typeof(null));

        assertEquals("boolean", JSUtil.typeof(false));
        assertEquals("boolean", JSUtil.typeof(true));

        assertEquals("boolean", JSUtil.typeof(eval.eval("false")));
        assertEquals("boolean", JSUtil.typeof(eval.eval("true")));

        assertEquals("number", JSUtil.typeof(1));
        assertEquals("number", JSUtil.typeof(0.0));

        assertEquals("number", JSUtil.typeof(eval.eval("1")));
        assertEquals("number", JSUtil.typeof(eval.eval("0.0")));

        assertEquals("string", JSUtil.typeof(""));
        assertEquals("string", JSUtil.typeof(eval.eval("''")));

        assertEquals("function", JSUtil.typeof(eval.eval("function(){}")));

        assertEquals("object", JSUtil.typeof(obj1));
        assertEquals("object", JSUtil.typeof(ary1));

        assertEquals(TestUtil.set("key1", "key2", "1"), TestUtil.set(JSUtil.keys(obj1)));
    }

    static interface EvalFunction {
        Object eval(String script) throws Exception;
    }
}
