package vcmsa.projects.loginpage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vcmsa.projects.loginpage.ExpenseElements.Expense

import vcmsa.projects.loginpage.ExpenseElements.ExpenseRepository

class ExpenseViewModel(private val expenseRepository: ExpenseRepository) : ViewModel() {

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.insertExpense(expense)
        }
    }
}
