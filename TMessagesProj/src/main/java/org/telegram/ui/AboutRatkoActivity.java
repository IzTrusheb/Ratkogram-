package org.telegram.ui;

import static org.telegram.messenger.AndroidUtilities.dp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class AboutRatkoActivity extends BaseFragment {

    private static final long RATKO_USER_ID = 8683381142L;
    private static final String RATKO_NAME = "Galigin #TeamRatko";
    private static final String RATKO_USERNAME = "@OwnerRatko";
    private static final String RATKOGRAM_GITHUB_URL = "https://github.com/IzTrusheb/Ratkogram-";

    private static final int LIGHT_BG = 0xFFF2F2F2;
    private static final int DARK_BG = 0xFF1C1C1C;
    private static final int LIGHT_CARD = 0xFFFFFFFF;
    private static final int DARK_CARD = 0xFF2B2B2B;
    private static final int ACCENT = 0xFF757575;
    private static final int LIGHT_TEXT = 0xFF000000;
    private static final int DARK_TEXT = 0xFFFFFFFF;
    private static final int LIGHT_SECONDARY_TEXT = 0xFF757575;
    private static final int DARK_SECONDARY_TEXT = 0xFFAAAAAA;

    @Override
    public View createView(Context context) {
        boolean dark = Theme.isCurrentThemeDark();
        int bgColor = dark ? DARK_BG : LIGHT_BG;
        int cardColor = dark ? DARK_CARD : LIGHT_CARD;
        int textColor = dark ? DARK_TEXT : LIGHT_TEXT;
        int secondaryTextColor = dark ? DARK_SECONDARY_TEXT : LIGHT_SECONDARY_TEXT;

        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("About Ratko");
        actionBar.setBackgroundColor(bgColor);
        actionBar.setItemsColor(textColor, false);
        actionBar.setTitleColor(textColor);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        FrameLayout root = new FrameLayout(context);
        root.setBackgroundColor(bgColor);

        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        root.addView(scrollView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER_HORIZONTAL);
        container.setPadding(dp(22), dp(58), dp(22), dp(34));
        scrollView.addView(container, new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));

        ImageView logoView = new ImageView(context);
        logoView.setImageResource(R.drawable.logo_middle);
        logoView.setColorFilter(new PorterDuffColorFilter(ACCENT, PorterDuff.Mode.SRC_IN));
        logoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        container.addView(logoView, LayoutHelper.createLinear(56, 56, Gravity.CENTER_HORIZONTAL, 0, 34, 0, 34));

        TextView titleView = new TextView(context);
        titleView.setText(BuildVars.RATKOGRAM_NAME);
        titleView.setTextColor(textColor);
        titleView.setGravity(Gravity.CENTER);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 42);
        titleView.setTypeface(AndroidUtilities.bold());
        container.addView(titleView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        TextView descriptionView = new TextView(context);
        descriptionView.setText("Custom Telegram-based client with Ratko features");
        descriptionView.setTextColor(secondaryTextColor);
        descriptionView.setGravity(Gravity.CENTER);
        descriptionView.setLineSpacing(dp(2), 1.0f);
        descriptionView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        container.addView(descriptionView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 8, 22, 8, 0));

        TextView githubButton = new TextView(context);
        githubButton.setText(RATKOGRAM_GITHUB_URL);
        githubButton.setTextColor(textColor);
        githubButton.setGravity(Gravity.CENTER);
        githubButton.setSingleLine(true);
        githubButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        githubButton.setBackground(createRoundRect(cardColor, dp(28), Theme.multAlpha(ACCENT, dark ? 0.45f : 0.35f), dp(1)));
        githubButton.setOnClickListener(v -> Browser.openUrl(getParentActivity(), RATKOGRAM_GITHUB_URL));
        container.addView(githubButton, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 58, Gravity.CENTER_HORIZONTAL, 0, 34, 0, 0));

        TextView developersTitle = new TextView(context);
        developersTitle.setText("Developers");
        developersTitle.setTextColor(textColor);
        developersTitle.setTypeface(AndroidUtilities.bold());
        developersTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
        developersTitle.setGravity(Gravity.LEFT);
        container.addView(developersTitle, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT, 0, 54, 0, 18));

        FrameLayout developerCard = createDeveloperCard(context, cardColor, textColor, secondaryTextColor);
        container.addView(developerCard, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 106, Gravity.FILL_HORIZONTAL));
        developerCard.post(() -> {
            TranslateAnimation animation = new TranslateAnimation(0, 0, dp(300), 0);
            animation.setDuration(400);
            animation.setInterpolator(new DecelerateInterpolator());
            developerCard.startAnimation(animation);
        });

        fragmentView = root;
        return fragmentView;
    }

    private FrameLayout createDeveloperCard(Context context, int cardColor, int textColor, int secondaryTextColor) {
        FrameLayout card = new FrameLayout(context);
        card.setBackground(createRoundRect(cardColor, dp(16), Color.TRANSPARENT, 0));
        card.setClickable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            card.setElevation(dp(4));
            card.setTranslationZ(dp(1));
            card.setClipToOutline(true);
        }
        card.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("user_id", RATKO_USER_ID);
            presentFragment(new ProfileActivity(args));
        });

        AvatarDrawable avatarDrawable = new AvatarDrawable();
        BackupImageView avatarView = new BackupImageView(context);
        avatarView.setRoundRadius(dp(28));
        TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(RATKO_USER_ID);
        if (user != null) {
            avatarDrawable.setInfo(currentAccount, user);
            avatarView.setForUserOrChat(user, avatarDrawable);
        } else {
            avatarDrawable.setInfo(RATKO_USER_ID, "Galigin", "TeamRatko");
            avatarView.setImageDrawable(avatarDrawable);
        }
        card.addView(avatarView, LayoutHelper.createFrame(56, 56, Gravity.LEFT | Gravity.CENTER_VERTICAL, 20, 0, 0, 0));

        TextView nameView = new TextView(context);
        nameView.setText(user != null ? UserObject.getUserName(user) : RATKO_NAME);
        nameView.setTextColor(textColor);
        nameView.setTypeface(AndroidUtilities.bold());
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        nameView.setSingleLine(true);
        card.addView(nameView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 94, 27, 56, 0));

        TextView usernameView = new TextView(context);
        usernameView.setText(RATKO_USERNAME);
        usernameView.setTextColor(secondaryTextColor);
        usernameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        usernameView.setSingleLine(true);
        card.addView(usernameView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 94, 56, 56, 0));

        ImageView arrowView = new ImageView(context);
        arrowView.setImageResource(R.drawable.msg_arrowright);
        arrowView.setColorFilter(new PorterDuffColorFilter(secondaryTextColor, PorterDuff.Mode.SRC_IN));
        arrowView.setScaleType(ImageView.ScaleType.CENTER);
        card.addView(arrowView, LayoutHelper.createFrame(28, 28, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0, 20, 0));

        return card;
    }

    private GradientDrawable createRoundRect(int color, int radius, int strokeColor, int strokeWidth) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        if (strokeWidth > 0) {
            drawable.setStroke(strokeWidth, strokeColor);
        }
        return drawable;
    }
}
