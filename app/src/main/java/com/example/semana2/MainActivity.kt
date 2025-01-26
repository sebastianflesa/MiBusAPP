package com.example.semana2

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationServices
import com.airbnb.lottie.compose.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainApp()
        }
    }
}

class Usuario(
    val email: String,
    val password: String,
    val username: String
)

class Bus(
    val id: String,
    val llegada: String
)

fun verificarCampos(vararg campos: String, accion: () -> Unit) {
    if (campos.all { it.isNotEmpty() }) {
        accion()
    } else {
        throw IllegalArgumentException("Campos vacios")
    }
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

val usuarios = mutableListOf(
    Usuario("test1@test.com", "123456", "TestUser1"),
    Usuario("test2@test.com", "123456", "TestUser2"),
    Usuario("test3@test.com", "123456", "TestUser3"),
)


@Composable
@Preview(showBackground = true)
fun MainApp(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { Login(navController) }
        composable("dashboard") { Principal(navController) }
        composable("registro") { Registro(navController) }
    }
}

@Composable
fun Registro(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    val logo: Painter = painterResource(id = R.drawable.bus)
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )

    Column(
        modifier = Modifier
            .padding(50.dp)
            .fillMaxSize(),
        horizontalAlignment  = Alignment.CenterHorizontally,
        verticalArrangement  = Arrangement.Center
    ){
        Text(
            modifier = Modifier.clickable {
            },
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 40.sp,
            text = "Crear cuenta",
            maxLines = 1
        )

        Image(
            painter = logo,
            contentDescription = "Bus Logo",
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Campo para ingresar su nombre"
                },
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Campo para ingresar el email de su cuenta"
                },
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Campo para ingresar la contraseña de su cuenta"
                },
        )

        Button(
            onClick = {
                try {
                    verificarCampos(email, password, username){
                        usuarios.plus(Pair(email, password))
                        context.showToast("Cuenta creada con éxito")
                        navController.navigate("login")
                    }
                } catch (e: Exception) {
                    context.showToast("Error inesperado: ${e.message}")
                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp)
                .semantics {
                    contentDescription = "Boton para crear cuenta"
                }
        ) {
            Text("Registrarse")
        }

        Text(
            modifier = Modifier.clickable {
                navController.navigate("login")
            },
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            text = "Ingresar",
            maxLines = 1
        )





    }
}


@Composable
fun Login(navController: NavHostController) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.bus))
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val logo: Painter = painterResource(id = R.drawable.bus)

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )


    LaunchedEffect(Unit) {
        Toast.makeText(
            context,
            "USER PRUEBA test1@test.com 123456",
            Toast.LENGTH_SHORT
        ).show()
    }
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.fillMaxWidth().height(300.dp)
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )

    Column(
        modifier = Modifier
            .padding(50.dp)
            .fillMaxSize()
            .offset(y = 50.dp),
        horizontalAlignment  = Alignment.CenterHorizontally,
        verticalArrangement  = Arrangement.Center
    ){



        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Campo para ingresar el email de su cuenta"
                },
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Campo para ingresar la contraseña de su cuenta"
                },
        )

        Button(
            onClick = {
                //navController.navigate("dashboard")
                val usuarioValido = usuarios.any { usuario ->
                    usuario.email == email && usuario.password == password
                }
                if (usuarioValido) {
                    navController.navigate("dashboard")
                } else {
                    Toast.makeText(
                        context,
                        "Correo o contraseña incorrecta",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp)
        ) {
            Text("Ingresar")
        }

        Text(
            modifier = Modifier.clickable {
                navController.navigate("registro")
            },
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            text = "Registrarse",
            maxLines = 1
        )


    }
}

@Composable
fun Principal(navController: NavHostController) {
    val context = LocalContext.current
    var location by remember { mutableStateOf<Location?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val logo: Painter = painterResource(id = R.drawable.bus)
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Paradas Cercanas", "Mi Perfil")
    val selectedIcons = listOf(Icons.Filled.LocationOn, Icons.Filled.AccountCircle)
    val unselectedIcons = listOf(Icons.Outlined.LocationOn, Icons.Outlined.AccountCircle)
    data class Bus(val id: String, val llegada: String)
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener{ loc ->
                loc.also { it.also { location = it } }
            }
        }

    }
    val buses = listOf(
        Bus("BUS 1", "10"),
        Bus("BUS 2", "20"),
        Bus("BUS 3", "25"),
        Bus("BUS 4", "30")
    )


    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                                contentDescription = item,
                                modifier = Modifier
                                    .size(40.dp)

                            )
                        },
                        label = { Text(item, fontSize = 15.sp) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (selectedItem) {
                0 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        item {
                            Text(
                                text = "Paradas Cercanas: Dirección Las Cabras",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(16.dp)
                            )
                            location?.let {
                                Text("Latitud: ${it.latitude}")
                                Text("Longitud: ${it.longitude}")
                            }

                        }

                        itemsIndexed(buses) {index, bus ->
                            var textoDescriptivo = "El ${bus.id}, llega en ${bus.llegada} minutos"
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)

                            ) {
                               Row(
                                   modifier = Modifier
                                       .fillMaxWidth()
                                       .padding(16.dp),
                                   verticalAlignment = Alignment.CenterVertically,
                                   horizontalArrangement = Arrangement.SpaceBetween
                               ){
                                   Row(
                                       verticalAlignment = Alignment.CenterVertically
                                   ) {
                                       Image(
                                           painter = logo,
                                           contentDescription = null,
                                           modifier = Modifier
                                               .size(70.dp)
                                               .padding(bottom = 16.dp)
                                       )
                                       Spacer(modifier = Modifier.width(16.dp))
                                       Text(
                                           text = "${bus.id} en ",
                                           style = MaterialTheme.typography.titleLarge
                                       )
                                       Text(
                                           modifier = Modifier.semantics {
                                               contentDescription = textoDescriptivo
                                           },
                                           text = "${bus.llegada} Minutos",
                                           style = MaterialTheme.typography.titleLarge.copy(
                                               fontWeight = FontWeight.Bold
                                           )


                                       )
                                   }

                               }

                            }
                        }
                    }
                }
                1 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )

                    Column(
                        modifier = Modifier
                            .padding(50.dp)
                            .fillMaxSize(),
                        horizontalAlignment  = Alignment.CenterHorizontally,
                        verticalArrangement  = Arrangement.Center
                    ){

                        Image(
                            painter = logo,
                            contentDescription = "Bus Logo",
                            modifier = Modifier
                                .size(100.dp)
                                .padding(bottom = 16.dp)
                        )

                        OutlinedTextField(

                            value = "email",
                            onValueChange = {},
                            label = { Text("Email") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics {
                                    contentDescription = "Campo email de su cuenta"
                                },
                        )

                        OutlinedTextField(
                            value = "******",
                            onValueChange = {  },
                            label = { Text("Contraseña") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics {
                                    contentDescription = "Campo contraseña de su cuenta"
                                },
                        )

                    }
                }
            }
        }
    }
}


