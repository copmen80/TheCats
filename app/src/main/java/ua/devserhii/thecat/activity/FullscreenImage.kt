package ua.devserhii.thecat.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.facebook.AccessToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fullscreen_image.*
import ua.devserhii.thecat.api.CatApiClient
import ua.devserhii.thecat.R

class FullscreenImage : AppCompatActivity() {
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fullscreen_image)

        Glide.with(this)
            .load(intent.getStringExtra("imagePath"))
            .into(ivDetail)

        fab_addFavorite.setOnClickListener {
            addToFavourites()
        }

        val favId = intent.getStringExtra("imageId") ?: ""


        fab_deleteFavorite.setOnClickListener {
            deleteFromFavourites(favId)
        }
    }

    private fun addToFavourites() {
        val accessToken = AccessToken.getCurrentAccessToken()
        if (accessToken != null && !accessToken.isExpired) {
            val urlPath = intent.getStringExtra("imagePath")
            val imageId = urlPath.substringAfterLast("/").substringBefore(".")
            val userId = intent.getStringExtra("subId")

            val postImage =
                CatApiClient.API_INTERFACE_CLIENT.postImageToFavourites(
                    hashMapOf(
                        "image_id" to imageId,
                        "sub_id" to userId
                    )
                )

            val subscribe = postImage
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Toast.makeText(
                            this, "Added to favorites", Toast.LENGTH_SHORT
                        ).show()
                    },
                    { error ->
                        Log.d("TAG", error.toString())
                        Toast.makeText(
                            this,
                            "Already added to favorites or an error occurred ",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                )
            compositeDisposable.add(subscribe)
        } else {
            Toast.makeText(this, "Unauthorized", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteFromFavourites(imageId: String) {
        val accessToken = AccessToken.getCurrentAccessToken()
        if (accessToken != null && !accessToken.isExpired) {

            val deleteImage =
                CatApiClient.API_INTERFACE_CLIENT.deleteImageFromFavourites(imageId)

            val subscribe = deleteImage
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Toast.makeText(
                            this, "Deleted from favorites", Toast.LENGTH_SHORT
                        ).show()
                    },
                    { error ->
                        Log.d("TAG", error.toString())
                        Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show()

                    }
                )
            compositeDisposable.add(subscribe)
        } else {
            Toast.makeText(this, "Unauthorized", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}