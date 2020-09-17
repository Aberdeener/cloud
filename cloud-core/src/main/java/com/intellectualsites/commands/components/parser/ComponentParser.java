//
// MIT License
//
// Copyright (c) 2020 Alexander Söderberg
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
package com.intellectualsites.commands.components.parser;

import com.intellectualsites.commands.context.CommandContext;
import com.intellectualsites.commands.sender.CommandSender;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

/**
 * Parser that parses strings into values of a specific type
 *
 * @param <C> Command sender type
 * @param <T> Value type
 */
@FunctionalInterface
public interface ComponentParser<C extends CommandSender, T> {

    /**
     * Parse command input into a command result
     *
     * @param commandContext Command context
     * @param inputQueue     The queue of arguments
     * @return Parsed command result
     */
    @Nonnull
    ComponentParseResult<T> parse(@Nonnull CommandContext<C> commandContext, @Nonnull Queue<String> inputQueue);

    /**
     * Get a list of suggested arguments that would be correctly parsed by this parser
     *
     * @param commandContext Command context
     * @param input          Input string
     * @return List of suggestions
     */
    @Nonnull
    default List<String> suggestions(@Nonnull final CommandContext<C> commandContext, @Nonnull final String input) {
        return Collections.emptyList();
    }

    /**
     * Check whether or not this component parser is context free. A context free
     * parser will not use the provided command context, and so supports impromptu parsing
     *
     * @return {@code true} if the parser is context free, else {@code false}
     */
    default boolean isContextFree() {
        return false;
    }

}