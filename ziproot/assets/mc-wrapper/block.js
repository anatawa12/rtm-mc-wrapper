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
    var global = this

    var NGTLog = Packages.jp.ngt.ngtlib.io.NGTLog
    var Block = Packages.net.minecraft.block.Block
    var ResourceLocation = Packages.net.minecraft.util.ResourceLocation

    var blockREGISTRY = Block.field_149771_c // Block.REGISTRY

    var getMCBlockByName = __rtm_mc_wrapper__.versioned_func(
        function (name) {
            var location = new ResourceLocation(name)
            if (!blockREGISTRY.func_148741_d(location)) // RegistryNamespaced.containsKey
                throw Error(name + " is not valid block name")
            return blockREGISTRY.func_82594_a(location)// RegistryNamespaced.getObject
        },
        function (name) {
            var location = name
            if (!blockREGISTRY.func_148741_d(location)) // RegistryNamespaced.containsKey
                throw Error(name + " is not valid block name")
            return blockREGISTRY.func_82594_a(location)// RegistryNamespaced.getObject
        }
    )

    /**
     *
     * @param array {string[]}
     * @param name {string}
     * @param propName {string}
     * @returns {number}
     */
    var getIndex = function (array, name, propName) {
        var meta = array.indexOf(name.toLowerCase())
        if (meta === -1) throw Error("invalid " + propName)
        return meta
    }

    /**
     *
     * @param array {string[]}
     * @param index {number}
     * @returns {string}
     */
    var getName = function (array, index) {
        return array[index] || array[0]
    }

    ////////////////////////////////

    /**
     * @typedef {WBlockParamsWithNameMeta|WBlockParamsWithNameBlock|WBlockParamsWithState} WBlockParams
     */
    /**
     * @typedef {Object} WBlockParamsWithNameMeta
     * @property {string} name 名前
     * @property {number} [meta] メタデータ
     */
    /**
     * @typedef {Object} WBlockParamsWithNameBlock
     * @property {Object} block Block
     * @property {number} [meta] メタデータ
     */
    /**
     * @typedef {Object} WBlockParamsWithState
     * @property {Object} state IBlockState(1.12.2のみ)
     */

    /**
     * @constructor
     * @param param {WBlockParams}
     * @returns {WBlock}
     */
    function WBlock (param) {
        if (!(this instanceof WBlock)) {
            NGTLog.debug("you should use Block as constructor")
            return new WBlock(param)
        }

        if (param.state != null) {
            var block = param.state.func_177230_c() // IBlockState.getBlock()
            param.name = block.getRegistryName().toString() // getRegistryName by forge
            param.meta = block.func_176201_c(blockState)//Block.getMetaFromState
        }

        if (param.block != null) {
            if (is1710) {
                param.name = blockREGISTRY.func_148750_c(param.block).toString()// RegistryNamespaced.getNameForObject
            } else {
                param.name = param.block.getRegistryName().toString() // getRegistryName by forge
            }
        }

        if (typeof param.name != "string")
            throw Error("name is not string")


        /**
         * metadata
         * @type {number}
         */
        this.__meta__ = param.meta || 0;

        /**
         * Blockの名前(minecraft:command_block)など
         * @type {string}
         */
        this.name = param.name;

        /**
         * instance of net.minecraft.block.Block
         * @type {object}
         */
        this.__real__ = getMCBlockByName(name);

        /**
         * instance of net.minecraft.block.Block
         * @type {Object|null|undefined}
         */
        this.__realState__ = param.state;
    }

    var colors = ["white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "silver",
        "cyan", "purple", "blue", "brown", "green", "red", "black"]

    /**
     * @typedef {"white"|"orange"|"magenta"|"light_blue"|"yellow"|"lime"|"pink"|"gray"
     *     |"silver"|"cyan"|"purple"|"blue"|"brown"|"green"|"red"|"black"} MCColor
     * 羊毛などの色付きブロックの色を示す
     */

    /**
     * metadata
     * @this WBlock
     * @type {number}
     */
    Object.defineProperty(WBlock.prototype, "meta", {
        /** @this WBlock */
        get: function () {
            return this.__meta__
        },
        /** @this WBlock */
        set: function (v) {
            this.__meta__ = v
            this.__realState__ = null;
        }
    })

    /**
     * 羊毛などの色
     * @this WBlock
     * @type {MCColor}
     */
    Object.defineProperty(WBlock.prototype, "color", {
        /** @this WBlock */
        get: function () {
            return getName(colors, this.meta)
        },
        /** @this WBlock */
        set: function (v) {
            this.meta = getIndex(colors, v, "color")
        }
    })

    if (!is1710) {
        /**
         * 1.12.2専用。IBlockStateを作成して返す
         * @memberOf WBlock
         * @this WBlock
         * @type {object}
         */
        WBlock.prototype.makeBlockState = function () {
            if (this.__realState__ != null) return this.__realState__;
            return this.__real__.func_176203_a(this.meta); // Block.getStateFromMeta
        }
    }

    /**
     * make block
     * @param block {WBlockParams}
     * @return {WBlock}
     */
    WBlock.create = function(block) {
        if (block instanceof WBlock) return block
        return new WBlock(block);
    }

    global.WBlock = WBlock;
})()
