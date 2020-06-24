/**
 * mc-rtm-wrapper
 * RTMの各種スクリプト向けマインクラフトのラッパー。
 * 1.7.10, 1.12.2両対応。
 *
 * MIT Licenseの日本語要約
 * 下記のライセンス全文を保持してる限り(それ以外の)いかなる制限なくこのスクリプトを扱う事ができる。
 * 原作者(私, anatawa12)はいかなる損害の保証はせず、原作者が各種責任を追わないものとする。
 *
 * MIT Licenseの日本語要約の補足
 * ここのブロックコメントはライセンス表記に含まないので削除していただいて問題ありません。
 * 次のブロックコメントがライセンス表記に当たります。
 */
/**
 * MIT License
 *
 * Copyright (c) 2020 anatawa12(翳河翔)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

if (__rtm_mc_wrapper__ == null) {
    /**
     * rtm mc wrapperのcommon API
     * どのファイルをロードしてもこのオブジェクトは常に存在する。
     */

    // noinspection ThisExpressionReferencesGlobalObjectJS
    /**
     * rtm mc wrapperで使用する内部的なオブジェクト
     * 使わないでください
     */
    Object.defineProperty(this, "rmw", {value: {}});

    // noinspection ThisExpressionReferencesGlobalObjectJS
    /**
     * rtm mc wrapperで使用する内部的なオブジェクト
     * 使わないでください
     */
    Object.defineProperty(this, "__rtm_mc_wrapper__", {value: {}});

    /**
     * 1.7.10かどうか
     */
    rmw.is1710 = Packages.net.minecraftforge.common.ForgeVersion.mcVersion === "1.7.10"

    /**
     * @function
     * @template T
     * @param for1710 {T}
     * @param for1122 {T}
     * @returns {T} for1710かfor1122を{@link rmw.is1710}に応じて返す
     */
    __rtm_mc_wrapper__.versioned_value = function (for1710, for1122) {
        if (rmw.is1710) {
            return for1710
        } else {
            return for1122
        }
    }

    __rtm_mc_wrapper__.guards = {};

    // noinspection ThisExpressionReferencesGlobalObjectJS
    (function (global) {
        /**
         * include guard
         * インクルード済なら何もせずに戻る。
         * インクルードしていなければ関数を実行する
         * 
         * 推奨される識別子について
         * 識別子は"<domain>:<name>"のような形で、<domain>部をモデルパックや作者ごとに固有なもの、<name>をその中での識別子
         * にするのを推奨しています
         * 
         * @param [shouldIncluded] {string[]} すでにincludeされてるはずなもの配列
         * @param name {string} インクルードの識別子。
         * @param func {function(object)} 実行する関数。引数にglobal object(common.jsの呼び出しコンテクスト)を渡します。
         */
        rmw.includeGuard = function (name, shouldIncluded, func) {
            if (__rtm_mc_wrapper__.guards[name])
                return
            if (typeof shouldIncluded == "function") {
                func = shouldIncluded
                shouldIncluded = []
            }
            shouldIncluded.forEach(function (value) {
                if (!rmw.isIncluded(value))
                    throw new Error(name + " can't load without loading " + shouldIncluded + ": " + value + " not loaded");
            })
            __rtm_mc_wrapper__.guards[name] = "included"
            return func(global)
        }

        /**
         * include guard
         * インクルード済かどうかを返す
         * @return {boolean} インクルード済かどうか
         */
        rmw.isIncluded = function (name) {
            return !!__rtm_mc_wrapper__.guards[name]
        }
    })(this)

    /**
     * polyfill
     * @type {(function(number): boolean)}
     */
    Number.isInteger = Number.isInteger || function (value) {
        return typeof value === 'number' &&
            isFinite(value) &&
            Math.floor(value) === value;
    };

    // mc-wrapper:commonを ロードされた状態にする
    rmw.includeGuard("mc-wrapper:common", function () {});
}
