package org.telegram.ui;

import static org.telegram.messenger.AndroidUtilities.dp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Switch;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ScaleStateListAnimator;

public class RatkoFeaturesActivity extends BaseFragment {

    private static final int CARD_RADIUS_DP = 16;
    public static final String PREFS_NAME = "ratko_features";
    public static final String PREF_SHOW_USER_ID = "show_user_id";
    public static final String PREF_ANTI_DELETE = "anti_delete";

    @Override
    public View createView(Context context) {
        boolean dark = Theme.isCurrentThemeDark();
        int backgroundColor = dark ? 0xFF1C1C1C : 0xFFFFFFFF;
        int cardColor = dark ? 0xFF2B2B2B : 0xFFFFFFFF;
        int titleColor = dark ? 0xFFFFFFFF : 0xFF000000;
        int descriptionColor = dark ? 0xFFAAAAAA : 0xFF757575;
        int sectionTitleColor = dark ? 0xFFAAAAAA : 0xFF757575;

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("Ratko Features");
        actionBar.setBackgroundColor(backgroundColor);
        actionBar.setItemsColor(titleColor, false);
        actionBar.setTitleColor(titleColor);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        FrameLayout root = new FrameLayout(context);
        root.setBackgroundColor(backgroundColor);

        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        root.addView(scrollView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(dp(16), dp(22), dp(16), dp(32));
        scrollView.addView(container, new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));

        TextView sectionTitle = new TextView(context);
        sectionTitle.setText("About Ratko");
        sectionTitle.setTextColor(sectionTitleColor);
        sectionTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        sectionTitle.setTypeface(AndroidUtilities.bold());
        container.addView(sectionTitle, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT, 4, 10, 4, 8));

