package kr.ac.kopo.we_can_be_rich

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class AddDataActivity : AppCompatActivity() {

    private lateinit var lottoImageViews: List<ImageView>
    private lateinit var generateButton: Button
    private lateinit var backButton: Button
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_data)

        lottoImageViews = listOf(
            findViewById(R.id.lottoImageView1),
            findViewById(R.id.lottoImageView2),
            findViewById(R.id.lottoImageView3),
            findViewById(R.id.lottoImageView4),
            findViewById(R.id.lottoImageView5),
            findViewById(R.id.lottoImageView6)
        )
        generateButton = findViewById(R.id.generateButton)
        backButton = findViewById(R.id.backButton)

        lottoImageViews.forEach { it.setImageResource(R.drawable.default_image) }

        generateButton.setOnClickListener {
            generateButton.visibility = Button.GONE
            backButton.visibility = Button.GONE
            generateLottoNumbers()
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun generateLottoNumbers() {
        val numbers = (1..45).shuffled().take(6).sorted()
        var currentIndex = 0

        val revealRunnable = object : Runnable {
            override fun run() {
                if (currentIndex < numbers.size) {
                    val number = numbers[currentIndex]
                    val resourceId = resources.getIdentifier("img_$number", "drawable", packageName)
                    lottoImageViews[currentIndex].setImageResource(resourceId)
                    currentIndex++
                    handler.postDelayed(this, 500L)
                } else {
                    handler.removeCallbacks(this)
                    showConfirmationDialog(numbers)
                }
            }
        }

        handler.post(revealRunnable)
    }

    private fun showConfirmationDialog(numbers: List<Int>) {
        val roundNumber = intent.getStringExtra("roundNumber") ?: "0"
        val builder = AlertDialog.Builder(this)
        builder.setTitle("확인")
        builder.setMessage("생성된 번호: ${numbers.joinToString(", ")}\n이 번호를 추가하시겠습니까?")
        builder.setPositiveButton("예") { _, _ ->
            val resultIntent = Intent().apply {
                putExtra("roundNumber", roundNumber)
                putExtra("newData", numbers.joinToString(","))
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
        builder.setNegativeButton("아니요") { _, _ ->
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        builder.show()
    }
}