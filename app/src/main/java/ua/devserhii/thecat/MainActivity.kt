package ua.devserhii.thecat

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.json.JSONObject
import ua.devserhii.thecat.adapter.CatAdapter
import ua.devserhii.thecat.adapter.CatUiModel
import ua.devserhii.thecat.adapter.NavigateAdapter
import ua.devserhii.thecat.model.Category


class MainActivity : AppCompatActivity() {

    private lateinit var category: List<Category>

    private val SUB_ID = "SubId"
    lateinit var sPref: SharedPreferences

    var typeRequest = 0
    var categoryById = 0

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    private var catAdapter: CatAdapter? = null
    private var navigateAdapter: NavigateAdapter? = null

    private val compositeDisposable = CompositeDisposable()

    private var callbackManager: CallbackManager? = null

    private var layoutManager: GridLayoutManager? = null

    var pages = 0
    var limits = 50

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationDrawer()
        getListOfCategories()
        facebookEntrance()

        rv_menu_items.layoutManager = LinearLayoutManager(this)
        rv_menu_items.setHasFixedSize(true)

        navigateAdapter = NavigateAdapter(this::requestByCategories)
        rv_menu_items.adapter = navigateAdapter

        layoutManager = GridLayoutManager(this, 3)
        rv_cat.layoutManager = layoutManager

        catAdapter = CatAdapter(this::openFullScreenImage)
        rv_cat.adapter = catAdapter

        mainRequest(pages, limits)
        setUpLoadMoreListener()

