package com.twistedplane.sealnote.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

/**
 * Cache to store external fonts.
 */
public class FontCache {
    public static final String TAG = "FontCache";

    final private static Map<String, Typeface> mFontMap = new HashMap<String, Typeface>();

    public static Typeface getFont(Context context, String fontName){
        if (mFontMap.containsKey(fontName)){
            return mFontMap.get(fontName);
        }
        else {
            Typeface tf = Typeface.createFromAsset(context.getApplicationContext().getAssets(),
                    "fonts/" + fontName + ".ttf");
            mFontMap.put(fontName, tf);
            return tf;
        }
    }
}
