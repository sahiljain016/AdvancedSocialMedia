package com.gic.memorableplaces.Home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.gic.memorableplaces.R;

public class StudentDashboardFragment extends Fragment {
    private static final String TAG = "ClassroomBSCITFragment";
    private CardView cvCollegeCard,cvClassGroup,cvCRCard,cvMap,cvQuestion,cvBooks,cvSocieties,cvWebsite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classroom_dashboard,container,false);
        Log.d(TAG, "onCreateView: claassBSCIT");

        cvCollegeCard = view.findViewById(R.id.College_card_Cv);
        cvClassGroup = view.findViewById(R.id.class_group_cv);
        cvCRCard = view.findViewById(R.id.CR_cv);
        cvMap = view.findViewById(R.id.Map_CV);
        cvQuestion = view.findViewById(R.id.Question_CV);
        cvBooks = view.findViewById(R.id.Books_CV);
        cvSocieties = view.findViewById(R.id.Societies_CV);
        cvWebsite = view.findViewById(R.id.Website_CV);

        cvCollegeCard.animate().alpha(1).setDuration(250).start();
        cvClassGroup.animate().alpha(1).setDuration(250).setStartDelay(250);
        cvCRCard.animate().alpha(1).setDuration(250).setStartDelay(500);
        cvMap.animate().alpha(1).setDuration(250).setStartDelay(750);
        cvQuestion.animate().alpha(1).setDuration(250).setStartDelay(1000);
        cvBooks.animate().alpha(1).setDuration(250).setStartDelay(1250);
        cvSocieties.animate().alpha(1).setDuration(250).setStartDelay(1500);
        cvWebsite.animate().alpha(1).setDuration(250).setStartDelay(1750);

        return view;
    }
}
