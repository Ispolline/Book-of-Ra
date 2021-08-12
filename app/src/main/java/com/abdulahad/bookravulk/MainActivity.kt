package com.abdulahad.bookravulk

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.abdulahad.bookravulk.common.PreferenceHelper.customPreference
import com.abdulahad.bookravulk.common.PreferenceHelper.finish
import com.abdulahad.bookravulk.common.PreferenceHelper.hint
import com.abdulahad.bookravulk.common.PreferenceHelper.isFirstPushSlot
import com.abdulahad.bookravulk.common.PreferenceHelper.is_user
import com.abdulahad.bookravulk.common.PreferenceHelper.music
import com.abdulahad.bookravulk.common.PreferenceHelper.slot
import com.abdulahad.bookravulk.imageview_scrolling.IEventEnd
import com.abdulahad.bookravulk.imageview_scrolling.ImageViewScrolling
import com.abdulahad.bookravulk.models.AgentModel
import com.abdulahad.bookravulk.models.PointsModel
import com.abdulahad.bookravulk.registration.RegistrationActivity
import com.abdulahad.bookravulk.registration.hide
import com.abdulahad.bookravulk.registration.visible
import com.abdulahad.bookravulk.retrofit.ApiClient
import com.abdulahad.bookravulk.vitrina.VitrinaActivity
import com.abdulahad.bookravulk.vitrina.VitrinaAdapter
import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AFInAppEventType
import com.appsflyer.AppsFlyerInAppPurchaseValidatorListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import uk.co.samuelwall.materialtaptargetprompt.extras.PromptBackground
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.FullscreenPromptBackground
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal
import java.util.*

enum class Hint {
    BALANCE, POINTS, START, FINISH
}

class MainActivity : AppCompatActivity(), IEventEnd {

    lateinit var pushBtn: Button
    lateinit var balanceBtn: TextView
    lateinit var pointsBtn: Button

        lateinit var image: ImageViewScrolling
    lateinit var image2: ImageViewScrolling
    lateinit var image3: ImageViewScrolling

    lateinit var musicSwitch: SwitchCompat
    lateinit var soundSwitch: SwitchCompat
    lateinit var closeBtn: Button
    lateinit var settings: ConstraintLayout

    lateinit var settingsBtn: ImageView

    var count_done = 0
    var balance = 1000
    lateinit var hintBtn: ImageView

    lateinit var prefs: SharedPreferences

    var timerPush: CountDownTimer? = null

    var pointsArray = arrayListOf<PointsModel>(
        PointsModel(5, 5),
        PointsModel(5, 5),
        PointsModel(25, 6),
        PointsModel(50, 15),
        PointsModel(100, 40)
    )

    lateinit var point: PointsModel

    var currentPointIndex = 0

    var mediaPlayer : MediaPlayer? = null
    var slotPlayer : MediaPlayer? = null

    var isTouched: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ApplicationInit.initializer

        prefs = customPreference(this)

        if(prefs.finish){
            val intent = Intent(this, VitrinaActivity::class.java)
            startActivity(intent)
            finish()
        }

        prefs.isFirstPushSlot = true

        pushBtn = findViewById(R.id.touchBtn)
        pointsBtn = findViewById(R.id.pointsBtn)
        balanceBtn = findViewById(R.id.balance_btn)
        image = findViewById(R.id.image)
        image2 = findViewById(R.id.image2)
        image3 = findViewById(R.id.image3)

        musicSwitch = findViewById(R.id.musicSwitch)
        soundSwitch = findViewById(R.id.soundSwitch)
        closeBtn = findViewById(R.id.closeSettBtn)
        settings = findViewById(R.id.settings)
        hintBtn = findViewById(R.id.question_btn)

        settingsBtn = findViewById(R.id.settings_btn)

        val display = windowManager.defaultDisplay
        var width = ((display.width - 180) /3)
        var  parms =  LinearLayout.LayoutParams(width, 240)
        image.layoutParams = parms
        image2.layoutParams = parms
        image3.layoutParams = parms

        writeBalance()
        changePoints()

        image.setedEventEnd(this)
        image2.setedEventEnd(this)
        image3.setedEventEnd(this)

        pushBtn.setOnClickListener {
            if(prefs.hint != Hint.FINISH.toString()){
                return@setOnClickListener
            }

            if (timerPush != null){
                timerPush?.cancel()
                timerPush = null
            }

            isTouched = true

            if(prefs.slot){
                startSlot()
            }
            pushBtn.isEnabled = false
            pushBtn.background.alpha = 128
            balance -= point.points
            writeBalance()

            fun  getRndImg(): Int{
                return arrayListOf<Int>(0, 1, 2).random()
            }


            val imgRand = getRndImg()

            val isFirstPush = prefs.isFirstPushSlot

            image.setValueRandom(Random().nextInt(3), Random().nextInt((15 - 5) + 1) + 5, if (isFirstPush) getRndImg() else imgRand)
            image2.setValueRandom(Random().nextInt(3), Random().nextInt((15 - 5) + 1) + 5, if (isFirstPush) getRndImg() else imgRand)
            image3.setValueRandom(Random().nextInt(3), Random().nextInt((15 - 5) + 1) + 5, if (isFirstPush) getRndImg() else imgRand)
            prefs.isFirstPushSlot = false
        }

        pointsBtn.setOnClickListener {
            changePoints()
        }

        getAgent()

        musicSwitch.isChecked = prefs.music
        soundSwitch.isChecked = prefs.slot

        musicSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            prefs.music = isChecked
            if (isChecked){
                startBackgroundSound()
            }else{
                stopBackgroundSound()
            }
        }

        soundSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            prefs.slot = isChecked
        }

        if (prefs.music) {
            startBackgroundSound()
        }

        settings.setOnClickListener {
            settings.hide()
        }

        closeBtn.setOnClickListener {
            settings.hide()
        }

        settingsBtn.setOnClickListener {
            settings.visible()
        }
        hintBtn.setOnClickListener {
             prefs.hint = Hint.BALANCE.toString()
            startHint()
        }
        startHint()
        setupTimer()
    }

    fun openDialog(){
        stopSlot()
        val mDialogView = Dialog(this, R.style.CustomAlertDialog)
        mDialogView.setContentView(R.layout.win_dialog_view)
        mDialogView.show()
        mDialogView.findViewById<ConstraintLayout>(R.id.idConst).setOnClickListener {
            mDialogView.dismiss()
        }

        val timer = object : CountDownTimer(1000, 100) {
            override fun onTick(millisUntilFinished: Long) {

            }
            override fun onFinish() {
                mDialogView.dismiss()
                if (prefs.is_user == 1){
                    goToReg()
                }
            }
        }.start()
    }

    fun setupTimer(){
        timerPush = object : CountDownTimer(6000, 100) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                if(!isTouched){
                pushBtn.callOnClick()}
            }
        }

    }

    override fun eventEnd(result: Int, count: Int) {

        if(count_done < 2){
            count_done ++
        }else{
            count_done = 0
            pushBtn.isEnabled = true
            pushBtn.background.alpha = 255
            if (image.getValue() == image2.getValue() && image2.getValue() == image3.getValue()){
                val timer = object : CountDownTimer(1000, 100) {
                    override fun onTick(millisUntilFinished: Long) {

                    }
                    override fun onFinish() {
                        try {
                            openDialog()
                        }catch (e: Exception){

                        }
                    }
                }.start()

                balance += point.points * point.increase
                writeBalance()
            }

        }
    }

    @SuppressLint("SetTextI18n")
    fun writeBalance(){
        balanceBtn.text = getString(R.string.balance) + " " + balance.toString()
    }

    @SuppressLint("SetTextI18n")
    fun changePoints(){
        if (currentPointIndex == 5){
            currentPointIndex = 0
        }

        point = pointsArray[currentPointIndex]
        pointsBtn.text = getString(R.string.point) + " " + point.points.toString()


        currentPointIndex += 1
    }

    fun goToReg(){
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun getAgent(){

        val call: Call<AgentModel> = ApiClient.getClient.getAgent()
        call.enqueue(object : Callback<AgentModel> {

            override fun onResponse(call: Call<AgentModel>?, response: Response<AgentModel>?) {
                val user = response?.body()
                prefs.is_user = (user?.content ?: 0)

                Log.d("is_user", (user?.content ?: 0).toInt().toString())
                Log.d("body2222", (user?.content ?: 0).toInt().toString())
            }

            override fun onFailure(call: Call<AgentModel>?, t: Throwable?) {
                Log.d("tag", t.toString())
                Log.d("body2222", t.toString())
            }

        })
    }

    fun startSlot(){
        if (slotPlayer != null){
            if (slotPlayer!!.isPlaying){
                return
            }
        }
        slotPlayer = MediaPlayer.create(this, R.raw.slot)
        slotPlayer?.isLooping = true
        slotPlayer?.start()
    }

    fun stopSlot(){
        if (slotPlayer != null){
            if (slotPlayer!!.isPlaying){
                slotPlayer!!.stop()
            }
        }
    }


    fun startBackgroundSound(){
        if (mediaPlayer != null){
            if (mediaPlayer!!.isPlaying){
                return
            }
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.melodi)
        mediaPlayer?.isLooping = true
//        val volume = Utils().setVolumeToMediaPlayer(50f)
//        mediaPlayer?.setVolume(volume, volume)
        mediaPlayer?.start()
    }

    fun stopBackgroundSound(){
        if (mediaPlayer != null){
            if (mediaPlayer!!.isPlaying){
                mediaPlayer!!.stop()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (prefs.music) {
            startBackgroundSound()
        }else{
            stopBackgroundSound()
        }
    }

    override fun onPause() {
        super.onPause()
        stopBackgroundSound()
    }

    fun startHint(){
        if (Hint.valueOf(prefs.hint.toString()) == Hint.FINISH){
            return
        }
        showHint(getString(R.string.hint_balance), R.id.balance_btn)
    }

    fun showHint(title: String, target: Int){

        MaterialTapTargetPrompt.Builder(this@MainActivity)
            .setTarget(target)
            .setPromptBackground(FullscreenPromptBackground())
            .setPromptFocal(RectanglePromptFocal())
            .setPrimaryText(title)
            .setBackgroundColour(Color.parseColor("#D3000000"))
            .setFocalColour(Color.TRANSPARENT)
            .setPromptStateChangeListener(MaterialTapTargetPrompt.PromptStateChangeListener { prompt, state ->
                if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED) {
                    Log.d("hintTAG", Hint.valueOf(prefs.hint.toString()).toString())
                    when(Hint.valueOf(prefs.hint.toString())){
                        Hint.BALANCE -> {
                            prefs.hint = Hint.POINTS.toString()
                            showHint(getString(R.string.hint_points), R.id.pointsBtn)
                        }
                        Hint.POINTS -> {
                            prefs.hint = Hint.START.toString()
                            showHint(getString(R.string.hint_start), R.id.touchBtn)
                        }
                        Hint.START -> {
                            prefs.hint = Hint.FINISH.toString()
                            timerPush?.start()
                        }
                    }
                }
            })
            .show()
    }

}
