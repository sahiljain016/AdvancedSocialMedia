package com.gic.memorableplaces.SignUp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gic.memorableplaces.R;


public class PrepareCardFragment extends Fragment {
    private static final String TAG = "PrepareCardFragment";

    private String sUsername;

    private Context mContext;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_card, container, false);
        Log.d(TAG, "onCreateView: PrepareCardFragment");
        mContext = getActivity();
        if (getArguments() != null) {
            sUsername = String.format("%s", getArguments().getString(requireActivity().getString(R.string.field_username)));

        }

        final CourseAndFullNameCardFragment courseAndFullNameCardFragment = new CourseAndFullNameCardFragment();

        Bundle CourseBundle = new Bundle();
        CourseBundle.putString(requireActivity().getString(R.string.field_username), sUsername);

        ChangeFragment(courseAndFullNameCardFragment, mContext.getString(R.string.course_and_full_name_Card_fragment), CourseBundle);



        return view;
    }

    private void ChangeFragment(Fragment fragment, String FragmentName, Bundle bundle) {

        fragment.setArguments(bundle);
        FragmentTransaction Transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        Transaction.replace(R.id.FrameLayoutCardSwitcher, fragment);
        Transaction.addToBackStack(FragmentName);
        Transaction.commit();
    }
}
