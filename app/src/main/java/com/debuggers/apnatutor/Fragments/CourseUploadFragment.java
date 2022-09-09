package com.debuggers.apnatutor.Fragments;

import static com.debuggers.apnatutor.App.QUEUE;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.debuggers.apnatutor.Helpers.API;
import com.debuggers.apnatutor.Helpers.MultipartUploadRequest;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.FragmentCourseUploadBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CourseUploadFragment extends Fragment {
    FragmentCourseUploadBinding binding;
    Uri thumbnail;
    private final ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
        if (result != null) {
            thumbnail = result;
            Cursor cursor = requireContext().getContentResolver().query(thumbnail, null, null, null, null);
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            binding.thumbnail.setImageURI(thumbnail);
            binding.thumbnailName.setText(cursor.getString(nameIndex));
        }
    });

    public CourseUploadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCourseUploadBinding.inflate(inflater, container, false);

        binding.thumbnail.setOnClickListener(view -> {
            launcher.launch("image/*");
        });

        return binding.getRoot();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.done) {
            upload();
        }
        return super.onOptionsItemSelected(item);
    }

    private void upload() {
        ProgressDialog pd = new ProgressDialog(requireContext());
        pd.setCancelable(false);
        pd.setTitle("Uploading thumbnail...");

        QUEUE.add(new MultipartUploadRequest(Request.Method.PUT, API.UPLOAD_THUMBNAIL, response -> {
            pd.dismiss();
            Log.d("TAG", "upload: "+response);
        }, error -> {
            Toast.makeText(requireContext(), API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }) {
            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> body = new HashMap<>();
                try {
                    body.put("thumbnail", new DataPart(
                            binding.thumbnailName.getText().toString(),
                            requireContext().getContentResolver().openInputStream(thumbnail),
                            requireContext().getContentResolver().getType(thumbnail)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return body;
            }
        }).setRetryPolicy(new DefaultRetryPolicy());
    }
}