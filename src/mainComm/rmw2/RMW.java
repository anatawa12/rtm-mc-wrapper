package rmw2;

import com.anatawa12.mcWrapper.internal.McWrapper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * rmwオブジェクトのクラス
 */
public final class RMW {
    public final boolean is1710 = McWrapper.is1710;
    public final boolean is1122 = !is1710;

    private final Object global;
    private final Set<String> guards = new HashSet<>();

    public RMW(Object global) {
        this.global = global;
        includeGuard("mc-wrapper:common", (it) -> {});
    }

    public void includeGuard(Object name, RMWModule func) {
        includeGuard(name, null, func);
    }

    public void includeGuard(Object name, Object[] modules, RMWModule func) {
        if (isIncluded(name)) return;
        for (Object module : modules) {
            if (isIncluded(module))
                throw new Error(name + " can't load without loading " + Arrays.toString(modules) + ": " + module + " not loaded");
        }
        guards.add(Objects.toString(name));
        func.eval(global);
    }

    private boolean isIncluded(Object name) {
        return guards.contains(Objects.toString(name));
    }
}
