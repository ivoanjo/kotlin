/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.common.serialization

import org.jetbrains.kotlin.backend.common.LoggingContext
import org.jetbrains.kotlin.builtins.FunctionInterfacePackageFragment
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.library.SerializedIrFile
import org.jetbrains.kotlin.library.SerializedIrModule

abstract class IrModuleSerializer<F : IrFileSerializer>(protected val logger: LoggingContext) {
    abstract fun createSerializerForFile(file: IrFile): F

    /**
     * Some files may contain declarations that should not be serialized.
     * For example, it might be classes that should be generated instead of deserialization.
     */
    protected open fun backendSpecificFileFilter(file: IrFile): Boolean =
        true

    private fun serializeIrFile(file: IrFile): SerializedIrFile {
        val fileSerializer = createSerializerForFile(file)
        return fileSerializer.serializeIrFile(file)
    }

    fun serializedIrModule(module: IrModuleFragment): SerializedIrModule {
        val serializedFiles = module.files
            .filter { it.packageFragmentDescriptor !is FunctionInterfacePackageFragment }
            .filter(this::backendSpecificFileFilter)
            .map(this::serializeIrFile)
        return SerializedIrModule(serializedFiles)
    }
}