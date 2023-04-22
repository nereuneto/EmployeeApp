package com.nlneto.employee

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nlneto.employee.core.EmployeeHandlerThread
import com.nlneto.employee.core.NetworkModule.employeeService
import com.nlneto.employee.core.Screen
import com.nlneto.employee.data.Employee

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(employeeService) as T
            }
        })[MainViewModel::class.java]
        setContent {
            Navigation()
        }
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        viewModel.getEmployees()
        val employeeHandlerThread = EmployeeHandlerThread(viewModel)
        employeeHandlerThread.start()
        return super.onCreateView(name, context, attrs)
    }

    @Composable
    fun Navigation() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Screen.mainActiviy.route) {
            composable(route = Screen.mainActiviy.route) {
                MakeMainScreen(navController)
            }
            composable(route = Screen.addEmployee.route) {
                AddEmployeeScreen(navController)
            }
            composable(
                route = "${Screen.employeeDetails.route}/{employeeId}",
                arguments = listOf(navArgument("employeeId") { type = NavType.IntType })
            ) { backStackEntry ->
                val employeeId = backStackEntry.arguments?.getInt("employeeId")
                EmployeeDetailsScreen(employeeId!!, viewModel, navController)
            }
        }
    }


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MakeMainScreen(navController: NavHostController) {
        val employees by viewModel.employeesData.observeAsState(emptyList())
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.app_name)) },
                    colors = TopAppBarDefaults.largeTopAppBarColors(),
                    actions = {
                        IconButton(onClick = { viewModel.getEmployees() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Atualizar")
                        }
                    },
                )
            },
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.addEmployee.route)
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar funcionário")
                }
            },
            content = {
                EmployeeList(
                    employeeList = employees,
                    navController
                )
            }
        )
    }

    @Composable
    fun EmployeeList(employeeList: List<Employee>, navController: NavHostController) {
        Column(Modifier.padding(top = 56.dp)) {
            LazyColumn(Modifier.fillMaxSize()) {
                items(employeeList) { employee ->
                    EmployeeCard(employee, navController)
                }
            }
        }
    }

    @Composable
    fun EmployeeCard(employee: Employee, navController: NavHostController) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    clip = true
                )
        ) {
            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = employee.nome,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = employee.sobrenome,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = employee.email,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "NIS: " + employee.nis.toString(),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(bottom = 8.dp, end = 10.dp)
                    )
                }
                IconButton(
                    onClick = {
                        viewModel.deleteEmployee(employee.id.toLong())
                        viewModel.getEmployees()
                    }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir funcionário")
                }
                IconButton(
                    onClick = {
                        navController.navigate(Screen.employeeDetails.route + "/${employee.id}") {
                            launchSingleTop = true
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Atualizar funcionário")
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddEmployeeScreen(navController: NavHostController) {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Adicionar funcionário") }) },
            content = { paddingValues ->
                FormularioEmployee(paddingValues = paddingValues)
            },
            bottomBar = {
                Button(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Voltar")
                }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FormularioEmployee(
        paddingValues: PaddingValues
    ) {
        val (nome, setNome) = remember { mutableStateOf("") }
        val (sobrenome, setSobrenome) = remember { mutableStateOf("") }
        val (email, setEmail) = remember { mutableStateOf("") }
        val (nis, setNis) = remember { mutableStateOf("") }

        Column(modifier = Modifier.padding(paddingValues)) {
            OutlinedTextField(
                value = nome,
                onValueChange = setNome,
                label = { Text("Nome") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = sobrenome,
                onValueChange = setSobrenome,
                label = { Text("Sobrenome") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = setEmail,
                label = { Text("E-mail") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = nis,
                onValueChange = setNis,
                label = { Text("NIS") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    if (nome.isEmpty()) {
                        Toast.makeText(
                            this@MainActivity,
                            "Nome não pode estar vazio!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (sobrenome.isEmpty()) {
                        Toast.makeText(
                            this@MainActivity,
                            "Sobrenome não pode estar vazio!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (email.isEmpty()) {
                        Toast.makeText(
                            this@MainActivity,
                            "E-mail não pode estar vazio!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (nis.isEmpty()) {
                        Toast.makeText(
                            this@MainActivity,
                            "NIS não pode estar vazio!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.addEmployee(this@MainActivity, nome, sobrenome, email, nis)
                        viewModel.getEmployees()
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Salvar")
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EmployeeDetailsScreen(
        employeeId: Int,
        viewModel: MainViewModel,
        navController: NavHostController
    ) {
        val nome = remember { mutableStateOf("") }
        val sobrenome = remember { mutableStateOf("") }
        val email = remember { mutableStateOf("") }
        val nis = remember { mutableStateOf("") }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = nome.value,
                onValueChange = { nome.value = it },
                label = { Text("Nome") },
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            OutlinedTextField(
                value = sobrenome.value,
                onValueChange = { sobrenome.value = it },
                label = { Text("Sobrenome") },
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") },
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            OutlinedTextField(
                value = nis.value,
                onValueChange = {
                    if (it.isNotBlank()) {
                        nis.value = it
                    }
                },
                label = { Text("Nis") },
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (nome.value.isEmpty()) {
                        Toast.makeText(
                            this@MainActivity,
                            "Nome não pode estar vazio!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (sobrenome.value.isEmpty()) {
                        Toast.makeText(
                            this@MainActivity,
                            "Sobrenome não pode estar vazio!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (email.value.isEmpty()) {
                        Toast.makeText(
                            this@MainActivity,
                            "E-mail não pode estar vazio!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (nis.value.isEmpty()) {
                        Toast.makeText(
                            this@MainActivity,
                            "NIS não pode estar vazio!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.updateEmployee(
                            this@MainActivity,
                            employeeId.toLong(),
                            nome.value,
                            sobrenome.value,
                            email.value,
                            nis.value
                        )
                        viewModel.getEmployees()
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.padding(16.dp),
            ) {
                Text(text = "Salvar")
            }
            Button(
                onClick = {
                    viewModel.getEmployees()
                    navController.popBackStack()
                },
                modifier = Modifier
                    .padding(16.dp),
            ) {
                Text(text = "Voltar")
            }
        }
    }
}


