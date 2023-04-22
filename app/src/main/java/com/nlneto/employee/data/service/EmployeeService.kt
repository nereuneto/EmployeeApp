package com.nlneto.employee.data.service

import com.nlneto.employee.data.Employee
import com.nlneto.employee.data.EmployeeRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET;
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface EmployeeService {
    @GET("employee")
    suspend fun getEmployees(): Response<List<Employee>>

    @POST("employee")
    suspend fun createEmployee(@Body employee: EmployeeRequest): Response<Employee>

    @PUT("employee/{id}")
    suspend fun updateEmployee(@Path("id") id: Long, @Body employee: EmployeeRequest): Response<Employee>

    @DELETE("employee/{id}")
    suspend fun deleteEmployee(@Path("id") id: Long)
}