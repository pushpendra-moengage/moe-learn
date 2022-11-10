package com.example.ps_news.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * This class is providing us with executors, so that we don;t have to recreate threads again and again for background work
 *  - Network and DiskIO, so whenever we want to swithc from one thread to another we can directly use this class from anywhere
 *  in App
 */
class AppExecutors(
    private val diskIO: Executor,
    private val networkIO: Executor,
    private val mainThread: Executor
) {

    constructor() : this(
        Executors.newFixedThreadPool(1),
        Executors.newFixedThreadPool(3),
        MainThreadExecutor()
    )

    fun diskIO(): Executor = diskIO
    fun networkIO(): Executor = networkIO
    fun mainThread(): Executor = mainThread

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }

    }
}