        FrameLayout aboutCard = createMd3Card(context, cardColor);
        aboutCard.setClickable(true);
        ScaleStateListAnimator.apply(aboutCard, 0.01f, 1.05f);
        aboutCard.setOnClickListener(v -> presentFragment(new AboutRatkoActivity()));
        container.addView(aboutCard, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 88, Gravity.FILL_HORIZONTAL));

        TextView cardTitle = new TextView(context);
        cardTitle.setText("Open About Ratko");
        cardTitle.setTextColor(titleColor);
        cardTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        cardTitle.setTypeface(AndroidUtilities.bold());
        cardTitle.setSingleLine(true);
        aboutCard.addView(cardTitle, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 18, 21, 54, 0));

        TextView cardDescription = new TextView(context);
        cardDescription.setText("Project details and developer profiles");
        cardDescription.setTextColor(descriptionColor);
        cardDescription.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        cardDescription.setSingleLine(true);
        aboutCard.addView(cardDescription, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 18, 49, 54, 0));

        ImageView arrowView = new ImageView(context);
        arrowView.setImageResource(R.drawable.msg_arrowright);
        arrowView.setColorFilter(new PorterDuffColorFilter(descriptionColor, PorterDuff.Mode.SRC_IN));
        arrowView.setScaleType(ImageView.ScaleType.CENTER);
        aboutCard.addView(arrowView, LayoutHelper.createFrame(28, 28, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0, 16, 0));

        TextView profileSectionTitle = new TextView(context);
        profileSectionTitle.setText("Профиль");
        profileSectionTitle.setTextColor(sectionTitleColor);
        profileSectionTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        profileSectionTitle.setTypeface(AndroidUtilities.bold());
        container.addView(profileSectionTitle, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT, 4, 20, 4, 8));

        FrameLayout profileCard = createMd3Card(context, cardColor);
        profileCard.setClickable(true);
        ScaleStateListAnimator.apply(profileCard, 0.01f, 1.05f);
        container.addView(profileCard, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 88, Gravity.FILL_HORIZONTAL));

        TextView profileTitle = new TextView(context);
        profileTitle.setText("Показывать ID пользователя");
        profileTitle.setTextColor(titleColor);
        profileTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        profileTitle.setTypeface(AndroidUtilities.bold());
        profileTitle.setSingleLine(true);
        profileCard.addView(profileTitle, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 18, 19, 80, 0));

        TextView profileDescription = new TextView(context);
        profileDescription.setText("Show user ID next to username in profile");
        profileDescription.setTextColor(descriptionColor);
        profileDescription.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        profileDescription.setSingleLine(true);
        profileCard.addView(profileDescription, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 18, 48, 80, 0));

        Switch profileSwitch = new Switch(context, null);
        profileSwitch.setColors(Theme.key_switchTrack, Theme.key_switchTrackChecked, Theme.key_windowBackgroundWhite, Theme.key_windowBackgroundWhite);
        profileSwitch.setChecked(isShowUserIdEnabled(), false);
        profileCard.addView(profileSwitch, LayoutHelper.createFrame(37, 20, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0, 22, 0));

        profileCard.setOnClickListener(v -> {
            boolean enabled = !isShowUserIdEnabled();
            getRatkoPreferences().edit().putBoolean(PREF_SHOW_USER_ID, enabled).apply();
            profileSwitch.setChecked(enabled, true);
        });

        TextView chatsSectionTitle = new TextView(context);
        chatsSectionTitle.setText("Чаты");
        chatsSectionTitle.setTextColor(sectionTitleColor);
        chatsSectionTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        chatsSectionTitle.setTypeface(AndroidUtilities.bold());
        container.addView(chatsSectionTitle, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT, 4, 20, 4, 8));

        FrameLayout antiDeleteCard = createMd3Card(context, cardColor);
        antiDeleteCard.setClickable(true);
        ScaleStateListAnimator.apply(antiDeleteCard, 0.01f, 1.05f);
        container.addView(antiDeleteCard, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 88, Gravity.FILL_HORIZONTAL));

        TextView antiDeleteTitle = new TextView(context);
        antiDeleteTitle.setText("Антиудаление сообщений");
        antiDeleteTitle.setTextColor(titleColor);
        antiDeleteTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        antiDeleteTitle.setTypeface(AndroidUtilities.bold());
        antiDeleteTitle.setSingleLine(true);
        antiDeleteCard.addView(antiDeleteTitle, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 18, 19, 80, 0));

        TextView antiDeleteDescription = new TextView(context);
        antiDeleteDescription.setText("Сохранять удалённые сообщения собеседника");
        antiDeleteDescription.setTextColor(descriptionColor);
        antiDeleteDescription.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        antiDeleteDescription.setSingleLine(true);
        antiDeleteCard.addView(antiDeleteDescription, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 18, 48, 80, 0));

        Switch antiDeleteSwitch = new Switch(context, null);
        antiDeleteSwitch.setColors(Theme.key_switchTrack, Theme.key_switchTrackChecked, Theme.key_windowBackgroundWhite, Theme.key_windowBackgroundWhite);
        antiDeleteSwitch.setChecked(isAntiDeleteEnabled(), false);
        antiDeleteCard.addView(antiDeleteSwitch, LayoutHelper.createFrame(37, 20, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0, 22, 0));

        antiDeleteCard.setOnClickListener(v -> {
            boolean enabled = !isAntiDeleteEnabled();
            getRatkoPreferences().edit().putBoolean(PREF_ANTI_DELETE, enabled).apply();
            antiDeleteSwitch.setChecked(enabled, true);
        });

        fragmentView = root;
        return fragmentView;
    }

    private FrameLayout createMd3Card(Context context, int cardColor) {
        FrameLayout card = new FrameLayout(context);
        GradientDrawable background = new GradientDrawable();
        background.setColor(cardColor);
        background.setCornerRadius(dp(CARD_RADIUS_DP));
        card.setBackground(background);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            card.setElevation(dp(3));
            card.setTranslationZ(dp(1));
            card.setClipToOutline(true);
        }
        return card;
    }

    private int getColor(Context context, int colorRes) {
        return ContextCompat.getColor(context, colorRes);
    }

    private boolean isShowUserIdEnabled() {
        return getRatkoPreferences().getBoolean(PREF_SHOW_USER_ID, false);
    }

    private boolean isAntiDeleteEnabled() {
        return getRatkoPreferences().getBoolean(PREF_ANTI_DELETE, false);
    }

    private SharedPreferences getRatkoPreferences() {
        return ApplicationLoader.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
