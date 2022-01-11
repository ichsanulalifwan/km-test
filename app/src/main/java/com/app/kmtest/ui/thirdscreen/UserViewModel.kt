package com.app.kmtest.ui.thirdscreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.kmtest.data.ApiResponse
import com.app.kmtest.data.network.ApiConfig
import com.app.kmtest.model.Data
import com.app.kmtest.model.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : ViewModel() {

    var totalPage = MutableLiveData<Int>()

    fun getListUser(pageNumber: Int, pageSize: Int): LiveData<ApiResponse<List<Data>>> {
        val users = MutableLiveData<ApiResponse<List<Data>>>()
        ApiConfig.invoke().getUsers(pageNumber, pageSize).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    users.value =
                        ApiResponse.Success(response.body()?.userData as List<Data>)
                    totalPage.postValue(response.body()?.totalPages)
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    users.value = ApiResponse.Error(response.message())
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                users.value = ApiResponse.Error(t.message.toString())
            }
        })

        return users
    }

    companion object {
        private const val TAG = "UserViewModel"
    }
}