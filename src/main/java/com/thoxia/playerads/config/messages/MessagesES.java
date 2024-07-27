package com.thoxia.playerads.config.messages;

import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Header("################################################################")
@Header("#                                                              #")
@Header("#                           PlayerAds                          #")
@Header("#                                                              #")
@Header("#     Need help configuring the plugin? Join our discord!      #")
@Header("#                  https://discord.thoxia.com                  #")
@Header("#                                                              #")
@Header("################################################################")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
@Getter @Setter
public class MessagesES extends MessagesEN {

    private String prefix = "<#f1c232>PlayerAds <dark_gray>•";

    private String invalidArgument = "<prefix> <red>Argumento inválido.";

    private String tooManyArguments = "<prefix> <red>Demasiados argumentos.";

    private String notEnoughArguments = "<prefix> <red>Argumentos insuficientes.";

    private String unknownCommand = "<prefix> <red>Comando desconocido.";

    private String notEnoughMoney = "<prefix> <red>Saldo insuficiente.";

    private String muted = "<prefix> <red>No puedes comprar anuncios mientras estás silenciado.";

    private String notEnoughPermission = "<prefix> <red>No tienes suficiente permiso para hacer esto.";

    private String invalidInput = "<prefix> <red>Entrada inválida. ¡Tu mensaje debe tener entre 10 y 80 caracteres! Por favor, vuelve a ingresar.";

    private String cancelled = "<prefix> <red>Se canceló el oyente de entrada. Ahora puedes hablar libremente.";

    private String emptyMessage = "<prefix> <red>¡Por favor ingresa un texto para anunciar!";

    private String reloaded = "<prefix> <green>El plugin se ha recargado.";

    private String taken = "<prefix> <red>¡Este lugar ya está ocupado!";

    private String bought = "<prefix> <green>¡Has comprado un anuncio con éxito!";

    private String inputStarted = "<prefix> <#ffffcc>Por favor escribe el mensaje que deseas mostrar. Escribe 'cancelar' para cancelar esta acción.";

    private String expired = "<prefix> <red>Malas noticias... tu anuncio ha expirado.";

    private String wait = "<prefix> <red>Alguien compró un anuncio recientemente, por favor espera un momento. (30 segundos)";

    private String max = "<prefix> <red>Solo puedes tener <max> anuncios al mismo tiempo.";

    private String removedAllAdmin = "<prefix> <#ffffcc>Se han eliminado con éxito todos los anuncios de <player>.";

    private String removedSpotAdmin = "<prefix> <#ffffcc>Se ha eliminado con éxito un anuncio del lugar <slot>.";

    private String clearedAllAdmin = "<prefix> <#ffffcc>Todos los anuncios se han eliminado con éxito.";

    private String adNotFoundAdmin = "<prefix> <red>No se pudo encontrar un anuncio.";

    private String expiredAnnouncement = "<prefix> <#ffffcc>¡Un anuncio acaba de expirar! ¡Ve y reclámalo!";

    private String inputCancelKeyword = "cancelar";

    private List<String> adMessage = List.of(
            "",
            "<prefix> <#f1c232><player> <#ffffcc>ha comprado un anuncio para su territorio!",
            "<prefix> <#f1c232>Mensaje: <#ffffcc><message>",
            ""
    );

    private String hours = "horas";
    private String days = "días";
    private String seconds = "segundos";
    private String minutes = "minutos";

    private String expiredPlaceholder = "<red>Expirado";

}
