package vcmsa.projects.loginpage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vcmsa.projects.loginpage.ExpenseElements.Expense
import vcmsa.projects.loginpage.ExpenseElements.ExpenseRepository

// This ViewModel acts as a bridge between the UI and the data layer
class ExpenseViewModel(private val expenseRepository: ExpenseRepository) : ViewModel() {

    // Function to add a new expense to the database
    fun addExpense(expense: Expense) {
        // Launching a coroutine to perform the DB operation asynchronously
        viewModelScope.launch {
            // Add the expense to the Room database
            expenseRepository.insertExpense(expense)
        }
    }

}
