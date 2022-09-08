package com.debuggers.apnatutor.Fragments;

import static com.debuggers.apnatutor.App.ME;
import static com.debuggers.apnatutor.App.PREFERENCES;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.debuggers.apnatutor.Activities.MainActivity;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentProfileBinding binding = FragmentProfileBinding.inflate(inflater, container, false);
        binding.userName.setText(ME.getName());
        binding.userEmail.setText(ME.getEmail());
        binding.userType.setText(ME.getType());

        binding.settingsBtn.setOnClickListener(v-> {

        });
        binding.faqsBtn.setOnClickListener(v-> {

        });
        binding.logoutBtn.setOnClickListener(v-> {
            PREFERENCES.edit().remove("EMAIL").remove("PASSWORD").apply();
            startActivity(new Intent(requireContext(), MainActivity.class));
            requireActivity().finishAffinity();
        });

        return binding.getRoot();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.search).setVisible(false);
    }
}