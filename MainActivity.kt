package com.fixtrack.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.fixtrack.app.data.FixTrackDatabase
import com.fixtrack.app.data.Product
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

private val Navy = Color(0xFF102A56)
private val Blue = Color(0xFF2F67D8)
private val PaleBlue = Color(0xFFEAF1FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dao = FixTrackDatabase.get(applicationContext).productDao()
        setContent {
            val products by dao.observeAll().collectAsState(initial = emptyList())
            FixTrackTheme {
                FixTrackHome(
                    products = products,
                    onAdd = { product -> lifecycleScope.launch { dao.insert(product) } },
                    onDelete = { product -> lifecycleScope.launch { dao.delete(product) } }
                )
            }
        }
    }
}

@Composable
private fun FixTrackTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(primary = Navy, secondary = Blue, background = Color(0xFFF6F8FC), surface = Color.White),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FixTrackHome(
    products: List<Product>,
    onAdd: (Product) -> Unit,
    onDelete: (Product) -> Unit
) {
    var showAdd by remember { mutableStateOf(false) }
    val totalValue = products.sumOf { it.purchasePricePaise }
    val money = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.fixtrack_logo),
                        contentDescription = "FixTrack logo",
                        modifier = Modifier.size(40.dp)
                    )
                    Column {
                        Text("FixTrack", fontWeight = FontWeight.Bold, color = Navy)
                        Text("Track It. Maintain It. Save More.", style = MaterialTheme.typography.labelSmall)
                    }
                }
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }, containerColor = Blue, contentColor = Color.White) {
                Icon(Icons.Default.Add, contentDescription = "Add product")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(Modifier.height(2.dp))
                Text("Your dashboard", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Navy)
                Text("Keep your important items and warranties in one place.", color = Color(0xFF5E687A))
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    SummaryCard("My products", products.size.toString(), Modifier.weight(1f))
                    SummaryCard("Purchase value", money.format(totalValue / 100.0), Modifier.weight(1f))
                }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = PaleBlue), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Stay one step ahead", fontWeight = FontWeight.Bold, color = Navy)
                        Text("Add a product and its warranty end date. Warranty reminders and maintenance tools are the next build phase.", color = Color(0xFF31415C))
                        Button(onClick = { showAdd = true }) { Icon(Icons.Default.Add, contentDescription = null); Text("  Add your first product") }
                    }
                }
            }
            item { Text("Recently added", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Navy) }
            if (products.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Image(
                                painter = painterResource(id = R.drawable.fixtrack_logo),
                                contentDescription = "FixTrack logo",
                                modifier = Modifier.size(56.dp)
                            )
                            Text("No products yet", fontWeight = FontWeight.Bold)
                            Text("Tap + to register your phone, laptop, appliance, or another item.", color = Color.Gray)
                        }
                    }
                }
            } else {
                items(products, key = { it.id }) { product ->
                    ProductCard(product = product, onDelete = { onDelete(product) })
                }
            }
            item { Spacer(Modifier.height(84.dp)) }
        }
    }
    if (showAdd) AddProductDialog(onDismiss = { showAdd = false }, onSave = { product -> onAdd(product); showAdd = false })
}

@Composable
private fun SummaryCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Navy)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, color = Color(0xFFD8E5FF), style = MaterialTheme.typography.labelLarge)
            Text(value, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProductCard(product: Product, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(46.dp).background(PaleBlue, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.HomeRepairService, contentDescription = null, tint = Navy)
            }
            Column(Modifier.weight(1f).padding(start = 12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold, color = Navy)
                Text(listOf(product.brand, product.category).filter { it.isNotBlank() }.joinToString(" • "), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text("Warranty ends: ${product.warrantyEndDate.ifBlank { "Not entered" }}", style = MaterialTheme.typography.bodySmall)
                Text("Status: ${product.status}", style = MaterialTheme.typography.labelSmall, color = Blue)
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.DeleteOutline, contentDescription = "Delete ${product.name}", tint = Color(0xFF9A3540)) }
        }
    }
}

@Composable
private fun AddProductDialog(onDismiss: () -> Unit, onSave: (Product) -> Unit) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Electronics") }
    var brand by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var warranty by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add product") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it; error = "" }, label = { Text("Product name *") }, singleLine = true)
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, singleLine = true)
                OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("Brand (optional)") }, singleLine = true)
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Purchase price (₹)") }, singleLine = true)
                OutlinedTextField(value = warranty, onValueChange = { warranty = it }, label = { Text("Warranty end (YYYY-MM-DD)") }, singleLine = true)
                if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            Button(onClick = {
                val parsedPrice = price.trim().ifBlank { "0" }.toBigDecimalOrNull()
                if (name.isBlank()) error = "Enter a product name."
                else if (parsedPrice == null || parsedPrice < java.math.BigDecimal.ZERO) error = "Enter a valid price, or 0."
                else if (warranty.isNotBlank() && !warranty.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) error = "Use date format YYYY-MM-DD."
                else onSave(Product(name = name.trim(), category = category.trim().ifBlank { "Other" }, brand = brand.trim(), purchasePricePaise = parsedPrice.movePointRight(2).setScale(0, java.math.RoundingMode.HALF_UP).longValueExact(), warrantyEndDate = warranty.trim()))
            }) { Text("Save") }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } }
    )
}
