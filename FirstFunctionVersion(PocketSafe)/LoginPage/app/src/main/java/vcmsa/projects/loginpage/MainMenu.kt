package vcmsa.projects.loginpage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import vcmsa.projects.loginpage.ui.theme.LoginPageTheme

class MainMenu : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginPageTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF8B5E3C)
                ) {
                    val context = LocalContext.current
                    val sharedPref = context.getSharedPreferences("GoalPrefs", Context.MODE_PRIVATE)

                    val minGoal = remember { mutableStateOf(0) }
                    val maxGoal = remember { mutableStateOf(0) }
                    val income = remember { mutableStateOf(0) }
                    val totalExpenses = remember { mutableStateOf(0) }

                    val statusMessage = remember { mutableStateOf("") }
                    val statusColor = remember { mutableStateOf(Color.Gray) }
                    val isRefreshing = remember { mutableStateOf(false) }

                    fun updateFinancialData() {
                        minGoal.value = sharedPref.getString("minGoal", "0")?.toIntOrNull() ?: 0
                        maxGoal.value = sharedPref.getString("maxGoal", "0")?.toIntOrNull() ?: 0
                        income.value = sharedPref.getString("income", "0")?.toIntOrNull() ?: 0
                        totalExpenses.value = sharedPref.getInt("totalExpenses", 0)

                        val remainder = income.value - totalExpenses.value
                        statusMessage.value = when {
                            remainder > maxGoal.value -> "🎯 Goal Achieved"
                            remainder > minGoal.value -> "⚠️ Partially Achieved"
                            else -> " Not Achieved"
                        }

                        statusColor.value = when (statusMessage.value) {
                            "🎯 Goal Achieved" -> Color(0xFF4CAF50)
                            "⚠️ Partially Achieved" -> Color(0xFFFFC107)
                            else -> Color(0xFF808080)
                        }
                    }

                    LaunchedEffect(Unit) {
                        updateFinancialData()
                    }

                    LaunchedEffect(isRefreshing.value) {
                        if (isRefreshing.value) {
                            updateFinancialData()
                            delay(1000)
                            isRefreshing.value = false
                        }
                    }

                    SwipeRefresh(
                        state = rememberSwipeRefreshState(isRefreshing.value),
                        onRefresh = { isRefreshing.value = true }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            // Back button with expanded clickable area
                            Text(
                                text = "Log out",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier
                                    .padding(bottom = 16.dp)
                                    .fillMaxWidth()  // Expand clickable area
                                    .clickable {
                                        val intent = Intent(context, MainActivity::class.java)
                                        context.startActivity(intent)
                                    }
                                    .padding(16.dp) // Optional padding to increase the clickable area further
                            )

                            Text(
                                text = "Welcome to the Main Menu!",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Text(
                                text = "Personal Budget Goals",
                                color = Color(0xFFF9D29D),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF6D4C41)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "🏁 Goal Ranges",
                                        color = Color(0xFFFFE0B2),
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    Text(
                                        text = "• Minimum Goal: R ${minGoal.value}",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "• Maximum Goal: R ${maxGoal.value}",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyLarge
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = "💼 Financial Snapshot",
                                        color = Color(0xFFFFE0B2),
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(
                                        text = "• Monthly Income: R ${income.value}",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "• Total Expenses: R ${totalExpenses.value}",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyLarge
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = "📊 Status: ${statusMessage.value}",
                                        color = statusColor.value,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Surface(
                                modifier = Modifier
                                    .weight(3.2f)
                                    .fillMaxWidth(),
                                color = Color(0xFF5D4037)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Column(
                                        modifier = Modifier
                                            .padding(start = 16.dp, top = 16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {}

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "View Expenses",
                                                color = Color.White,
                                                style = MaterialTheme.typography.bodyLarge,
                                                modifier = Modifier.clickable {
                                                    val intent = Intent(context, ViewExpensesActivity::class.java)
                                                    context.startActivity(intent)
                                                }
                                            )

                                            Text(
                                                text = "Add Expense",
                                                color = Color.White,
                                                style = MaterialTheme.typography.bodyLarge,
                                                modifier = Modifier.clickable {
                                                    val intent = Intent(context, ExpenseEntry::class.java)
                                                    context.startActivity(intent)
                                                }
                                            )

                                            Text(
                                                text = "Set Goals",
                                                color = Color.White,
                                                style = MaterialTheme.typography.bodyLarge,
                                                modifier = Modifier.clickable {
                                                    val intent = Intent(context, vcmsa.projects.loginpage.BudgetGoals.Goals::class.java)
                                                    context.startActivity(intent)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
