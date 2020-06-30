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

rmw.includeGuard("mc-wrapper:entity", ["mc-wrapper:world", "mc-wrapper:nbt"], function (global) {

    var Entity = Packages.net.minecraft.entity.Entity
    var EntityList = Packages.net.minecraft.entity.EntityList
    var NGTLog = Packages.jp.ngt.ngtlib.io.NGTLog

    var getWNBTCompoundByEntity = __rtm_mc_wrapper__.versioned_value(
        /**
         * @param entity {i_net_minecraft_entity_Entity} entity
         * @return {WNBTCompound}
         */
        function (entity) {
            var compound = new WNBTCompound();
            entity.func_70109_d(compound.__real__);
            compound.set("id", EntityList.func_75621_b(entity))
            return compound;
        },
        function (entity) {
            var compound = new WNBTCompound();
            entity.func_189511_e(compound.__real__);
            compound.set("id", EntityList.func_191301_a(entity).toString())
            return compound;
        }
    )

    /**
     * @param tileData {WNBTCompound}
     * @param world {WWorld}
     * @return i_net_minecraft_entity_Entity
     */
    var makeEntity = function (tileData, world) {
        return EntityList.func_75615_a(tileData.__real__, world.__real__)
    }

    ////////////////////////////////

    /**
     * @typedef {Entity|NBTCompoundConvertible|WNBTCompound} WEntityParams
     */

    /**
     * @constructor
     * @param param {WEntityParams}
     * @param [world] {WWorld}
     * @returns {WEntity}
     */
    function WEntity(param, world) {
        if (!(this instanceof WEntity)) {
            NGTLog.debug("you should use WEntity as constructor")
            return new WEntity(param, world)
        }

        /** @type WNBTCompound */
        var nbt;
        /** @type i_net_minecraft_entity_Entity */
        var entity;
        if (param instanceof WNBTCompound) {
            nbt = param;
            entity = makeEntity(nbt, world);
        } else if (param instanceof Entity) {
            // noinspection JSValidateTypes
            entity = param;
        } else {
            nbt = new WNBTCompound(param)
            entity = makeEntity(nbt, world);
        }
        this.__nbt__ = nbt;
        /** @type i_net_minecraft_entity_Entity */
        this.entity = entity;
    }

    Object.defineProperty(WEntity.prototype, "nbt", {
        get: function () {
            if (this.__nbt__ == null) {
                this.__nbt__ = getWNBTCompoundByEntity(this.entity);
            }
            return this.__nbt__
        }
    })

    WEntity.prototype.resetNBT = function () {
        this.__nbt__ = null;
    }

    WEntity.prototype.resetAndGetNBT = function () {
        this.resetNBT()
        return this.nbt
    }

    Object.defineProperty(WEntity.prototype, "world", {
        get: function () {
            return WWorld.wrap(this.world.field_70170_p);
        }
    })

    Object.defineProperty(WEntity.prototype, "x", {
        get: function () {
            return this.entity.field_70165_t
        }
    })

    Object.defineProperty(WEntity.prototype, "y", {
        get: function () {
            return this.entity.field_70163_u
        }
    })

    Object.defineProperty(WEntity.prototype, "z", {
        get: function () {
            return this.entity.field_70161_v
        }
    })

    /**
     * the id of the tile entity
     *
     * @type string
     */
    Object.defineProperty(WEntity.prototype, "entityId", {
        get: function () {
            if (this.__entityId__ == null) {
                this.__entityId__ = EntityList.func_75621_b(this.entity)
            }
            return this.__entityId__;
        }
    })

    global.WEntity = WEntity;
})
