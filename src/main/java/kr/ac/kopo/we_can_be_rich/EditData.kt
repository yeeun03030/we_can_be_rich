package kr.ac.kopo.we_can_be_rich

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class EditData : AppCompatActivity() {

    private lateinit var numberListContainer: LinearLayout
    private lateinit var lotteryDao: LotteryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_data)

        lotteryDao = LotteryDao(this)
        numberListContainer = findViewById(R.id.numberListContainer)

        val editButton: Button = findViewById(R.id.editButton)
        editButton.setOnClickListener {
            showConfirmationDialog()
        }

        loadLotteryNumbers()
    }

    private fun loadLotteryNumbers() {
        val lotteryNumbers = lotteryDao.getAllNumbers()
        updateNumberList(lotteryNumbers)
    }

    private fun updateNumberList(lotteryNumbers: List<List<Int>>) {
        numberListContainer.removeAllViews()
        for (numbers in lotteryNumbers) {
            addNumberToLayout(numbers)
        }
    }

    private fun addNumberToLayout(numbers: List<Int>) {
        val linearLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
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

        // Delete 버튼 추가
        val deleteButton = Button(this).apply {
            text = "Delete"
            setOnClickListener {
                showDeleteConfirmationDialog(numbers)
            }
        }
        linearLayout.addView(deleteButton)
        numberListContainer.addView(linearLayout)
    }

    private fun showDeleteConfirmationDialog(numbers: List<Int>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("삭제 확인")
        builder.setMessage("해당 데이터를 삭제하시겠습니까?")
        builder.setPositiveButton("예") { _, _ ->
            lotteryDao.deleteNumbers(numbers) // 여기서 numbers를 올바르게 전달
            loadLotteryNumbers() // 데이터베이스에서 다시 로드하여 업데이트
        }
        builder.setNegativeButton("아니요", null)
        builder.show()
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("편집 완료")
        builder.setMessage("편집을 끝내겠습니까?")
        builder.setPositiveButton("예") { _, _ ->
            setResult(Activity.RESULT_OK)
            finish()
        }
        builder.setNegativeButton("아니요", null)
        builder.show()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}