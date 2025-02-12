package com.apps.kunalfarmah.coroutine_flow_example

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CancellationException

class MainViewModel: ViewModel() {

    val TAG = "MainViewModel"
    var toastFlow = MutableStateFlow<Boolean>(false)
    var toastSharedFlow = MutableStateFlow<Boolean>(false)

    var flow1 = flow {
        for (i in 1..5) {
            delay(1000)
            Log.e(TAG, "inside flow1 emitted: $i")
            emit(i)
        }
    }


    var flow2 = flow {
        for (i in 10..15) {
            delay(500)
            Log.e(TAG, "inside flow2 emitted: $i")
            emit(i)
        }
    }

    var flow3 = flow {
        for (i in 20..25) {
            delay(1200)
            Log.e(TAG, "inside flow3 emitted: $i")
            emit(i)
        }
    }

        fun collect(){
            viewModelScope.launch {
                flow1.collect{
                    Log.e(TAG, "flow1 emitted: $it")
                }
                flow2.collect{
                    Log.e(TAG, "flow2 emitted: $it")
                }
            }

        }

    fun collectLatest(){
        viewModelScope.launch {
            flow1.collectLatest{
                delay(1500)
                Log.e(TAG, "flow1 emitted: $it")
            }
        }

    }

        fun combine(){
            viewModelScope.launch {
                flow1.combine(flow2){ a, b ->
                    "$a, $b"
                }.combine(flow3) { a, b ->
                    "$a , $b"
                }.collect{
                    Log.e(TAG, "combined flows emitted: $it")
                }
            }
        }

        fun zip(){
            viewModelScope.launch {
                flow1.zip(flow2){ a, b ->
                    "$a, $b"
                }.collect{
                    Log.e(TAG, "zipped flows emitted: $it")
                }
            }
        }

