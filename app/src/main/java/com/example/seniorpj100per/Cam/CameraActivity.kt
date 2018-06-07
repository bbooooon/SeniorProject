package com.example.seniorpj100per.Cam

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.example.seniorpj100per.FoodList.ResultActivity
import com.example.seniorpj100per.R
import com.example.seniorpj100per.UserObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_camera.*
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
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : AppCompatActivity() {

    val REQUEST_IMAGE_CAPTURE = 123
    lateinit var photoImageView: ImageView
    private var fpath:String? = null
    private var filePath: Uri? = null
    private var filename:String? = null
    private var result:String? = null
    internal var storage: FirebaseStorage? = null
    internal var storageReference: StorageReference? = null
    internal var username: String? = ""
    private var bitmap:Bitmap? = null
    private var checkbitmap:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        val date = Date()
        val dateformat = SimpleDateFormat("yyyyMMdd_HHmmss")
        filename = "food_"+dateformat.format(date)

        photoImageView = findViewById(R.id.imgView_camera)

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

        launchCamera()
        buttonCameraResult.setVisibility(View.GONE)
        buttonCameraResult.setOnClickListener {
            val user = UserObject.getUser()
            username = user.username
            val intent2 = Intent(this, ResultActivity::class.java)
            val resultText = hmap.get(result!!)!!.toInt()
            Log.e("result int", resultText.toString())
            intent2.putExtra("position", resultText)
            intent2.putExtra("username", username)
            intent2.putExtra("foodlist", "camera")
            intent2.putExtra("filename", filename.toString())
            startActivity(intent2)
        }
        btn_camera.setOnClickListener{
            launchCamera()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            buttonCameraResult.setVisibility(View.VISIBLE)
            btn_camera.setVisibility(View.GONE)
            bitmap = BitmapFactory.decodeFile(fpath)
            checkbitmap = true
            galleryAddPic()
            callRetrofit()
            uploadToStorage()
            photoImageView.setImageBitmap(bitmap)
        }
    }

    fun launchCamera() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (e: IOException) {
            }

            if (photoFile != null) {
                filePath = FileProvider.getUriForFile(this,
                        applicationContext.packageName+".provider",
                        photoFile)
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, filePath)

                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun callRetrofit(){
        val snackbar = Snackbar.make(photoImageView, "Loading...", Snackbar.LENGTH_INDEFINITE)
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

        val file = File(fpath)
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
            }
        })
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_DCIM)
        val image = File.createTempFile(filename,".jpg", storageDir)
        fpath = image.absolutePath
        return image
    }

    private fun galleryAddPic() {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(filePath.toString())
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        this.sendBroadcast(mediaScanIntent)
    }

    private fun uploadToStorage(){
        if (filePath!=null){
            val imageRef = storageReference!!.child("submit_food/"+filename.toString())

            val loadingDialog: ProgressDialog
            loadingDialog = ProgressDialog.show(this, "Uploading photo", "Loading...", true, false)
            imageRef.putFile(filePath!!)
                    .addOnCompleteListener{
                        loadingDialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"Storage Upload Failed",Toast.LENGTH_SHORT).show()
                    }
        }
    }
}
