package junhyeon.com.alarm;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v7.widget.AppCompatRadioButton;
import android.widget.RadioGroup;

public class ViewUtils {
    private static final int PADDING_LEFT = 10;
    private static final int PADDING_TOP = 20;
    private static final int PADDING_RIGHT = 10;
    private static final int PADDING_BOTTOM = 20;

    //context, title, colorStateList, checkedItemTitle, id
    public static AppCompatRadioButton getRadioButton(Context context, RadioGroup.LayoutParams layoutParams, float scale,
                                                      String text, ColorStateList colorStateList, int id){
        AppCompatRadioButton radioButton = new AppCompatRadioButton(context);
        radioButton.setText(text);
        radioButton.setTextColor(colorStateList);
        radioButton.setSupportButtonTintList(colorStateList);
        radioButton.setPadding(
                getScaledDensityPixel(PADDING_LEFT, scale),
                getScaledDensityPixel(PADDING_TOP, scale),
                getScaledDensityPixel(PADDING_RIGHT, scale),
                getScaledDensityPixel(PADDING_BOTTOM, scale)
        );
        radioButton.setLayoutParams(layoutParams);
        radioButton.setId(id);

        return radioButton;
    }

    private static int getScaledDensityPixel(int value, float scale){
        return (int)(value * scale + 0.5f);
    }
}
