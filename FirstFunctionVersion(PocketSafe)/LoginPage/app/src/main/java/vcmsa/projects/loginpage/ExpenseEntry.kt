package vcmsa.projects.loginpage

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.setPadding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import vcmsa.projects.loginpage.data.AppDatabase
import vcmsa.projects.loginpage.ExpenseElements.Expense
import vcmsa.projects.loginpage.ExpenseElements.ExpenseRepository
import java.io.File
import java.util.*

class ExpenseEntry : Activity() {

    private lateinit var db: AppDatabase
    private lateinit var expenseRepository: ExpenseRepository

    private var selectedPhotoPath: String? = null
    private val PICK_IMAGE_REQUEST = 1

    private lateinit var photoPreview: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = AppDatabase.getDatabase(applicationContext)
        expenseRepository = ExpenseRepository(db.expenseDao(), db.categoryDao())

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(android.graphics.Color.parseColor("#8B5E3C")) // Brown background

        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(60, 150, 60, 60)
        }
        scrollView.addView(layout)

        fun addWithMargin(view: View, bottomMargin: Int = 24) {
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, bottomMargin)
            }
            view.layoutParams = params
            layout.addView(view)
        }

        val backButton = TextView(this).apply {
            text = "Back"
            textSize = 18f
            setPadding(10, 10, 10, 10)
            setTextColor(Color.parseColor("#E1C699"))
            setOnClickListener {
                val intent = Intent(this@ExpenseEntry, MainMenu::class.java)
                startActivity(intent)
                finish()
            }
        }

        val headingTextView = TextView(this).apply {
            text = "Add Expense"
            textSize = 26f
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#E1C699"))
        }

        val categoryLabel = TextView(this).apply {
            text = "Choose Category"
            textSize = 18f
            setTextColor(Color.parseColor("#E1C699"))
        }

        val spinner = Spinner(this)

        val categoryInput = EditText(this).apply {
            hint = "Enter custom category (if not listed)"
            setTextColor(Color.parseColor("#E1C699"))
            setHintTextColor(Color.parseColor("#E1C699"))
        }

        val descriptionInput = EditText(this).apply {
            hint = "Description"
            setTextColor(Color.parseColor("#E1C699"))
            setHintTextColor(Color.parseColor("#E1C699"))
        }

        val startDateInput = EditText(this).apply {
            hint = "Start Date"
            inputType = android.text.InputType.TYPE_NULL
            setTextColor(Color.parseColor("#E1C699"))
            setHintTextColor(Color.parseColor("#E1C699"))
        }

        val endDateInput = EditText(this).apply {
            hint = "End Date"
            inputType = android.text.InputType.TYPE_NULL
            setTextColor(Color.parseColor("#E1C699"))
            setHintTextColor(Color.parseColor("#E1C699"))
        }

        val amountInput = EditText(this).apply {
            hint = "Amount"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setTextColor(Color.parseColor("#E1C699"))
            setHintTextColor(Color.parseColor("#E1C699"))
        }

        val addPhotoButton = Button(this).apply { text = "Add Photograph" }
        val confirmButton = Button(this).apply { text = "Confirm" }
        val viewExpensesButton = Button(this).apply { text = "View Expenses" }

        photoPreview = ImageView(this).apply {
            visibility = View.GONE
            layoutParams = LinearLayout.LayoutParams(300, 300).apply {
                gravity = Gravity.CENTER
            }
        }

        addWithMargin(backButton, 40)
        addWithMargin(headingTextView, 40)
        addWithMargin(categoryLabel)
        addWithMargin(spinner)
        addWithMargin(categoryInput)
        addWithMargin(descriptionInput)
        addWithMargin(startDateInput)
        addWithMargin(endDateInput)
        addWithMargin(amountInput)
        addWithMargin(addPhotoButton)
        addWithMargin(photoPreview)
        addWithMargin(confirmButton, 20)
        addWithMargin(viewExpensesButton, 60)

        setContentView(scrollView)

        MainScope().launch(Dispatchers.IO) {
            try {
                val expenseCategories = expenseRepository.getDistinctExpenseCategories()
                runOnUiThread {
                    val adapter = object : ArrayAdapter<String>(this@ExpenseEntry, android.R.layout.simple_spinner_item, expenseCategories) {
                        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val view = super.getView(position, convertView, parent)
                            (view as TextView).setTextColor(Color.parseColor("#E1C699")) // Set the desired color for the selected item
                            return view
                        }

                        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val view = super.getDropDownView(position, convertView, parent)
                            view.setBackgroundColor(Color.parseColor("#3E2723"))
                            (view as TextView).setTextColor(Color.parseColor("#E1C699")) // Set the desired color for the dropdown items
                            return view
                        }
                    }
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter

                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@ExpenseEntry,
                        "Failed to load categories from expenses: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }

        fun showDatePicker(onDateSelected: (String) -> Unit) {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this,
                { _, year, month, dayOfMonth ->
                    val formatted = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    onDateSelected(formatted)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        startDateInput.setOnClickListener {
            showDatePicker { selected -> startDateInput.setText(selected) }
        }
        endDateInput.setOnClickListener {
            showDatePicker { selected -> endDateInput.setText(selected) }
        }

        addPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        confirmButton.setOnClickListener {
            val categoryName = if (categoryInput.text.toString().isNotEmpty()) {
                categoryInput.text.toString().replace(" ", "_")
            } else {
                spinner.selectedItem?.toString()?.replace(" ", "_") ?: ""
            }

            val description = descriptionInput.text.toString()
            val startDate = startDateInput.text.toString()
            val endDate = endDateInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()
            val photoPath = selectedPhotoPath ?: "no_photo"

            if (categoryName.isEmpty() || description.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || amount == null || amount <= 0) {
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isValidDate = Regex("""\d{4}-\d{2}-\d{2}""")
            if (!startDate.matches(isValidDate) || !endDate.matches(isValidDate)) {
                Toast.makeText(this, "Please select valid dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            MainScope().launch(Dispatchers.IO) {
                try {
                    val expense = Expense(
                        categoryName = categoryName,
                        amount = amount,
                        description = description,
                        startDate = startDate,
                        endDate = endDate,
                        photo = photoPath
                    )
                    expenseRepository.insertExpense(expense)
                    runOnUiThread {
                        Toast.makeText(
                            this@ExpenseEntry,
                            "Expense successfully added to $categoryName",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(
                            this@ExpenseEntry,
                            "Error adding expense: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        viewExpensesButton.setOnClickListener {
            val intent = Intent(this, ViewExpensesActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val uri: Uri? = data.data

            if (uri != null) {
                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    if (inputStream != null) {
                        val fileName = "expense_${System.currentTimeMillis()}.jpg"
                        val file = File(filesDir, fileName)

                        file.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }

                        selectedPhotoPath = file.absolutePath

                        photoPreview.visibility = View.VISIBLE
                        photoPreview.setImageURI(Uri.fromFile(file))
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Failed to load image: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
