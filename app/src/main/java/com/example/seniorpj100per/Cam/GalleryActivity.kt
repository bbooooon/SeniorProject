package com.example.seniorpj100per.Cam

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.seniorpj100per.FoodList.ResultActivity
import com.example.seniorpj100per.HamburgerActivity
import com.example.seniorpj100per.R
import com.example.seniorpj100per.UserObject
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_gallery.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class GalleryActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 1234
    private var filePath: Uri? = null
    private var path: String? = null
    private var result:String? = null
    private var filename:String? = null
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        val date = Date()
        val dateformat = SimpleDateFormat("yyyyMMdd_HHmmss")
        filename = "food_"+dateformat.format(date)

        val hmap = HashMap<String, Int>()
        hmap.put("[moodeang]",6)
        hmap.put("[radnahr]",3)
        hmap.put("[etc]",0)
        hmap.put("[kapi]",5)
        hmap.put("[kaijiew]",10)
        hmap.put("[kapaogai]",9)
        hmap.put("[kamoo]",4)
        hmap.put("[mungai]",7)
        hmap.put("[mokgai]",8)
        hmap.put("[padthai]",2)
        hmap.put("[paddeeiew]",1)

        buttonGalleryResult.setVisibility(View.GONE)
        showImage()

        buttonGalleryResult.setOnClickListener {
            val user = UserObject.getUser()
            val username = user.username
            uploadToStorage()
            val resultText = hmap.get(result!!)!!.toInt()
            val intent2 = Intent(this, ResultActivity::class.java)
            intent2.putExtra("position", resultText)
            intent2.putExtra("username", username)
            intent2.putExtra("foodlist", "gallery")
            intent2.putExtra("filename", filename.toString())
            startActivity(intent2)
        }
        buttonLoadPicture.setOnClickListener{
            showImage()
        }
    }

    private fun callRetrofit(){
        val snackbar = Snackbar.make(imgView_gallery, "Loading...", Snackbar.LENGTH_INDEFINITE)
        snackbar.show()

        val gson = GsonBuilder()
                .setLenient()
                .create()
        val client = OkHttpClient.Builder()
        client.addInterceptor{ chain ->
            val req = chain.request()
            val requestBuilder = req.newBuilder().method(req.method(),req.body())
            val request = requestBuilder.build()
            chain.proceed(request)
        }

        val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.43.22:8080/runpy/run.php/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

        val file = File(path)
        val reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("uploaded_file", file.getName(),reqFile)

        val service = retrofit.create(Service::class.java)
        val call = service.postImage(body)
        call.enqueue(object : Callback<Prediction> {
            override fun onResponse(call : Call<Prediction>, response: Response<Prediction>) {
                val pre  = response.body()!!
                result = pre.msg.toString()

                if(result!=null){
                    snackbar.dismiss()
                }
            }
            override fun onFailure(call : Call<Prediction>, t: Throwable) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), t.message, Toast.LENGTH_SHORT).show();
                finish()
                startActivity(Intent(this@GalleryActivity, HamburgerActivity::class.java))
            }
        })
    }

    private fun showImage(){
        intent = Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            try{
                buttonGalleryResult.setVisibility(View.VISIBLE)
                buttonLoadPicture.setVisibility(View.GONE)
                filePath = data.data
                val FILE = arrayOf(MediaStore.Images.Media.DATA)

                val cursor = getContentResolver().query(filePath!!,
                        FILE, null, null, null)

                cursor!!.moveToFirst()

                val columnIndex = cursor!!.getColumnIndex(FILE[0])
                path = cursor!!.getString(columnIndex)

                cursor!!.close()

                callRetrofit()

                bitmap = MediaStore.Images.Media.getBitmap(contentResolver,filePath)
                bitmap = Bitmap.createScaledBitmap(bitmap, (bitmap!!.width * 0.5).toInt(), (bitmap!!.height*0.5).toInt(), true)

                uploadToStorage()
                imgView_gallery.setImageBitmap(bitmap)

            } catch (e: Exception) {
                Toast.makeText(this, "Please try again", Toast.LENGTH_LONG)
                        .show()
            }
        }
    }

    private fun uploadToStorage(){
        if (filePath!=null){
            val storage = FirebaseStorage.getInstance()
            val storageReference = storage!!.reference
            val imageRef = storageReference!!.child("submit_food/"+filename.toString())//send to firebase folder
            val loadingDialog: ProgressDialog
            loadingDialog = ProgressDialog.show(this, "Uploading photo", "Loading...", true, false);

            val baos = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            val data = baos.toByteArray()
            imageRef.putBytes(data)
                    .addOnCompleteListener{
                        loadingDialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"Storage Upload Failed",Toast.LENGTH_SHORT).show()
                    }
        }
    }
}
