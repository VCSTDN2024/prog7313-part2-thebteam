package vcmsa.projects.loginpage.BudgetGoals

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import vcmsa.projects.loginpage.MainMenu
import vcmsa.projects.loginpage.data.AppDatabase
import vcmsa.projects.loginpage.ui.theme.LoginPageTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)  // Suppress experimental API warning
class Goals : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginPageTheme {
                GoalsScreen()
            }
        }
    }
}

@Composable
fun GoalsScreen() {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("GoalPrefs", Context.MODE_PRIVATE)

    var minGoal by remember { mutableStateOf("") }
    var maxGoal by remember { mutableStateOf("") }
    var income by remember { mutableStateOf("") }

    var currentMin by remember { mutableStateOf("") }
    var currentMax by remember { mutableStateOf("") }
    var currentIncome by remember { mutableStateOf("") }

    var totalExpenses by remember { mutableStateOf(0.0) }
    var goalStatus by remember { mutableStateOf("") }
    var goalColor by remember { mutableStateOf(Color.Transparent) }

    LaunchedEffect(Unit) {
        currentMin = sharedPref.getString("minGoal", "") ?: ""
        currentMax = sharedPref.getString("maxGoal", "") ?: ""
        currentIncome = sharedPref.getString("income", "") ?: ""

        val db = AppDatabase.getDatabase(context)
        val expenses = db.expenseDao().getAllExpenses()

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentMonthExpenses = expenses.filter { expense ->
            try {
                val date = formatter.parse(expense.startDate)
                date != null && isInCurrentMonth(date)
            } catch (e: Exception) {
                false
            }
        }

        totalExpenses = currentMonthExpenses.sumOf { it.amount }
        val (status, color) = getGoalStatus(currentMin, currentMax, currentIncome, totalExpenses)
        goalStatus = status
        goalColor = color
    }
    sharedPref.edit().putInt("totalExpenses", totalExpenses.toInt()).apply()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8B5E3C)) // Medium oak color
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // BACK BUTTON with a larger clickable area
        Text(
            text = "Back",
            modifier = Modifier
                .clickable {
                    val intent = Intent(context, MainMenu::class.java)
                    context.startActivity(intent)
                }
                .padding(16.dp), // Added padding to increase clickable area
            color = Color(0xFFF5F5DC), // Teak color for the back button
            style = MaterialTheme.typography.bodyLarge.copy(
                textDecoration = TextDecoration.Underline
            )
        )

        Text("Current Min Goal: $currentMin", color = Color(0xFFF5F5DC)) // Slightly stained off-white
        Text("Current Max Goal: $currentMax", color = Color(0xFFF5F5DC)) // Slightly stained off-white
        Text("Current Monthly Income: $currentIncome", color = Color(0xFFF5F5DC)) // Slightly stained off-white
        Text("Total Expenses (This Month): $totalExpenses", color = Color(0xFFF5F5DC)) // Slightly stained off-white

        if (goalStatus.isNotEmpty()) {
            Text(
                text = goalStatus,
                color = goalColor,
                style = MaterialTheme.typography.titleMedium
            )
        }

        OutlinedTextField(
            value = minGoal,
            onValueChange = { minGoal = it },
            label = { Text("Min Goal") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFDEB887), // Light brown color for the focused border
                unfocusedBorderColor = Color(0xFFDEB887), // Light brown color for the unfocused border
                focusedLabelColor = Color(0xFFDEB887), // Light brown color for label
                unfocusedLabelColor = Color(0xFFDEB887), // Light brown color for label
                focusedTextColor = Color(0xFFDEB887), // Light brown color for text
                unfocusedTextColor = Color(0xFFDEB887), // Light brown color for text
                focusedContainerColor = Color.Transparent, // Transparent background for focused state
                unfocusedContainerColor = Color.Transparent, // Transparent background for unfocused state
                cursorColor = Color(0xFFDEB887) // Light brown color for cursor
            )
        )

        OutlinedTextField(
            value = maxGoal,
            onValueChange = { maxGoal = it },
            label = { Text("Max Goal") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFDEB887), // Light brown color for the focused border
                unfocusedBorderColor = Color(0xFFDEB887), // Light brown color for the unfocused border
                focusedLabelColor = Color(0xFFDEB887), // Light brown color for label
                unfocusedLabelColor = Color(0xFFDEB887), // Light brown color for label
                focusedTextColor = Color(0xFFDEB887), // Light brown color for text
                unfocusedTextColor = Color(0xFFDEB887), // Light brown color for text
                focusedContainerColor = Color.Transparent, // Transparent background for focused state
                unfocusedContainerColor = Color.Transparent, // Transparent background for unfocused state
                cursorColor = Color(0xFFDEB887) // Light brown color for cursor
            )
        )

        OutlinedTextField(
            value = income,
            onValueChange = { income = it },
            label = { Text("Monthly Income") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFDEB887), // Light brown color for the focused border
                unfocusedBorderColor = Color(0xFFDEB887), // Light brown color for the unfocused border
                focusedLabelColor = Color(0xFFDEB887), // Light brown color for label
                unfocusedLabelColor = Color(0xFFDEB887), // Light brown color for label
                focusedTextColor = Color(0xFFDEB887), // Light brown color for text
                unfocusedTextColor = Color(0xFFDEB887), // Light brown color for text
                focusedContainerColor = Color.Transparent, // Transparent background for focused state
                unfocusedContainerColor = Color.Transparent, // Transparent background for unfocused state
                cursorColor = Color(0xFFDEB887) // Light brown color for cursor
            )
        )

        Button(
            onClick = {
                val finalMin = if (minGoal.isNotBlank()) minGoal else currentMin
                val finalMax = if (maxGoal.isNotBlank()) maxGoal else currentMax
                val finalIncome = if (income.isNotBlank()) income else currentIncome

                saveGoalsToPreferences(context, finalMin, finalMax, finalIncome)

                currentMin = finalMin
                currentMax = finalMax
                currentIncome = finalIncome

                val (status, color) = getGoalStatus(finalMin, finalMax, finalIncome, totalExpenses)
                goalStatus = status
                goalColor = color

                minGoal = ""
                maxGoal = ""
                income = ""
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD2B29D)) // Teak color
        ) {
            Text("Set", color = Color(0xFF5C4033)) // Beige color for text
        }
    }
}

fun isInCurrentMonth(date: Date): Boolean {
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

    calendar.time = date
    val expenseMonth = calendar.get(Calendar.MONTH)
    val expenseYear = calendar.get(Calendar.YEAR)

    return expenseMonth == currentMonth && expenseYear == currentYear
}

fun saveGoalsToPreferences(context: Context, min: String, max: String, income: String) {
    val sharedPref = context.getSharedPreferences("GoalPrefs", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putString("minGoal", min)
        putString("maxGoal", max)
        putString("income", income)
        apply()
    }
}

fun getGoalStatus(min: String, max: String, income: String, totalExpenses: Double): Pair<String, Color> {
    return try {
        val minVal = min.toDouble()
        val maxVal = max.toDouble()
        val incomeVal = income.toDouble()
        val remainder = incomeVal - totalExpenses

        when {
            remainder > maxVal -> "Monthly Goal has been reached" to Color(0xFFDEB887) // Green
            remainder > minVal && remainder < maxVal -> "Monthly goal Partially achieved" to Color(0xFFFFA000) // Orange
            else -> "Monthly Goal not achieved" to Color.Gray
        }
    } catch (e: Exception) {
        "" to Color.Transparent
    }
}
