package com.alphavpn.admin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase

data class VpnConfig(
    val id: String = "",
    val name: String = "",
    val serverIp: String = "",
    val host: String = "",
    val payload: String = ""
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AdminScreen(this)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(activity: ComponentActivity) {
    var serverName by remember { mutableStateOf("") }
    var serverIp by remember { mutableStateOf("") }
    var host by remember { mutableStateOf("") }
    var payload by remember { mutableStateOf("") }

    val database = FirebaseDatabase.getInstance("https://alphavpn-847aa-default-rtdb.firebaseio.com/").getReference("servers")

    Scaffold(
        topBar = { TopAppBar(title = { Text("AlphaVPN Admin Panel") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Add VPN Server Configuration", style = MaterialTheme.typography.titleMedium)
            
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = serverName, onValueChange = { serverName = it }, label = { Text("Server Name (e.g. Germany 01)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = serverIp, onValueChange = { serverIp = it }, label = { Text("Server IP / SNI") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = host, onValueChange = { host = it }, label = { Text("Bug Host") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = payload, onValueChange = { payload = it }, label = { Text("Payload") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (serverName.isNotEmpty() && serverIp.isNotEmpty()) {
                        val id = database.push().key ?: System.currentTimeMillis().toString()
                        val config = VpnConfig(id, serverName, serverIp, host, payload)
                        database.child(id).setValue(config).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(activity, "Server saved successfully!", Toast.LENGTH_SHORT).show()
                                serverName = ""
                                serverIp = ""
                                host = ""
                                payload = ""
                            } else {
                                Toast.makeText(activity, "Failed to save server!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(activity, "Please fill Server Name and IP", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Server to Firebase")
            }
        }
    }
}
