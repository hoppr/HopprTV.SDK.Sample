/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hoppr.jetstream.presentation.screens.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.hoppr.jetstream.R
import com.hoppr.jetstream.presentation.theme.JetStreamCardShape
import com.hoppr.jetstream.tvmaterial.StandardDialog
import com.hoppr.hopprtvandroid.Hoppr
import com.hoppr.hopprtvandroid.core.model.HopprParameter
import com.hoppr.hopprtvandroid.core.model.HopprTrigger

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class,
    ExperimentalTvMaterial3Api::class
)
@Composable
fun AccountsSectionDeleteDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    StandardDialog(
        showDialog = showDialog,
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            AccountsSectionDialogButton(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(R.string.yes_delete_account),
                shouldRequestFocus = true,
                onClick = {
                    Hoppr.trigger(HopprTrigger.ON_ELEMENT_CLICKED, bundleOf().apply {
                        this.putString(HopprParameter.BUTTON_ID, "DeleteAccountYes")
                    }) {
                        onDismissRequest()
                    }
                }
            )
        },
        dismissButton = {
            AccountsSectionDialogButton(
                modifier = Modifier.padding(end = 8.dp),
                text = stringResource(R.string.no_keep_it),
                shouldRequestFocus = false,
                onClick = {
                    Hoppr.trigger(HopprTrigger.ON_ELEMENT_CLICKED, bundleOf().apply {
                        this.putString(HopprParameter.BUTTON_ID, "DeleteAccountNo")
                    }) {
                        onDismissRequest()
                    }
                }
            )
        },
        title = {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(R.string.delete_account_dialog_title),
                color = MaterialTheme.colorScheme.surface,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = stringResource(R.string.delete_account_dialog_text),
                color = MaterialTheme.colorScheme.surface,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        containerColor = MaterialTheme.colorScheme.onSurface,
        shape = JetStreamCardShape
    )
}
