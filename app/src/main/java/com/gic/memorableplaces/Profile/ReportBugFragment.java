package com.gic.memorableplaces.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gic.memorableplaces.R;

import java.util.Objects;

public class ReportBugFragment extends Fragment {
    private static final String TAG = "LogOutFragment";
    private TextView cancelButtonTop;
    private EditText editText;
    private Button report_bug;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_bug,container,false);

        cancelButtonTop = view.findViewById(R.id.cancelButtonToolBar);
        editText = view.findViewById(R.id.report_a_bug_ET);
        report_bug = view.findViewById(R.id.Report_Bug);

        cancelButtonTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireFragmentManager().popBackStackImmediate();
            }
        });

        report_bug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"reportbugsxsc@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Bug Report");
                i.putExtra(Intent.EXTRA_TEXT   , editText.getText().toString());
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}
