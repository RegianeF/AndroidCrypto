package com.plcoding.androidcrypto

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.plcoding.androidcrypto.ui.CryptoManager2
import com.plcoding.androidcrypto.ui.theme.AndroidCryptoTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : ComponentActivity() {

    private val Context.dataStore by dataStore(
        fileName = "user-settings.json",
        serializer = UserSettingsSerializer(CryptoManager2())
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidCryptoTheme {

                Column {

                   // TestDoPhilip(CryptoManager2())
                    CryptoTest(dataStore)

                }

            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CryptoDeuBomComText() {
    var text by remember { mutableStateOf("") }

    var cipherText by remember { mutableStateOf("") }

    var plainText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Username") }
        )

        Spacer(modifier = Modifier.height(50.dp))


        Button(
            onClick = {
                cipherText = encrypt9(algorithm, text, key, iv)
            }
        ) {
            Text(text = "encrypt")
        }

        Text(text = "Encrypt: $cipherText")

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                plainText = decrypt9(algorithm, cipherText, key, iv)
            }
        ) {
            Text(text = "decrypt")
        }

        Text(text = "Decrypt: $plainText")

    }
}

@Composable
fun CryptoTest(dataStore: DataStore<UserSettings>) {
    var username by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var settings by remember {
        mutableStateOf(UserSettings())
    }
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        TextField(
            value = username,
            onValueChange = { username = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Username") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Password") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Button(onClick = {
                scope.launch {
                    dataStore.updateData {
                        UserSettings(
                            username = username,
                            password = password
                        )
                    }
                }
            }) {
                Text(text = "Save")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                scope.launch {
                    settings = dataStore.data.first()
                }
            }) {
                Text(text = "Load")
            }
        }
        Text(text = settings.toString())
    }
}

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun TestDoPhilip(cryptoManager2: CryptoManager2) {
    val context = LocalContext.current

    var messageToEncrypt by remember {
        mutableStateOf("")
    }
    var messageToDecrypt by remember {
        mutableStateOf("")
    }
    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {

        TextField(
            value = messageToEncrypt,
            onValueChange = { messageToEncrypt = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Encrypt string") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {

            Button(
                onClick = {
                val bytes = messageToEncrypt.encodeToByteArray()
                val file = File(context.filesDir, "secret.txt")
                if (!file.exists()) {
                    file.createNewFile()
                }
                val fos = FileOutputStream(file)

                cryptoManager2.encrypt(
                    bytes = bytes,
                    outputStream = fos
                )
            }) {
                Text(text = "Encrypt")
            }

            Spacer(modifier = Modifier.width(16.dp))


            Button(
                onClick = {
                val file = File(context.filesDir, "secret.txt")
                    messageToDecrypt = cryptoManager2.decrypt(
                    inputStream = FileInputStream(file)
                ).decodeToString()
            }) {
                Text(text = "Decrypt")
            }

        }


        Text(text = messageToDecrypt)
    }
}
