/*
 * This file is part of Cyclone, licensed under the MIT License.
 *
 *  Copyright (c) 2022-122 powercas_gamer
 *  Copyright (c) 2022-122 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.deltapvp.cyclone.command.arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.bukkit.BukkitCaptionKeys;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import net.deltapvp.cyclone.Cyclone;
import net.deltapvp.cyclone.module.api.Module;

public class ModuleArgument<C> extends CommandArgument<C, Module> {

    private ModuleArgument(final boolean required, final @NotNull String name,
            final @NotNull String defaultValue,
            final @Nullable BiFunction<@NotNull CommandContext<C>, @NotNull String, @NotNull List<@NotNull String>> suggestionsProvider,
            final @NotNull ArgumentDescription defaultDescription) {
        super(required, name, new ModuleParser<>(), defaultValue, Module.class, suggestionsProvider,
                defaultDescription);
    }

    /**
     * Create a new builder
     *
     * @param name Name of the component
     * @param <C> Command sender type
     * @return Created builder
     */
    public static <C> @NotNull Builder<C> newBuilder(final @NotNull String name) {
        return new Builder<>(name);
    }

    /**
     * Create a new required command component
     *
     * @param name Component name
     * @param <C> Command sender type
     * @return Created component
     */
    public static <C> @NotNull CommandArgument<C, Module> of(final @NotNull String name) {
        return ModuleArgument.<C>newBuilder(name).asRequired().build();
    }

    /**
     * Create a new optional command component
     *
     * @param name Component name
     * @param <C> Command sender type
     * @return Created component
     */
    public static <C> @NotNull CommandArgument<C, Module> optional(final @NotNull String name) {
        return ModuleArgument.<C>newBuilder(name).asOptional().build();
    }

    /**
     * Create a new required command component with a default value
     *
     * @param name Component name
     * @param defaultModule Default Module
     * @param <C> Command sender type
     * @return Created component
     */
    public static <C> @NotNull CommandArgument<C, Module> optional(final @NotNull String name,
            final @NotNull String defaultModule) {
        return ModuleArgument.<C>newBuilder(name).asOptionalWithDefault(defaultModule).build();
    }


    public static final class Builder<C> extends CommandArgument.Builder<C, Module> {

        private Builder(final @NotNull String name) {
            super(Module.class, name);
        }

        /**
         * Builder a new boolean component
         *
         * @return Constructed component
         */
        @Override
        public @NotNull ModuleArgument<C> build() {
            return new ModuleArgument<>(this.isRequired(), this.getName(), this.getDefaultValue(),
                    this.getSuggestionsProvider(), this.getDefaultDescription());
        }

    }

    public static final class ModuleParser<C> implements ArgumentParser<C, Module> {

        @Override
        @SuppressWarnings("deprecation")
        public @NotNull ArgumentParseResult<Module> parse(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull Queue<@NotNull String> inputQueue) {
            final String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult
                        .failure(new NoInputProvidedException(ModuleParser.class, commandContext));
            }
            inputQueue.remove();

            Module module = Cyclone.getInstance().getModules().get(input.toLowerCase());

            if (module == null) {
                return ArgumentParseResult.failure(new ModuleParseException(input, commandContext));
            }

            return ArgumentParseResult.success(module);
        }

        @Override
        public @NotNull List<@NotNull String> suggestions(
                final @NotNull CommandContext<C> commandContext, final @NotNull String input) {
            List<String> output = new ArrayList<>();

            for (Module module : Cyclone.getInstance().getModules().values()) {
                if (!module.isEnabled()) {
                    continue;
                }
                output.add(module.getName());
            }

            return output;
        }

    }

    /**
     * Module parse exception
     */
    public static final class ModuleParseException extends ParserException {

        private static final long serialVersionUID = 927476591631127552L;
        private final String input;

        /**
         * Construct a new Module parse exception
         *
         * @param input String input
         * @param context Command context
         */
        public ModuleParseException(final @NotNull String input,
                final @NotNull CommandContext<?> context) {
            super(ModuleParser.class, context, BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER,
                    CaptionVariable.of("input", input));
            this.input = input;
        }

        /**
         * Get the supplied input
         *
         * @return String value
         */
        public @NotNull String getInput() {
            return this.input;
        }

    }

}
