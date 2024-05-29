package com.example.buzzchat;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutAppFragment extends Fragment {

    private View aboutAppScreenView ;
    private View bottomNavigattion;     private AppCompatButton app_about_btn;

    @Override
    public void onResume() {
        super.onResume();

        bottomNavigattion = requireActivity().findViewById(R.id.main_fragment_bottom_navigation);
        bottomNavigattion.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bottomNavigattion.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        aboutAppScreenView = inflater.inflate(R.layout.fragment_about_app, container, false);
        FieldsInitializations();

        app_about_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        return aboutAppScreenView;
    }

    private void FieldsInitializations() {

        app_about_btn = aboutAppScreenView.findViewById(R.id.app_about_btn);
    }
}