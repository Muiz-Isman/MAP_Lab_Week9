package com.example.map_lab_week9

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.map_lab_week9.ui.theme.MAP_Lab_Week9Theme
import com.example.map_lab_week9.ui.theme.OnBackgroundTitleText
import com.example.map_lab_week9.ui.theme.OnBackgroundItemText
import com.example.map_lab_week9.ui.theme.PrimaryTextButton

// Data model
data class Student(var name: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MAP_Lab_Week9Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    App(navController)
                }
            }
        }
    }
}

// Root composable untuk navigasi
@Composable
fun App(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Home { listData ->
                // Konversi list ke string (seperti di modul halaman 18)
                val listString = listData.joinToString(", ")
                navController.navigate("resultContent/?listData=$listString")
            }
        }
        composable(
            "resultContent/?listData={listData}",
            arguments = listOf(navArgument("listData") { type = NavType.StringType })
        ) { backStackEntry ->
            val listData = backStackEntry.arguments?.getString("listData").orEmpty()
            ResultContent(listData)
        }
    }
}

// Home menerima fungsi navigasi
@Composable
fun Home(navigateFromHomeToResult: (List<Student>) -> Unit) {
    val listData = remember { mutableStateListOf(
        Student("Tanu"),
        Student("Tina"),
        Student("Tono")
    )}
    var inputField by remember { mutableStateOf(Student("")) }

    HomeContent(
        listData = listData,
        inputField = inputField,
        onInputValueChange = { input ->
            inputField = inputField.copy(name = input)
        },
        onButtonClick = {
            // ✅ ASSIGNMENT: CEGAH SUBMIT KOSONG
            if (inputField.name.isNotBlank()) {
                listData.add(inputField)
                inputField = Student("")
            }
        },
        navigateFromHomeToResult = {
            // Kirim seluruh list ke result
            navigateFromHomeToResult(listData.toList())
        }
    )
}

// HomeContent dengan tombol "Finish"
@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>,
    inputField: Student,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    navigateFromHomeToResult: () -> Unit
) {
    LazyColumn {
        item {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnBackgroundTitleText(text = stringResource(id = R.string.enter_item))

                TextField(
                    value = inputField.name,
                    onValueChange = onInputValueChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Row {
                    PrimaryTextButton(text = stringResource(id = R.string.button_click)) {
                        onButtonClick()
                    }
                    PrimaryTextButton(text = stringResource(id = R.string.button_navigate)) {
                        navigateFromHomeToResult()
                    }
                }
            }
        }
        items(listData) { item ->
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnBackgroundItemText(text = item.name)
            }
        }
    }
}

// Halaman hasil
@Composable
fun ResultContent(listData: String) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnBackgroundItemText(text = "Submitted Names:")
        OnBackgroundItemText(text = listData)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    MAP_Lab_Week9Theme {
        Home { }
    }
}