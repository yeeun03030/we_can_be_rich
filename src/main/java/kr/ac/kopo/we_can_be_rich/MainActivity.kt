package kr.ac.kopo.we_can_be_rich

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ImageView
import android.widget.TextView
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var numberListContainer: LinearLayout
    private lateinit var roundNumberInput: EditText
    private lateinit var searchedRoundContainer: LinearLayout
    private lateinit var lotteryDao: LotteryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lotteryDao = LotteryDao(this)
        roundNumberInput = findViewById(R.id.roundNumberInput)
        numberListContainer = findViewById(R.id.numberListContainer)
        searchedRoundContainer = findViewById(R.id.searchedRoundContainer)
        val addButton: Button = findViewById(R.id.addButton)
        val editButton: Button = findViewById(R.id.editButton)
        val submitRoundButton: Button = findViewById(R.id.submitRoundButton)

        addButton.setOnClickListener {
            val roundNumber = roundNumberInput.text.toString()
            if (roundNumber.isNotBlank()) {
                val intent = Intent(this, AddDataActivity::class.java)
                intent.putExtra("roundNumber", roundNumber)
                startActivityForResult(intent, REQUEST_CODE_ADD_DATA)
            } else {
                Toast.makeText(this, "회차를 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        editButton.setOnClickListener {
            val intent = Intent(this, EditData::class.java)
            startActivityForResult(intent, REQUEST_CODE_EDIT_DATA)
        }

        submitRoundButton.setOnClickListener {
            val roundNumber = roundNumberInput.text.toString()
            if (roundNumber.isNotBlank()) {
                fetchLottoNumbers(roundNumber)
            } else {
                Toast.makeText(this, "회차를 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        loadLotteryNumbers()
    }

    private fun fetchLottoNumbers(round: String) {
        val url = "https://www.dhlottery.co.kr/common.do?method=getLottoNumber&drwNo=$round"
        val queue = Volley.newRequestQueue(this)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                if (response.getString("returnValue") == "fail") {
                    Toast.makeText(this, "이 번호는 아직 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                    searchedRoundContainer.visibility = View.GONE
                } else {
                    parseLottoResponse(response)
                }
            },
            {
                Toast.makeText(this, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                searchedRoundContainer.visibility = View.GONE
            }
        )

        queue.add(jsonObjectRequest)
    }

    private fun parseLottoResponse(response: JSONObject) {
        val numbers = listOf(
            response.getInt("drwtNo1"),
            response.getInt("drwtNo2"),
            response.getInt("drwtNo3"),
            response.getInt("drwtNo4"),
            response.getInt("drwtNo5"),
            response.getInt("drwtNo6")
        )
        val bonusNumber = response.getInt("bnusNo")
        updateSearchedRound(numbers, bonusNumber)
    }

    private fun updateSearchedRound(numbers: List<Int>, bonusNumber: Int) {
        searchedRoundContainer.removeAllViews()
        for ((index, number) in numbers.withIndex()) {
            val imageView = ImageView(this).apply {
                val resourceId = resources.getIdentifier("img_$number", "drawable", packageName)
                setImageResource(
                    if (resourceId != 0) resourceId else R.drawable.default_image
                )
                layoutParams = LinearLayout.LayoutParams(
                    dpToPx(40), dpToPx(40)  // 크기 조절
                ).apply {
                    setMargins(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5))
                }
            }
            searchedRoundContainer.addView(imageView)

            // 6번째 이미지 뒤에 '+' 기호 추가
            if (index == 5) {
                val plusTextView = TextView(this).apply {
                    text = "+"
                    textSize = 20f
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(dpToPx(5), 0, dpToPx(5), 0)
                    }
                }
                searchedRoundContainer.addView(plusTextView)
            }
        }

        // 보너스 번호 추가
        val bonusImageView = ImageView(this).apply {
            val resourceId = resources.getIdentifier("img_$bonusNumber", "drawable", packageName)
            setImageResource(
                if (resourceId != 0) resourceId else R.drawable.default_image
            )
            layoutParams = LinearLayout.LayoutParams(
                dpToPx(40), dpToPx(40)  // 크기 조절
            ).apply {
                setMargins(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5))
            }
        }
        searchedRoundContainer.addView(bonusImageView)

        searchedRoundContainer.visibility = View.VISIBLE
    }

    private fun loadLotteryNumbers() {
        val lotteryNumbers = lotteryDao.getAllNumbers()
        updateNumberList(lotteryNumbers)
    }

    private fun updateNumberList(lotteryNumbers: List<List<Int>>) {
        numberListContainer.removeAllViews()
        for ((index, numbers) in lotteryNumbers.withIndex()) {
            addNumberToLayout(numbers)
            // 각 데이터 사이에 구분선 추가
            if (index < lotteryNumbers.size - 1) {
                addSeparator()
            }
        }
    }

    private fun addSeparator() {
        val separator = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(1)
            ).apply {
                setMargins(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10))
            }
            setBackgroundColor(android.graphics.Color.GRAY)
        }
        numberListContainer.addView(separator)
    }

    private fun addNumberToLayout(numbers: List<Int>) {
        val linearLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            gravity = android.view.Gravity.CENTER
        }

        for (number in numbers) {
            val imageView = ImageView(this).apply {
                val resourceId = resources.getIdentifier("img_$number", "drawable", packageName)
                setImageResource(
                    if (resourceId != 0) resourceId else R.drawable.default_image
                )
                layoutParams = LinearLayout.LayoutParams(
                    dpToPx(40), dpToPx(40)
                ).apply {
                    setMargins(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5))
                }
            }
            linearLayout.addView(imageView)
        }

        numberListContainer.addView(linearLayout)
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_DATA && resultCode == Activity.RESULT_OK) {
            val round = data?.getStringExtra("roundNumber") ?: ""
            val numbers = data?.getStringExtra("newData")?.split(",")?.map { it.toInt() }
            if (round.isNotBlank() && numbers != null) {
                lotteryDao.insertNumbers(round, numbers)
                loadLotteryNumbers()
            }
        } else if (requestCode == REQUEST_CODE_EDIT_DATA && resultCode == Activity.RESULT_OK) {
            loadLotteryNumbers()
        }
    }

    companion object {
        const val REQUEST_CODE_ADD_DATA = 1
        const val REQUEST_CODE_EDIT_DATA = 2
    }
}