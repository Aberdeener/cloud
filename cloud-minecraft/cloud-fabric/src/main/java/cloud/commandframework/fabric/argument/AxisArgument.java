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
package cloud.commandframework.fabric.argument;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.brigadier.argument.WrappedBrigadierParser;
import cloud.commandframework.context.CommandContext;
import io.leangen.geantyref.TypeToken;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import net.minecraft.commands.arguments.coordinates.SwizzleArgument;
import net.minecraft.core.Direction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An argument for a set of {@link net.minecraft.core.Direction.Axis axes}, described in Vanilla as a "swizzle".
 *
 * @param <C> the sender type
 * @since 1.5.0
 */
public final class AxisArgument<C> extends CommandArgument<C, EnumSet<Direction.Axis>> {

    private static final TypeToken<EnumSet<Direction.Axis>> TYPE = new TypeToken<EnumSet<Direction.Axis>>() {
    };

    AxisArgument(
            final boolean required,
            final @NonNull String name,
            final @NonNull String defaultValue,
            final @Nullable BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider,
            final @NonNull ArgumentDescription defaultDescription
    ) {
        super(
                required,
                name,
                new WrappedBrigadierParser<>(SwizzleArgument.swizzle()),
                defaultValue,
                TYPE,
                suggestionsProvider,
                defaultDescription
        );
    }

    /**
     * Create a new {@link Builder}.
     *
     * @param name Name of the argument
     * @param <C>  Command sender type
     * @return Created builder
     * @since 1.5.0
     */
    public static <C> @NonNull Builder<C> builder(final @NonNull String name) {
        return new Builder<>(name);
    }

    /**
     * Create a new required {@link AxisArgument}.
     *
     * @param name Component name
     * @param <C>  Command sender type
     * @return Created argument
     * @since 1.5.0
     */
    public static <C> @NonNull AxisArgument<C> of(final @NonNull String name) {
        return AxisArgument.<C>builder(name).asRequired().build();
    }

    /**
     * Create a new optional {@link AxisArgument}.
     *
     * @param name Component name
     * @param <C>  Command sender type
     * @return Created argument
     * @since 1.5.0
     */
    public static <C> @NonNull AxisArgument<C> optional(final @NonNull String name) {
        return AxisArgument.<C>builder(name).asOptional().build();
    }

    /**
     * Create a new optional {@link AxisArgument} with the specified default value.
     *
     * @param name         Argument name
     * @param defaultValue Default axes to include
     * @param <C>          Command sender type
     * @return Created argument
     * @since 1.5.0
     */
    public static <C> @NonNull AxisArgument<C> optional(
            final @NonNull String name,
            final @NonNull Set<Direction.@NonNull Axis> defaultValue
    ) {
        return AxisArgument.<C>builder(name).asOptionalWithDefault(defaultValue).build();
    }


    /**
     * Builder for {@link AxisArgument}.
     *
     * @param <C> sender type
     * @since 1.5.0
     */
    public static final class Builder<C> extends TypedBuilder<C, EnumSet<Direction.Axis>, Builder<C>> {

        Builder(final @NonNull String name) {
            super(TYPE, name);
        }

        /**
         * Build a new {@link AxisArgument}.
         *
         * @return Constructed argument
         * @since 1.5.0
         */
        @Override
        public @NonNull AxisArgument<C> build() {
            return new AxisArgument<>(
                    this.isRequired(),
                    this.getName(),
                    this.getDefaultValue(),
                    this.getSuggestionsProvider(),
                    this.getDefaultDescription()
            );
        }

        /**
         * Sets the command argument to be optional, with the specified default value.
         *
         * @param defaultValue default value
         * @return this builder
         * @see CommandArgument.Builder#asOptionalWithDefault(String)
         * @since 1.5.0
         */
        public @NonNull Builder<C> asOptionalWithDefault(final @NonNull Set<Direction.@NonNull Axis> defaultValue) {
            if (defaultValue.isEmpty()) {
                throw new IllegalArgumentException("Default value must include at least one Axis!");
            }
            final StringBuilder builder = new StringBuilder();
            for (final Direction.Axis axis : defaultValue) {
                builder.append(axis.getName());
            }
            return this.asOptionalWithDefault(builder.toString());
        }

    }

}
