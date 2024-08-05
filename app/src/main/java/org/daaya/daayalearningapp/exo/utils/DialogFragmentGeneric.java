package org.daaya.daayalearningapp.exo.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import org.daaya.daayalearningapp.exo.R;

public class DialogFragmentGeneric extends AppCompatDialogFragment {
    TextView mContentTextView;

    public static final String TAG = DialogFragmentGeneric.class.getSimpleName();

    private DialogInterface.OnClickListener mPositiveListener;
    private DialogInterface.OnClickListener mNegativeListener;
    private String mTitle;
    private String mContent;
    private String mPositiveButtonTitle;
    private String mNegativeButtonTitle;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setRetainInstance(true);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.Theme_DaayaLearningApp);
        View contentView = LayoutInflater.from(dialogBuilder.getContext()).inflate(R.layout.fragment_generic_dialog, null);
        AlertDialog genericDialog = dialogBuilder.setView(contentView)
                .setPositiveButton(mPositiveButtonTitle, mPositiveListener)
                .setNegativeButton(mNegativeButtonTitle, mNegativeListener)
                .create();
        genericDialog.setTitle(mTitle);

        mContentTextView = getView().findViewById(R.id.dialog_content);
        TextView titleView = new TextView(getActivity());
        titleView.setTextAppearance(android.R.style.TextAppearance_Material_Subhead);
        titleView.setMaxLines(3);
        titleView.setText(mTitle);
        int padding = DeviceUtil.getPixelsFromDip(getActivity(), 25);
        titleView.setPadding(padding, padding, padding, 0);
        genericDialog.setCustomTitle(titleView);

        if (null != mContent) {
            mContentTextView.setText(mContent);
        } else {
            mContentTextView.setVisibility(View.GONE);
        }
        return genericDialog;
    }

    public void setContent(String title, String content, String positiveButtonTitle, String negativeButtonTitle, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        mTitle = title;
        mContent = content;
        mPositiveListener = positiveListener;
        mNegativeListener = negativeListener;
        mPositiveButtonTitle = positiveButtonTitle;
        mNegativeButtonTitle = negativeButtonTitle;
    }
}
