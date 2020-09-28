//
// MIT License
//
// Copyright (c) 2020 Alexander Söderberg & Contributors
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
package cloud.commandframework.exceptions;

import cloud.commandframework.arguments.CommandArgument;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Exception sent when a command sender inputs invalid command syntax
 */
@SuppressWarnings("unused")
public class InvalidSyntaxException extends CommandParseException {

    private final String correctSyntax;

    /**
     * Create a new invalid syntax exception instance
     *
     * @param correctSyntax Expected syntax
     * @param commandSender Sender that sent the command
     * @param currentChain  Chain leading up to issue
     */
    public InvalidSyntaxException(@Nonnull final String correctSyntax,
                                  @Nonnull final Object commandSender,
                                  @Nonnull final List<CommandArgument<?, ?>> currentChain) {
        super(commandSender, currentChain);
        this.correctSyntax = correctSyntax;
    }

    /**
     * Get the correct syntax of the command
     *
     * @return Correct command syntax
     */
    @Nonnull
    public String getCorrectSyntax() {
        return this.correctSyntax;
    }


    @Override
    public final String getMessage() {
        return String.format("Invalid command syntax. Correct syntax is: %s", this.correctSyntax);
    }

}