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
package com.intellectualsites.commands.arguments;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

/**
 * {@link CommandSyntaxFormatter} implementation that uses the following rules:
 * <ul>
 *     <li>static arguments are serialized as their name, without a bracket</li>
 *     <li>required arguments are serialized as their name, surrounded by angle brackets</li>
 *     <li>optional arguments are serialized as their name, surrounded by square brackets</li>
 * </ul>
 *
 * @param <C> Command sender type
 */
public class StandardCommandSyntaxFormatter<C> implements CommandSyntaxFormatter<C> {

    @Nonnull
    @Override
    public final String apply(@Nonnull final List<CommandArgument<C, ?>> commandArguments) {
        final StringBuilder stringBuilder = new StringBuilder();
        final Iterator<CommandArgument<C, ?>> iterator = commandArguments.iterator();
        while (iterator.hasNext()) {
            final CommandArgument<?, ?> commandArgument = iterator.next();
            if (commandArgument instanceof StaticArgument) {
                stringBuilder.append(commandArgument.getName());
            } else {
                if (commandArgument.isRequired()) {
                    stringBuilder.append("<").append(commandArgument.getName()).append(">");
                } else {
                    stringBuilder.append("[").append(commandArgument.getName()).append("]");
                }
            }
            if (iterator.hasNext()) {
                stringBuilder.append(" ");
            }
        }
        return stringBuilder.toString();
    }

}