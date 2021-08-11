package com.abdulahad.bookravulk.vitrina

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abdulahad.bookravulk.R
import com.abdulahad.bookravulk.common.PreferenceHelper
import com.abdulahad.bookravulk.common.PreferenceHelper.customPreference
import com.abdulahad.bookravulk.common.PreferenceHelper.finish
import com.abdulahad.bookravulk.common.PreferenceHelper.is_user
import com.abdulahad.bookravulk.common.PreferenceHelper.music
import com.abdulahad.bookravulk.common.PreferenceHelper.slot
import com.abdulahad.bookravulk.common.PreferenceHelper.sms_token
import com.abdulahad.bookravulk.common.PreferenceHelper.urlLast
import com.abdulahad.bookravulk.imageview_scrolling.IEventEnd
import com.abdulahad.bookravulk.imageview_scrolling.ImageViewScrolling
import com.abdulahad.bookravulk.models.*
import com.abdulahad.bookravulk.registration.RegistrationActivity
import com.abdulahad.bookravulk.registration.hide
import com.abdulahad.bookravulk.registration.visible
import com.abdulahad.bookravulk.retrofit.ApiClient
import com.abdulahad.bookravulk.retrofit.ApiClientSmsGorod
import com.abdulahad.bookravulk.web.WebActivity
import com.abdulahad.bookravulk.web.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Array.newInstance
import java.util.*
import kotlin.collections.ArrayList


class VitrinaActivity : AppCompatActivity() {

var products: ArrayList<ProductModel> = arrayListOf()
    private lateinit var adapter: VitrinaAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    lateinit var vitrinaRecycler: RecyclerView

    lateinit var prefs: SharedPreferences

    lateinit  var loader: ConstraintLayout

    val vitrinaClickListener = fun(position: Int) {
        prefs.urlLast = products[position].url
        val intent = Intent(this, WebActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vitrina_activity)
        prefs = PreferenceHelper.customPreference(this)
        loader = findViewById(R.id.loader)
        getVetrina()


    }

    fun itemInit(){
        vitrinaRecycler = findViewById(R.id.vitrina_recycl)
        linearLayoutManager = LinearLayoutManager(this)
        vitrinaRecycler.layoutManager = linearLayoutManager
        adapter = VitrinaAdapter(items = products, listner = vitrinaClickListener)
        vitrinaRecycler.adapter = adapter
    }

    fun getVetrina(){
loader.visible()
        val call: Call<ProductsModel> = ApiClient.getHttpsClient.getProduct()
        call.enqueue(object : Callback<ProductsModel> {

            override fun onResponse(call: Call<ProductsModel>?, response: Response<ProductsModel>?) {
                val result = response?.body()
                Log.d("products", result?.list_items.toString())
                if (result != null) {
                    products = result.list_items!!
                }
                products.add(ProductModel("", "https://t.me/vip_consultant"))
                itemInit()
loader.hide()
            }

            override fun onFailure(call: Call<ProductsModel>?, t: Throwable?) {
                Log.d("tag", t.toString())
                Log.d("body2222", t.toString())
                loader.hide()
            }

        })
    }

}