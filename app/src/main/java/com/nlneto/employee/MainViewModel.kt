package com.nlneto.employee

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.nlneto.employee.data.Employee
import com.nlneto.employee.data.EmployeeRequest
import com.nlneto.employee.data.ErrorResponse
import com.nlneto.employee.data.service.EmployeeService
import kotlinx.coroutines.launch

class MainViewModel(private val service: EmployeeService) : ViewModel() {
    private val _employeesData = MutableLiveData<List<Employee>>()
    val employeesData: LiveData<List<Employee>>
        get() = _employeesData

    fun getEmployees() {
        viewModelScope.launch {
            try {
                val response = service.getEmployees()
                if (response.isSuccessful) {
                    _employeesData.value = response.body()
                }
            } catch (e: Exception) {
                Log.d("Service Error!", e.toString())
            }
        }
    }

    fun addEmployee(context: Context, nome: String, sobrenome: String, email: String, nis: String) {
        val newEmployee =
            EmployeeRequest(nome = nome, sobrenome = sobrenome, email = email, nis = nis.toLong())

        viewModelScope.launch {
            try {
                val response = service.createEmployee(newEmployee)
                if (!response.isSuccessful) {
                    Toast.makeText(
                        context,
                        "Ocorreu erro ao processar dados, verifique se o e-mail ou nis estão digitados corretamente",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Log.d("Service Error!", e.toString())
            }
        }
    }

    fun updateEmployee(
        context: Context,
        id: Long,
        nome: String,
        sobrenome: String,
        email: String,
        nis: String
    ) {
        viewModelScope.launch {
            try {
                val newEmployee =
                    EmployeeRequest(
                        nome = nome,
                        sobrenome = sobrenome,
                        email = email,
                        nis = nis.toLong()
                    )
                val response = service.updateEmployee(id, newEmployee)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Funcionário atualizado!", Toast.LENGTH_SHORT).show()
                } else {
                    val errorResponse = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        ErrorResponse::class.java
                    )
                    Toast.makeText(
                        context,
                        "Ocorreu um erro: " + errorResponse.errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.d("Service Error!", e.toString())
            }
        }
    }

    fun deleteEmployee(id: Long) {
        viewModelScope.launch {
            try {
                service.deleteEmployee(id)
            } catch (e: Exception) {
                Log.d("Service Error!", e.toString())
            }
        }
    }

}