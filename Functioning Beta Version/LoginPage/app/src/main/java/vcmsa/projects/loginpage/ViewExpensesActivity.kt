package vcmsa.projects.loginpage

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.setPadding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import vcmsa.projects.loginpage.data.AppDatabase
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ViewExpensesActivity : Activity() {

    private lateinit var db: AppDatabase
    private lateinit var expensesLayout: LinearLayout
    private lateinit var categorySpinner: Spinner
    private lateinit var selectedCategoryLabel: TextView
    private lateinit var startDateEditText: TextView
    private lateinit var endDateEditText: TextView
    private lateinit var filterButton: Button
    private lateinit var totalAmountTextView: TextView  // Added for total amount
    private var startDate: Date? = null
    private var endDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = AppDatabase.getDatabase(applicationContext)
        val whiteStainedColor = Color.parseColor("#F5F5F5")
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#8B5E3C")) // Light Yellow background
            setPadding(30, 30, 30, 30)
        }

        val backButton = Button(this).apply {
            text = "Back to Main Menu"
            setPadding(40, 40, 40, 40) // Adjust padding to create a more symmetrical round shape

            // Create a drawable for the round shape
            val drawable = GradientDrawable()
            drawable.shape = GradientDrawable.OVAL // Use oval shape for round button
            drawable.setColor(Color.parseColor("#D2B48C")) // Light wood color
            background = drawable

            // Set an OnClickListener to navigate to Main Menu
            setOnClickListener {
                val intent = Intent(this@ViewExpensesActivity, MainMenu::class.java)
                startActivity(intent)
                finish()  // Optionally finish this activity
            }
        }

// Set LayoutParams to adjust positioning
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            // Adjust the top margin to drop the button down
            topMargin = 100 // Increase this value to drop the button further down
            leftMargin = 40 // Adjust for horizontal positioning
        }

