package com.example.ecg4u_koltin

import UserAdapter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecg4u_koltin.model.ResultModelClass
import kotlinx.android.synthetic.main.activity_display_results.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import android.content.Context
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class DisplayResultsActivity : AppCompatActivity() {
    private var imageData: ByteArray? = null
    private val postURL = "https://ecg4u-api.herokuapp.com/predict"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_results)
        val location = intent.getStringExtra("ecg4u_location").toString()
        val image = File(location)

        if (!image.exists()) throw RuntimeException("Image $location not found")
        if (image.isDirectory) throw RuntimeException("Image $location is a directory")

        val bImage: Bitmap = BitmapFactory.decodeFile(location)
        val imageView: ImageView = findViewById(R.id.testImage)
        imageView.setImageBitmap(bImage)
        var context: Context = this
        uploadFile(location,this)

        //TODO: Wait for JSON response Here


        //Todo: Parse JSON here
    }


    fun uploadFile(filename: String?,context: Context) {
        val image = File(filename.toString())

        val filePart : RequestBody = RequestBody.create(MediaType.parse("image/*"),image);
        val file : MultipartBody.Part = MultipartBody.Part.createFormData("image", image.name, filePart)

        val builder : Retrofit.Builder = Retrofit.Builder().baseUrl("https://ecg4u-api.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
        var retrofit : Retrofit = builder.build()
        val client : UserClient = retrofit.create(UserClient::class.java)
        var call : Call<ResponseBody>?= client.uploadFile(file)

        call?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                val json = JSONObject(response.body()!!.string())
                println(json)
                val resultList: ArrayList<ResultModelClass> = ArrayList()

                try {
                    // As we have JSON object, so we are getting the object
                    //Here we are calling a Method which is returning the JSON object
                    val obj = json
                    // fetch JSONArray named users by using getJSONArray

                    val resultArray = obj.getJSONArray("results")
                    // Get the users data using for loop i.e. id, name, email and so on

                    for (i in 0 until resultArray.length()) {
                        // Create a JSONObject for fetching single User's Data
                        val result_single = resultArray.getJSONObject(i)
                        // Fetch id store it in variable
                        val classification = result_single.getString("classification")
                        val result = result_single.getString("result")
                        // Now add all the variables to the data model class and the data model class to the array list.
                        val resultDetails =
                            ResultModelClass(classification, result)

                        // add the details in the list
                        resultList.add(resultDetails)
                    }
                } catch (e: JSONException) {
                    //exception
                    e.printStackTrace()
                }
                // Set the LayoutManager that this RecyclerView will use.
                resultsView.layoutManager = LinearLayoutManager(context)
                // Adapter class is initialized and list is passed in the param.
                val itemAdapter = UserAdapter(context, resultList)
                // adapter instance is set to the recyclerview to inflate the items.
                resultsView.adapter = itemAdapter
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println("Did NOT Something!")
            }

        })
    }



    }
