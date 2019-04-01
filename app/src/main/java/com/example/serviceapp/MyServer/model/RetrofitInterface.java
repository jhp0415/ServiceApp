package com.example.serviceapp.MyServer.model;

import com.example.serviceapp.MyServer.POJO.sAccess;
import com.example.serviceapp.MyServer.POJO.sPlace;
import com.example.serviceapp.MyServer.POJO.sPlaceOverview;
import com.example.serviceapp.MyServer.POJO.sPlaceWithComment;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RetrofitInterface {

    public class signCheckRequest {
        private String accessToken;
        public signCheckRequest(String accessToken) {
            this.accessToken = accessToken;
        }
    }

    public class addReviewBody {
        private String fbId;
        private String poiId;
        private String captionTitle;
        private String captionBody;

        public addReviewBody(String fbId, String poiId, String captionTitle, String captionBody) {
            this.fbId = fbId;
            this.poiId = poiId;
            this.captionTitle = captionTitle;
            this.captionBody = captionBody;
        }
    }

    public class updateReviewBody {
        private String fbId;
        private String reviewId;
        private String captionTitle;
        private String captionBody;

        public updateReviewBody(String fbId, String reviewId, String captionTitle, String captionBody) {
            this.fbId = fbId;
            this.reviewId = reviewId;
            this.captionTitle = captionTitle;
            this.captionBody = captionBody;
        }
    }

    public class addMyListBody {
        private String fbId;
        private String poiId;

        public addMyListBody(String fbId, String poiId) {
            this.fbId = fbId;
            this.poiId = poiId;
        }
    }

    @POST("user/signInOrSignUp")
    Call<sAccess> getSignCheck(@Body signCheckRequest body);

    @Multipart
    @POST("places/addPlaceInfo")
    Call<sPlaceOverview> addPhoto(@Query("poiId") String poiId,
                                  @Part MultipartBody.Part file);

    @POST("comments/addCommentToPoi")
    Call<sPlaceWithComment> addReview(@Body addReviewBody body);

    @POST("comments/updateCommentOfPoi")
    Call<HashMap<String, Object>> updateReview(@Body updateReviewBody body);

    @DELETE("comments/deleteCommentOfPoi")
    Call<HashMap<String, Object>> deleteReview(@Query("_id") String _id,
                                               @Query("fbId") String fbId,
                                               @Query("poiId") String poiId);

    @POST("places/addToUsersList")
    Call<HashMap<String, Object>> addMyList(@Body addMyListBody body);

    @GET("places/getMyPlacesLists")
    Call<ArrayList<sPlace>> getMyPlacesLists(@Query("fbId") String fbId);

    @GET("places/getCurrentPlaceInfo")
    Call<sPlaceWithComment> getCurrentPlace(@Query("poiId") String poiId);

    @DELETE("places/deletePlaceFromMyList")
    Call<ArrayList<sPlace>> delMyPlace(@Query("fbId") String fbId,
                                       @Query("poiId") String poiId);
}