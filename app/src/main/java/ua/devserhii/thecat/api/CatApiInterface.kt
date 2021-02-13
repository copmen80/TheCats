package ua.devserhii.thecat.api

import io.reactivex.Single
import retrofit2.http.*
import ua.devserhii.thecat.model.Category
import ua.devserhii.thecat.model.FavoritesResponse
import ua.devserhii.thecat.model.GetFavoritesResponse
import ua.devserhii.thecat.model.ImageResponse


interface CatApiInterface {
    @Headers("x-api-key: 8a4f417d-2535-4237-ae63-7e12b9238643")
    @GET("v1/images/search")
    fun getImages(
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Single<List<ImageResponse>>

    @Headers("x-api-key: 8a4f417d-2535-4237-ae63-7e12b9238643")
    @GET("v1/categories")
    fun getCategories(
    ): Single<List<Category>>

    @Headers("x-api-key: 8a4f417d-2535-4237-ae63-7e12b9238643")
    @GET("v1/images/search")
    fun getImagesByCategories(
        @Query("category_ids") category_ids: Int,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Single<List<ImageResponse>>

    @Headers("x-api-key: 8a4f417d-2535-4237-ae63-7e12b9238643")
    @POST("v1/favourites")
    fun postImageToFavourites(
        @Body body: HashMap<String, String>
    ): Single<FavoritesResponse>

    @Headers("x-api-key: 8a4f417d-2535-4237-ae63-7e12b9238643")
    @GET("v1/favourites")
    fun getFavouritesImages(
        @Query("sub_id") sub_id: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Single<List<GetFavoritesResponse>>

    @Headers("x-api-key: 8a4f417d-2535-4237-ae63-7e12b9238643")
    @DELETE("v1/favourites/{favourite_id}")
    fun deleteImageFromFavourites(
        @Path("favourite_id") favourites_id: String
    ): Single<FavoritesResponse>
}