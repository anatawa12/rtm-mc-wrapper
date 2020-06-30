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
     * @type {function(i_net_minecraft_tileentity_TileEntity):string}
     */
    var getTileEntityName;

    if (rmw.is1710) {
        // noinspection JSUnresolvedVariable
        var ReflectionHelper = Packages.cpw.mods.fml.relauncher.ReflectionHelper;
        // noinspection JSUnresolvedVariable,JSUnresolvedFunction
        var classToNameMap = ReflectionHelper.findField(TileEntity.class, "field_145853_j");

        /**
         * @param tile {i_net_minecraft_tileentity_TileEntity}
         * @return {string}
         */
        getTileEntityName = function (tile) {
            // noinspection JSUnresolvedFunction
            return classToNameMap.get(null, tile.getClass())
        }

    } else {
        /**
         * @param tile {i_net_minecraft_tileentity_TileEntity}
         * @return {string}
         */
        getTileEntityName = function (tile) {
            // noinspection JSUnresolvedFunction
            return TileEntity.func_190559_a(tile.getClass()) //RegistryNamespaced.getNameForObject
                .toString();
        }
    }

    var getWNBTCompoundByTile = __rtm_mc_wrapper__.versioned_value(
        /**
         * @param tile {b_net_minecraft_tileentity_TileEntity} tile
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

    var makeTileEntity = __rtm_mc_wrapper__.versioned_value(
        /**
         * @param tileData {WNBTCompound}
         * @return b_net_minecraft_tileentity_TileEntity
         */
        function (tileData) {
            return TileEntity.func_145827_c(tileData.__real__)
        },
        function (tileData) {
            return TileEntity.func_190200_a(null, tileData.__real__)
        }
    )

    ////////////////////////////////

    /**
     * @typedef {TileEntity|NBTCompoundConvertible|WNBTCompound} WTileEntityParams
     */

    /**
     * @constructor
     * @param param {WTileEntityParams}
     * @returns {WTileEntity}
     */
    function WTileEntity(param) {
        if (!(this instanceof WTileEntity)) {
            NGTLog.debug("you should use WTileEntity as constructor")
            return new WTileEntity(param)
        }

        /** @type WNBTCompound */
        var nbt;
        /** @type i_net_minecraft_tileentity_TileEntity */
        var tile;
        if (param instanceof WNBTCompound) {
            nbt = param;
            tile = makeTileEntity(nbt);
        } else if (param instanceof TileEntity) {
            // noinspection JSValidateTypes
            tile = param;
        } else {
            nbt = new WNBTCompound(param)
            tile = makeTileEntity(nbt);
        }
        if (!(tile instanceof TileEntity))
            throw Error(param + " canot convert to TileEntity")
        this.__nbt__ = nbt;
        /** @type i_net_minecraft_tileentity_TileEntity */
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
            return WWorld.wrap(this.tile.func_145831_w());
        }
    })

    Object.defineProperty(WTileEntity.prototype, "x", {
        get: __rtm_mc_wrapper__.versioned_value(
            function () {
                return this.tile.field_145851_c
            },
            function () {
                return this.tile.func_174877_v()
                    .func_177958_n()
            }
        )
    })

    Object.defineProperty(WTileEntity.prototype, "y", {
        get: __rtm_mc_wrapper__.versioned_value(
            function () {
                return this.tile.field_145848_d // TileEntity.yCoord
            },
            function () {
                return this.tile.func_174877_v() // TileEntity.getPos()
                    .func_177956_o() // BlockPos.getY() = Vec3i.getY()
            }
        )
    })

    Object.defineProperty(WTileEntity.prototype, "z", {
        get: __rtm_mc_wrapper__.versioned_value(
            function () {
                return this.tile.field_145849_e // TileEntity.zCoord
            },
            function () {
                return this.tile.func_174877_v() // TileEntity.getPos()
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

    WTileEntity.wrap = function (tile) {
        if (tile == null) return null;
        else return new WTileEntity(tile);
    }

    global.WTileEntity = WTileEntity;
})
