package com.example.seniorpj100per.Signin_SignUp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.seniorpj100per.R
import com.google.firebase.database.*

/**
 * Created by Smew on 28/1/2561.
 */

class SignupActivity : AppCompatActivity() {

    private val gender_array = arrayOf("ผู้ชาย", "ผู้หญิง")

    private lateinit var spinner_gender: Spinner
    private lateinit var img_submit: ImageView
    private lateinit var img_back: ImageView
    private lateinit var edt_email: EditText
    private lateinit var edt_username_regis: EditText
    private lateinit var edt_password_regis: EditText
    private lateinit var edt_age_regis: EditText

    private var email: String = ""
    private var username: String = ""
    private var password: String = ""
    private var age: String = ""
    private var gender: String = ""

    private var username_list: MutableList<String> = mutableListOf()

    lateinit var dataReference: DatabaseReference
    lateinit var msgList: MutableList<AddSignUpToFirebase>

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val v = currentFocus

        if (v != null &&
                (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) &&
                v is EditText &&
                !v.javaClass.name.startsWith("android.webkit.")) {
            val scrcoords = IntArray(2)
            v.getLocationOnScreen(scrcoords)
            val x = ev.rawX + v.left - scrcoords[0]
            val y = ev.rawY + v.top - scrcoords[1]

            if (x < v.left || x > v.right || y < v.top || y > v.bottom)
                hideKeyboard(this)
        }
        return super.dispatchTouchEvent(ev)
    }

    fun hideKeyboard(activity: Activity?) {
        if (activity != null && activity.window != null && activity.window.decorView != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        spinner_gender = findViewById<Spinner>(R.id.spinner_gender)
        val spinner = spinner_gender
        val adapter = ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, gender_array)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)

        img_submit = findViewById<ImageView>(R.id.img_submit_regis)
        img_back = findViewById<ImageView>(R.id.img_back_regis)
        edt_email = findViewById<EditText>(R.id.edt_email)
        edt_username_regis = findViewById<EditText>(R.id.edt_username)
        edt_password_regis = findViewById<EditText>(R.id.edt_password)
        edt_age_regis = findViewById<EditText>(R.id.edt_age)

        img_back.setOnClickListener {
            finish()
        }

        img_submit.setOnClickListener {
            email = edt_email.text.toString()
            username = edt_username_regis.text.toString().toLowerCase()
            password = edt_password_regis.text.toString()
            age = edt_age_regis.text.toString()
            gender = spinner_gender.selectedItem.toString()

            if (password == "") {
                Toast.makeText(baseContext, "กรุณาใส่รหัสผ่าน", Toast.LENGTH_SHORT).show()
            }
            if (username == "") {
                Toast.makeText(baseContext, "กรุณาใส่ชื่อผู้ใช้", Toast.LENGTH_SHORT).show()
            }
            if (age == "") {
                Toast.makeText(baseContext, "กรุณาใส่อายุ", Toast.LENGTH_SHORT).show()
            }
            if (email == "") {
                Toast.makeText(baseContext, "กรุณาใส่อีเมล", Toast.LENGTH_SHORT).show()
            }
            if (username in username_list) {
                Toast.makeText(baseContext, "กรุณาเปลี่ยนชื่อผู้ใช้", Toast.LENGTH_SHORT).show()
            }
            if (username.isNotEmpty() && password.isNotEmpty()) {
                if (username in username_list) {
                    Toast.makeText(baseContext, "กรุณาเปลี่ยนชื่อผู้ใช้", Toast.LENGTH_SHORT).show()
                } else {
                    saveData()
                    finish()
                    val intent = Intent(this, SigninActivity::class.java)
                    startActivity(intent)
                }
            }

        }

        dataReference = FirebaseDatabase.getInstance().getReference("addregis")
        msgList = mutableListOf()
        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.exists()) {
                    msgList.clear()
                    for (i in p0.children) {
                        val message = i.getValue(AddSignUpToFirebase::class.java)
                        msgList.add(message!!)
                        username_list.add(message.username)
                    }
                }
            }
        })
    }

    private fun saveData() {
        val messageId = dataReference.push().key
        val journalEntry1 = AddSignUpToFirebase(username, password, email, age, gender)
        dataReference.child(messageId).setValue(journalEntry1).addOnCompleteListener {
            Toast.makeText(applicationContext, "สมัครสมาชิกเรียบร้อย", Toast.LENGTH_SHORT).show()
        }
    }

}
