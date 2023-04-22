package com.nlneto.employee.data

import com.google.gson.annotations.SerializedName

data class Employee(
    @SerializedName("id") val id : String,
    @SerializedName("nome") val nome : String,
    @SerializedName("sobrenome") val sobrenome: String,
    @SerializedName("email") val email: String,
    @SerializedName("nis") val nis: Long
)

data class EmployeeRequest(
    @SerializedName("nome") val nome : String,
    @SerializedName("sobrenome") val sobrenome: String,
    @SerializedName("email") val email: String,
    @SerializedName("nis") val nis: Long
)
