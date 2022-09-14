package com.debuggers.apnatutor.Helpers;

import com.android.volley.VolleyError;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class API {
    private static final String BASE_URL = "https://apnatutor.herokuapp.com";

    public static final String USER_BY_ID = String.format("%s/users", BASE_URL);
    public static final String USERS_IN = String.format("%s/users/in", BASE_URL);
    public static final String SIGNUP = String.format("%s/users/signup", BASE_URL);
    public static final String LOGIN = String.format("%s/users/login", BASE_URL);
    public static final String USER_UPDATE = String.format("%s/users/update", BASE_URL);
    public static final String USER_DELETE = String.format("%s/users/delete", BASE_URL);

    public static final String COURSE_BY_ID = String.format("%s/courses", BASE_URL);
    public static final String COURSES_ALL = String.format("%s/courses/all", BASE_URL);
    public static final String COURSES_IN = String.format("%s/courses/in", BASE_URL);
    public static final String COURSES_FOLLOWED = String.format("%s/courses/followed", BASE_URL);
    public static final String COURSES_UPLOADED = String.format("%s/courses/uploaded", BASE_URL);
    public static final String COURSE_ADD = String.format("%s/courses/add", BASE_URL);
    public static final String COURSE_UPDATE = String.format("%s/courses/update", BASE_URL);
    public static final String COURSE_DELETE = String.format("%s/courses/delete", BASE_URL);

    public static final String VIDEO_BY_ID = String.format("%s/videos", BASE_URL);
    public static final String VIDEOS_ALL = String.format("%s/videos/all", BASE_URL);
    public static final String VIDEOS_IN = String.format("%s/videos/in", BASE_URL);
    public static final String VIDEO_ADD = String.format("%s/videos/add", BASE_URL);
    public static final String VIDEO_ADD_VIEW = String.format("%s/videos/add-view", BASE_URL);
    public static final String VIDEO_ADD_LIKE = String.format("%s/videos/add-like", BASE_URL);
    public static final String VIDEO_REMOVE_LIKE = String.format("%s/videos/remove-like", BASE_URL);
    public static final String VIDEO_ADD_NOTES = String.format("%s/videos/add-notes", BASE_URL);
    public static final String VIDEO_REMOVE_NOTES = String.format("%s/videos/remove-notes", BASE_URL);
    public static final String VIDEO_UPDATE = String.format("%s/videos/update", BASE_URL);
    public static final String VIDEO_DELETE = String.format("%s/videos/delete", BASE_URL);

    public static final String COMMENT_BY_ID = String.format("%s/comments", BASE_URL);
    public static final String COMMENTS_ALL = String.format("%s/comments/all", BASE_URL);
    public static final String COMMENTS_IN = String.format("%s/comments/in", BASE_URL);
    public static final String COMMENT_ADD = String.format("%s/comments/add", BASE_URL);
    public static final String COMMENT_ADD_LIKE = String.format("%s/comments/add-like", BASE_URL);
    public static final String COMMENT_REMOVE_LIKE = String.format("%s/comments/remove-like", BASE_URL);
    public static final String COMMENT_UPDATE = String.format("%s/comments/update", BASE_URL);
    public static final String COMMENT_DELETE = String.format("%s/comments/delete", BASE_URL);


    public static final String UPLOAD_THUMBNAIL = String.format("%s/upload/thumbnail", BASE_URL);
    public static final String UPLOAD_AVATAR = String.format("%s/upload/avatar", BASE_URL);
    public static final String UPLOAD_VIDEO = String.format("%s/upload/video", BASE_URL);

    public static String getQuery(String url, Map<String, String> params) {
        StringBuilder queryBuilder = new StringBuilder(url);
        queryBuilder.append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            queryBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        return queryBuilder.deleteCharAt(queryBuilder.length() - 1).toString();
    }

    public static String parseVolleyError(VolleyError error) {
        try {
            return new String(error.networkResponse.data, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return "Something went wrong!";
        }
    }
}
