package com.nlneto.employee.core

sealed class Screen(val route: String) {
    object mainActiviy : Screen("mainActivity")
    object addEmployee : Screen("addEmployee")
    object employeeDetails: Screen("employeeDetails")
}