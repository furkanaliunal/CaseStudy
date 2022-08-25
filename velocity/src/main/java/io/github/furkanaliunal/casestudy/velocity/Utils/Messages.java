package io.github.furkanaliunal.casestudy.velocity.Utils;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.velocitypowered.api.proxy.Player;
import io.github.furkanaliunal.casestudy.velocity.CaseStudy;
import io.github.furkanaliunal.casestudy.velocity.TeleportRules;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import java.time.Instant;
import java.util.*;

/**
 * Created by Onwexrys
 * This class is used to load and store messages from the lang.yml file.
 * This class is used to send messages to players.
 */
public enum Messages {
    TPA_LIST_TITLE_MESSAGE(),
    TPA_LIST_INTERMEDIATE_LINES(),
    TPA_LIST_SUMMARY_LINE(),
    TPA_LIST_HOVER_DETAILS(),

    MUST_BE_PLAYER(),
    TPA_COMMAND_HELP(),
    TPA_PLAYER_NOT_FOUND(),
    TPA_CANT_TPA_TO_YOURSELF(),
    TPA_REQUEST_SENT(),
    TPA_CANT_TPA_THIS_SERVER(),
    TPA_REQUEST_RECEIVED(),
    TPA_ALREADY_SEND(),
    TPA_REQUEST_REJECTED(),
    TPA_REQUEST_TIMED_OUT(),
    TPA_REQUEST_IN_COUNTDOWN(),

    TPA_CONFIRM(),
    TPA_DENY(),

    TPA_ACCEPT_COMMAND_HELP(),
    TPA_ACCEPT_PLAYER_NOT_FOUND(),

    TPA_DENY_COMMAND_HELP(),
    TPA_DENY_PLAYER_NOT_FOUND(),
    TPA_DENY_SUCCESSFUL(),
    ;
    /**
     * Raw content of the message.
     */
    @Getter
    private String message;
    /**
     * The message which shows up when player hover to the message.
     */
    @Getter
    private String hoverMessage;
    /**
     * The message which is executed when player clicks the message.
     */
    @Getter
    private String runCommand;

    /**
     * Constructor of the Messages enum.
     */
    Messages(){
        this.message = null;
    }

    /**
     * Loads the messages from the lang.yml file.
     */
    public static void reload(){
        Configuration configuration = new Configuration(CaseStudy.instance.getDataDirectory(), "lang.yml");
        configuration.reload();
        Arrays.stream(Messages.values()).forEach(message -> {
            message.message = configuration.getOrDefault(message.name(), null);
            message.hoverMessage = configuration.getOrDefault(message.name()+"_HoverMessage", null);
            message.runCommand = configuration.getOrDefault(message.name()+"_RunCommand", null);
        });
    }

    /**
     * Sends the message to the player.
     * @param audience - The audience of the player.
     * @param message - The message enum to be sent.
     * @param args - The arguments which will be replaced with {0}, {1} in the message.
     */
    public static void sendMessage(final Audience audience, final Messages message, final String ... args){
        if (message.message == null) return;
        String strMessage = parseParameters(message.message, args);

        TextComponent.Builder textBuilder = Component.text();

        textBuilder.append(Component.text(strMessage));
        if (message.hoverMessage != null){
            textBuilder.hoverEvent(HoverEvent.showText(Component.text(parseParameters(message.hoverMessage, args))));
        }
        if (message.runCommand != null){
            textBuilder.clickEvent(ClickEvent.runCommand(parseParameters(message.runCommand, args)));
        }
        audience.sendMessage(textBuilder.build());
    }

    /**
     * Replaces the parameters in the message with the given arguments.
     * Replaces {0} to args[0], {1} to args[1] and so on.
     * @param message - The message to be parsed.
     * @param args - The arguments to be replaced.
     * @return
     */
    private static String parseParameters(String message, final String ... args){
        if (args != null){
            for (int i = 0; i < args.length; i++){
                message = message.replace("{" + i + "}", args[i]);
            }
        }
        return message;
    }

    /**
     * Sends the message to the player.
     * @param player - The player to send the message to.
     */
    public static void sendTPAHistory(final Player player){
        final Audience audience = Audience.audience(player);
        final MongoCursor cursor = MongoUtils.getCollection().find(Filters.and(Filters.exists("location"), Filters.eq("UUID", player.getUniqueId().toString()))).sort(Sorts.descending("date")).limit(TeleportRules.getTeleportListHistorySize()).iterator();
        final List<TextComponent> components = new ArrayList<>();
        components.add(Component.text(TPA_LIST_INTERMEDIATE_LINES.message));
        int i = 1;
        while (cursor.hasNext()){
            final Map<String, Object> data = (Map<String, Object>) cursor.next();
            final String serverName = (String) data.get("server");
            final String location = (String) data.get("location");
            final String targetPlayer = (String) data.get("targetName");
            final Long strDate = (Long) data.get("date");
            final String date = Date.from(Instant.ofEpochMilli(strDate)).toString();
            final String message = parseParameters(TPA_LIST_SUMMARY_LINE.message, i+"", targetPlayer);
            final String hoverMessage = parseParameters(TPA_LIST_HOVER_DETAILS.message, serverName, location, targetPlayer, date);
            final TextComponent.Builder textBuilder = Component.text().content(message);
            textBuilder.hoverEvent(HoverEvent.showText(Component.text(hoverMessage)));
            components.add(textBuilder.build());
            i++;
            components.add(Component.text(TPA_LIST_INTERMEDIATE_LINES.message));
        }
        cursor.close();
        Messages.sendMessage(audience, Messages.TPA_LIST_TITLE_MESSAGE);
        components.forEach(audience::sendMessage);
    }
}
