package com.example.CommunityEx3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.communityex3.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private val mAuth = FirebaseAuth.getInstance()

    private var mId: EditText? = null
    private var mPassword: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mId = findViewById(R.id.login_id)
        mPassword = findViewById(R.id.login_password)

        findViewById<View>(R.id.login_signup).setOnClickListener(this)
        findViewById<View>(R.id.login_success).setOnClickListener(this)
    }

    /*
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!= null){
            //Toast.makeText(this, "자동 로그인 : " + user.getUid(), Toast.LENGTH_SHORT).show();;
            startActivity(new Intent(this, MainActivity.class));
        }
    }
*/
    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.login_signup) {
            startActivity(Intent(this, SignupActivity::class.java))
        } else if (id == R.id.login_success) {
            mAuth.signInWithEmailAndPassword(mId!!.text.toString(), mPassword!!.text.toString())
                .addOnCompleteListener(
                    this
                ) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        if (user != null) {
                            //Toast.makeText(LoginActivity.this, "로그인 성공" + user.getUid(),Toast.LENGTH_SHORT).show();
                            startActivity(
                                Intent(
                                    this@LoginActivity,
                                    RegActivity::class.java
                                )
                            )
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity, "Login Error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}