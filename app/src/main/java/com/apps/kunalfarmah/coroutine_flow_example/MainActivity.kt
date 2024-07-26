package com.apps.kunalfarmah.coroutine_flow_example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apps.kunalfarmah.coroutine_flow_example.ui.theme.Coroutine_Flow_ExampleTheme

class MainActivity : ComponentActivity() {
    val vm: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Coroutine_Flow_ExampleTheme {
                val showToast = vm.toastFlow.collectAsState()
                val showSharedToast = vm.toastSharedFlow.collectAsState()


                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(onClick = { vm.collect() }) {
                        Text(text = "Collect")
                    }

                    Button(onClick = { vm.collectLatest() }) {
                        Text(text = "CollectLatest")
                    }

                    Button(onClick = { vm.combine() }) {
                        Text(text = "Combine")
                    }

                    Button(onClick = { vm.merge() }) {
                        Text(text = "Merge")
                    }

                    Button(onClick = { vm.zip() }) {
                        Text(text = "Zip")

                    }

                    Button(onClick = { vm.concat() }) {
                        Text(text = "Concat")

                    }

                    Button(onClick = { vm.testDelay1() }) {
                        Text(text = "Delay nested coroutine")

                    }

                    Button(onClick = { vm.testDelay2() }) {
                        Text(text = "Delay suspend function")

                    }

                    Button(onClick = { vm.testAsyncIncorrect() }) {
                        Text(text = "Test Async incorrectly")

                    }

                    Button(onClick = { vm.testAsyncAwaitCorrect() }) {
                        Text(text = "Test Async correctly")

                    }

                    Button(onClick = { vm.testAsyncAwaitAll() }) {
                        Text(text = "Test Async in list 1")

                    }

                    Button(onClick = { vm.testAsyncAwaitAllAlt() }) {
                        Text(text = "Test Async in list 2")

                    }
                    
                    Button(onClick = { vm.showToast()}) {
                        Text(text = "Show Toast")
                    }

                    Button(onClick = { vm.testTryCatchIncorrect()}) {
                        Text(text = "Try catch incorrect")
                    }

                    Button(onClick = { vm.testTryCatchCorrect()}) {
                        Text(text = "Try catch correct")
                    }

                    Button(onClick = { vm.exceptionHandler()}) {
                        Text(text = "Exception handler")
                    }

                    Button(onClick = { vm.cancellationParent()}) {
                        Text(text = "Cancel parent")
                    }

                    Button(onClick = { vm.cancellationChild()}) {
                        Text(text = "Cancel child")
                    }

                    Button(onClick = { vm.cancellationWithoutChecking()}) {
                        Text(text = "Cancel without checks")
                    }

                    Button(onClick = { vm.cancellationWithChecking()}) {
                        Text(text = "Cancel with checks")
                    }

                    showToast.let {
                        if (it.value) {
                            Toast.makeText(this@MainActivity, "Hello StateFlow", Toast.LENGTH_SHORT).show()
                        }
                    }

                    showSharedToast.let {
                        if (it.value) {
                            Toast.makeText(this@MainActivity, "Hello SharedFlow", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}