// Add the button to the layout
        mainLayout.addView(backButton, layoutParams)




        // Add some space after the back button
        val space = View(this).apply {
            setLayoutParams(LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 20))
        }
        mainLayout.addView(space)
        setupCategorySpinner(mainLayout)
        setupDateRangeFilter(mainLayout)
        setupFilterButton(mainLayout)
        setupTotalAmountView(mainLayout)  // Set up total amount display
        setupExpensesScrollView(mainLayout)

        setContentView(mainLayout)

        loadCategories()

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = categorySpinner.selectedItem.toString()
                selectedCategoryLabel.text = "Showing Expenses for: $selectedCategory"
                loadExpenses(selectedCategory)
                selectedCategoryLabel.setTextColor(Color.parseColor("#D2B48C"))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupCategorySpinner(mainLayout: LinearLayout) {
        val spinnerLabel = TextView(this).apply {
            text = ""
            textSize = 20f
            setPadding(0, 0, 0, 10)
            setBackgroundColor(Color.parseColor("#8B5E3C"))

        }
        mainLayout.addView(spinnerLabel)

        categorySpinner = Spinner(this)
        mainLayout.addView(categorySpinner)

        selectedCategoryLabel = TextView(this).apply {
            textSize = 18f
            setPadding(0, 30, 0, 20)
            setBackgroundColor(Color.parseColor("#8B5E3C"))

        }
        mainLayout.addView(selectedCategoryLabel)
    }

    private fun setupDateRangeFilter(mainLayout: LinearLayout) {
        val dateLabel = TextView(this).apply {
            text = "Select Date Range:"
            textSize = 20f
            setPadding(0, 20, 0, 10)
            setBackgroundColor(Color.parseColor("#8B5E3C"))
            setTextColor(Color.parseColor("#D2B48C"))
        }
        mainLayout.addView(dateLabel)

        startDateEditText = TextView(this).apply {
            text = "Start Date"
            setPadding(0, 10, 0, 20)
            setBackgroundColor(Color.parseColor("#8B5E3C"))
            setOnClickListener { showDatePickerDialog(true) }
            setTextColor(Color.parseColor("#D2B48C"))
        }
        mainLayout.addView(startDateEditText)

        endDateEditText = TextView(this).apply {
            text = "End Date"
            setPadding(0, 10, 0, 20)
            setBackgroundColor(Color.parseColor("#8B5E3C"))
            setOnClickListener { showDatePickerDialog(false) }
            setTextColor(Color.parseColor("#D2B48C"))
        }
        mainLayout.addView(endDateEditText)
    }

    private fun setupFilterButton(mainLayout: LinearLayout) {
        filterButton = Button(this).apply {
            text = "Apply Date Filter"
            setPadding(0, 20, 0, 20)
            setBackgroundColor(Color.parseColor("#8B5E3C")) // Light Blue for buttons
            setTextColor(Color.parseColor("#D2B48C"))
        }
        mainLayout.addView(filterButton)

        filterButton.setOnClickListener {
            val selectedCategory = categorySpinner.selectedItem.toString()

            if (startDate == null) {
                showToast("Please select a start date.")
                return@setOnClickListener
            }
            if (endDate == null) {
                showToast("Please select an end date.")
                return@setOnClickListener
            }

            loadExpenses(selectedCategory)
        }
    }

    private fun setupTotalAmountView(mainLayout: LinearLayout) {
        totalAmountTextView = TextView(this).apply {
            text = "Total Amount: 0"
            textSize = 18f
            setPadding(0, 20, 0, 20)
            setBackgroundColor(Color.parseColor("#8B5E3C"))
            setTextColor(Color.parseColor("#D2B48C"))
        }
        mainLayout.addView(totalAmountTextView)
    }

    private fun setupExpensesScrollView(mainLayout: LinearLayout) {
        expensesLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
            setBackgroundColor(Color.parseColor("#8B5E3C"))
        }

        val scrollView = ScrollView(this).apply {
            addView(expensesLayout)
        }

        mainLayout.addView(scrollView)
    }

    private fun showDatePickerDialog(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val formatted = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDayOfMonth)
                val parsedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(formatted)

                if (isStartDate) {
                    startDate = parsedDate
                    startDateEditText.text = "Start Date: $formatted"
                } else {
                    endDate = parsedDate
                    endDateEditText.text = "End Date: $formatted"
                }
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun loadCategories() {
        MainScope().launch(Dispatchers.IO) {
            try {
                val categories = db.expenseDao().getAllExpenseCategories()
                val categoriesWithAll = listOf("All Categories") + categories

                runOnUiThread {
                    val adapter = object : ArrayAdapter<String>(
                        this@ViewExpensesActivity,
                        android.R.layout.simple_spinner_item,
                        categoriesWithAll
                    ) {
                        override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                            val view = super.getView(position, convertView, parent)
                            (view as TextView).setTextColor(Color.parseColor("#D2B48C")) // Set text color to light brown
                            return view
                        }

                        override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                            val view = super.getDropDownView(position, convertView, parent)
                            view.setBackgroundColor(Color.parseColor("#8B5E3C"))
                            (view as TextView).setTextColor(Color.parseColor("#D2B48C")) // Set text color to light brown
                            return view
                        }
                    }

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    categorySpinner.adapter = adapter
                }
            } catch (e: Exception) {
                runOnUiThread {
                    showToast("Failed to load categories: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun loadExpenses(selectedCategory: String) {
        MainScope().launch(Dispatchers.IO) {
            try {
                val expenses = if (selectedCategory == "All Categories") {
                    db.expenseDao().getAllExpenses()
                } else {
                    db.expenseDao().getExpensesByCategory(selectedCategory)
                }

                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                val filteredExpenses = expenses.filter { expense ->
                    val expenseStartDate = sdf.parse(expense.startDate)
                    val expenseEndDate = sdf.parse(expense.endDate)

                    (startDate == null || expenseStartDate >= startDate) &&
                            (endDate == null || expenseEndDate <= endDate)
                }

                var totalAmount = 0.0 // Track total amount

                runOnUiThread {
                    expensesLayout.removeAllViews()

                    if (filteredExpenses.isEmpty()) {
                        expensesLayout.addView(TextView(this@ViewExpensesActivity).apply {
                            text = "No expenses found."
                            textSize = 18f
                            setBackgroundColor(Color.parseColor("#8B5E3C"))
                        })
                    } else {
                        filteredExpenses.forEach { expense ->
                            val container = LinearLayout(this@ViewExpensesActivity).apply {
                                orientation = LinearLayout.VERTICAL
                                setPadding(20, 20, 20, 20)
                                setBackgroundColor(Color.parseColor("#8B5E3C"))
                                layoutParams = LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    setMargins(0, 0, 0, 40)
                                }
                            }

                            val expenseDetails = TextView(this@ViewExpensesActivity).apply {
                                text = """
                                    Category: ${expense.categoryName}
                                    Amount: ${expense.amount}
                                    Description: ${expense.description}
                                    Start Date: ${expense.startDate}
                                    End Date: ${expense.endDate}
                                """.trimIndent()
                                textSize = 16f
                                setBackgroundColor(Color.parseColor("#8B5E3C"))
                                setTextColor(Color.parseColor("#E1C699"))
                            }
                            container.addView(expenseDetails)

                            totalAmount += expense.amount // Add to total amount

                            if (!expense.photo.isNullOrEmpty()) {
                                val file = File(expense.photo)
                                if (file.exists()) {
                                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                    val imageView = ImageView(this@ViewExpensesActivity).apply {
                                        setImageBitmap(bitmap)
                                        setPadding(0, 10, 0, 10)
                                    }
                                    container.addView(imageView)

                                    val downloadButton = Button(this@ViewExpensesActivity).apply {
                                        text = "Download Image"
                                        setBackgroundColor(Color.parseColor("#D2B48C"))
                                        setOnClickListener {
                                            saveImageToDownloads(file)
                                        }
                                    }
                                    container.addView(downloadButton)
                                }
                            }

                            val deleteButton = Button(this@ViewExpensesActivity).apply {
                                text = "Delete Expense"
                                setBackgroundColor(Color.parseColor("#F5F5F5"))
                                setOnClickListener {
                                    MainScope().launch(Dispatchers.IO) {
                                        db.expenseDao().deleteExpense(expense)
                                        runOnUiThread {
                                            loadExpenses(selectedCategory)
                                            showToast("Expense deleted successfully.")
                                        }
                                    }
                                }
                            }
                            container.addView(deleteButton)

                            expensesLayout.addView(container)
                        }
                    }

                    // Update total amount
                    totalAmountTextView.text = "Total Amount: $totalAmount"
                }
            } catch (e: Exception) {
                runOnUiThread {
                    showToast("Failed to load expenses: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun saveImageToDownloads(file: File) {
        try {
            val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val destinationFile = File(downloadsFolder, file.name)
            file.copyTo(destinationFile, overwrite = true)
            showToast("Image saved to Downloads")
        } catch (e: IOException) {
            showToast("Failed to save image: ${e.localizedMessage}")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
