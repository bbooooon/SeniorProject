package com.example.seniorpj100per.Signin_SignUp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.seniorpj100per.HamburgerActivity
import com.example.seniorpj100per.R
import com.example.seniorpj100per.UserObject
import com.google.firebase.database.*

/**
 * Created by Smew on 28/1/2561.
 */

internal class SigninActivity : AppCompatActivity() {

    lateinit var dataReference: DatabaseReference
    lateinit var msgList: MutableList<AddSignUpToFirebase>

    private var email_list: MutableList<String> = mutableListOf()
    private var username_list: MutableList<String> = mutableListOf()
    private var password_list: MutableList<String> = mutableListOf()
    private var age_list: MutableList<String> = mutableListOf()
    private var gender_list: MutableList<String> = mutableListOf()

    private lateinit var edt_username_login: EditText
    private lateinit var edt_password_login: EditText
    private lateinit var img_submit_login: ImageView
    private lateinit var img_back_login: ImageView

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
        setContentView(R.layout.activity_login)

        dataReference = FirebaseDatabase.getInstance().getReference("addregis")
        msgList = mutableListOf()
        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.exists()) {
                    msgList.clear()
                    username_list.clear()
                    password_list.clear()
                    for (i in p0.children) {
                        val message = i.getValue(AddSignUpToFirebase::class.java)
                        msgList.add(message!!)
                        email_list.add(message.email)
                        username_list.add(message.username)
                        password_list.add(message.password)
                        age_list.add(message.age)
                        gender_list.add(message.gender)
                    }
                }
            }
        })

        img_submit_login = findViewById<ImageView>(R.id.img_submit_login)
        img_back_login = findViewById<ImageView>(R.id.img_back_login)
        edt_username_login = findViewById<EditText>(R.id.edt_username_login)
        edt_password_login = findViewById<EditText>(R.id.edt_password_login)

        img_back_login.setOnClickListener {
            finish()
        }

        img_submit_login.setOnClickListener {
            finish()

            val username_edt: String = edt_username_login.text.toString()
            val password_edt: String = edt_password_login.text.toString()
            for ((index, value) in username_list.withIndex()) {
                if (username_edt in username_list[index] && password_edt.equals(password_list[index])) {
                    val intent = Intent(baseContext, HamburgerActivity::class.java)

                    UserObject.getUser().username = username_list[index]

                    intent.putExtra("username",username_list[index])
                    startActivity(intent)
                }
                if (username_edt in username_list[index] && password_edt != password_list[index]) {
                    Toast.makeText(baseContext, "เข้าสู่ระบบไม่สำเร็จ", Toast.LENGTH_SHORT).show()
                }
            }
            if (username_edt !in username_list || password_edt !in password_list) {
                Toast.makeText(baseContext, "เข้าสู่ระบบไม่สำเร็จ", Toast.LENGTH_SHORT).show()
            }
            if (username_edt in username_list && password_edt !in password_list) {
                Toast.makeText(baseContext, "เข้าสู่ระบบไม่สำเร็จ", Toast.LENGTH_SHORT).show()
            }
        }
    }
}