//
// MIT License
//
// Copyright (c) 2021 Alexander Söderberg & Contributors
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package cloud.commandframework.kotlin.coroutines.annotations

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.annotations.MethodCommandExecutionHandler
import cloud.commandframework.context.CommandContext
import cloud.commandframework.execution.CommandExecutionCoordinator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.concurrent.CompletableFuture
import java.util.function.Predicate
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.full.callSuspend
import kotlin.reflect.jvm.kotlinFunction

/**
 * Adds coroutine support to the [AnnotationParser].
 *
 * @param scope coroutine scope
 * @param context coroutine context
 * @return annotation parser
 * @since 1.6.0
 */
public fun <C> AnnotationParser<C>.installCoroutineSupport(
    scope: CoroutineScope = GlobalScope,
    context: CoroutineContext = EmptyCoroutineContext
): AnnotationParser<C> {
    if (manager().commandExecutionCoordinator() is CommandExecutionCoordinator.SimpleCoordinator) {
        RuntimeException(
            """You are highly advised to not use the simple command execution coordinator together
                            with coroutine support. Consider using the asynchronous command execution coordinator instead."""
        )
            .printStackTrace()
    }

    val predicate = Predicate<Method> { it.kotlinFunction?.isSuspend == true }
    registerCommandExecutionMethodFactory(predicate) {
        KotlinMethodCommandExecutionHandler(scope, context, it)
    }

    return this
}

private class KotlinMethodCommandExecutionHandler<C>(
    private val coroutineScope: CoroutineScope,
    private val coroutineContext: CoroutineContext,
    context: CommandMethodContext<C>
) : MethodCommandExecutionHandler<C>(context) {

    private val paramsWithoutContinuation = parameters().filterNot { Continuation::class.java == it.type }.toTypedArray()

    override fun executeFuture(commandContext: CommandContext<C>): CompletableFuture<Void?> {
        val instance = context().instance()
        val params = createParameterValues(
            commandContext,
            commandContext.flags(),
            paramsWithoutContinuation
        )

        // We need to propagate exceptions to the caller.
        return coroutineScope.future(this@KotlinMethodCommandExecutionHandler.coroutineContext) {
            try {
                context().method().kotlinFunction!!.callSuspend(instance, *params.toTypedArray())
            } catch (e: InvocationTargetException) { // unwrap invocation exception
                e.cause?.let { throw it } ?: throw e // if cause exists, throw, else rethrow invocation exception
            }
            null
        }
    }
}
