package com.debuggers.apnatutor.Fragments;

import static com.debuggers.apnatutor.App.ME;
import static com.debuggers.apnatutor.App.QUEUE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.debuggers.apnatutor.Activities.PlaylistActivity;
import com.debuggers.apnatutor.Adapters.CourseAdapter;
import com.debuggers.apnatutor.Helpers.API;
import com.debuggers.apnatutor.Models.Course;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.FragmentHomeBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class StudentHomeFragment extends Fragment {
    FragmentHomeBinding binding;

    public StudentHomeFragment() {
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
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        binding.homeRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.homeRV.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        updateUi();
        binding.homeRefresher.setOnRefreshListener(this::updateUi);

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
                if (binding.homeRV.getAdapter() != null) ((CourseAdapter) binding.homeRV.getAdapter()).getFilter().filter(newText);
                return false;
            }
        });
    }

    private void updateUi() {
        binding.homeRefresher.setRefreshing(true);
        QUEUE.add(new JsonArrayRequest(Request.Method.GET, API.COURSES_ALL, null, response -> {
            List<Course> courses = new Gson().fromJson(response.toString(), new TypeToken<List<Course>>(){}.getType());
            binding.homeRV.setAdapter(new CourseAdapter(courses, new ArrayList<>(Collections.nCopies(courses.size(), null)), new CourseAdapter.setEventListeners() {
                @Override
                public void OnClickListener(Course course, int position) {
                    startActivity(new Intent(requireContext(), PlaylistActivity.class).putExtra("COURSE", course.get_id()));
                }

                @Override
                public void OnFollowListener(Course course, boolean follow, int position) {
                    if (follow) {
                        QUEUE.add(new JsonObjectRequest(Request.Method.POST, String.format("%s?course=%s", API.COURSE_ADD_FOLLOWER, course.get_id()), null, courseRes -> {
                            Course newCourse = new Gson().fromJson(courseRes.toString(), Course.class);
                            Log.d("TAG", "OnFollowListener: "+newCourse.getFollowedBy());
                            courses.set(position, newCourse);
                            Objects.requireNonNull(binding.homeRV.getAdapter()).notifyItemChanged(position);
                        }, error -> {
                            Toast.makeText(requireContext(), API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
                        }) {
                            @Override
                            public byte[] getBody() {
                                JSONObject object = new JSONObject();
                                try {
                                    object.put("user", ME.get_id());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                return object.toString().getBytes(StandardCharsets.UTF_8);
                            }
                        }).setRetryPolicy(new DefaultRetryPolicy());
                    } else {
                        QUEUE.add(new JsonObjectRequest(Request.Method.POST, String.format("%s?course=%s", API.COURSE_REMOVE_FOLLOWER, course.get_id()), null, courseRes -> {
                            Course newCourse = new Gson().fromJson(courseRes.toString(), Course.class);
                            courses.set(position, newCourse);
                            Objects.requireNonNull(binding.homeRV.getAdapter()).notifyItemChanged(position);
                        }, error -> {
                            Toast.makeText(requireContext(), API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
                        }) {
                            @Override
                            public byte[] getBody() {
                                JSONObject object = new JSONObject();
                                try {
                                    object.put("user", ME.get_id());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                return object.toString().getBytes(StandardCharsets.UTF_8);
                            }
                        }).setRetryPolicy(new DefaultRetryPolicy());
                    }
                }

                @Override
                public void OnDeleteListener(Course course, int position) {
                    AlertDialog dialog = new AlertDialog.Builder(requireContext())
                            .setTitle("Be sure before delete!")
                            .setMessage("Are you sure you want to delete this course? All the videos, views and comments will be deleted permanently as soon as you delete the course!")
                            .setCancelable(false)
                            .setPositiveButton("DELETE", (dialogInterface, i) -> {
                                QUEUE.add(new JsonObjectRequest(Request.Method.DELETE, String.format("%s?course=%s", API.COURSE_DELETE, course.get_id()), null, courseRes -> {
                                    courses.remove(course);
                                    dialogInterface.dismiss();
                                    Objects.requireNonNull(binding.homeRV.getAdapter()).notifyItemRemoved(position);
                                }, error -> {
                                    dialogInterface.dismiss();
                                    Toast.makeText(requireContext(), API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
                                })).setRetryPolicy(new DefaultRetryPolicy());
                            }).setNegativeButton("CANCEL", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                            }).create();
                    dialog.show();
                }
            }));
            binding.homeRefresher.setRefreshing(false);
        }, error -> {
            binding.homeRefresher.setRefreshing(false);
            Toast.makeText(requireContext(), API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
        })).setRetryPolicy(new DefaultRetryPolicy());
    }
}