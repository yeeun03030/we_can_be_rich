package kr.ac.kopo.we_can_be_rich

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class RealMain : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_main)

        var mainBtn: Button = findViewById(R.id.mainBtn)

        mainBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
//            startActivityForResult(intent)
            startActivity(intent)
        }
    }
}