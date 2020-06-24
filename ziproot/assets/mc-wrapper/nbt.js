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

//include <mc-wrapper:common.js>
if (__rtm_mc_wrapper__ == null) throw new Error("couldn't load common.js of mc-wrapper");

(function () {
    if (__rtm_mc_wrapper__.includeGuard("nbt")) return
    var global = this
    var NGTLog = Packages.jp.ngt.ngtlib.io.NGTLog

    var NBTBase = Packages.net.minecraft.nbt.NBTBase

    var NBTTagCompound = Packages.net.minecraft.nbt.NBTTagCompound
    var NBTTagString = Packages.net.minecraft.nbt.NBTTagString
    var NBTTagList = Packages.net.minecraft.nbt.NBTTagList

    var NBTTagByteArray = Packages.net.minecraft.nbt.NBTTagByteArray
    var NBTTagIntArray = Packages.net.minecraft.nbt.NBTTagIntArray

    var NBTTagByte = Packages.net.minecraft.nbt.NBTTagByte
    var NBTTagShort = Packages.net.minecraft.nbt.NBTTagShort
    var NBTTagInt = Packages.net.minecraft.nbt.NBTTagInt
    var NBTTagLong = Packages.net.minecraft.nbt.NBTTagLong
    var NBTTagFloat = Packages.net.minecraft.nbt.NBTTagFloat
    var NBTTagDouble = Packages.net.minecraft.nbt.NBTTagDouble

    var Integer = Packages.java.lang.Integer
    var Byte = Packages.java.lang.Byte

    ////////////////////////////////

    /**
     * @param param {NBTBase}
     * @return {WNBTBase | undefined}
     */
    function wrapNBT(param) {
        if (param instanceof NBTTagByte) {
            return new WNBTByte(param)
        } else if (param instanceof NBTTagShort) {
            return new WNBTShort(param)
        } else if (param instanceof NBTTagInt) {
            return new WNBTInt(param)
        } else if (param instanceof NBTTagLong) {
            return new WNBTLong(param)
        } else if (param instanceof NBTTagFloat) {
            return new WNBTFloat(param)
        } else if (param instanceof NBTTagDouble) {
            return new WNBTDouble(param)
        } else if (param instanceof NBTTagByteArray) {
            return new WNBTByteArray(param)
        } else if (param instanceof NBTTagString) {
            return new WNBTString(param)
        } else if (param instanceof NBTTagList) {
            return new WNBTList(param)
        } else if (param instanceof NBTTagCompound) {
            return new WNBTCompound(param)
        } else if (param instanceof NBTTagIntArray) {
            return new WNBTIntArray(param)
        } else {
            return undefined
        }
    }

    /**
     * @typedef {NBTBase | NBTIntArrayConvertible | NBTCompoundConvertible | NBTStringConvertible | NBTDoubleConvertible} NBTConvertible
     */

    /**
     * @typedef {NBTTagCompound|Object.<string, NBTConvertible>} NBTCompoundConvertible
     */

    /**
     * @typedef {NBTTagIntArray|number[]} NBTIntArrayConvertible
     */

    /**
     * @typedef {NBTBase | NBTIntArrayConvertible | NBTCompoundConvertible | NBTStringConvertible} NBTConvertibleListElement
     */
    /**
     * @typedef {NBTTagList|NBTConvertibleListElement[]} NBTListConvertible
     */

    /**
     * @typedef {NBTTagString|string} NBTStringConvertible
     */

    /**
     * @typedef {NBTTagDouble|number} NBTDoubleConvertible
     */

    /**
     * if the value is empty array, it will be unwrap to {@link WNBTIntArray}
     * @param param {NBTConvertible}
     * @return {NBTBase}
     */
    function unwrapNBT(param) {
        if (param === null) throw new Error("can't unwrap null")
        if (param instanceof NBTBase) {
            return param
        } else if (Array.isArray(param)) {
            if (param.length === 0 || typeof param[0] == "number")
                return new WNBTIntArray(param).__real__
            else
                return new WNBTList(param).__real__
        }
        switch (typeof param) {
            case "object":
                return new WNBTCompound(param).__real__
            case "number":
                return new WNBTDouble(param).__real__
            case "string":
                return new WNBTString(param).__real__
            case "undefined":
                throw new Error("can't unwrap undefined")
            case "boolean":
                throw new Error("can't unwrap boolean")
            case "function":
                throw new Error("can't unwrap function")
            case "symbol":
                throw new Error("can't unwrap symbol")
            case "bigint":
                throw new Error("can't unwrap bigint")
            default:
                throw new Error("can't unwrap runtime-defined value(typeof returns non-standard value)")
        }
    }

    ////////////////////////////////

    /**
     * @template NBT
     * @param nbt {NBT & NBTBase}
     * @constructor
     * @property {NBT & NBTBase} __real__
     */
    function WNBTBase (nbt) {
        if (!(this instanceof WNBTBase)) {
            NGTLog.debug("you should use WNBTBase as constructor")
            return new WNBTBase(nbt)
        }

        this.__real__ = nbt
    }

    /**
     * get string. if this is {@link WNBTString} this returns real component of string.
     * @return {string}
     */
    WNBTBase.prototype.getString = function () {
        return this.__real__.func_150285_a_()
    }

    /**
     * get string. if this is {@link WNBTString} this returns real component of string.
     * @return {string}
     */
    WNBTBase.prototype.toString = function () {
        return this.__real__.toString()
    }

    ////////////////////////////////

    /**
     *
     * @param nbt {NBTBase}
     * @return {WNBTPrimitive}
     * @extends {WNBTBase}
     * @constructor
     */
    function WNBTPrimitive (nbt) {
        if (!(this instanceof WNBTPrimitive)) {
            NGTLog.debug("you should use WNBTPrimitive as constructor")
            return new WNBTPrimitive(nbt)
        }
        WNBTBase.call(this, nbt)
    }

    WNBTPrimitive.prototype = Object.create(WNBTBase.prototype)

    /**
     * @return {number} value
     */
    WNBTPrimitive.prototype.asLong = function () {
        return this.__real__.func_150291_c() // NBTPrimitive.getLong
    }

    /**
     * @return {number} value
     */
    WNBTPrimitive.prototype.asInt = function () {
        return this.__real__.func_150287_d() // NBTPrimitive.getInt
    }

    /**
     * @return {number} value
     */
    WNBTPrimitive.prototype.asShort = function () {
        return this.__real__.func_150289_e() // NBTPrimitive.getShort
    }

    /**
     * @return {number} value
     */
    WNBTPrimitive.prototype.asByte = function () {
        return this.__real__.func_150290_f() // NBTPrimitive.getByte
    }

    /**
     * @return {number} value
     */
    WNBTPrimitive.prototype.asDouble = function () {
        return this.__real__.func_150286_g() // NBTPrimitive.getDouble
    }

    /**
     * @return {number} value
     */
    WNBTPrimitive.prototype.asFloat = function () {
        return this.__real__.func_150288_h() // NBTPrimitive.getFloat
    }

    /**
     * @param mc {typeof WNBTPrimitive}
     * @return {typeof WNBTPrimitive}
     */
    function mkNBTPrimitiveClass(mc) {
        /**
         *
         * @param nbt
         * @constructor
         */
        var ctor = function (nbt) {
            if (!(this instanceof ctor)) {
                NGTLog.debug("you should use "+ mc.name + " as constructor")
                return new ctor(nbt)
            }
            if (!(nbt instanceof mc)) {
                nbt = new mc(nbt)
            }
            WNBTPrimitive.call(this, nbt)
        }
        ctor.prototype = Object.create(WNBTPrimitive.prototype)
        return ctor
    }

    /**
     * @param nbt {number|NBTTagByte}
     * @extends {WNBTPrimitive}
     * @constructor
     */
    var WNBTByte = mkNBTPrimitiveClass(NBTTagByte)
    /**
     * @param nbt {number|NBTTagFloat}
     * @extends {WNBTPrimitive}
     * @constructor
     */
    var WNBTFloat = mkNBTPrimitiveClass(NBTTagFloat)
    /**
     * @param nbt {number|NBTTagDouble}
     * @extends {WNBTPrimitive}
     * @constructor
     */
    var WNBTDouble = mkNBTPrimitiveClass(NBTTagDouble)
    /**
     * @param nbt {number|NBTTagInt}
     * @extends {WNBTPrimitive}
     * @constructor
     */
    var WNBTInt = mkNBTPrimitiveClass(NBTTagInt)
    /**
     * @param nbt {number|NBTTagLong}
     * @extends {WNBTPrimitive}
     * @constructor
     */
    var WNBTLong = mkNBTPrimitiveClass(NBTTagLong)
    /**
     * @param nbt {number|NBTTagShort}
     * @extends {WNBTPrimitive}
     * @constructor
     */
    var WNBTShort = mkNBTPrimitiveClass(NBTTagShort)

    ////////////////////////////////

    /**
     *
     * @param [param] {NBTCompoundConvertible}
     * @return {WNBTCompound}
     * @extends {NBTBase}
     * @constructor
     */
    function WNBTCompound(param) {
        if (!(this instanceof WNBTCompound)) {
            NGTLog.debug("you should use WNBTCompound as constructor")
            return new WNBTCompound(param)
        }
        var nbt = param
        if (nbt == null) {
            nbt = new NBTTagCompound()
        } else if (!(nbt instanceof NBTTagCompound)) {
            if (typeof nbt != "object")
                throw new Error("parameter of WNBTCompound must be object, null or undefined")
            nbt = new NBTTagCompound()
        }
        WNBTBase.call(this, nbt)

        if (!(param instanceof NBTTagCompound)) {
            for (var key in param) {
                if (param.hasOwnProperty(key)) {
                    this.set(key, param[key])
                }
            }
        }
    }

    WNBTCompound.prototype = Object.create(WNBTBase.prototype)

    /**
     * @param name {string}
     * @return {WNBTBase|undefined}
     */
    WNBTCompound.prototype.get = function (name) {
        return wrapNBT(this.__real__.func_74781_a(name)) // NBTTagCompound.getTag
    }

    /**
     * @param name {string}
     * @param value {NBTConvertible}
     */
    WNBTCompound.prototype.set = function (name, value) {
        this.__real__.func_74782_a(name, unwrapNBT(value)) // NBTTagCompound.setTag
    }

    ////////////////////////////////

    /**
     * @param nbt {string|NBTTagString}
     * @return {WNBTString}
     * @extends {NBTBase}
     * @constructor
     */
    function WNBTString(nbt) {
        if (!(this instanceof WNBTString)) {
            NGTLog.debug("you should use WNBTString as constructor")
            return new WNBTString(nbt)
        }
        if (!(nbt instanceof NBTTagString)) {
            nbt = new NBTTagString(nbt)
        }
        WNBTBase.call(this, nbt)
    }

    WNBTString.prototype = Object.create(WNBTBase.prototype)

    ////////////////////////////////

    /**
     * @param param {NBTListConvertible}
     * @return {WNBTList}
     * @extends {NBTBase}
     * @constructor
     */
    function WNBTList(param) {
        if (!(this instanceof WNBTList)) {
            NGTLog.debug("you should use WNBTList as constructor")
            return new WNBTList(param)
        }
        var nbt = param
        if (!(nbt instanceof NBTTagList)) {
            if (!Array.isArray(param))
                throw new Error("parameter of WNBTList must be array")
            nbt = new NBTTagList()
        }
        WNBTBase.call(this, nbt)
        if (Array.isArray(param)) {
            param.forEach(function (v) {
                this.add(unwrapNBT(param[v]))
            })
        }
    }

    WNBTList.prototype = Object.create(WNBTBase.prototype)

    /**
     * get {@link WNBTCompound} at {@param index}
     * @param index {number}
     * @return {WNBTCompound}
     */
    WNBTList.prototype.getCompoundAt = function (index) {
        return new WNBTCompound(this.__real__.func_150305_b(index)) // NBTTagList.getCompoundTagAt
    }

    /**
     * get int array at {@param index}
     * @param index {number}
     * @return {number[]} array of int
     */
    WNBTList.prototype.getIntArrayAt = function (index) {
        return this.__real__.func_150306_c(index) // NBTTagList.getIntArrayAt
    }

    /**
     * get double value at {@param index}
     * @param index {number}
     * @return {number}
     */
    WNBTList.prototype.getDoubleAt = function (index) {
        return this.__real__.func_150309_d(index) // NBTTagList.getDoubleAt
    }

    /**
     * get float value at {@param index}
     * @param index {number}
     * @return {number}
     */
    WNBTList.prototype.getFloatAt = function (index) {
        return this.__real__.func_150308_e(index) // NBTTagList.getFloatAt
    }

    /**
     * get string at {@param index}
     * @param index {number}
     * @return {string}
     */
    WNBTList.prototype.getStringAt = function (index) {
        return this.__real__.func_150307_f(index) // NBTTagList.getStringTagAt
    }

    /**
     * set {@param value} to {@param index}
     * @param index {number}
     * @param value {NBTConvertible}
     */
    WNBTList.prototype.set = function (index, value) {
        this.__real__.func_150304_a(index, unwrapNBT(value)) // NBTTagCompound.setTag
    }

    /**
     * adds {@param value} to last of this list
     * @param value {NBTConvertible}
     */
    WNBTList.prototype.add = function (value) {
        this.__real__.func_74742_a(unwrapNBT(value)) // NBTTagCompound.setTag
    }

    /**
     * removes tag at the index
     * @param index {number}
     */
    WNBTList.prototype.remove = function (index) {
        this.__real__.func_74744_a(index) // NBTTagCompound.remove
    }

    /**
     * removes tag at the index
     * @param index {number}
     * @property WNBTList#size
     */
    Object.defineProperty(WNBTList.prototype, "size", {
        get: function () {
            return this.__real__.func_74745_c()
        }
    })

    ////////////////////////////////

    /**
     * @param array {number[]}
     */
    function makeByteArray(array) {
        var javaArray = Packages.java.lang.reflect.Array.newInstance(Byte.TYPE, array.length)
        for (var i = 0; i < array.length; i++) {
            if (!Number.isInteger(array[i]))
                throw TypeError("parameter array of WNBTIntArray at " + i + " is not integer")
            if (array[i] <= Byte.MIN_VALUE || Byte.MAX_VALUE <= array[i])
                throw TypeError("parameter array of WNBTIntArray at " + i + " is too small or too big for byte")
            javaArray[i] = array[i]
        }
        return javaArray
    }

    /**
     * @param nbt
     * @return {WNBTByteArray}
     * @extends {NBTBase}
     * @constructor
     */
    function WNBTByteArray(nbt) {
        if (!(this instanceof WNBTByteArray)) {
            NGTLog.debug("you should use WNBTByteArray as constructor")
            return new WNBTByteArray(nbt)
        }
        if (!(nbt instanceof NBTTagByteArray)) {
            nbt = new NBTTagByteArray(makeByteArray(nbt))
        }
        WNBTBase.call(this, nbt)
    }

    WNBTByteArray.prototype = Object.create(WNBTBase.prototype)

    /**
     * @return {number[]} byte[] in java
     */
    WNBTByteArray.prototype.getByteArray = function () {
        return this.__real__.func_150292_c() // getByteArray
    }

    ////////////////////////////////

    /**
     * @param array {number[]}
     */
    function makeIntArray(array) {
        var javaArray = Packages.java.lang.reflect.Array.newInstance(Integer.TYPE, array.length)
        for (var i = 0; i < array.length; i++) {
            if (!Number.isInteger(array[i]))
                throw TypeError("parameter array of WNBTIntArray at " + i + " is not integer")
            if (array[i] <= Integer.MIN_VALUE || Integer.MAX_VALUE <= array[i])
                throw TypeError("parameter array of WNBTIntArray at " + i + " is too small or too big for int")
            javaArray[i] = array[i]
        }
        return javaArray
    }

    /**
     * @param nbt
     * @return {WNBTIntArray}
     * @extends {NBTBase}
     * @constructor
     */
    function WNBTIntArray(nbt) {
        if (!(this instanceof WNBTIntArray)) {
            NGTLog.debug("you should use WNBTIntArray as constructor")
            return new WNBTIntArray(nbt)
        }
        if (!(nbt instanceof NBTTagIntArray)) {
            nbt = new NBTTagIntArray(makeIntArray(nbt))
        }
        WNBTBase.call(this, nbt)
    }

    WNBTIntArray.prototype = Object.create(WNBTBase.prototype)

    /**
     * @return {number[]} int[] in java
     */
    WNBTIntArray.prototype.getIntArray = function () {
        return this.__real__.func_150302_c() // getIntArray
    }

    global.WNBTBase = WNBTBase;
    global.WNBTByte = WNBTByte;
    global.WNBTShort = WNBTShort;
    global.WNBTInt = WNBTInt;
    global.WNBTLong = WNBTLong;
    global.WNBTFloat = WNBTFloat;
    global.WNBTDouble = WNBTDouble;
    global.WNBTByteArray = WNBTByteArray;
    global.WNBTString = WNBTString;
    global.WNBTList = WNBTList;
    global.WNBTCompound = WNBTCompound;
    global.WNBTIntArray = WNBTIntArray;
})()
