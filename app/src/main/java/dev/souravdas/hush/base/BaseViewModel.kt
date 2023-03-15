package dev.souravdas.hush.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseViewModel:ViewModel() {
    protected fun executedSuspendedCodeBlock(
        operationTag: String = String(),
        codeBlock: suspend () -> Any,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = codeBlock()
                onSuspendResponse(operationTag, data)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

    abstract fun onSuspendResponse(operationTag: String, resultResponse: Any)
}