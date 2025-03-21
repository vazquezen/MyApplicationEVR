package com.evr.tes.ui.initscreen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evr.tes.R
import com.evr.tes.core_model.Constants
import com.evr.tes.core_model.fields.Field


@Composable
fun MainScreen(
    paddingValues: PaddingValues,
    mainScreenViewModel: MainScreenViewModel? = hiltViewModel()
) {
    val fieldsList = remember { mainScreenViewModel?.fields ?: mutableListOf() }
    Box(Modifier.padding(paddingValues)) {
        UIState(fieldsList, mainScreenViewModel!!)
    }
}

@Composable
private fun UIState(
    list: MutableList<Field>,
    viewModel: MainScreenViewModel
) {
    val uiState by viewModel.fieldsState.collectAsStateWithLifecycle(
        initialValue = FieldsState.Nonce
    )
    var loading by remember { mutableStateOf(false) }

    when (uiState) {
        FieldsState.Loading -> {
            loading = true
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }

        FieldsState.Error -> {
            loading = false
            viewModel.resetState()
        }

        is FieldsState.SuccessFieldsList -> {
            DynamicForm(list, viewModel)
            loading = false
        }

        FieldsState.Nonce -> {
            loading = false
            viewModel.resetState()
        }
    }
}

@Composable
fun DynamicForm(
    fields: List<Field>,
    viewModel: MainScreenViewModel,
    context: Context = LocalContext.current
) {
    Column(modifier = Modifier.padding(8.dp)) {
        fields.forEachIndexed() { index, field ->
            when (field.type) {
                Constants.TEXT_FIELD -> {
                    FieldText(field, viewModel, index)
                }

                Constants.SELECTOR_FIELD -> {
                    FieldSelect(field, viewModel, index)
                }

                Constants.CHECK_BOX_FIELD -> {
                    FieldCheckbox(field, viewModel, index)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            onClick = {
                if (viewModel.isFormFieldsValid()) {
                    Toast.makeText(context, context.getString(R.string.valid_field), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, context.getString(R.string.invalid_field), Toast.LENGTH_LONG).show()
                }
            }
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = context.getString(R.string.field_button)
            )
        }
    }
}

@Composable
fun FieldText(
    field: Field,
    viewModel: MainScreenViewModel,
    index: Int,
    context: Context = LocalContext.current
) {
    var textContent by remember { mutableStateOf(Constants.EMPTY) }
    var isValid by remember { mutableStateOf(true) }
    val textRegex = Regex(field.regex ?: ".*")

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = textContent,
        onValueChange = {
            textContent = it
            isValid = textRegex.matches(it) || it.isEmpty()
            viewModel.updateValidField(index, isValid)
        },
        label = { Text(context.getString(R.string.field_name, field.type, field.order.toString())) },
        isError = !isValid,
        singleLine = true,
        maxLines = field.maxlength!!,
        supportingText = {

            if (!isValid) {
                Text(context.getString(R.string.error_field), color = Color.Red)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldSelect(
    field: Field,
    viewModel: MainScreenViewModel,
    index: Int,
    context: Context = LocalContext.current
) {
    var selectedText by remember { mutableStateOf(Constants.EMPTY) }
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = { selectedText = it },
            readOnly = true,
            label = { Text(context.getString(R.string.field_name, field.type, field.order.toString())) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .clickable { expanded = true }
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            field.valuesList.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        expanded = false
                        selectedText = option
                        viewModel.updateValidField(index, true)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
fun FieldCheckbox(
    field: Field,
    viewModel: MainScreenViewModel,
    index: Int,
    context: Context = LocalContext.current
) {
    var isChecked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Checkbox(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                viewModel.updateValidField(index, isChecked)
            }
        )
        Text(
            text = context.getString(R.string.field_name, field.type, field.order.toString()),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}