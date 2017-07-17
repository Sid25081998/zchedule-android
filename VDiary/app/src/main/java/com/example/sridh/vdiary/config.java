package com.example.sridh.vdiary;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;


import com.example.sridh.vdiary.Classes.Server;
import com.example.sridh.vdiary.Classes.Theme;
import com.example.sridh.vdiary.Classes.themeProperty;
import com.example.sridh.vdiary.Utils.prefs;


import java.util.HashMap;
import java.util.Map;


public class config {
    public static String semStart;
    public static String semEnd;
    public static String cat1;
    public static String cat2;
    public static String fat;
    public static int width;
    public static int  height;
    public static Typeface fredoka,nunito_Extrabold,nunito_bold,nunito_reg;
    public static void setStatusBar(Window window, Context context,int color) {
        if(Build.VERSION.SDK_INT>=21){
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(context,color));
        }
    }  //CHANGE THE COLOR OF THE STATUS BAR
    public static String FIREBASE_URL= "https://vdiary-a25b2.firebaseio.com/";

    //PARAMETERS FOR CHANGING THE LINKS
    public static String SEM = "WS";
    public static String VERSION= "Release 2.0";

    public static Server server;

    //HOLIDAYS

    public static void getFonts(Context context){
        fredoka=Typeface.createFromAsset(context.getAssets(),"fonts/FredokaOne-Regular.ttf");
        nunito_bold=Typeface.createFromAsset(context.getAssets(),"fonts/Nunito-Bold.ttf");
        nunito_Extrabold=Typeface.createFromAsset(context.getAssets(),"fonts/Nunito-ExtraBold.ttf");
        nunito_reg = Typeface.createFromAsset(context.getAssets(), "fonts/Nunito-Regular.ttf");
    }

    public static Theme CurrentTheme;

    public static Map<Theme,themeProperty> AppThemes= new HashMap<>();
    static {
        AppThemes.put(Theme.red,new themeProperty(Theme.red));
        AppThemes.put(Theme.blue,new themeProperty(Theme.blue));
        AppThemes.put(Theme.teal,new themeProperty(Theme.teal));
        AppThemes.put(Theme.yellow, new themeProperty(Theme.yellow));
        AppThemes.put(Theme.pink, new themeProperty(Theme.pink));

        AppThemes.put(Theme.purple, new themeProperty(Theme.purple));
        AppThemes.put(Theme.gray, new themeProperty(Theme.gray));
        AppThemes.put(Theme.green, new themeProperty(Theme.green));
        AppThemes.put(Theme.orange, new themeProperty(Theme.orange));
    }
    public static themeProperty getCurrentTheme(Context context){
        if(CurrentTheme==null){
            CurrentTheme = prefs.getTheme(context); //GETTING THE THEME FROM THE SHARED PREFERENCES
        }
        return AppThemes.get(CurrentTheme);
    }
    public static String CHENNAI_URL = "https://academicscc.vit.ac.in/";
    public static String VELLORE_URL = "https://vtop.vit.ac.in/";

    public static String MY_CAMPUS="https://academicscc.vit.ac.in/";


}


