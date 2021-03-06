package com.walton.smarthrm.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.walton.smarthrm.databinding.ActivityMainBinding
import com.walton.smarthrm.model.UserLogin
import com.walton.smarthrm.model.UserLoginResponse
import com.walton.smarthrm.retrofit.ApiConfig
import com.walton.smarthrm.retrofit.ClientInstance
import com.walton.smarthrm.utils.SharedPreferenceInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceInfo: SharedPreferenceInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceInfo = SharedPreferenceInfo(this)


        binding.btnSignIn.setOnClickListener {
            if (binding.etEmailField.text.toString().isNotEmpty() && binding.etPasswordField.text.isNotEmpty()) {
                callUserLoginApi()
            }
        }
    }

    private fun callUserLoginApi() {
        val userLogin =
            UserLogin(binding.etEmailField.text.toString(), binding.etPasswordField.text.toString())
        val config = ClientInstance().getRetrofitInstance()!!.create(ApiConfig::class.java)
        val call: Call<UserLoginResponse> = config.userLogin(userLogin)
        call.enqueue(object : Callback<UserLoginResponse> {
            override fun onResponse(
                call: Call<UserLoginResponse?>,
                response: Response<UserLoginResponse>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                    preferenceInfo.storeToken("Bearer " + response.body()!!.accessToken, "token")
                    val intent = Intent(this@MainActivity, UserListActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                Log.d("error", t.message!!)
            }
        })
    }
}