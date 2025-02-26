package net.mrliam2614.mrliamapi.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;

public class Messages {

    public static Component createMessage(String message) {
        List<String> lines = List.of(message.split("\n"));
        Component component = Component.text("");
        for (String line : lines) {
            component = component.append(LegacyComponentSerializer.legacy('&').deserialize(line));
            if(lines.indexOf(line) != lines.size() - 1)
                component = component.append(Component.newline());
        }
        return component;
    }
}