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
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

// --- MOSHI SETUP ---
val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// --- DATA MODEL ---
@JsonClass(generateAdapter = false)
data class Student(var name: String)

// --- MAIN ACTIVITY ---
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

// --- ROOT COMPOSABLE (NAVIGATION) ---
@Composable
fun App(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Home { listData ->
                // ✅ BONUS: Convert list to JSON using Moshi
                val listType = Types.newParameterizedType(List::class.java, Student::class.java)
                val adapter = moshi.adapter<List<Student>>(listType)
                val jsonString = adapter.toJson(listData)
                navController.navigate("resultContent/?listData=$jsonString")
            }
        }
        composable(
            "resultContent/?listData={listData}",
            arguments = listOf(navArgument("listData") { type = NavType.StringType })
        ) { backStackEntry ->
            val jsonString = backStackEntry.arguments?.getString("listData").orEmpty()
            ResultContent(jsonString)
        }
    }
}

// --- HOME SCREEN ---
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
            // ✅ ASSIGNMENT NO. 1: CEGAH SUBMIT KOSONG
            if (inputField.name.isNotBlank()) {
                listData.add(inputField)
                inputField = Student("")
            }
        },
        navigateFromHomeToResult = {
            navigateFromHomeToResult(listData.toList())
        }
    )
}

// --- HOME CONTENT (UI) ---
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

// --- RESULT SCREEN (BONUS: JSON + LAZYCOLUMN) ---
@Composable
fun ResultContent(jsonString: String) {
    // ✅ Parse JSON back to List<Student>
    val listType = Types.newParameterizedType(List::class.java, Student::class.java)
    val adapter = moshi.adapter<List<Student>>(listType)
    val studentList = try {
        adapter.fromJson(jsonString) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    // ✅ Display with LazyColumn (as per Bonus requirement)
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            OnBackgroundTitleText(text = "Submitted Names (from JSON):")
        }
        items(studentList) { student ->
            OnBackgroundItemText(text = student.name)
        }
    }
}

// --- PREVIEW (for development only) ---
@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    MAP_Lab_Week9Theme {
        Home { }
    }
}