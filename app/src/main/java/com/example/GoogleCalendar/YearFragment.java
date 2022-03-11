package com.example.GoogleCalendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link YearFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YearFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int year;


    public YearFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static YearFragment newInstance(int year) {
        YearFragment fragment = new YearFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, year);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            year = getArguments().getInt(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_year, container, false);
        YearView yearView = view.findViewById(R.id.yearview);
        yearView.updateYearView(year);
        return view;
    }
}