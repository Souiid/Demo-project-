package com.souiid.demoproject.ui.components.others

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.souiid.demoproject.ui.components.button.TButton
import com.souiid.demoproject.ui.components.button.TButtonType
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.chooseFromGallery
import demoproject.shared.generated.resources.deletePhoto
import demoproject.shared.generated.resources.takePhoto
import io.github.ismoy.imagepickerkmp.config.CropConfig
import io.github.ismoy.imagepickerkmp.config.GalleryConfig
import io.github.ismoy.imagepickerkmp.picker.ImagePickerKMPConfig
import io.github.ismoy.imagepickerkmp.picker.ImagePickerResult
import io.github.ismoy.imagepickerkmp.picker.rememberImagePickerKMP
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoPicker(
    photoUri: String?,
    onPhotoSelected: (String?) -> Unit,
    content: @Composable (photoUri: String?, openPicker: () -> Unit) -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val picker = rememberImagePickerKMP(
        config = ImagePickerKMPConfig(
            galleryConfig = GalleryConfig(allowMultiple = false),
            cropConfig = CropConfig(
                enabled = true,
                circularCrop = true,
                squareCrop = false
            )
        )
    )

    LaunchedEffect(picker.result) {
        when (val result = picker.result) {
            is ImagePickerResult.Success -> {
                result.first?.uri?.let(onPhotoSelected)
                showSheet = false
                picker.reset()
            }
            is ImagePickerResult.Dismissed, is ImagePickerResult.Error -> picker.reset()
            else -> Unit
        }
    }

    content(photoUri) { showSheet = true }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                TButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.takePhoto),
                    isTextAlignStart = true,
                    type = TButtonType.TEXT,
                    onClick = { picker.launchCamera() }
                )

                TButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.chooseFromGallery),
                    isTextAlignStart = true,
                    type = TButtonType.TEXT,
                    onClick = { picker.launchGallery() }
                )

                if (photoUri != null) {
                    TButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(Res.string.deletePhoto),
                        isTextAlignStart = true,
                        type = TButtonType.TEXT,
                        color = MaterialTheme.colorScheme.error,
                        onClick = {
                            onPhotoSelected(null)
                            showSheet = false
                        }
                    )
                }
            }
        }
    }
}