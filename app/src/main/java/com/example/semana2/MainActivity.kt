package com.example.semana2

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainApp()
        }
    }
}


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
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().getReference("users")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )

    Column(
        modifier = Modifier
            .padding(50.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.clickable { },
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 40.sp,
            text = "Crear cuenta",
            maxLines = 1
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
                if (email.isNotEmpty() && password.length >= 6 && username.isNotEmpty()) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid
                                if (userId != null) {
                                    val user = User(username, email, password)
                                    database.child(userId).setValue(user)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show()
                                            navController.navigate("login")
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(context, "Error al guardar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            } else {
                                Toast.makeText(context, "Error: ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp)
                .semantics {
                    contentDescription = "Botón para crear cuenta"
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
data class User(val nombre: String, val email: String, val contrasena: String)

@Composable
fun Login(navController: NavHostController) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.bus))
    val context = LocalContext.current
    var email by remember { mutableStateOf("test@test.com") }
    var password by remember { mutableStateOf("123456") }
    val logo: Painter = painterResource(id = R.drawable.bus)
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().getReference("users")

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )


    LaunchedEffect(Unit) {
        Toast.makeText(
            context,
            "USER PRUEBA test@test.com 123456",
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
                if (email.isNotEmpty() && password.isNotEmpty()){
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid
                                if (userId != null) {
                                    database.child(userId).get().addOnSuccessListener { snapshot ->
                                        if (snapshot.exists()) {
                                            val nombreUsuario = snapshot.child("nombre").value.toString()
                                            guardarDataUsuario(context, userId, email, nombreUsuario)
                                            Toast.makeText(context, "Bienvenido, $nombreUsuario", Toast.LENGTH_SHORT).show()
                                            navController.navigate("dashboard")
                                        } else {
                                            Toast.makeText(context, "Usuario o contraseña invalido", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Correo o contraseña incorrecta", Toast.LENGTH_SHORT).show()
                            }
                        }
                }else{
                    Toast.makeText(context, "Debe ingresar datos", Toast.LENGTH_SHORT).show()
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

    var error by remember { mutableStateOf<String?>(null) }
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Paradas Cercanas","Escribir","Hablar","Mi Perfil")
    val selectedIcons = listOf(Icons.Filled.LocationOn,Icons.Filled.PlayArrow,Icons.Filled.Edit,Icons.Filled.AccountCircle)
    val unselectedIcons = listOf(Icons.Outlined.LocationOn,Icons.Filled.PlayArrow,Icons.Filled.Edit, Icons.Outlined.AccountCircle)

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
                    ParadasCercanas()
                }
                1 -> {
                    STT()
                }
                2->{
                    TTS()
                }
                3->{
                    MiPerfil()
                }
            }
        }
    }
}

@Composable
fun rememberTextToSpeech(): MutableState<TextToSpeech?> {
    val context = LocalContext.current
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(context) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.value?.language = Locale("es","MX")
            }
        }
        tts.value = textToSpeech

        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }
    return tts
}

@Composable
fun TTS() {
    val tts = rememberTextToSpeech()
    var sentenceHistory by remember { mutableStateOf(listOf<String>()) }
    var textInput by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val database = FirebaseDatabase.getInstance().getReference("users").child(userId ?: "").child("frasesTTS")

    LaunchedEffect(userId) {

        if (userId != null) {
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val phrases = snapshot.children.mapNotNull { it.getValue(String::class.java) }
                    sentenceHistory = phrases
                    Log.d("FirebaseTTS", "Frase agregada: $phrases")
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error al cargar frases", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    Scaffold(
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        label = { Text("Escribe una frase") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (textInput.text.isNotBlank()) {
                                sentenceHistory = sentenceHistory + textInput.text
                                speakText(tts, textInput.text)
                                guardarFraseTTSUsuario(context,textInput.text )
                                textInput = TextFieldValue("")

                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reproducir \uD83D\uDD0A")
                    }
                }
            }
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    items(sentenceHistory) { sentence ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(sentence, modifier = Modifier.weight(1f))

                            Button(onClick = {
                                speakText(tts, sentence)
                            }) {
                                Text("🔊")
                            }
                        }
                    }
                }
    }

}

fun speakText(tts: MutableState<TextToSpeech?>, text: String) {
    tts.value?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
}

