package com.abdulahad.bookravulk.web

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.abdulahad.bookravulk.R
import com.abdulahad.bookravulk.common.PreferenceHelper
import com.abdulahad.bookravulk.common.PreferenceHelper.urlLast
import com.abdulahad.bookravulk.registration.hide
import com.abdulahad.bookravulk.registration.visible


class WebActivity: AppCompatActivity() {

    lateinit var webView: WebView

    lateinit  var loader: ConstraintLayout
    lateinit var prefs: SharedPreferences

    lateinit  var internetConn: ConstraintLayout
    lateinit  var okInetId: Button


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_view)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        prefs = PreferenceHelper.customPreference(this)
        loader = findViewById(R.id.loader)
        internetConn = findViewById(R.id.internet_conn)
        okInetId = findViewById(R.id.okInetId)
        webView = findViewById(R.id.webview)

setupWeb()


if (isNetworkAvailbale()){
    internetConn.hide()
}else{
    internetConn.visible()
}

        okInetId.setOnClickListener {
            internetConn.hide()
            if (isNetworkAvailbale()){
                    webView.loadUrl(prefs.urlLast.toString())
                internetConn.hide()
            }else{
                internetConn.visible()
            }
        }

    }

    fun setupWeb(){
        // Get the web view settings instance
        val settings = webView.settings
settings.domStorageEnabled = true
        // Enable java script in web view
        settings.javaScriptEnabled = true
        // Enable and setup web view cache
        settings.setAppCacheEnabled(true)
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.setAppCachePath(cacheDir.path)


        // Enable zooming in web view
        settings.setSupportZoom(false)
        settings.builtInZoomControls = false
        settings.displayZoomControls = false

        // Zoom web view text
        settings.textZoom = 100



//         More web view settings
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            settings.safeBrowsingEnabled = true  // api 26
//        }
        settings.pluginState = WebSettings.PluginState.OFF
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.mediaPlaybackRequiresUserGesture = false


        // More optional settings, you can enable it by yourself
        settings.domStorageEnabled = true
        settings.setSupportMultipleWindows(true)
        settings.loadWithOverviewMode = true
        settings.allowContentAccess = true
        settings.setGeolocationEnabled(true)
        settings.allowUniversalAccessFromFileURLs = true
        settings.allowFileAccess = true

//         WebView settings
        webView.fitsSystemWindows = true


        /*
            if SDK version is greater of 19 then activate hardware acceleration
            otherwise activate software acceleration
        */
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        webView.loadUrl(prefs.urlLast.toString())

        // Set web view client
        webView.webViewClient = object : WebViewClient() {
//
//            override fun onReceivedError(
//                view: WebView?,
//                request: WebResourceRequest?,
//                error: WebResourceError?
//            ) {
//                super.onReceivedError(view, request, error)
//
//                webView
//                        .loadData(
//                            "",
//                            "text/html", "UTF-8"
//                        );
//            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler,
                error: SslError?
            ) {
                handler.proceed() // Ignore SSL certificate errors
            }


            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url.toString());
                Log.d("teleg123", url.toString())
                if (url!!.startsWith("tg:resolve")) {
                    view?.stopLoading()
                    try {
                        val intent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()))
                        startActivity(intent)

//                        val whatsappIntent = Intent(Intent.ACTION_SEND)
//                        whatsappIntent.type = "text/plain"
//                        whatsappIntent.setPackage("org.telegram.messenger")
//                        whatsappIntent.putExtra(
//                            Intent.EXTRA_TEXT,
//                            view?.getUrl().toString() + "  - Shared from webview "
//                        )
//                        startActivity(whatsappIntent)

                    } catch (ex: ActivityNotFoundException) {
                        val MakeShortText = "Telegram have not been installed"
                        Toast.makeText(view?.context, MakeShortText, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
//                if (url != null) {
//                    if (url.startsWith("http") || url.startsWith("https")) {
//                        return true;
//                    }else {
//                        view?.stopLoading();
//                        view?.goBack();
//                        Toast.makeText(
//                            view?.context,
//                            "Unknown Link, unable to handle",
//                            Toast.LENGTH_SHORT
//                        ).show();
//                    }
//                }
                return false;

            }
        }
    }


    // Method to show app exit dialog
    private fun showAppExitDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Нет подключения к интернету")
        builder.setMessage("No back history found, want to exit the app?")
        builder.setCancelable(true)

        builder.setPositiveButton("Yes") { _, _ ->

             onBackPressed()
        }

        builder.setNegativeButton("No") { _, _ ->
            // Do something when want to stay in the app
            toast("thank you.")
        }

        // Create the alert dialog using alert dialog builder
        val dialog = builder.create()

        // Finally, display the dialog when user press back button
        dialog.show()
    }


    override fun onBackPressed() {
            if (webView.canGoBack()) {
                webView.goBack()
            }else{
                super.onBackPressed()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.isNetworkAvailbale():Boolean{
    val conManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val internetInfo =conManager.activeNetworkInfo
    return internetInfo!=null && internetInfo.isConnected
}