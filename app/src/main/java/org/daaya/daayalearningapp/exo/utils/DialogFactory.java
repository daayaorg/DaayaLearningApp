package org.daaya.daayalearningapp.exo.utils;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AlertDialog;

import org.daaya.daayalearningapp.exo.R;


public final class DialogFactory {
    public static Dialog createSimpleOkErrorDialog(Context context, String title, String message,
                                                   DialogInterface.OnClickListener neutralListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, neutralListener);
        return alertDialog.create();
    }

    public static Dialog createSimpleOkCancelDialog(Context context, String title, String message,
                                                    String okText, String cancelText,
                                                    DialogInterface.OnClickListener okListener,
                                                    DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, okListener)
                .setNegativeButton(android.R.string.cancel, cancelListener);
        if (!TextUtils.isEmpty(okText)) {
            alertDialog.setPositiveButton(okText, okListener);
        }
        if (!TextUtils.isEmpty(cancelText)) {
            alertDialog.setNegativeButton(cancelText, cancelListener);
        }
        return alertDialog.create();
    }


    public static Dialog createSimpleOkErrorDialog(Context context,
                                                   @StringRes int titleResource,
                                                   @StringRes int messageResource) {
        return createSimpleOkErrorDialog(context,
                context.getString(titleResource),
                context.getString(messageResource),
                null);
    }

    public static DialogFragmentGeneric createGenericDialog() {
        return new DialogFragmentGeneric();
    }

    public static Dialog createGenericErrorDialog(Context context, String title, String message, @StyleRes int themeResId,
                                                  DialogInterface.OnClickListener neutralListener,
                                                  DialogInterface.OnClickListener okListener,
                                                  DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, themeResId)
                .setMessage(message);
        if (null == neutralListener && null == okListener && null == cancelListener) {
            alertDialog.setNeutralButton(android.R.string.ok, neutralListener);
        } else {
            if (neutralListener != null)
                alertDialog.setNeutralButton(android.R.string.ok, neutralListener);
            else {
                if (okListener != null || cancelListener != null) {
                    alertDialog.setPositiveButton(android.R.string.ok, okListener);
                    alertDialog.setNegativeButton(android.R.string.cancel, cancelListener);
                }
            }
        }

        if (!TextUtils.isEmpty(title)) {
            alertDialog.setTitle(title);
        }

        return alertDialog.create();
    }


    public static ProgressDialog createProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        return progressDialog;
    }

    public static ProgressDialog createProgressDialog(Context context,
                                                      @StringRes int messageResource) {
        return createProgressDialog(context, context.getString(messageResource));
    }

    public static Dialog createImagePopup(Context context, Drawable imageDrawable) {
        Dialog builder = new Dialog(context);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.BLACK));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });

        ImageView imageView = new ImageView(context);
        imageView.setImageDrawable(imageDrawable);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return builder;
    }

    /**
     * Creates and returns a Dialog with a list of items.
     *
     * @param context             Context to use
     * @param titleResource       resource Id of the Title String.
     * @param stringArrayResource resource Id of the array of strings to be displayed in the list
     * @param selectedPosition    position of the selected item(starts with 0)
     * @param listener            the click listener
     * @return Dialog that can be just displayed with Dialog.show()
     */
    public static Dialog createListDialog(@NonNull Context context,
                                          @StringRes int titleResource,
                                          @ArrayRes int stringArrayResource,
                                          int selectedPosition,
                                          @Nullable DialogInterface.OnClickListener listener) {
        CharSequence[] choices = context.getResources().getTextArray(stringArrayResource);
        return createListDialog(context, titleResource, choices, selectedPosition, listener);
    }


    /**
     * Creates and returns a Dialog with a list of items.
     *
     * @param context          Context to use
     * @param titleResource    resource Id of the Title String.
     * @param values           array of the values to be shown
     * @param selectedPosition position of the selected item(starts with 0)
     * @param listener         the click listener
     * @return Dialog that can be just displayed with Dialog.show()
     */
    public static Dialog createListDialog(@NonNull Context context,
                                          @StringRes int titleResource,
                                          Object[] values,
                                          int selectedPosition,
                                          @Nullable DialogInterface.OnClickListener listener) {
        CharSequence[] choices = new CharSequence[values.length];
        for (int ii = 0; ii < values.length; ii++) {
            choices[ii] = values[ii].toString();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleResource)
                .setSingleChoiceItems(choices, selectedPosition, listener);
        return builder.create();
    }
}
