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
    var rmw = {}
    /**
     * 1.7.10かどうか
     */
    rmw.is1710 = Packages.net.minecraftforge.common.ForgeVersion.mcVersion === "1.7.10"

    /**
     * rtm mc wrapperで使用する内部的なオブジェクト
     * できる限り使わないでください
     */
    var __rtm_mc_wrapper__ = {}

    /**
     * @function
     * @template T
     * @param for1710 {T}
     * @param for1122 {T}
     * @returns {T} for1710かfor1122を{@link rmw.is1710}に応じて返す
     */
    __rtm_mc_wrapper__.versioned_func = function (for1710, for1122) {
        if (rmw.is1710) {
            return for1710
        } else {
            return for1122
        }
    }

    /**
     * include guard
     * インクルード済の場合はtrueを返す
     * @param name インクルードの識別子
     * @return {boolean}
     */
    __rtm_mc_wrapper__.includeGuard = function (name) {
        if (__rtm_mc_wrapper__[name])
            return true
        __rtm_mc_wrapper__[name] = "included"
        return false
    }

    /**
     * polyfill
     * @type {*|(function(number): boolean)}
     */
    Number.isInteger = Number.isInteger || function (value) {
        return typeof value === 'number' &&
            isFinite(value) &&
            Math.floor(value) === value;
    };
}
