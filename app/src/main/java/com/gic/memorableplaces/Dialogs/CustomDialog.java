package com.gic.memorableplaces.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gic.memorableplaces.R;


public class CustomDialog extends Dialog {
    private int LayoutID = R.layout.dialog_upload_progress;

    public CustomDialog(@NonNull Context context) {
        super(context);
    }

    protected CustomDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public CustomDialog(Context context, int LayoutID) {
        super(context);
        this.LayoutID = LayoutID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LayoutID);

    }
}
