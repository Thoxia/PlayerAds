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
@Header("#              Destek için: https://tr.thoxia.com              #")
@Header("#                                                              #")
@Header("################################################################")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
@Getter
@Setter
public class MessagesTR extends MessagesEN {

    private String prefix = "<#f1c232>Reklam <dark_gray>•";

    private String invalidArgument = "<prefix> <red>Geçersiz argüman.";

    private String tooManyArguments = "<prefix> <red>Çok fazla argüman.";

    private String notEnoughArguments = "<prefix> <red>Yetersiz argüman.";

    private String unknownCommand = "<prefix> <red>Bilinmeyen komut.";

    private String notEnoughMoney = "<prefix> <red>Yetersiz bakiye.";

    private String muted = "<prefix> <red>Susturulmuş olduğunuz için reklam satın alamazsınız.";

    private String notEnoughPermission = "<prefix> <red>Bunu yapmak için yeterli izniniz yok.";

    private String invalidInput = "<prefix> <red>Geçersiz mesaj. Mesajınızın uzunluğu 10 ile 80 karakter arasında olmalıdır! Lütfen tekrar girin.";

    private String cancelled = "<prefix> <red>Mesaj dinleyicisi iptal edildi. Artık konuşabilirsiniz.";

    private String emptyMessage = "<prefix> <red>Lütfen reklam yapmak için bir metin girin!";

    private String reloaded = "<prefix> <green>Eklenti yeniden yüklendi.";

    private String taken = "<prefix> <red>Bu yer zaten alınmış!";

    private String bought = "<prefix> <green>Başarıyla bir reklam satın aldınız!";

    private String inputStarted = "<prefix> <#ffffcc>Görünmesini istediğiniz mesajı yazın. Bu işlemi iptal etmek için 'iptal' yazın.";

    private String expired = "<prefix> <red>Kötü haber... reklamınızın süresi doldu!";

    private String wait = "<prefix> <red>Biri yakın zamanda reklam satın aldı, lütfen biraz bekleyin. (30 saniye)";

    private String max = "<prefix> <red>Aynı anda sadece <max> reklamınız olabilir.";

    private String removedAllAdmin = "<prefix> <#ffffcc><player> kullanıcısının tüm reklamları başarıyla kaldırıldı.";

    private String removedSpotAdmin = "<prefix> <#ffffcc><slot> slotundan bir reklam başarıyla kaldırıldı.";

    private String clearedAllAdmin = "<prefix> <#ffffcc>Tüm reklamlar başarıyla temizlendi.";

    private String adNotFoundAdmin = "<prefix> <red>Bir reklam bulunamadı.";

    private String expiredAnnouncement = "<prefix> <#ffffcc>Bir reklamın süresi doldu! Hemen gidip alın!";

    private String inputCancelKeyword = "iptal";

    private List<String> adMessage = List.of(
            "",
            "<prefix> <#f1c232><player> <#ffffcc>bölgesi için bir reklam satın aldı!",
            "<prefix> <#f1c232>Mesaj: <#ffffcc><message>",
            ""
    );

    private String hours = "saat";
    private String days = "gün";
    private String seconds = "saniye";
    private String minutes = "dakika";

    private String expiredPlaceholder = "<red>Süresi Dolmuş";

}
