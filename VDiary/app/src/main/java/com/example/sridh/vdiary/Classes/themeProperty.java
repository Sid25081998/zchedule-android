package com.example.sridh.vdiary.Classes;

import com.example.sridh.vdiary.R;

/**
 * Created by sid on 6/15/17.
 */
public class themeProperty{
    public int colorPrimaryDark;
    public int colorPrimary;
    public int colorAccent;
    public int theme;

    //TODO GENERATE THE THEME COLORS AND ADD THEM HERE
    public themeProperty(Theme theme){
        switch (theme){
            case blue:
                colorPrimaryDark= R.color.colorPrimaryDarkBlue;
                colorPrimary=R.color.colorPrimaryBlue;
                colorAccent=R.color.colorAccentBlue;
                this.theme=R.style.AppTheme_Blue;
                break;
            case teal:
                colorPrimaryDark=R.color.colorPrimaryDarkTeal;
                colorPrimary=R.color.colorPrimaryTeal;
                colorAccent=R.color.colorAccentTeal;
                this.theme=R.style.AppTheme_Teal;
                break;
            case red:
                colorPrimaryDark=R.color.colorPrimaryDarkRed;
                colorPrimary=R.color.colorPrimaryRed;
                colorAccent=R.color.colorAccentRed;
                this.theme=R.style.AppTheme_Red;
                break;
            case yellow:
                colorPrimaryDark=R.color.colorPrimaryDarkYellow;
                colorPrimary=R.color.colorPrimaryYellow;
                colorAccent = R.color.colorAccentYellow;
                this.theme = R.style.AppTheme_Yellow;
                break;
            case pink:
                colorPrimaryDark=R.color.colorPrimaryDarkPink;
                colorPrimary=R.color.colorPrimaryPink;
                colorAccent=R.color.colorAccentPink;
                this.theme = R.style.AppTheme_Pink;
                break;
            case black:
                colorPrimaryDark=R.color.colorPrimaryDarkBlack;
                colorPrimary=R.color.colorPrimaryBlack;
                colorAccent=R.color.colorAccentBlack;
                this.theme = R.style.AppTheme_Black;
                break;
            case purple:
                colorPrimaryDark=R.color.colorPrimaryDarkPurple;
                colorPrimary=R.color.colorPrimaryPurple;
                colorAccent=R.color.colorAccentPurple;
                this.theme = R.style.AppTheme_Purple;
                break;
            case gray:
                colorPrimaryDark=R.color.colorPrimaryDarkGray;
                colorPrimary=R.color.colorPrimaryGray;
                colorAccent=R.color.colorAccentGray;
                this.theme = R.style.AppTheme_Gray;
                break;
            case orange:
                colorPrimaryDark=R.color.colorPrimaryDarkOrange;
                colorPrimary=R.color.colorPrimaryOrange;
                colorAccent=R.color.colorAccentOrange;
                this.theme = R.style.AppTheme_Orange;
                break;
            case green:
                colorPrimaryDark=R.color.colorPrimaryDarkGreen;
                colorPrimary=R.color.colorPrimaryGreen;
                colorAccent=R.color.colorAccentGreen;
                this.theme = R.style.AppTheme_Green;
                break;

            //TODO ADD MORE THEMES
        }
    }
}