@Composable
fun STT() {
    PermisoMicrofono()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val context = LocalContext.current
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }

    var currentText by remember { mutableStateOf("") }
    var frases by remember { mutableStateOf<List<String>>(emptyList()) }
    var isListening by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val database = FirebaseDatabase.getInstance().getReference("users").child(userId ?: "").child("frasesSTT")

    LaunchedEffect(userId) {
        if (userId != null) {
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val frasesCargadas = snapshot.children.mapNotNull { it.getValue(String::class.java) }
                    frases = frasesCargadas
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error al cargar frases", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
    val recognitionListener = object : RecognitionListener {
        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val recognizedText = matches?.firstOrNull() ?: "Error al reconocer voz"

            currentText = recognizedText
            frases = frases + recognizedText
            isListening = false
            coroutineScope.launch {
                listState.animateScrollToItem(frases.size)
            }
            guardarFraseSTTUsuario(context, currentText)
        }

        override fun onError(error: Int) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            isListening = false
        }

        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {
            Toast.makeText(context, "Escuchando..", Toast.LENGTH_SHORT).show()
        }
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
        override fun onPartialResults(partialResults: Bundle?) {}
    }

    speechRecognizer.setRecognitionListener(recognitionListener)

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-MX")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "es-MX")
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "es-MX")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Hablar")
        }

        speechRecognizer.startListening(intent)
        isListening = true
    }

    Scaffold(
        bottomBar = {
            Button(
                onClick = { startListening() },
                enabled = !isListening,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    if (isListening){
                        "Escuchando..."
                    }else{
                        "Escuchar \uD83C\uDFA4"
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Última frase",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = currentText,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Historial",
                style = MaterialTheme.typography.titleMedium
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                items(frases) { frase ->
                    Text(text = "- $frase", modifier = Modifier.padding(4.dp))
                }
            }
        }
    }
}

@Composable
fun PermisoMicrofono() {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Se requiere permiso de micrófono", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch(android.Manifest.permission.RECORD_AUDIO)
    }
}

@Composable
fun ParadasCercanas(){
    val logo: Painter = painterResource(id = R.drawable.bus)
    val context = LocalContext.current
    var location by remember { mutableStateOf<Location?>(null) }
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    data class Bus(val id: String, val llegada: Int, var ultimo_update: Int)
    val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("horario_buses")

    //Actualizar los datos al iniciar, simula un servicio que actualiza los datos
    val busesOrdenaditos = listOf(
        Bus("JUGX35", Random.nextInt(1, 50), 1),
        Bus("JUGX36", Random.nextInt(1, 50), 1),
        Bus("JUGX37", Random.nextInt(1, 50), 1),
        Bus("JUGX38", Random.nextInt(1, 50), 1)
    ).sortedBy { it.llegada }


    busesOrdenaditos.forEach { bus ->
        database.child(bus.id).setValue(bus)
            .addOnSuccessListener {
                println("Actualizado en firebase")
            }
            .addOnFailureListener {
                println("Error")
            }
    }
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

        itemsIndexed(busesOrdenaditos) {index, bus ->
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

@Composable
fun MiPerfil(){
    val logo: Painter = painterResource(id = R.drawable.bus)
    val context = LocalContext.current
    val userData = getDataUsuario(context)
    val emailUserData = userData["email"].toString()
    val usernameUserData = userData["nombreUsuario"].toString()

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

            value = usernameUserData,
            onValueChange = {},
            label = { Text("Nombre") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Campo nombre de usuario de su cuenta"
                },
        )

        OutlinedTextField(

            value = emailUserData,
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


    }
}

fun guardarDataUsuario(context: Context, userId: String, email: String, nombreUsuario: String) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("dataUsuario", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("userId", userId)
    editor.putString("email", email)
    editor.putString("nombreUsuario", nombreUsuario)
    editor.putBoolean("isLoggedIn", true)
    editor.apply()
}

fun getDataUsuario(context: Context): Map<String, String?> {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("dataUsuario", Context.MODE_PRIVATE)
    return mapOf(
        "userId" to sharedPreferences.getString("userId", null),
        "email" to sharedPreferences.getString("email", null),
        "nombreUsuario" to sharedPreferences.getString("nombreUsuario", null),
        "isLoggedIn" to sharedPreferences.getBoolean("isLoggedIn", false).toString()
    )
}

fun guardarFraseTTSUsuario(context: Context, phrase: String) {
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    if (userId != null) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("frasesTTS")
        val phraseKey = database.push().key

        if (phraseKey != null) {
            database.child(phraseKey).setValue(phrase)
                .addOnFailureListener {
                    Toast.makeText(context, "Error al guardar la frase", Toast.LENGTH_SHORT).show()
                }
        }
    } else {
        Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
    }
}

fun guardarFraseSTTUsuario(context: Context, phrase: String) {
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    if (userId != null) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("frasesSTT")
        val phraseKey = database.push().key

        if (phraseKey != null) {
            database.child(phraseKey).setValue(phrase)
                .addOnFailureListener {
                    Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                }
        }
    } else {
        Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
    }
}



