package com.abdulahad.bookravulk.registration

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.abdulahad.bookravulk.R
import com.abdulahad.bookravulk.common.PreferenceHelper
import com.abdulahad.bookravulk.common.PreferenceHelper.codePass
import com.abdulahad.bookravulk.common.PreferenceHelper.finish
import com.abdulahad.bookravulk.common.PreferenceHelper.phone
import com.abdulahad.bookravulk.common.PreferenceHelper.sms_token
import com.abdulahad.bookravulk.common.showAlert
import com.abdulahad.bookravulk.models.PhoneModel
import com.abdulahad.bookravulk.models.SmsModel
import com.abdulahad.bookravulk.retrofit.ApiClient
import com.abdulahad.bookravulk.retrofit.ApiClientSmsGorod
import com.abdulahad.bookravulk.vitrina.VitrinaActivity
import com.abdulahad.bookravulk.web.WebActivity
import com.github.pinball83.maskededittext.MaskedEditText
import com.google.android.material.textfield.TextInputEditText
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class RegistrationActivity: AppCompatActivity(), AdapterView.OnItemSelectedListener  {

var phones: ArrayList<PhoneModel> = arrayListOf(PhoneModel("+7", "(***) ***-**-**", "RU", R.drawable.ru_flag),
    PhoneModel("+380", "** *** ****", "UA", R.drawable.ua_flag),
    PhoneModel("+7", "(***) ***-**-**", "KZ", R.drawable.kz_flag))

    var selectedPhone:  PhoneModel? = null

    lateinit  var loader: ConstraintLayout

    lateinit  var authCard: CardView
    lateinit var titleText: TextView
    lateinit  var maskedField: MaskedEditText
    lateinit  var maskedField2: MaskedEditText
    lateinit  var nameField: TextInputEditText
    lateinit  var sendSmsButton: Button

    lateinit  var smsCard: CardView
    lateinit var wrongNumberTxt: TextView
    lateinit  var codeField: EditText
    lateinit var repeatSms: TextView
    lateinit  var nextButton: Button

    var numberSuccessful: Boolean = false

    lateinit var prefs: SharedPreferences

    var timer_interval_to_repeat_send: Long = 60000
    lateinit var timerToRepeat: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_view)
