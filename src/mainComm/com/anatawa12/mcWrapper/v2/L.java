package com.anatawa12.mcWrapper.v2;

import com.anatawa12.mcWrapper.internal.utils.JSUtil;

/**
 * loading helpers. you should not use manually.
 */
public class L {
    private L() {}

    /**
     * rmwをglobalにロードします。もしrmwが存在すれば何もしません。
     * common.jsの実装でのみ使われます。
     *
     * @param global rmwをロードする先の環境。
     */
    public static void lc(Object global) {
        if (!(JSUtil.get(global, "rmw") instanceof RMW)) {
            JSUtil.set(global, "rmw", new RMW(global));
        }
    }

    /**
     * modulesがロード済かどうかを確認し、nameで指定された名前のモジュールを読み込み済とマークします。
     * もしrmwがglobalにロードされていないか、modulesのいずれかのモジュールがロードできていないと例外を投げます。
     * 各api.jsの実装でのみ使われます。
     *
     * @param global rmwがロードされているはずのglobal環境
     * @param name ロード済としてマークするモジュール名
     * @param modules ロードされているべきモジュール名一覧
     */
    public static void l(Object global, String name, String... modules) {
        Object rmw = JSUtil.get(global, "rmw");
        if (!(rmw instanceof RMW))
            throw new IllegalStateException("you have to load(include) common.js of mc-wrapper before include other mc-wrapper scripts");
        ((RMW) rmw).includeGuard(name, modules, (it) -> {});
    }

    /**
     * modulesがをロード済としてマークします。
     * all.jsの実装で使われます。
     *
     * @param global rmwがロードされているはずのglobal環境
     * @param modules モジュール名一覧
     */
    public static void la(Object global, String... modules) {
        Object rmw = JSUtil.get(global, "rmw");
        if (!(rmw instanceof RMW))
            throw new IllegalStateException("you have to load(include) common.js of mc-wrapper before include other mc-wrapper scripts");
        for (String name : modules) {
            ((RMW) rmw).includeGuard(name, (it) -> {});
        }
    }
}
