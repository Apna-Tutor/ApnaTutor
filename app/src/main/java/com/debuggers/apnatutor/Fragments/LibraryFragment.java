package com.debuggers.apnatutor.Fragments;

import static com.debuggers.apnatutor.App.QUEUE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.debuggers.apnatutor.Adapters.CourseAdapter;
import com.debuggers.apnatutor.Helpers.API;
import com.debuggers.apnatutor.Models.Course;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.FragmentLibraryBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class LibraryFragment extends Fragment {
    FragmentLibraryBinding binding;

    public LibraryFragment() {
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
        binding = FragmentLibraryBinding.inflate(inflater, container, false);

        updateUi();
        binding.libraryRefresher.setOnRefreshListener(this::updateUi);

        return binding.getRoot();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.done).setVisible(false);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (binding.libraryRV.getAdapter() != null) ((CourseAdapter) binding.libraryRV.getAdapter()).getFilter().filter(newText);
                return false;
            }
        });
    }

    private void updateUi() {
        binding.libraryRefresher.setRefreshing(true);
        QUEUE.add(new JsonObjectRequest(Request.Method.GET, API.COURSES_FOLLOWED, null, response -> {
            List<Course> courses = new Gson().fromJson(response.toString(), new TypeToken<List<Course>>(){}.getType());
            binding.libraryRV.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.libraryRV.setAdapter(new CourseAdapter(courses, (course, position) -> {

            }));
            binding.libraryRefresher.setRefreshing(false);
        }, error -> {
            binding.libraryRefresher.setRefreshing(false);
            Toast.makeText(requireContext(), API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
        })).setRetryPolicy(new DefaultRetryPolicy());
    }

}