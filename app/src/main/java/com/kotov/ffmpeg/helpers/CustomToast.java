package com.kotov.ffmpeg.helpers;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kotov.ffmpeg.R;

import androidx.cardview.widget.CardView;

public class CustomToast extends Toast {

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public CustomToast(Context context) {
        super(context);
    }
    public static void getToast(Context context, int drawable, int string, int color, int length) {
        Toast toast = new Toast(context);
        toast.setDuration(length);
        View inflate = LayoutInflater.from(context).inflate(R.layout.toast_icon_text, (ViewGroup) null);
        ((TextView) inflate.findViewById(R.id.message)).setText(string);
        ((ImageView) inflate.findViewById(R.id.icon)).setImageResource(drawable);
        ((CardView) inflate.findViewById(R.id.parent_view)).setCardBackgroundColor(context.getResources().getColor(color));
        toast.setView(inflate);
        toast.show();
    }

}
