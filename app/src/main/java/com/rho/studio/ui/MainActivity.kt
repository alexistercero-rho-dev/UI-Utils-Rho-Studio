package com.rho.studio.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity

import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import com.rho.studio.ui.databinding.ActivityMainBinding
import com.rho.studio.ui.viewmodel.LoginViewModel

@BindingAdapter("toastMessage")
fun runMe(view: View, message: String?) { // Added ? for null safety
    if (!message.isNullOrEmpty()) {      // Checked for null/empty
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
    }
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ViewModel updates the Model after observing changes in the View
        // Model will also update the View via the ViewModel
        val activityMainBinding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        activityMainBinding.loginViewModel = LoginViewModel()
        activityMainBinding.executePendingBindings()
    }
}

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    UITheme {
//        Greeting("Android")
//    }
//}