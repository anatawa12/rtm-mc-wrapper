package com.anatawa12.dtsGenerator

import org.objectweb.asm.Opcodes

object EmptyPackageRemover {
    fun removeEmptyPackage(genProcess: GenProcessArgs, classes: ClassesManager) {
        removeEmptyPackageImpl(classes.rootPackage) {}
    }

    /**
     * @return isRemoved
     */
    private fun removeEmptyPackageImpl(thePackage: ThePackage, remover: () -> Unit): Boolean {
        val itr = thePackage.children.values.iterator()
        var willRemove = true
        while (itr.hasNext()) {
            val child = itr.next()
            if (!GenUtil.elementFilter(child)) continue
            when (child) {
                TheDuplicated -> willRemove = false
                is ThePackage -> {
                    if (!removeEmptyPackageImpl(child, itr::remove))
                        willRemove = false
                }
                is TheClass -> {
                    if (child.accessExternally.and(Opcodes.ACC_PUBLIC) != 0)
                        willRemove = false
                }
                is TheMethods -> error("method cannot child of package")
                is TheField -> error("field cannot child of package")
            }
        }
        if (willRemove) 
            remover()
        return willRemove
    }
}
