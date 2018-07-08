package com.example.myapplication.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication.R;

import utils.AppUtil;

/**
 * Created by Ryusuke on 2016/08/12.
 */
public class UriConfigDialogFragment extends DialogFragment {
    public static final String FRAGMENT_TAG = UriConfigDialogFragment.class.getSimpleName();

    private SubmitButtonCallback mCallbackListener;

    private DialogInterface.OnClickListener mCancelClickListener;

    public static UriConfigDialogFragment newInstance() {
        return new UriConfigDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getString(R.string.config_dialog_title_label);
        String message = getString(R.string.config_dialog_message_label);

        LayoutInflater inflater =
                (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.config_dialog_layout, null);
        final EditText editText1 = (EditText) content.findViewById(R.id.config_dialog_edit_text_1);
        final EditText editText2 = (EditText) content.findViewById(R.id.config_dialog_edit_text_2);
        final EditText editText3 = (EditText) content.findViewById(R.id.config_dialog_edit_text_3);
        final EditText editText4 = (EditText) content.findViewById(R.id.config_dialog_edit_text_4);
        final Button button = (Button) content.findViewById(R.id.config_dialog_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String entry1 = editText1.getText().toString();
                String entry2 = editText2.getText().toString();
                String entry3 = editText3.getText().toString();
                String entry4 = editText4.getText().toString();
                if (TextUtils.isEmpty(entry1) || TextUtils.isEmpty(entry2) ||
                        TextUtils.isEmpty(entry3) || TextUtils.isEmpty(entry4)) {
                    AppUtil.showToast(getActivity(),
                            getString(R.string.config_dialog_incorrent_ip_message));
                    return;
                }
                mCallbackListener.onSubmitButtonCallback(
                        getString(R.string.webSocket_uri, entry1, entry2, entry3, entry4));
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setView(content)
                .setNegativeButton(android.R.string.cancel, mCancelClickListener)
                .create();
    }

    public void setOnCancelClickListener(DialogInterface.OnClickListener listener) {
        mCancelClickListener = listener;
    }

    public void setSubmitButtonCallback(SubmitButtonCallback callbackListener) {
        mCallbackListener = callbackListener;
    }

    public interface SubmitButtonCallback {
        void onSubmitButtonCallback(String ip);
    }

}
