package com.scv.slackgo.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.scv.slackgo.R;

/**
 * Created by ayelen@scvsoft.com
 */
public class ErrorUtils {

    public static void showErrorAlert(Context context) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(context.getText(R.string.error_title));
        alertDialog.setMessage(context.getText(R.string.error_msg));
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, context.getText(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.hide();
            } });
        alertDialog.show();
    }
}
