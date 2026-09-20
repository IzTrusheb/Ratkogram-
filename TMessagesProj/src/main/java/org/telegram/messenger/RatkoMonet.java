package org.telegram.messenger;

import android.content.Context;
import android.os.Build;

import org.telegram.ui.ActionBar.Theme;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class RatkoMonet {

    public static final String LIGHT_NAME = "Ratkogram Monet Light";
    public static final String DARK_NAME = "Ratkogram Monet Dark";

    private static final String LIGHT_FILE = "ratkogram_monet_light.attheme";
    private static final String DARK_FILE = "ratkogram_monet_dark.attheme";

    public static boolean isAvailable() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
    }

    public static void registerThemes() {
        if (!isAvailable()) {
            return;
        }
        try {
            Context context = ApplicationLoader.applicationContext;
            if (context == null) {
                return;
            }
            File light = generateThemeFile(context, false);
            File dark = generateThemeFile(context, true);
            if (light == null || dark == null) {
                return;
            }

            unregister(context, LIGHT_NAME);
            unregister(context, DARK_NAME);

            Theme.ThemeInfo lightInfo = Theme.fillThemeValues(light, LIGHT_NAME, null);
            if (lightInfo != null) {
                lightInfo.sortIndex = 96;
                Theme.registerExternalTheme(lightInfo);
            }
            Theme.ThemeInfo darkInfo = Theme.fillThemeValues(dark, DARK_NAME, null);
            if (darkInfo != null) {
                darkInfo.sortIndex = 97;
                Theme.registerExternalTheme(darkInfo);
            }
        } catch (Throwable t) {
            FileLog.e(t);
        }
    }

    private static void unregister(Context context, String name) {
        try {
            ArrayList<Theme.ThemeInfo> toRemove = new ArrayList<>();
            for (Theme.ThemeInfo info : Theme.themes) {
                if (name.equals(info.name)) {
                    toRemove.add(info);
                }
            }
            for (Theme.ThemeInfo info : toRemove) {
                Theme.unregisterExternalTheme(info);
            }
        } catch (Throwable t) {
            FileLog.e(t);
        }
    }

    private static File generateThemeFile(Context context, boolean dark) {
        try {
            File file = new File(context.getFilesDir(), dark ? DARK_FILE : LIGHT_FILE);
            StringBuilder builder = new StringBuilder();

            int accent100 = sysColor(context, "system_accent1_100");
            int accent200 = sysColor(context, "system_accent1_200");
            int accent300 = sysColor(context, "system_accent1_300");
            int accent400 = sysColor(context, "system_accent1_400");
            int accent500 = sysColor(context, "system_accent1_500");
            int accent600 = sysColor(context, "system_accent1_600");
            int accent700 = sysColor(context, "system_accent1_700");
            int accent800 = sysColor(context, "system_accent1_800");
            int accent900 = sysColor(context, "system_accent1_900");
            int neutral10 = sysColor(context, "system_neutral1_10");
            int neutral50 = sysColor(context, "system_neutral1_50");
            int neutral100 = sysColor(context, "system_neutral1_100");
            int neutral400 = sysColor(context, "system_neutral1_400");
            int neutral500 = sysColor(context, "system_neutral1_500");
            int neutral600 = sysColor(context, "system_neutral1_600");
            int neutral700 = sysColor(context, "system_neutral1_700");
            int neutral800 = sysColor(context, "system_neutral1_800");
            int neutral900 = sysColor(context, "system_neutral1_900");

            int windowWhite = dark ? neutral900 : neutral100;
            int windowGray = dark ? neutral800 : neutral500; // fixed below for light
            if (!dark) {
                windowGray = neutral500;
            } else {
                windowGray = neutral800;
            }
            // Light gray background should be a bit darker than white bg
            if (!dark) {
                windowGray = blend(neutral100, neutral700, 0.28f);
            } else {
                windowGray = blend(neutral900, neutral10, 0.06f);
            }
            int strongText = dark ? neutral10 : neutral10;
            int primaryText = dark ? neutral50 : neutral600;
            int secondaryText = dark ? neutral500 : neutral700;
            int accent = dark ? accent300 : accent600;
            int accentPressed = dark ? accent400 : accent700;
            int accentSoft = dark ? accent800 : accent100;
            int accentTextOnSoft = dark ? accent100 : accent900;
            int actionBar = dark ? neutral900 : neutral100;
            int outBubble = dark ? accent800 : accent100;
            int outBubbleText = dark ? neutral50 : neutral900;
            int serviceBg = dark ? accent700 : accent700;
            int serviceText = 0xFFFFFFFF;
            int switchTrack = dark ? accent400 : accent600;

            put(builder, "windowBackgroundWhite", windowWhite);
            put(builder, "windowBackgroundGray", windowGray);
            put(builder, "windowBackgroundWhiteBlackText", strongText);
            put(builder, "windowBackgroundWhiteGrayText", secondaryText);
            put(builder, "windowBackgroundWhiteGrayText2", secondaryText);
            put(builder, "windowBackgroundWhiteGrayText3", primaryText);
            put(builder, "windowBackgroundWhiteGrayText4", secondaryText);
            put(builder, "windowBackgroundWhiteGrayText5", primaryText);
            put(builder, "windowBackgroundWhiteGrayText6", secondaryText);
            put(builder, "windowBackgroundWhiteGrayIcon", primaryText);
            put(builder, "windowBackgroundWhiteBlueIcon", accent);
            put(builder, "windowBackgroundWhiteValueText", accent);

            put(builder, "actionBarDefault", actionBar);
            put(builder, "actionBarDefaultTitle", strongText);
            put(builder, "actionBarDefaultSubtitle", secondaryText);
            put(builder, "actionBarDefaultIcon", strongText);
            put(builder, "actionBarDefaultSelector", blend(windowWhite, strongText, dark ? 0.10f : 0.06f));
            put(builder, "actionBarDefaultSearch", accent);
            put(builder, "actionBarDefaultSearchPlaceholder", secondaryText);
            put(builder, "actionBarTabTitle", strongText);
            put(builder, "actionBarTabLine", accent);
            put(builder, "actionBarDefaultSubmenuItem", strongText);
            put(builder, "actionBarDefaultSubmenuBackground", actionBar);
            put(builder, "actionBarMenu", windowWhite);
            put(builder, "actionBarMenuTextExtra", accent);

            put(builder, "dialogBackground", windowWhite);
            put(builder, "dialogTextBlack", strongText);
            put(builder, "dialogTextGray", primaryText);
            put(builder, "dialogTextGray2", secondaryText);
            put(builder, "dialogTextGray3", secondaryText);
            put(builder, "dialogTextBlue", accent);
            put(builder, "dialogTextLink", accent);
            put(builder, "dialogButton", accent);
            put(builder, "dialogLineProgress", accent);
            put(builder, "dialogFloatingButtonBackground", accent);
            put(builder, "dialogFloatingButtonIcon", dark ? neutral100 : 0xFFFFFFFF);
            put(builder, "dialogRoundCheckboxCheck", accent);
            put(builder, "dialogInputField", blend(windowWhite, strongText, 0.12f));
            put(builder, "dialogInputFieldActivated", accent);

            put(builder, "text_RedBold", 0xFFD33F45);

            put(builder, "chats_actionBackground", dark ? neutral700 : neutral600);
            put(builder, "chats_actionBackgroundPressed", dark ? neutral500 : neutral700);
            put(builder, "chats_actionIcon", dark ? neutral50 : neutral100);
            put(builder, "chats_name", strongText);
            put(builder, "chats_message", primaryText);
            put(builder, "chats_date", secondaryText);
            put(builder, "chats_muteIcon", secondaryText);
            put(builder, "chats_unreadCounter", accent);
            put(builder, "chats_unreadCounterText", dark ? neutral100 : 0xFFFFFFFF);
            put(builder, "chats_unreadCounterMuted", secondaryText);
            put(builder, "chats_menuBackground", windowWhite);
            put(builder, "chats_menuName", strongText);
            put(builder, "chats_menuIcon", primaryText);
            put(builder, "chats_menuItemIcon", accent);
            put(builder, "chats_pinnedOverlay", windowWhite);
            put(builder, "chats_pinnedIcon", accent);
            put(builder, "chats_verifiedBackground", accent);
            put(builder, "chats_nameMessage", strongText);
            put(builder, "chats_actionPressedBackground", accentSoft);

            put(builder, "chat_inBubble", windowWhite);
            put(builder, "chat_inBubbleShadow", blend(windowWhite, neutral10, 0.10f));
            put(builder, "chat_inBubbleText", strongText);
            put(builder, "chat_inBubbleLinks", accent);
            put(builder, "chat_inReplyLine", accent);
            put(builder, "chat_inReplyNameText", accent);
            put(builder, "chat_inReplyMessageText", primaryText);
            put(builder, "chat_inReplyMediaMessageSelected", accentSoft);
            put(builder, "chat_inTimeText", secondaryText);
            put(builder, "chat_outBubble", outBubble);
            put(builder, "chat_outBubbleGradient", outBubble);
            put(builder, "chat_outBubbleText", outBubbleText);
            put(builder, "chat_outBubbleLinks", dark ? accent200 : accent700);
            put(builder, "chat_outReplyLine", dark ? accent300 : accent700);
            put(builder, "chat_outReplyNameText", dark ? accent200 : accent700);
            put(builder, "chat_outReplyMessageText", outBubbleText);
            put(builder, "chat_outTimeText", blend(outBubbleText, outBubble, 0.30f));
            put(builder, "chat_serviceText", serviceText);
            put(builder, "chat_serviceBackground", serviceBg);
            put(builder, "chat_serviceBackgroundSelected", accent800);
            put(builder, "chat_selectedBackground", blend(accent, windowWhite, 0.30f));
            put(builder, "chat_messagePanelBackground", dark ? neutral800 : neutral100);
            put(builder, "chat_messagePanelText", strongText);
            put(builder, "chat_messagePanelHint", secondaryText);
            put(builder, "chat_messagePanelIcons", accent);
            put(builder, "chat_messagePanelSend", accent);
            put(builder, "chat_messagePanelSendBackground", accent);
            put(builder, "chat_messagePanelVoicePressed", accentPressed);
            put(builder, "chat_wallpaper", dark ? neutral900 : neutral500);
            put(builder, "chat_wallpaper_selected_to", dark ? neutral800 : neutral600);
            put(builder, "chat_status", accent);
            put(builder, "chat_addContact", accent);
            put(builder, "chat_goDownButtonCounterBackground", accent);
            put(builder, "chat_goDownButtonIcon", dark ? neutral50 : neutral600);
            put(builder, "chat_goDownButtonBackground", windowWhite);
            put(builder, "chat_inLoader", accent);
            put(builder, "chat_inLoaderSelected", accentPressed);
            put(builder, "chat_inReactionButtonBackground", accentSoft);
            put(builder, "chat_outReactionButtonBackground", blend(outBubble, outBubbleText, 0.10f));

            put(builder, "featuredStickers_addButton", accent);
            put(builder, "featuredStickers_addButtonPressed", accentPressed);
            put(builder, "featuredStickers_buttonText", dark ? neutral100 : 0xFFFFFFFF);
            put(builder, "featuredStickers_text", strongText);
            put(builder, "featuredStickers_subtitle", secondaryText);

            put(builder, "switchTrack", blend(strongText, windowWhite, dark ? 0.30f : 0.55f));
            put(builder, "switchTrackChecked", switchTrack);
            put(builder, "switch2Track", blend(strongText, windowWhite, dark ? 0.30f : 0.55f));
            put(builder, "switch2TrackChecked", switchTrack);
            put(builder, "radioBackgroundChecked", accent);
            put(builder, "radioBackground", blend(strongText, windowWhite, 0.15f));
            put(builder, "checkbox", windowWhite);
            put(builder, "checkboxCheck", accent);
            put(builder, "text_link", accent);

            put(builder, "picker_enabledButton", accent);
            put(builder, "picker_badge", accent);
            put(builder, "picker_badgeText", dark ? neutral100 : 0xFFFFFFFF);
            put(builder, "picker_disabledButton", secondaryText);

            put(builder, "groupcreate_spanBackground", accentSoft);
            put(builder, "groupcreate_spanText", accentTextOnSoft);
            put(builder, "groupcreate_spanDelete", primaryText);

            put(builder, "profile_actionBackground", dark ? neutral700 : neutral100);
            put(builder, "profile_actionPressedBackground", accentSoft);
            put(builder, "profile_actionIcon", dark ? accent200 : accent700);
            put(builder, "profile_title", strongText);
            put(builder, "profile_subtitle", secondaryText);
            put(builder, "profile_status", accent);
            put(builder, "profile_sectionName", secondaryText);
            put(builder, "profile_actionBarWhiteSelector", blend(windowWhite, strongText, dark ? 0.10f : 0.06f));

            put(builder, "divider", blend(strongText, windowWhite, dark ? 0.15f : 0.12f));
            put(builder, "graySectionText", secondaryText);
            put(builder, "emptyListIcon", blend(strongText, windowWhite, 0.08f));
            put(builder, "listSelector", blend(strongText, windowWhite, dark ? 0.08f : 0.05f));
            put(builder, "player_progress", accent);
            put(builder, "loading", accent);
            put(builder, "chat_emojiSearchIcon", secondaryText);
            put(builder, "chat_emojiPanelBackground", dark ? neutral900 : neutral100);
            put(builder, "chat_emojiPanelStickerPackIcon", accent);

            put(builder, "key_chats_pinnedOverlay", windowWhite);

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                writer.write(builder.toString());
            }
            return file;
        } catch (Throwable t) {
            FileLog.e(t);
            return null;
        }
    }

    private static int sysColor(Context context, String name) {
        int id = context.getResources().getIdentifier(name, "color", "android");
        if (id != 0) {
            return context.getResources().getColor(id, context.getTheme());
        }
        return 0xFF3399FF;
    }

    private static int blend(int colorA, int colorB, float ratio) {
        int aA = (colorA >>> 24) & 0xFF, rA = (colorA >>> 16) & 0xFF, gA = (colorA >>> 8) & 0xFF, bA = colorA & 0xFF;
        int aB = (colorB >>> 24) & 0xFF, rB = (colorB >>> 16) & 0xFF, gB = (colorB >>> 8) & 0xFF, bB = colorB & 0xFF;
        int r = (int) (rA + (rB - rA) * ratio);
        int g = (int) (gA + (gB - gA) * ratio);
        int b = (int) (bA + (bB - bA) * ratio);
        int a = (int) (aA + (aB - aA) * ratio);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static void put(StringBuilder builder, String key, int color) {
        builder.append(key).append("=").append(color).append('\n');
    }
}
