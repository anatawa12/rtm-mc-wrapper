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

if (__rtm_mc_wrapper__ == null) throw new Error("you have to load(include) common.js of mc-wrapper before include other mc-wrapper scripts");

rmw.includeGuard("mc-wrapper:tile-entity", ["mc-wrapper:common", "mc-wrapper:nbt"], function (global) {

    var NGTLog = Packages.jp.ngt.ngtlib.io.NGTLog
    var TileEntity = Packages.net.minecraft.tileentity.TileEntity

    /**
     * @type {function(TileEntity):string}
     */
    var getTileEntityName;

    if (is1710) {
        var ReflectionHelper = Packages.cpw.mods.fml.relauncher.ReflectionHelper;
        var classToNameMap = ReflectionHelper.findField(TileEntity.class, "field_145853_j");

        getTileEntityName = function (tile) {
            return classToNameMap.get(null, tile.getClass())
        }
    } else {
        var tileEntityREGISTRY = TileEntity.field_190562_f // TileEntity.REGISTRY

        getTileEntityName = function (tile) {
            var name = tileEntityREGISTRY
                .func_177774_c(tile.getClass()) //RegistryNamespaced.getNameForObject
                .toString();
            return name;
        }
    }

    var getWNBTCompoundByTile = __rtm_mc_wrapper__.versioned_func(
        /**
         * @param tile {TileEntity} tile
         * @return {WNBTCompound}
         */
        function (tile) {
            var compound = new WNBTCompound();
            tile.func_145841_b(compound.__real__);
            return compound;
        },
        function (tile) {
            var compound = new WNBTCompound();
            tile.func_189515_b(compound.__real__);
            return compound;
        }
    )

    var makeTileEntity = __rtm_mc_wrapper__.versioned_func(
        /**
         * @param tileData {WNBTCompound}
         * @return TileEntity
         */
        function (tileData) {
            return TileEntity.func_145827_c(tileData.__real__) // TileEntity.createAndLoadEntity
        },
        function (tileData) {
            return TileEntity.func_190200_a(tileData.__real__) // TileEntity.create
        }
    )

    ////////////////////////////////

    /**
     * @typedef {TileEntity|NBTCompoundConvertible|WNBTCompound} WTileEntityParams
     */

    /**
     * @constructor
     * @param param {WTileEntityParams}
     * @returns {WBlock}
     */
    function WTileEntity(param) {
        if (!(this instanceof WTileEntity)) {
            NGTLog.debug("you should use WTileEntity as constructor")
            return new WTileEntity(param)
        }

        /** @type WNBTCompound */
        var nbt;
        /** @type TileEntity */
        var tile;
        if (param instanceof WNBTCompound) {
            nbt = param;
            tile = makeTileEntity(nbt);
        } else if (param instanceof TileEntity) {
            tile = param;
        } else {
            nbt = new WNBTCompound(param)
            tile = makeTileEntity(nbt);
        }
        this.__nbt__ = nbt;
        /** @type TileEntity */
        this.tile = tile;
    }

    Object.defineProperty(WTileEntity.prototype, "nbt", {
        get: function () {
            if (this.__nbt__ == null) {
                this.__nbt__ = getWNBTCompoundByTile(this.tile);
            }
            return this.__nbt__
        }
    })

    WTileEntity.prototype.resetNBT = function () {
        this.__nbt__ = null;
    }

    WTileEntity.prototype.resetAndGetNBT = function () {
        this.resetNBT()
        return this.nbt
    }

    Object.defineProperty(WTileEntity.prototype, "world", {
        get: function () {
            return this.tile.field_145850_b; // TileEntity.worldObj
        }
    })

    Object.defineProperty(WTileEntity.prototype, "x", {
        get: __rtm_mc_wrapper__.versioned_func(
            function () {
                return this.tile.field_145851_c // TileEntity.xCoord
            },
            function () {
                return this.tile.field_174879_c // TileEntity.pos
                    .func_177958_n() // BlockPos.getX() = Vec3i.getX()
            }
        )
    })

    Object.defineProperty(WTileEntity.prototype, "y", {
        get: __rtm_mc_wrapper__.versioned_func(
            function () {
                return this.tile.field_145848_d // TileEntity.yCoord
            },
            function () {
                return this.tile.field_174879_c // TileEntity.pos
                    .func_177956_o() // BlockPos.getY() = Vec3i.getY()
            }
        )
    })

    Object.defineProperty(WTileEntity.prototype, "z", {
        get: __rtm_mc_wrapper__.versioned_func(
            function () {
                return this.tile.field_145849_e // TileEntity.zCoord
            },
            function () {
                return this.tile.field_174879_c // TileEntity.pos
                    .func_177952_p() // BlockPos.getZ() = Vec3i.getZ()
            }
        )
    })

    /**
     * the id of the tile entity
     * 
     * @type string
     */
    Object.defineProperty(WTileEntity.prototype, "tileId", {
        get: function () {
            if (this.__tileId__ == null) {
                this.__tileId__ = getTileEntityName(this.tile);
            }
            return this.__tileId__;
        }
    })

    global.WTileEntity = WTileEntity;
})()
