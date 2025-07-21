package com.example.prmcar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prmcar.data.model.TransactionRequest
import com.example.prmcar.data.model.TransactionResponse
import com.example.prmcar.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// UI State cho Transaction
data class TransactionUiState(
    val isLoading: Boolean = false,
    val transactions: List<TransactionResponse> = emptyList(),
    val selectedTransaction: TransactionResponse? = null,
    val errorMessage: String? = null,
    val isActionSuccess: Boolean = false
)

class TransactionViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    fun loadTransactions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isActionSuccess = false)
            transactionRepository.getTransactions()
                .onSuccess { transactions ->
                    _uiState.value = _uiState.value.copy(isLoading = false, transactions = transactions, errorMessage = null)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = exception.message)
                }
        }
    }

    fun getTransactionById(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, selectedTransaction = null)
            transactionRepository.getTransactionById(id)
                .onSuccess { transaction ->
                    _uiState.value = _uiState.value.copy(isLoading = false, selectedTransaction = transaction)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = exception.message)
                }
        }
    }

    fun createTransaction(transaction: TransactionRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isActionSuccess = false)
            transactionRepository.createTransaction(transaction)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, isActionSuccess = true)
                    loadTransactions()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = exception.message)
                }
        }
    }

    fun updateTransaction(id: Int, transaction: TransactionRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isActionSuccess = false)
            transactionRepository.updateTransaction(id, transaction)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, isActionSuccess = true)
                    loadTransactions()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = exception.message)
                }
        }
    }

    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isActionSuccess = false)
            transactionRepository.deleteTransaction(id)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, isActionSuccess = true)
                    loadTransactions()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = exception.message)
                }
        }
    }

    fun clearSelectedTransaction() {
        _uiState.value = _uiState.value.copy(selectedTransaction = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearActionSuccess() {
        _uiState.value = _uiState.value.copy(isActionSuccess = false)
    }
} 