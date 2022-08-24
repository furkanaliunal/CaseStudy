package furkan.studio.casestudy.velocity.Utils;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.velocitypowered.api.proxy.Player;
import furkan.studio.casestudy.velocity.CaseStudy;
import furkan.studio.casestudy.velocity.TeleportRules;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import java.time.Instant;
import java.util.*;

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

    @Getter
    private String message;
    @Getter
    private String hoverMessage;
    @Getter
    private String runCommand;

    Messages(){
        this.message = null;
    }

    public static void reload(){
        Configuration configuration = new Configuration(CaseStudy.instance.getDataDirectory(), "lang.yml");
        configuration.reload();
        Arrays.stream(Messages.values()).forEach(message -> {
            message.message = configuration.getOrDefault(message.name(), null);
            message.hoverMessage = configuration.getOrDefault(message.name()+"_HoverMessage", null);
            message.runCommand = configuration.getOrDefault(message.name()+"_RunCommand", null);
        });
    }
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

    private static String parseParameters(String message, final String ... args){
        if (args != null){
            for (int i = 0; i < args.length; i++){
                message = message.replace("{" + i + "}", args[i]);
            }
        }
        return message;
    }

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
