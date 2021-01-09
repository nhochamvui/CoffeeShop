package com.example.coffeeshop;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http.HttpHeaders;

public class HttpRequestHelper {
    String BASE_URL = "http://192.168.13.105:3000/api";
    public HttpRequestHelper(String baseUrl){
        this.BASE_URL = baseUrl;
    }
    public Request getGetRequest(String controller, String accessToken){
        Request request = new Request.Builder()
                .url(BASE_URL + controller)
                .get()
                .addHeader("Authorization", "Bearer "+ accessToken)
                .build();
            return request;
    }

    public Request getGetRequest(String controller, String objectId, String accessToken){
        Request request = new Request.Builder()
                .url(BASE_URL + controller + "/" + objectId)
                .get()
                .addHeader("Authorization", "Bearer "+ accessToken)
                .build();
        return request;
    }

    public Request getDeleteRequest(String controller, String objectId, String accessToken){
        Request request = new Request.Builder()
                .url(BASE_URL + controller + "/" + objectId)
                .delete()
                .addHeader("Authorization", "Bearer "+accessToken)
                .build();
        return request;
    }

    public Request getPostRequest(String controller, String content){
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), content);
        Request request = new Request.Builder()
                .url(BASE_URL + controller)
                .post(body)
                .build();
        return request;
    }
    public Request getPostRequest(String controller, Map<String, String> content){
        Gson gson = new Gson();
        String json = gson.toJson(content);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Request request = new Request.Builder()
                .url(BASE_URL + controller)
                .post(body)
                .build();
        return request;
    }
    public Request getPostRequest(String controller, Map<String, String> content, String accessToken){
        Gson gson = new Gson();
        String json = gson.toJson(content);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Request request = new Request.Builder()
                .url(BASE_URL + controller)
                .addHeader("Authorization", "Bearer "+accessToken)
                .post(body)
                .build();
        return request;
    }
    public Request getPostRequest(String controller, String content, String accessToken){
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), content);
        Request request = new Request.Builder()
                .url(BASE_URL + controller)
                .addHeader("Authorization", "Bearer "+accessToken)
                .post(body)
                .build();
        return request;
    }
    public Request getEditRequest(String controller, String json, String accessToken){
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Request request = new Request.Builder()
                .url(BASE_URL + controller)
                .put(body)
                .addHeader("Authorization", "Bearer "+accessToken)
                .build();
        return request;
    }
    public Request getEditRequest(String controller, String objId, String json, String accessToken){
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Request request = new Request.Builder()
                .url(BASE_URL + controller +"/"+ objId)
                .put(body)
                .addHeader("Authorization", "Bearer "+accessToken)
                .build();
        return request;
    }
}
