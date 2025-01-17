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
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import cloud.commandframework.fabric.FabricCaptionKeys;
import cloud.commandframework.fabric.FabricCommandContextKeys;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.leangen.geantyref.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Argument for getting a values from a {@link Registry}.
 *
 * <p>Both static and dynamic registries are supported.</p>
 *
 * @param <C> the command sender type
 * @param <V> the registry entry type
 * @since 1.5.0
 */
public class RegistryEntryArgument<C, V> extends CommandArgument<C, V> {

    private static final String NAMESPACE_MINECRAFT = "minecraft";

    RegistryEntryArgument(
            final boolean required,
            final @NonNull String name,
            final @NonNull ResourceKey<? extends Registry<V>> registry,
            final @NonNull String defaultValue,
            final @NonNull TypeToken<V> valueType,
            final @Nullable BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider,
            final @NonNull ArgumentDescription defaultDescription
    ) {
        super(
                required,
                name,
                new Parser<>(registry),
                defaultValue,
                valueType,
                suggestionsProvider,
                defaultDescription
        );
    }

    /**
     * Create a new {@link Builder}.
     *
     * @param name     Name of the argument
     * @param type     The type of registry entry
     * @param registry A key for the registry to get values from
     * @param <C>      Command sender type
     * @param <V>      Registry entry type
     * @return Created builder
     * @since 1.5.0
     */
    public static <C, V> RegistryEntryArgument.@NonNull Builder<C, V> newBuilder(
            final @NonNull String name,
            final @NonNull Class<V> type,
            final @NonNull ResourceKey<? extends Registry<V>> registry
    ) {
        return new Builder<>(registry, type, name);
    }

    /**
     * Create a new {@link Builder}.
     *
     * @param name     Name of the argument
     * @param type     The type of registry entry
     * @param registry A key for the registry to get values from
     * @param <C>      Command sender type
     * @param <V>      Registry entry type
     * @return Created builder
     * @since 1.5.0
     */
    public static <C, V> RegistryEntryArgument.@NonNull Builder<C, V> newBuilder(
            final @NonNull String name,
            final @NonNull TypeToken<V> type,
            final @NonNull ResourceKey<? extends Registry<V>> registry
    ) {
        return new Builder<>(registry, type, name);
    }

    /**
     * Create a new required {@link RegistryEntryArgument}.
     *
     * @param name     Argument name
     * @param type     The type of registry entry
     * @param registry A key for the registry to get values from
     * @param <C>      Command sender type
     * @param <V>      Registry entry type
     * @return Created argument
     * @since 1.5.0
     */
    public static <C, V> @NonNull RegistryEntryArgument<C, V> of(
            final @NonNull String name,
            final @NonNull Class<V> type,
            final @NonNull ResourceKey<? extends Registry<V>> registry
    ) {
        return RegistryEntryArgument.<C, V>newBuilder(name, type, registry).asRequired().build();
    }

    /**
     * Create a new optional {@link RegistryEntryArgument}.
     *
     * @param name     Argument name
     * @param type     The type of registry entry
     * @param registry A key for the registry to get values from
     * @param <C>      Command sender type
     * @param <V>      Registry entry type
     * @return Created argument
     * @since 1.5.0
     */
    public static <C, V> @NonNull RegistryEntryArgument<C, V> optional(
            final @NonNull String name,
            final @NonNull Class<V> type,
            final @NonNull ResourceKey<? extends Registry<V>> registry
    ) {
        return RegistryEntryArgument.<C, V>newBuilder(name, type, registry).asOptional().build();
    }

    /**
     * Create a new optional {@link RegistryEntryArgument} with the specified default value.
     *
     * @param name         Argument name
     * @param type         The type of registry entry
     * @param registry     A key for the registry to get values from
     * @param defaultValue Default value
     * @param <C>          Command sender type
     * @param <V>          Registry entry type
     * @return Created argument
     * @since 1.5.0
     */
    public static <C, V> @NonNull RegistryEntryArgument<C, V> optional(
            final @NonNull String name,
            final @NonNull Class<V> type,
            final @NonNull ResourceKey<? extends Registry<V>> registry,
            final @NonNull ResourceKey<V> defaultValue
    ) {
        return RegistryEntryArgument.<C, V>newBuilder(name, type, registry)
                .asOptionalWithDefault(defaultValue)
                .build();
    }

    /**
     * A parser for values stored in a {@link Registry}.
     *
     * @param <C> Command sender type
     * @param <V> Registry entry type
     * @since 1.5.0
     */
    public static final class Parser<C, V> implements ArgumentParser<C, V> {

        private final ResourceKey<? extends Registry<V>> registryIdent;

