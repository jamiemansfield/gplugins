/*
 * This file is part of gplugins, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2017, Jamie Mansfield <https://www.jamierocks.uk/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package uk.jamierocks.canary.gplugins.command;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.canarymod.chat.MessageReceiver;

import java.util.List;
import java.util.Map;

public final class Command implements CommandCallable {

    public static Builder builder() {
        return new Builder();
    }

    private final CommandProcessor processor;
    private final String[] permissions;
    private final String description;
    private final String usage;

    private Command(CommandProcessor processor, String permission, String description, String usage) {
        this.processor = processor;
        this.permissions = new String[] { permission };
        this.description = description;
        this.usage = usage;
    }

    @Override
    public void process(MessageReceiver caller, String[] parameters) {
        this.processor.process(caller, parameters);
    }

    @Override
    public String[] getPermissions() {
        return this.permissions;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getToolTip() {
        return this.usage;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("processor", this.processor)
                .add("permission", this.permissions[0])
                .add("description", this.description)
                .add("usage", this.usage)
                .toString();
    }

    public static final class Builder {

        private CommandProcessor processor;
        private String permission;
        private String description;
        private String usage;
        private Map<List<String>, CommandCallable> childCommandMap = Maps.newHashMap();

        private Builder() {
        }

        public Builder processor(CommandProcessor processor) {
            checkNotNull(processor, "processor");
            this.processor = processor;
            return this;
        }

        public Builder permission(String permission) {
            this.permission = permission;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder usage(String usage) {
            this.usage = usage;
            return this;
        }

        public Builder child(CommandCallable child, String... aliases) {
            this.childCommandMap.put(ImmutableList.copyOf(aliases), child);
            return this;
        }

        public Command build() {
            if (this.childCommandMap.isEmpty()) {
                checkNotNull(this.processor, "A processor is required if there are no children present!");
            } else {
                this.processor(new ChildCommandProcessor(this.processor, this.childCommandMap));
            }

            return new Command(this.processor, this.permission, this.description, this.usage);
        }

    }

}