//        val locale = Utils().getUserCountry(this)

        prefs = PreferenceHelper.customPreference(this)

        loader = findViewById(R.id.loader)

        authCard = findViewById(R.id.authCard)
        titleText = findViewById(R.id.titleText)
        nameField = findViewById(R.id.nameField)
        maskedField = findViewById(R.id.masked_edit_text)
        maskedField2 = findViewById(R.id.masked_edit_text2)
        sendSmsButton = findViewById(R.id.sign_in)

        smsCard = findViewById(R.id.smsCard)
        wrongNumberTxt = findViewById(R.id.wrongNumber)
        codeField = findViewById(R.id.code_field)
        repeatSms = findViewById(R.id.repeatSms)
        nextButton = findViewById(R.id.nextId)

        val adapter =  CustomDropDownAdapter(this, phones)
        val dropDown = findViewById<Spinner>(R.id.spinner)
        dropDown.adapter = adapter
        dropDown!!.onItemSelectedListener = this

        maskedField.addTextChangedListener {
            textChanged(maskedField)
        }

        maskedField2.addTextChangedListener {
            textChanged(maskedField2)
        }

        nameField.addTextChangedListener{
           if (nameField.text.toString().isNotEmpty() && numberSuccessful){
               disabledButton(false, sendSmsButton)
           }else{
               disabledButton(true, sendSmsButton)
           }
        }

        codeField.addTextChangedListener {
            if(codeField.text.count() < 5){
                disabledButton(true, nextButton)
            }else{
                disabledButton(false, nextButton)
            }
        }

        sendSmsButton.setOnClickListener {
            titleText.text = getString(R.string.send_code).plus(" +").plus(prefs.phone)
            sendSmsToken()
            setupTimer()
        }

        wrongNumberTxt.setOnClickListener {
            smsCard.hide()
            authCard.visible()
        }

        repeatSms.setOnClickListener {
            sendSmsToken()
            setupTimer()
        }

        nextButton.setOnClickListener {
            if(prefs.codePass.toString() == codeField.text.toString()) {
                prefs.finish = true
                val intent = Intent(this, VitrinaActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        val localeCountryCode = this.resources.configuration.locale.country

//        Toast.makeText(this, localeCountryCode, Toast.LENGTH_LONG).show()
        getSmsToken()
        disabledButton(true, nextButton)
    }

    fun disabledButton(disabled: Boolean, button: Button){
        if(disabled){
            button.background.alpha = 128
            button.isEnabled = false
        }else{
            button.background.alpha = 255
            button.isEnabled = true
        }
    }

    fun textChanged(mask: MaskedEditText){

        if (mask.text.toString().replace(" ", "").count() == selectedPhone?.mask?.replace(" ", "")?.count()){
            numberSuccessful = true
            prefs.phone = (selectedPhone?.dialCode + mask.unmaskedText).replace("+", "")
            if(nameField.text.toString().isEmpty()){
                return
            }
            disabledButton(false, sendSmsButton)
        }else{
//            sendSmsButton.alpha = 0.5f
            disabledButton(true, sendSmsButton)
        }
    }

    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        selectedPhone = phones[position]

        when(phones[position].code){
            "UA" -> {
                maskedField2.visibility = View.VISIBLE
                maskedField.visibility = View.GONE
                maskedField2.setMaskedText(maskedField.unmaskedText)
            } else -> {
            maskedField2.visibility = View.GONE
            maskedField.visibility = View.VISIBLE
            maskedField.setMaskedText(maskedField2.unmaskedText)
            }
        }
//Toast.makeText(this, "changed", Toast.LENGTH_LONG).show()
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }


    fun getSmsToken(){

        val call: Call<SmsModel> = ApiClient.getClient.getSmsToken()
        call.enqueue(object : Callback<SmsModel> {

            override fun onResponse(call: Call<SmsModel>?, response: Response<SmsModel>?) {
                val sms = response?.body()
                prefs.sms_token = sms?.key
                Log.d("body2222", sms?.key.toString())
            }

            override fun onFailure(call: Call<SmsModel>?, t: Throwable?) {
                Log.d("tag", t.toString())
                Log.d("body2222", t.toString())
            }

        })
    }

    fun sendSmsToken(){
        hideKeyboard()
        loader.visible()
        val passCode = (1000..99999).random()
prefs.codePass = passCode
        var fullTextSms = "Ваш код подтверждения: $passCode"

        Log.d("body2222", prefs.sms_token + " " + prefs.phone + " " + fullTextSms)


        val call: Call<ResponseBody>? = ApiClientSmsGorod.getClient.sendSms( prefs.sms_token ,prefs.phone, fullTextSms)
        call?.enqueue(object : Callback<ResponseBody>{

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                loader.hide()
                authCard.hide()
                smsCard.visible()
                Log.d("body2222", "success")
//                prefs.sms_token = sms?.key
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable?) {
                loader.hide()
                showAlert("Ошибка при отправке кода подтверждения", "Попробуйте заного" )
            }

        })
    }

    fun setupTimer(){
        repeatSms.setTextColor(ContextCompat.getColor(repeatSms.context, R.color.textColor))
        timerToRepeat = object : CountDownTimer(timer_interval_to_repeat_send, 100) {
            override fun onTick(millisUntilFinished: Long) {
repeatSms.text = getString(R.string.repeat_send_sms).plus(" ").plus(millisUntilFinished/1000)
            }
            override fun onFinish() {
                repeatSms.text = getString(R.string.send_sms)
                repeatSms.setTextColor(ContextCompat.getColor(repeatSms.context, R.color.btnText))
            }
        }
        timerToRepeat.start()
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.please_click_back_to_exit), Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }


}
fun Activity.hideKeyboard() {
    val view: View = currentFocus ?: View(this)
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}