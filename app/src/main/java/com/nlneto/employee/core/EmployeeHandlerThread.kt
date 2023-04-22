package com.nlneto.employee.core

import android.os.Handler
import android.os.HandlerThread
import com.nlneto.employee.MainViewModel

class EmployeeHandlerThread(viewModel: MainViewModel) : HandlerThread("EmployeeHandlerThread") {
    private lateinit var handler: Handler
    private var isRunning = false
    private val mainViewModel = viewModel

    override fun onLooperPrepared() {
        super.onLooperPrepared()
        handler = Handler(looper)
        isRunning = true
        startFetching()
    }

    private fun startFetching() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isRunning) {
                    mainViewModel.getEmployees()
                    handler.postDelayed(this, 5000)
                }
            }
        }, 5000)
    }

    fun stopFetching() {
        isRunning = false
        handler.removeCallbacksAndMessages(null)
    }
}