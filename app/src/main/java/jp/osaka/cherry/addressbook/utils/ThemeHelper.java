package jp.osaka.cherry.addressbook.utils;


import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.constants.COLOR;

/**
 * テーマヘルパ
 */
public class ThemeHelper {

    /**
     * 画像編集テーマ取得
     *
     * @param color 色
     * @return 画像編集テーマ
     */
    public static int getImageEditTheme(COLOR color) {
        return getDetailTheme(color);
    }

    /**
     * 画像新規作成テーマ取得
     *
     * @param color 色
     * @return 画像新規作成テーマ
     */
    public static int getImageCreateTheme(COLOR color) {
        return getDetailTheme(color);
    }

    /**
     * 画像詳細テーマ取得
     *
     * @param color 色
     * @return 画像詳細テーマ
     */
    public static int getImageDetailTheme(COLOR color) {
        return getDetailTheme(color);
    }

    /**
     * 詳細テーマ取得
     *
     * @param color 色
     * @return 詳細テーマ
     */
    public static int getDetailTheme(COLOR color) {
        int result;
        switch (color) {
            case RED: {
                result = R.style.AppTheme_Detail_Red;
                break;
            }
            case PINK: {
                result = R.style.AppTheme_Detail_Pink;
                break;
            }
            case PURPLE: {
                result = R.style.AppTheme_Detail_Purple;
                break;
            }
            case DEEP_PURPLE: {
                result = R.style.AppTheme_Detail_DeepPurple;
                break;
            }
            case INDIGO: {
                result = R.style.AppTheme_Detail_Indigo;
                break;
            }
            case BLUE: {
                result = R.style.AppTheme_Detail_Blue;
                break;
            }
            case LIGHT_BLUE: {
                result = R.style.AppTheme_Detail_LightBlue;
                break;
            }
            case CYAN: {
                result = R.style.AppTheme_Detail_Cyan;
                break;
            }
            case TEAL: {
                result = R.style.AppTheme_Detail_Teal;
                break;
            }
            case GREEN: {
                result = R.style.AppTheme_Detail_Green;
                break;
            }
            case LIGHT_GREEN: {
                result = R.style.AppTheme_Detail_LightGreen;
                break;
            }
            case LIME: {
                result = R.style.AppTheme_Detail_Lime;
                break;
            }
            case YELLOW: {
                result = R.style.AppTheme_Detail_Yellow;
                break;
            }
            case AMBER: {
                result = R.style.AppTheme_Detail_Amber;
                break;
            }
            case ORANGE: {
                result = R.style.AppTheme_Detail_Orange;
                break;
            }
            case DEEP_ORANGE: {
                result = R.style.AppTheme_Detail_DeepOrange;
                break;
            }
            case BROWN: {
                result = R.style.AppTheme_Detail_Brown;
                break;
            }
            case BLUE_GREY: {
                result = R.style.AppTheme_Detail_BlueGrey;
                break;
            }
            case GREY: {
                result = R.style.AppTheme_Detail_Grey;
                break;
            }
            default: {
                result = R.style.AppTheme_Detail;
                break;
            }
        }
        return result;
    }

    /**
     * テーマ取得
     *
     * @param color 色
     * @return テーマ
     */
    public static int getTheme(COLOR color) {
        int result;
        switch (color) {
            case RED: {
                result = R.style.AppTheme_Red;
                break;
            }
            case PINK: {
                result = R.style.AppTheme_Pink;
                break;
            }
            case PURPLE: {
                result = R.style.AppTheme_Purple;
                break;
            }
            case DEEP_PURPLE: {
                result = R.style.AppTheme_DeepPurple;
                break;
            }
            case INDIGO: {
                result = R.style.AppTheme_Indigo;
                break;
            }
            case BLUE: {
                result = R.style.AppTheme_Blue;
                break;
            }
            case LIGHT_BLUE: {
                result = R.style.AppTheme_LightBlue;
                break;
            }
            case CYAN: {
                result = R.style.AppTheme_Cyan;
                break;
            }
            case TEAL: {
                result = R.style.AppTheme_Teal;
                break;
            }
            case GREEN: {
                result = R.style.AppTheme_Green;
                break;
            }
            case LIGHT_GREEN: {
                result = R.style.AppTheme_LightGreen;
                break;
            }
            case LIME: {
                result = R.style.AppTheme_Lime;
                break;
            }
            case YELLOW: {
                result = R.style.AppTheme_Yellow;
                break;
            }
            case AMBER: {
                result = R.style.AppTheme_Amber;
                break;
            }
            case ORANGE: {
                result = R.style.AppTheme_Orange;
                break;
            }
            case DEEP_ORANGE: {
                result = R.style.AppTheme_DeepOrange;
                break;
            }
            case BROWN: {
                result = R.style.AppTheme_Brown;
                break;
            }
            case BLUE_GREY: {
                result = R.style.AppTheme_BlueGrey;
                break;
            }
            case GREY: {
                result = R.style.AppTheme_Grey;
                break;
            }
            default: {
                result = R.style.AppTheme;
                break;
            }
        }
        return result;
    }
}
