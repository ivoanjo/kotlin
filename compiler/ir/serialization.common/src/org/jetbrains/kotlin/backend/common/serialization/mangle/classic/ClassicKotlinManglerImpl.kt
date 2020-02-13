/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.common.serialization.mangle.classic

import org.jetbrains.kotlin.backend.common.serialization.mangle.*
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.util.KotlinMangler


abstract class ClassicKotlinManglerImpl : AbstractKotlinMangler<IrDeclaration>(), KotlinMangler.ClassicMangler {

    override val IrDeclaration.mangleString: String
        get() = getMangleComputer("").computeMangle(this)

    override fun IrDeclaration.isExported(): Boolean = getExportChecker().check(this, SpecialDeclarationType.REGULAR)


//    override fun getExportChecker(): KotlinExportChecker<IrDeclaration> {
//        TODO("Not yet implemented")
//    }
//
//    override fun getMangleComputer(prefix: String): KotlinMangleComputer<IrDeclaration> {
//        TODO("Not yet implemented")
//    }

//    override val manglerName = "Classic"
//
//    override val IrDeclaration.mangleString: String
//        get() = mangleImpl(this)
//
//    private fun mangleImpl(declaration: IrDeclaration): String {
//        return getMangleComputer("").computeMangle(declaration)
//    }
//
//    // We can't call "with (super) { this.isExported() }" in children.
//    // So provide a hook.
//    protected open fun IrDeclaration.isPlatformSpecificExported(): Boolean = false
//
//    override fun IrDeclaration.isExported(): Boolean {
//        return getExportChecker().check(this, SpecialDeclarationType.REGULAR)
//    }

//
//    private fun mapTypeParameterContainers(element: IrElement): List<IrDeclaration> {
//        return collectTypeParameterContainers(element)
//    }
//
//    protected open fun mangleTypeParameter(typeParameter: IrTypeParameter, typeParameterNamer: (IrTypeParameter) -> String?): String {
//        return typeParameterNamer(typeParameter) ?: error("No parent for ${typeParameter.render()}")
//    }
//
//    protected fun acyclicTypeMangler(type: IrType, typeParameterNamer: (IrTypeParameter) -> String?): String {
//
//        var hashString = type.classifierOrNull?.let {
//            when (it) {
//                is IrClassSymbol -> it.owner.fqNameForIrSerialization.asString()
//                is IrTypeParameterSymbol -> typeParameterNamer(it.owner)
//                else -> error("Unexpected type constructor")
//            }
//        } ?: "<dynamic>"
//
//        when (type) {
//            is IrSimpleType -> {
//                if (!type.arguments.isEmpty()) {
//                    hashString += "<${type.arguments.map {
//                        when (it) {
//                            is IrStarProjection -> "*"
//                            is IrTypeProjection -> {
//                                val variance = it.variance.label
//                                val projection = if (variance == "") "" else "${variance}|"
//                                projection + acyclicTypeMangler(it.type, typeParameterNamer)
//                            }
//                            else -> error(it)
//                        }
//                    }.joinToString(",")}>"
//                }
//
//                if (type.hasQuestionMark) hashString += "?"
//            }
//            !is IrDynamicType -> {
//                error(type)
//            }
//        }
//        return hashString
//    }
//
//    protected fun typeToHashString(type: IrType, typeParameterNamer: (IrTypeParameter) -> String?) =
//        acyclicTypeMangler(type, typeParameterNamer)
//
//    fun IrValueParameter.extensionReceiverNamePart(typeParameterNamer: (IrTypeParameter) -> String?): String =
//        "@${typeToHashString(this.type, typeParameterNamer)}"
//
//    open fun IrFunction.valueParamsPart(typeParameterNamer: (IrTypeParameter) -> String?): String {
//        return this.valueParameters.joinToString(";", "(", ")") {
//            "${typeToHashString(it.type, typeParameterNamer)}${if (it.isVararg) "..." else ""}"
//        }
//    }
//
//    open fun IrFunction.typeParamsPart(typeParameters: List<IrTypeParameter>, typeParameterNamer: (IrTypeParameter) -> String?): String {
////        if (typeParameters.isEmpty()) return ""
//
//        fun mangleTypeParameter(index: Int, typeParameter: IrTypeParameter): String {
//            // We use type parameter index instead of name since changing name is not a binary-incompatible change
//            return typeParameter.superTypes.joinToString("&", "$index§<", ">") {
//                acyclicTypeMangler(it, typeParameterNamer)
//            }
//        }
//
//        return typeParameters.withIndex().joinToString(";", "{", "}") { (i, tp) ->
//            mangleTypeParameter(i, tp)
//        }
//    }
//
//    open fun IrFunction.signature(typeParameterNamer: (IrTypeParameter) -> String?): String {
//        val extensionReceiverPart = this.extensionReceiverParameter?.extensionReceiverNamePart(typeParameterNamer) ?: ""
//        val valueParamsPart = this.valueParamsPart(typeParameterNamer)
//        // Distinguish value types and references - it's needed for calling virtual methods through bridges.
//        // Also is function has type arguments - frontend allows exactly matching overrides.
//        val signatureSuffix =
//            when {
//                this is IrConstructor -> ""
////                this.typeParameters.isNotEmpty() -> "Generic"
////                returnType.isInlined -> "ValueType"
//                !returnType.isUnit() -> typeToHashString(returnType, typeParameterNamer)
//                else -> ""
//            }
//
//        val typesParamsPart = this.typeParamsPart(typeParameters, typeParameterNamer)
//
//        return "$extensionReceiverPart$valueParamsPart$typesParamsPart$signatureSuffix"
//    }
//
//    open val IrFunction.platformSpecificFunctionName: String? get() = null
//
//    // TODO: rename to indicate that it has signature included
//    override val IrFunction.functionName: String
//        get() {
//            // TODO: Again. We can't call super in children, so provide a hook for now.
//            this.platformSpecificFunctionName?.let { return it }
//
//            val typeContainerList = mapTypeParameterContainers(this)
//            val typeParameterNamer: (IrTypeParameter) -> String? = {
//                val eParent = it.effectiveParent()
//                typeContainerList.indexOf(eParent).let { ci ->
//                    "$ci:${it.index}"
//                }
//            }
//
//            if (isAccessor) {
//                val property = (this as IrSimpleFunction).correspondingPropertySymbol!!.owner
//                val suffix = if (this === property.getter) ":getter:" else ":setter:"
//                val extensionReceiverPart = extensionReceiverParameter?.extensionReceiverNamePart(typeParameterNamer) ?: ""
//
//                return property.name.asString() + extensionReceiverPart + suffix
//            }
//
//            val name = this.name.mangleIfInternal(this.module, this.visibility)
//            return "$name${signature(typeParameterNamer)}"
//        }
//
//    override val Long.isSpecial: Boolean
//        get() = specialHashes.contains(this)
//
//    fun Name.mangleIfInternal(moduleDescriptor: ModuleDescriptor, visibility: Visibility): String =
//        if (visibility != Visibilities.INTERNAL) {
//            this.asString()
//        } else {
//            val moduleName = moduleDescriptor.name.asString()
//                .let { it.substring(1, it.lastIndex) } // Remove < and >.
//
//            "$this\$$moduleName"
//        }
//
//    val IrField.symbolName: String
//        get() {
//            val containingDeclarationPart = parent.fqNameForIrSerialization.let {
//                if (it.isRoot) "" else "$it."
//            }
//            return "kfield:$containingDeclarationPart$name"
//
//        }
//
//    val IrClass.typeInfoSymbolName: String
//        get() {
//            assert(isExportedImpl(this))
//            if (isBuiltInFunction(this))
//                return KotlinMangler.functionClassSymbolName(name)
//            return "kclass:" + this.fqNameForIrSerialization.toString()
//        }
//
//    val IrTypeParameter.symbolName: String
//        get() {
//
//            val parentDeclaration = (parent as? IrSimpleFunction)?.correspondingPropertySymbol?.owner ?: parent
//            val containingDeclarationPart = when (parentDeclaration) {
//                is IrDeclaration -> parentDeclaration.uniqSymbolName()
//                else -> error("Unexpected type parameter parent")
//            }
//            return "ktypeparam:$containingDeclarationPart@$index"
//        }
//
//    val IrTypeAlias.symbolName: String
//        get() {
//            val containingDeclarationPart = parent.fqNameForIrSerialization.let {
//                if (it.isRoot) "" else "$it."
//            }
//            return "ktypealias:$containingDeclarationPart$name"
//        }
//
//// This is a little extension over what's used in real mangling
//// since some declarations never appear in the bitcode symbols.
//
//    internal fun IrDeclaration.uniqSymbolName(): String = when (this) {
//        is IrFunction -> this.uniqFunctionName
//        is IrProperty -> this.symbolName
//        is IrClass -> this.typeInfoSymbolName
//        is IrField -> this.symbolName
//        is IrEnumEntry -> this.symbolName
//        is IrTypeParameter -> this.symbolName
//        is IrTypeAlias -> this.symbolName
//        else -> error("Unexpected exported declaration: $this")
//    } + expectPart
//
//    // TODO: need to figure out the proper OptionalExpectation behavior
//    private val IrDeclaration.expectPart get() = if (this.isProperExpect) "#expect" else ""
//
//    private val IrDeclarationParent.fqNameUnique: FqName
//        get() = when (this) {
//            is IrPackageFragment -> this.fqName
//            is IrDeclaration -> this.parent.fqNameUnique.child(this.uniqName)
//            else -> error(this)
//        }
//
//    private val IrDeclaration.uniqName: Name
//        get() = when (this) {
//            is IrSimpleFunction -> Name.special("<${this.uniqFunctionName}>")
//            else -> this.nameForIrSerialization
//        }
//
//    private val IrProperty.symbolName: String
//        get() {
//            val typeContainerList = mapTypeParameterContainers(this)
//            val typeParameterNamer: (IrTypeParameter) -> String = {
//                val eParent = it.effectiveParent()
//                typeContainerList.indexOf(eParent).let { ci ->
//                    "$ci:${it.index}"
//                }
//            }
//
//            val extensionReceiver: String = getter?.extensionReceiverParameter?.extensionReceiverNamePart(typeParameterNamer) ?: ""
//
//            val containingDeclarationPart = parent.fqNameForIrSerialization.let {
//                if (it.isRoot) "" else "$it."
//            }
//            return "kprop:$containingDeclarationPart$extensionReceiver$name"
//        }
//
//    private val IrEnumEntry.symbolName: String
//        get() {
//            val containingDeclarationPart = parent.fqNameForIrSerialization.let {
//                if (it.isRoot) "" else "$it."
//            }
//            return "kenumentry:$containingDeclarationPart$name"
//        }
//
//    // This is basicly the same as .symbolName, but disambiguates external functions with the same C name.
//// In addition functions appearing in fq sequence appear as <full signature>.
//    private val IrFunction.uniqFunctionName: String
//        get() {
//            if (isBuiltInFunction(this))
//                return KotlinMangler.functionInvokeSymbolName(parentAsClass.name)
//            val parent = this.parent
//
//            val containingDeclarationPart = parent.fqNameUnique.let {
//                if (it.isRoot) "" else "$it."
//            }
//
//            return "kfun:$containingDeclarationPart#$functionName"
//        }
//
//    private val specialHashes = listOf("Function", "KFunction", "SuspendFunction", "KSuspendFunction")
//        .flatMap { name ->
//            (0..255).map { KotlinMangler.functionClassSymbolName(Name.identifier(name + it)) }
//        }.map { it.hashMangle }
//        .toSet()
}
