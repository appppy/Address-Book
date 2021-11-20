package jp.osaka.cherry.addressbook.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;

import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.constants.COLOR;


/**
 * ヘルパ
 */
public class BackgroundHelper {

    /**
     * 背景の設定
     *
     * @param context コンテキスト
     * @param view    表示
     */
    public static void setLineBackground(Context context, View view, boolean isSelected) {
        if(isSelected) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setBackground(getSelectedLineDrawable(context));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setBackground(getLineDrawable(context));
            }
        }
    }

    /**
     * 背景の設定
     *
     * @param context コンテキスト
     * @param view    表示
     */
    public static void setModuleBackground(Context context, View view, boolean isSelected, COLOR color) {
        if(isSelected) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setBackground(getSelectedModuleDrawable(context, color));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setBackground(getModuleDrawable(context, color));
            }
        }
    }

    /**
     * モジュール画像取得
     *
     * @param context コンテキスト
     * @param color 色
     * @return モジュール画像
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static Drawable getModuleDrawable(Context context, COLOR color) {
        Drawable result;
        switch (color) {
            default:
            case WHITE: {
                result = context.getResources().getDrawable(R.drawable.ripple_background_white, context.getTheme());
                break;
            }
            case RED: {
                result = context.getResources().getDrawable(R.drawable.ripple_background_red, context.getTheme());
                break;
            }
            case PINK: {
                result = context.getResources().getDrawable(R.drawable.ripple_background_pink, context.getTheme());
                break;
            }
            case PURPLE: {
                result = context.getResources().getDrawable(R.drawable.ripple_background_purple, context.getTheme());
                break;
            }
            case DEEP_PURPLE: {
                result = context.getResources().getDrawable(R.drawable.ripple_background_deep_purple, context.getTheme());
                break;
            }
            case INDIGO: {
                result = context.getResources().getDrawable(R.drawable.ripple_background_indigo, context.getTheme());
                break;
            }
            case BLUE: {
                result = context.getResources().getDrawable(R.drawable.ripple_background_blue, context.getTheme());
                break;
            }
            case LIGHT_BLUE: {
                result = context.getResources().getDrawable(R.drawable.ripple_background_light_blue, context.getTheme());
                break;
            }
            case CYAN: {
                result = context.getResources().getDrawable(R.drawable.ripple_background_cyan, context.getTheme());
                break;
            }
            case TEAL: {
                result = context.getResources().getDrawable(R.drawable.ripple_background_teal, context.getTheme());
                break;
            }
            case GREEN: {
                result = context.getResources().getDrawable(R.drawable.ripple_background_green, context.getTheme());
                break;
            }
            case LIGHT_GREEN: {
                result = context.getResources().getDrawable(R.drawable.ripple_background_light_green, context.getTheme());
                break;
            }
            case LIME: {
                result = context.getResources().getDrawable(R.drawable.ripple_background_lime, context.getTheme());
                break;
            }
            case YELLOW: {
                result = context.getResources().getDrawable(R.drawable.ripple_background_yellow, context.getTheme());
                break;
            }
            case AMBER: {
                result = context.getResources().getDrawable(R.drawable.ripple_background_amber, context.getTheme());
                break;
            }
            case ORANGE: {
                result = context.getResources().getDrawable(R.drawable.ripple_background_orange, context.getTheme());
                break;
            }
            case DEEP_ORANGE: {
                result = context.getResources().getDrawable(R.drawable.ripple_background_deep_orange, context.getTheme());
                break;
            }
            case BROWN: {
                result = context.getResources().getDrawable(R.drawable.ripple_background_brown, context.getTheme());
                break;
            }
            case BLUE_GREY: {
                result = context.getResources().getDrawable(R.drawable.ripple_background_blue_grey, context.getTheme());
                break;
            }
        }
        return result;
    }

    /**
     * ライン画像取得
     *
     * @param context コンテキスト
     * @return ライン画像
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static Drawable getLineDrawable(Context context) {
        return context.getResources().getDrawable(R.drawable.ripple_background_white, context.getTheme());
    }

    /**
     * モジュール選択画像取得
     *
     * @param context コンテキスト
     * @param color　色
     * @return モジュール選択画像
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static Drawable getSelectedModuleDrawable(Context context, COLOR color) {
        Drawable result;
        switch (color) {
            default:
            case WHITE: {
                result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_white, context.getTheme());
                break;
            }
            case RED: {
                result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_red, context.getTheme());
                break;
            }
            case PINK: {
                result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_pink, context.getTheme());
                break;
            }
            case PURPLE: {
                result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_purple, context.getTheme());
                break;
            }
            case DEEP_PURPLE: {
                result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_deep_purple, context.getTheme());
                break;
            }
            case INDIGO: {
                result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_indigo, context.getTheme());
                break;
            }
            case BLUE: {
                result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_blue, context.getTheme());
                break;
            }
            case LIGHT_BLUE: {
                result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_light_blue, context.getTheme());
                break;
            }
            case CYAN: {
                result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_cyan, context.getTheme());
                break;
            }
            case TEAL: {
                result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_teal, context.getTheme());
                break;
            }
            case GREEN: {
                result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_green, context.getTheme());
                break;
            }
            case LIGHT_GREEN: {
                result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_light_green, context.getTheme());
                break;
            }
            case LIME: {
                result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_lime, context.getTheme());
                break;
            }
            case YELLOW: {
                result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_yellow, context.getTheme());
                break;
            }
            case AMBER: {
                result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_amber, context.getTheme());
                break;
            }
            case ORANGE: {
                result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_orange, context.getTheme());
                break;
            }
            case DEEP_ORANGE: {
                result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_deep_orange, context.getTheme());
                break;
            }
            case BROWN: {
                result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_brown, context.getTheme());
                break;
            }
            case BLUE_GREY: {
                result = context.getResources().getDrawable(R.drawable.ripple_selected_module_background_blue_grey, context.getTheme());
                break;
            }
        }
        return result;
    }

    /**
     * 選択ライン画像取得
     *
     * @param context コンテキスト
     * @return 選択ライン画像
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static Drawable getSelectedLineDrawable(Context context) {
        return context.getResources().getDrawable(R.drawable.ripple_selected_line_background, context.getTheme());
    }

}
