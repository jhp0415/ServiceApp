package com.example.serviceapp.MyServer.model;

import android.util.Log;

import com.example.serviceapp.MyServer.POJO.sAccess;
import com.example.serviceapp.MyServer.POJO.sPlace;
import com.example.serviceapp.MyServer.POJO.sPlaceOverview;
import com.example.serviceapp.MyServer.POJO.sPlaceWithComment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MyServerServiceModel {
    private Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
    private Retrofit retrofit = retrofitBuilder.baseUrl("http://52.79.72.47:3000/").addConverterFactory(GsonConverterFactory.create()).build();

    public interface callSignCheckListener {
        public void onSignCheckFinished(sAccess response);
        public void onSignCheckFailure(Throwable t);
    }

    public void callSignCheck(String accessToken, final callSignCheckListener onFinishedListener) {
        Call<sAccess> call = retrofit.create(RetrofitInterface.class).getSignCheck(new RetrofitInterface.signCheckRequest(accessToken));

        call.enqueue(new Callback<sAccess>() {
            @Override
            public void onResponse(Call<sAccess> call, Response<sAccess> response) {
                try {
                    onFinishedListener.onSignCheckFinished(response.body());
                } catch (Exception e) {
                    Log.d("callSignCheck", "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<sAccess> call, Throwable t) {
                Log.d("callSignCheck", "fail");
                //TODO: onSignCheckFailure
            }
        });
    }

    public interface callMyPlaceListListener {
        public void onGetMyPlaceListFinished(ArrayList<sPlace> response);
        public void onGetMyPlaceListFailure(Throwable t);
    }

    public void callMyPlaceList(String fbId, final callMyPlaceListListener onFinishedListener) {
        Call<ArrayList<sPlace>> call = retrofit.create(RetrofitInterface.class).getMyPlacesLists(fbId);
        Log.d("callMyPlaceList", "callMyPlaceList: " + call.request().toString());

        call.enqueue(new Callback<ArrayList<sPlace>>() {
            @Override
            public void onResponse(Call<ArrayList<sPlace>> call, Response<ArrayList<sPlace>> response) {
                Log.d("callMyPlaceList", "onResponse: ");
                onFinishedListener.onGetMyPlaceListFinished(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<sPlace>> call, Throwable t) {
                Log.d("callMyPlaceList", "onFailure: ");
                //TODO: onGetMyPlaceListFailure
                onFinishedListener.onGetMyPlaceListFailure(t);
            }
        });
    }

    public interface delMyPlaceListener {
        public void onDelMyPlaceFinished(ArrayList<sPlace> response);
        public void onDelMyPlaceFailure(Throwable t);
    }

    public void delMyPlace(String fbId, String poiId, final delMyPlaceListener onFinishedListener) {
        Call<ArrayList<sPlace>> call = retrofit.create(RetrofitInterface.class).delMyPlace(fbId, poiId);
        Log.d("delMyPlace", "delMyPlace: " + call.request().toString());

        call.enqueue(new Callback<ArrayList<sPlace>>() {
            @Override
            public void onResponse(Call<ArrayList<sPlace>> call, Response<ArrayList<sPlace>> response) {
                Log.d("delMyPlace", "onResponse: ");
                onFinishedListener.onDelMyPlaceFinished(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<sPlace>> call, Throwable t) {
                Log.d("delMyPlace", "onFailure: ");
                //TODO: onDelMyPlaceFailure
                onFinishedListener.onDelMyPlaceFailure(t);
            }
        });
    }

    public interface callCurrentPlaceListener {
        public void onGetCurrentPlaceFinished(sPlaceWithComment response);
        public void onGetCurrentPlaceFailure(Throwable t);
    }

    public void callCurrentPlace(String poiId, final callCurrentPlaceListener onFinishedListener) {
        Log.d("ddd", "이미지 call 보내기");
        Call<sPlaceWithComment> call = retrofit.create(RetrofitInterface.class).getCurrentPlace(poiId);

        call.enqueue(new Callback<sPlaceWithComment>() {
            @Override
            public void onResponse(Call<sPlaceWithComment> call, Response<sPlaceWithComment> response) {
                Log.d("ddd", "이미지 call 성공");
                onFinishedListener.onGetCurrentPlaceFinished(response.body());
            }

            @Override
            public void onFailure(Call<sPlaceWithComment> call, Throwable t) {
                //TODO: onGetCurrentPlaceFailure
                Log.d("ddd", "이미지 call 실패 : " + t.getMessage());
                // view 초기화하기
                onFinishedListener.onGetCurrentPlaceFailure(t);
            }
        });
    }

    public interface addPlacePhotoListener {
        public void onAddPlacePhotoFinished(sPlaceOverview response);
        public void onAddPlacePhotoFailure(Throwable t);
    }

    public void addPlacePhoto(String poiId, File imgFile, final addPlacePhotoListener onFinishedListener) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imgFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("imgFile", imgFile.getName(), requestFile);

        Call<sPlaceOverview> call = retrofit.create(RetrofitInterface.class).addPhoto(poiId, body);

        call.enqueue(new Callback<sPlaceOverview>() {
            @Override
            public void onResponse(Call<sPlaceOverview> call, Response<sPlaceOverview> response) {
                onFinishedListener.onAddPlacePhotoFinished(response.body());
                Log.d("addPlacePhoto", "success");
            }

            @Override
            public void onFailure(Call<sPlaceOverview> call, Throwable t) {
                //TODO: onAddPlacePhotoFailure
                Log.d("addPlacePhoto", "fail");
                Log.d("addPlacePhoto", t.getMessage().toString());
            }
        });
    }

    public interface addPlaceReviewListener {
        public void onAddPlaceReviewFinished(sPlaceWithComment response);
        public void onAddPlaceReviewFailure(Throwable t);
    }

    public void addPlaceReview(String fbId, String poiId, String commentTitle, String commentBody, final addPlaceReviewListener onFinishedListener) {
        Call<sPlaceWithComment> call = retrofit.create(RetrofitInterface.class).addReview(
                new RetrofitInterface.addReviewBody(fbId, poiId, commentTitle, commentBody));

        Log.d("addPlaceReview", call.request().toString());

        call.enqueue(new Callback<sPlaceWithComment>() {
            @Override
            public void onResponse(Call<sPlaceWithComment> call, Response<sPlaceWithComment> response) {
                onFinishedListener.onAddPlaceReviewFinished(response.body());
                Log.d("addPlaceReview", "success");
            }

            @Override
            public void onFailure(Call<sPlaceWithComment> call, Throwable t) {
                //TODO: onAddPlacePhotoFailure
                Log.d("addPlaceReview", "fail");
                t.printStackTrace();
                Log.d("addPlaceReview", t.getMessage().toString());
            }
        });
    }

    public interface addMyListListener {
        public void onAddMyListFinished(HashMap<String, Object> response);
        public void onAddMyListFailure(Throwable t);
    }

    public void addMyListList(String fbId, String poiId, final addMyListListener onFinishedListener) {
        Call<HashMap<String, Object>> call = retrofit.create(RetrofitInterface.class)
                .addMyList(new RetrofitInterface.addMyListBody(fbId, poiId));
        Log.d("ddd", "즐겨찾기에 추가 call 보내기");
        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                onFinishedListener.onAddMyListFinished(response.body());
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.d("ddd", "onFailure: ");
                //TODO: onGetMyPlaceListFailure
            }
        });
    }


    /*public class myPlaceDeserializer implements JsonDeserializer<sPlace> {
        @Override
        public sPlace deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Log.d("myPlaceDeserializer", "oh");
            if (((JsonObject) json).get("comments") instanceof JsonObject){
                Log.d("myPlaceDeserializer", "wii");
                return new Gson().fromJson(json, new TypeToken<ArrayList<sComment>>(){}.getType());
            } else {
                Log.d("myPlaceDeserializer", "arae");
                return new Gson().fromJson(json, new TypeToken<ArrayList<String>>(){}.getType());
            }
        }
    }*/
}