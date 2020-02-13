/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.konan.diagnostics


import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.DescriptorToSourceUtils
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.constants.ArrayValue
import org.jetbrains.kotlin.resolve.constants.ConstantValue
import org.jetbrains.kotlin.resolve.descriptorUtil.firstArgument
import org.jetbrains.kotlin.utils.DFS

object NativeThrowsChecker : DeclarationChecker {
    private val throwsFqName = FqName("kotlin.native.Throws")

    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
        val throwsAnnotation = descriptor.annotations.findAnnotation(throwsFqName)
        if (throwsAnnotation == null) {
            checkForIncompatibleMultipleInheritance(declaration, descriptor, context)
            return
        }

        val reportLocation = DescriptorToSourceUtils.getSourceFromAnnotation(throwsAnnotation) ?: declaration

        if (!checkThrowsOnOverride(descriptor, context, throwsAnnotation, reportLocation)) return

        if (throwsAnnotation.getVariadicArguments().isEmpty()) {
            context.trace.report(ErrorsNative.THROWS_LIST_EMPTY.on(reportLocation))
        }
    }

    private fun checkThrowsOnOverride(
        descriptor: DeclarationDescriptor,
        context: DeclarationCheckerContext,
        throwsAnnotation: AnnotationDescriptor,
        reportLocation: KtElement
    ): Boolean {
        if (descriptor !is CallableDescriptor || descriptor.overriddenDescriptors.isEmpty()) {
            // Not an override.
            return true
        }

        val reported = mutableSetOf<ThrowsFilter>()
        val ownThrows = decodeThrowsFilter(throwsAnnotation)

        for (overridden in descriptor.overriddenDescriptors) {
            val inheritedThrows = findInheritedThrows(overridden)
            if (ownThrows !in inheritedThrows.values) {
                // Own @Throws differs from all the inherited ones. Report any:
                val (member, throws) = inheritedThrows.entries.firstOrNull()
                    ?: continue // Should not happen though.

                if (throws !in reported) {
                    reported.add(throws)
                    context.trace.report(ErrorsNative.INCOMPATIBLE_THROWS_OVERRIDE.on(reportLocation, member.containingDeclaration))
                }
            }
        }

        return reported.isEmpty()
    }

    private fun checkForIncompatibleMultipleInheritance(
        declaration: KtDeclaration,
        descriptor: DeclarationDescriptor,
        context: DeclarationCheckerContext
    ) {
        if (descriptor !is CallableDescriptor) return

        if (descriptor.overriddenDescriptors.size < 2) return // No multiple inheritance here.

        val incompatible = findInheritedThrows(descriptor).entries.distinctBy { it.value }.map { it.key }

        if (incompatible.size < 2) return

        context.trace.report(ErrorsNative.INCOMPATIBLE_THROWS_INHERITED.on(declaration, incompatible.map { it.containingDeclaration }))
    }

    private fun findInheritedThrows(descriptor: CallableDescriptor): Map<CallableDescriptor, ThrowsFilter> {
        val result = mutableMapOf<CallableDescriptor, ThrowsFilter>()

        DFS.dfs(
            listOf(descriptor),
            { current -> current.overriddenDescriptors },
            object : DFS.AbstractNodeHandler<CallableDescriptor, Unit>() {
                override fun beforeChildren(current: CallableDescriptor): Boolean {
                    val throwsAnnotation = current.annotations.findAnnotation(throwsFqName)
                    return if (throwsAnnotation == null && !current.overriddenDescriptors.isEmpty()) {
                        // Visit overridden members:
                        true
                    } else {
                        // Take current and ignore overridden:
                        result[current.original] = decodeThrowsFilter(throwsAnnotation)
                        false
                    }
                }

                override fun result() {}
            })

        return result
    }

    private fun AnnotationDescriptor.getVariadicArguments(): List<ConstantValue<*>> {
        val argument = this.firstArgument() as? ArrayValue ?: return emptyList()
        return argument.value
    }

    private fun decodeThrowsFilter(throwsAnnotation: AnnotationDescriptor?) =
        ThrowsFilter(throwsAnnotation?.getVariadicArguments()?.toSet())

    private data class ThrowsFilter(val classes: Set<ConstantValue<*>>?)

}