        fun merge(){
            viewModelScope.launch {
                merge(flow1, flow2, flow3).collect{
                    Log.e(TAG, "merged flows emitted: $it")
                }
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun concat(){
        viewModelScope.launch {
            flow1.flatMapConcat { a ->
                flow2.flatMapConcat { b ->
                    flow3.flatMapConcat { c ->
                        flow {
                            emit("$a, $b, $c")
                        }
                    }
                }
            }.collect {
                Log.e(TAG, "concatenated flows emitted: $it")
            }
        }
    }

    suspend fun delay1(){
        val startTime = System.currentTimeMillis()
        delay(5000)
        Log.e(TAG, "c1 delayed by 5 seconds")
        viewModelScope.launch{
            delay(3000)
            Log.e(TAG, "c2 delayed by 3 seconds")
        }
        viewModelScope.launch{
            delay(4000)
            Log.e(TAG, "c3 delayed by 4 seconds")
        }
        Log.e(TAG, "c1 ended after: ${(System.currentTimeMillis() - startTime)/1000} s")
    }

    fun testDelay1(){
        viewModelScope.launch {
            delay1()
        }
    }

    suspend fun delay2_1(){
        delay(5000)
        Log.e(TAG, "suspend delayed by 5 seconds")
        viewModelScope.launch{
            delay(2000)
            Log.e(TAG, "suspend c3 delayed by 2 seconds")
        }
    }

    suspend fun delay2_2(){
        delay(3000)
        Log.e(TAG, "suspend delayed by 3 seconds")
    }

    fun testDelay2(){
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            delay(5000)
            Log.e(TAG, "c1 delayed by 5 seconds")
            delay2_1()
            delay2_2()
            Log.e(TAG, "c1 ended after: ${(System.currentTimeMillis() - startTime)/1000} s")
        }
    }

    // this is incorrect
    fun testAsyncIncorrect() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val value1 = async {
                delay(2000)
                2
            }.await()
            val value2 = async {
                delay(1500)
                4
            }.await()
            val value3 = async {
                delay(4000)
                3
            }.await()
            Log.e(
                TAG,
                "value1: $value1, value2: $value2, value3: $value3 received after ${(System.currentTimeMillis() - startTime) / 1000} s"
            )
        }
    }

    fun testAsyncAwaitCorrect() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val value1 = async {
                delay(2000)
                2
            }
            val value2 = async {
                delay(1500)
                4
            }
            val value3 = async {
                delay(4000)
                3
            }
            Log.e(
                TAG,
                "value1: ${value1.await()}, value2: ${value2.await()}, value3: ${value3.await()} received after ${(System.currentTimeMillis() - startTime) / 1000} s"
            )
        }
    }

        fun testAsyncAwaitAll(){
            viewModelScope.launch {
                val startTime = System.currentTimeMillis()
                val values = listOf(
                    async{
                        delay(2000)
                        2
                    },
                    async{
                        delay(1500)
                        4
                    },
                    async{
                        delay(4000)
                        3
                    }
                ).awaitAll()
                Log.e(TAG, "values: $values received after ${(System.currentTimeMillis() - startTime)/1000} s")
            }
        }

        fun testAsyncAwaitAllAlt(){
            viewModelScope.launch {
                val startTime = System.currentTimeMillis()
                val list = mutableListOf<Deferred<Int>>()
                list.add(
                    async{
                        delay(2000)
                        2
                    })
                list.add(async{
                        delay(1500)
                        4
                    })
                list.add(
                    async{
                        delay(4000)
                        3
                    }
                )
                Log.e(TAG, "values: ${list.awaitAll()} received after ${(System.currentTimeMillis() - startTime)/1000} s")
            }
        }

    suspend fun getValue1(): Int{
        delay(2000)
        return 2
    }

    suspend fun getValue2(): Int{
        delay(3000)
        return 3
    }

    fun showToast(){
        viewModelScope.launch {
            toastFlow.value = true
            toastSharedFlow.value = true
        }
    }

    fun testTryCatchIncorrect(){
        viewModelScope.launch {
            try{
                launch{
                    throw (Exception("error"))
                }
            }
            catch (e: Exception){
                Log.e(TAG, "caught exception: $e")
            }
        }
    }

    fun testTryCatchCorrect() {
        viewModelScope.launch {
            launch {
                try {
                    throw (Exception("error"))
                } catch (e: Exception) {
                    Log.e(TAG, "caught exception: $e")
                }
            }
        }

    }

    val handler =  CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "caught exception: $throwable")
    }

    fun exceptionHandler() {
        viewModelScope.launch(handler) {
            Log.e(TAG, "inside first launch")
            launch(Dispatchers.IO) {
                Log.e(TAG, "inside second launch")
                launch {
                    Log.e(TAG, "inside third launch")
                    throw (Exception("error"))
                    Log.e(TAG, "finished third launch")
                }
                delay(1000)
                Log.e(TAG, "finished second launch")
            }
            delay(2000)
            Log.e(TAG, "finished first launch")
        }

    }

    fun cancellationParent(){
        val job1 = viewModelScope.launch(handler) {
            Log.e(TAG, "inside job1")
            val job2 = launch{
                Log.e(TAG, "inside job2")
                val job3 = launch {
                    Log.e(TAG, "inside job3")
                    delay(5000)
                    Log.e(TAG, "finished job3")
                }
                delay(4000)
                Log.e(TAG, "finished job2")
            }
            delay(1000)
            Log.e(TAG, "finshed job1")
        }
        viewModelScope.launch {
            delay(2000)
            Log.e(TAG, "cancelling job1")
            job1.cancelAndJoin()
        }
    }

    fun cancellationChild(){

        var job1: Job? = null
        var job2: Job? = null
        var job3: Job? = null

        job1 = viewModelScope.launch(handler) {
            Log.e(TAG, "inside job1")
            job2 = launch{
                Log.e(TAG, "inside job2")
                job3 = launch {
                    Log.e(TAG, "inside job3")
                    delay(4000)
                    Log.e(TAG, "finished job3")
                }
                delay(5000)
                Log.e(TAG, "finished job2")
            }
            delay(1000)
            Log.e(TAG, "finshed job1")
        }

        viewModelScope.launch {
            delay(4000)
            Log.e(TAG, "cancelling job2")
            job2?.cancel()
        }
    }

    fun cancellationWithoutChecking() {
        val startTime = System.currentTimeMillis()

            val job = viewModelScope.launch(handler) {
                var nextPrintTime = startTime
                var i = 0
                while (i < 10) {
                    // print a message twice a second
                    if (System.currentTimeMillis() >= nextPrintTime) {
                        Log.e(TAG, "Hello ${i++}")
                        nextPrintTime += 500L
                    }
                }

            delay(1000)
            Log.e(TAG, "cancelling job")
            throw CancellationException("job cancelled")
        }
    }

    fun cancellationWithChecking() {
        val startTime = System.currentTimeMillis()
        viewModelScope.launch(handler) {
            var nextPrintTime = startTime
            var i = 0
            while (i < 10) {
                ensureActive()
                // print a message twice a second
                if (System.currentTimeMillis() >= nextPrintTime) {
                    Log.e(TAG, "Hello ${i++}")
                    nextPrintTime += 500L
                }
            }

            delay(1000)
            Log.e(TAG, "cancelling job")
            throw CancellationException("job1 Cancelled")
        }
    }


}