        tv_favorites.setOnClickListener {
            requestByFavourites(pages, limits)
        }
        loadText()

    }

    private fun setUpLoadMoreListener() {
        rv_cat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private val VISIBLE_THRESHOLD = 1
            private var lastVisibleItem = 0
            private var totalItemCount = 0

            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int, dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)
                val isEmpty = layoutManager?.itemCount == 0
                totalItemCount = layoutManager!!.itemCount
                lastVisibleItem = layoutManager!!.findLastVisibleItemPosition()
                if ((totalItemCount <= lastVisibleItem + VISIBLE_THRESHOLD) && !isEmpty) {
                    pages++
                    progress_bar.visibility = View.VISIBLE
                    when (typeRequest) {
                        1 -> mainRequest(pages, limits)
                        2 -> requestByCategories(categoryById)
                        3 -> requestByFavourites(pages, limits)
                        else -> Toast.makeText(
                            this@MainActivity, "be", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    private fun mainRequest(page: Int, limit: Int) {
        if (typeRequest != 1) {
            typeRequest = 1
            pages = 0
        }
        val getImages =
            CatApiClient.API_INTERFACE_CLIENT.getImages(limit, page)

        val subscribe = getImages
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { images ->
                    val catUiModel = images.map { CatUiModel(it.url, it.id) }
                    catAdapter?.addAll(catUiModel)
                    progress_bar.visibility = View.GONE
                },
                { error ->
                    Log.e("TAG", error.toString())
                }
            )
        compositeDisposable.add(subscribe)

    }

    private fun requestByCategories(categoryId: Int) {
        if (typeRequest != 2) {
            typeRequest = 2
            catAdapter?.update(emptyList())
            categoryById = categoryId
            pages = 0
        } else {
            if (categoryById != categoryId) {
                catAdapter?.update(emptyList())
                categoryById = categoryId
                pages = 0
            }
        }

        var index = 0
        var result = 0
        for (i in category) {
            if (i.id != categoryId)
                index += 1
            else
                result = index
        }

        val getImagesByCategories =
            CatApiClient.API_INTERFACE_CLIENT.getImagesByCategories(
                category[result].id, limits, pages
            )

        val subscribe = getImagesByCategories
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { images ->
                    val catUiModel = images.map { CatUiModel(it.url, it.id) }
                    catAdapter?.addAll(catUiModel)
                    progress_bar.visibility = View.GONE
                },
                { error ->
                    Log.e("TAG", error.toString())
                }
            )
        compositeDisposable.add(subscribe)
    }

    private fun getListOfCategories() {
        val getCategories =
            CatApiClient.API_INTERFACE_CLIENT.getCategories()

        val subscribeCategories = getCategories
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    navigateAdapter?.update(it)
                    category = it
                },
                { error ->
                    Log.e("TAG_LIST_CAT", error.toString())
                }
            )
        compositeDisposable.add(subscribeCategories)
    }

    private fun requestByFavourites(page: Int, limit: Int) {
        if (typeRequest != 3) {
            typeRequest = 3
            catAdapter?.update(emptyList())
            pages = 0
        }

        val accessToken = AccessToken.getCurrentAccessToken()
        if (accessToken != null && !accessToken.isExpired) {
            val getFavoritesImages =
                CatApiClient.API_INTERFACE_CLIENT.getFavouritesImages(
                    sPref.getString(SUB_ID, "")!!,
                    limit,
                    page
                )

            val subscribe = getFavoritesImages
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { images ->
                        val catUiModel =
                            images.map { CatUiModel(it.image.url, it.id.toString()) }
                        catAdapter?.addAll(catUiModel)
                        progress_bar.visibility = View.GONE
                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                    },
                    { error ->
                        Log.d("TAG", error.toString())
                    }
                )
            compositeDisposable.add(subscribe)
        } else {
            Toast.makeText(this, "Unauthorized", Toast.LENGTH_SHORT).show()
        }
    }

    private fun facebookEntrance() {
        callbackManager = CallbackManager.Factory.create()
        login_button.setPermissions("email", "public_profile")
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                Log.d("Facebook", "Connection success")
            }

            override fun onCancel() {
                Log.d("Facebook", "Cancel")
            }

            override fun onError(error: FacebookException?) {
                Log.d("Facebook", "Error")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        val graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
            object : GraphRequest.GraphJSONObjectCallback {
                override fun onCompleted(`object`: JSONObject?, response: GraphResponse?) {
                    Log.i("Facebook", `object`.toString())
                    try {
                        val firstName = `object`?.getString("first_name")
                        val lastName = `object`?.getString("last_name")
                        val id = `object`?.getString("id")
                        val email = `object`?.getString("email")
                        nav_header_profileName.text = "$firstName $lastName"
                        nav_header_profileEmail.text = email
                        Glide.with(this@MainActivity)
                            .load("https://graph.facebook.com/$id/picture?type=large")
                            .into(nav_header_profileImage)
                        sPref = getPreferences(MODE_PRIVATE)
                        val editor = sPref.edit()
                        editor.putString("name", "$firstName $lastName")
                        editor.putString("email", email)
                        editor.putString(
                            "photo",
                            "https://graph.facebook.com/$id/picture?type=large"
                        )
                        editor?.apply()
                    } catch (e: Exception) {
                    }
                }
            })
        val bundle = Bundle()
        bundle.putString("fields", "email, id, first_name, last_name")
        graphRequest.parameters = bundle
        graphRequest.executeAsync()
    }

    private val accessTokenTracker = object : AccessTokenTracker() {
        override fun onCurrentAccessTokenChanged(
            oldAccessToken: AccessToken?,
            currentAccessToken: AccessToken?
        ) {
            if (currentAccessToken == null) {
                LoginManager.getInstance().logOut()
                nav_header_profileName.text = ""
                nav_header_profileEmail.text = ""
                nav_header_profileImage.setImageResource(0)
            } else {
                sPref = getPreferences(MODE_PRIVATE)
                val editor = sPref.edit()
                editor.putString("subId", currentAccessToken.userId)
                editor.apply()
            }
        }
    }

    private fun navigationDrawer() {
        setSupportActionBar(toolbar_main)
        drawer = drawer_layout

        toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar_main,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        accessTokenTracker.stopTracking()
        compositeDisposable.dispose()
    }

    private fun openFullScreenImage(imagePath: String, imageId: String) {
        val intent = Intent(this, FullscreenImage::class.java)
        intent.putExtra("imagePath", imagePath)
        intent.putExtra("subId", sPref.getString("subId", ""))
        intent.putExtra("imageId", imageId)
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun loadText() {
        sPref = getPreferences(MODE_PRIVATE)
        nav_header_profileName.text = sPref.getString("name", "")
        nav_header_profileEmail.text = sPref.getString("email", "")
        Glide.with(this).load(sPref.getString("photo", "")).into(nav_header_profileImage)
    }
}