        /**
         * Create a new {@link Parser}.
         *
         * @param registryIdent the registry identifier
         * @since 1.5.0
         */
        public Parser(final ResourceKey<? extends Registry<V>> registryIdent) {
            this.registryIdent = requireNonNull(registryIdent, "registryIdent");
        }

        @Override
        public @NonNull ArgumentParseResult<@NonNull V> parse(
                @NonNull final CommandContext<@NonNull C> commandContext,
                @NonNull final Queue<@NonNull String> inputQueue
        ) {
            final String possibleIdentifier = inputQueue.peek();
            if (possibleIdentifier == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(RegistryEntryArgument.class, commandContext));
            }

            final ResourceLocation key;
            try {
                key = ResourceLocation.read(new StringReader(possibleIdentifier));
            } catch (final CommandSyntaxException ex) {
                return ArgumentParseResult.failure(ex);
            }
            inputQueue.poll();

            final Registry<V> registry = this.resolveRegistry(commandContext);
            if (registry == null) {
                return ArgumentParseResult.failure(new IllegalArgumentException("Unknown registry " + this.registryIdent));
            }

            final V entry = registry.get(key);
            if (entry == null) {
                return ArgumentParseResult.failure(new UnknownEntryException(commandContext, key, this.registryIdent));
            }

            return ArgumentParseResult.success(entry);
        }

        @SuppressWarnings("unchecked")
        Registry<V> resolveRegistry(final CommandContext<C> ctx) {
            final SharedSuggestionProvider reverseMapped = ctx.get(FabricCommandContextKeys.NATIVE_COMMAND_SOURCE);
            // First try dynamic registries (for things loaded from data-packs)
            Registry<V> registry = reverseMapped.registryAccess().registry(this.registryIdent).orElse(null);
            if (registry == null) {
                // And then static registries
                registry = (Registry<V>) Registry.REGISTRY.get(this.registryIdent.location());
            }
            return registry;
        }

        @Override
        public @NonNull List<@NonNull String> suggestions(
                final @NonNull CommandContext<C> commandContext,
                final @NonNull String input
        ) {
            final Set<ResourceLocation> ids = this.resolveRegistry(commandContext).keySet();
            final List<String> results = new ArrayList<>(ids.size());
            for (final ResourceLocation entry : ids) {
                if (entry.getNamespace().equals(NAMESPACE_MINECRAFT)) {
                    results.add(entry.getPath());
                }
                results.add(entry.toString());
            }

            return results;
        }

        @Override
        public boolean isContextFree() {
            return true;
        }

        /**
         * Get the registry associated with this parser.
         *
         * @return the registry
         * @since 1.5.0
         */
        public ResourceKey<? extends Registry<?>> registryKey() {
            return this.registryIdent;
        }

    }

    /**
     * A builder for {@link RegistryEntryArgument}.
     *
     * @param <C> The sender type
     * @param <V> The registry value type
     * @since 1.5.0
     */
    public static final class Builder<C, V> extends CommandArgument.TypedBuilder<C, V, Builder<C, V>> {

        private final ResourceKey<? extends Registry<V>> registryIdent;

        Builder(
                final ResourceKey<? extends Registry<V>> key,
                final @NonNull Class<V> valueType,
                final @NonNull String name
        ) {
            super(valueType, name);
            this.registryIdent = key;
        }

        Builder(
                final ResourceKey<? extends Registry<V>> key,
                final @NonNull TypeToken<V> valueType,
                final @NonNull String name
        ) {
            super(valueType, name);
            this.registryIdent = key;
        }

        @Override
        public @NonNull RegistryEntryArgument<@NonNull C, @NonNull V> build() {
            return new RegistryEntryArgument<>(
                    this.isRequired(),
                    this.getName(),
                    this.registryIdent,
                    this.getDefaultValue(),
                    this.getValueType(),
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
        public @NonNull Builder<C, V> asOptionalWithDefault(final @NonNull ResourceKey<V> defaultValue) {
            return this.asOptionalWithDefault(defaultValue.location().toString());
        }

    }

    /**
     * An exception thrown when an entry in a registry could not be found.
     *
     * @since 1.5.0
     */
    private static final class UnknownEntryException extends ParserException {

        private static final long serialVersionUID = 7694424294461849903L;

        UnknownEntryException(
                final CommandContext<?> context,
                final ResourceLocation key,
                final ResourceKey<? extends Registry<?>> registry
        ) {
            super(
                    RegistryEntryArgument.class,
                    context,
                    FabricCaptionKeys.ARGUMENT_PARSE_FAILURE_REGISTRY_ENTRY_UNKNOWN_ENTRY,
                    CaptionVariable.of("id", key.toString()),
                    CaptionVariable.of("registry", registry.toString())
            );
        }

    }

}
