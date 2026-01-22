package hytale.doryanbessiere.fr.utils.command;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CustomArgTypes {

    public static final SingleArgumentType<Long> LONG = new SingleArgumentType<Long>("server.commands.parsing.argtype.long.name", "server.commands.parsing.argtype.long.usage", new String[]{"-27432", "-1", "0", "1", "56346"}) {
        @Nullable
        public Long parse(@Nonnull String input, ParseResult parseResult) {
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException var4) {
                parseResult.fail(Message.translation("server.commands.parsing.argtype.long.fail").param("input", input));
                return null;
            }
        }
    };

    public static final SingleArgumentType<String> STRING = new SingleArgumentType<String>("server.commands.parsing.argtype.string.name", "server.commands.parsing.argtype.string.usage", new String[]{"\"Hytale is really cool!\"", "\"Numbers work 2!\"", "\"If you can type it...\""}) {
        @Nullable
        public String parse(@Nonnull String input, ParseResult parseResult) {
            return input.replace("\"", "");
        }
    };
}
