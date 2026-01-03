import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bunnix.backend.VendorProductViewModel
import com.example.bunnix.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    vendorId: Int,
    product: Product?,
    onDone: () -> Unit,
    viewModel: VendorProductViewModel = hiltViewModel()
) {

    var name by remember { mutableStateOf(product?.name ?: "") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var category by remember { mutableStateOf(product?.category ?: "") }
    var price by remember { mutableStateOf(product?.price ?: "") }
    var quantity by remember { mutableStateOf(product?.quantity ?: "") }
    var imageUrl by remember { mutableStateOf(product?.image_url ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (product == null) "Add Product" else "Edit Product")
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(name, { name = it }, label = { Text("Name") })
            OutlinedTextField(description, { description = it }, label = { Text("Description") })
            OutlinedTextField(category, { category = it }, label = { Text("Category") })
            OutlinedTextField(price, { price = it }, label = { Text("Price") })
            OutlinedTextField(quantity, { quantity = it }, label = { Text("Quantity") })
            OutlinedTextField(imageUrl, { imageUrl = it }, label = { Text("Image URL") })

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val productToSave = Product(
                        id = product?.id ?: 0,
                        image_url = imageUrl,
                        name = name,
                        description = description,
                        category = category,
                        price = price,
                        quantity = quantity,
                        vendor_id = vendorId
                    )

                    viewModel.saveProduct(productToSave)
                    onDone()
                }
            ) {
                Text("Save Product")
            }
        }
    }
}
