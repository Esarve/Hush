package dev.souravdas.hush.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import dagger.Provides
import dev.souravdas.hush.BuildConfig
import dev.souravdas.hush.R

/**
 * Created by Sourav
 * On 3/23/2023 7:16 PM
 * For Hush!
 */

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                title = { Text(text = "About") },
                navigationIcon = {
                    Icon(
                        Icons.Outlined.ArrowBack,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "BACK",
                        modifier = Modifier.clickable {
                            navController.popBackStack()
                        }
                    )
                }
            )
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                Text(
                    text = "Version: " + BuildConfig.VERSION_NAME,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Developer",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    GlideImage(
                        model = "https://github.com/Esarve.png",
                        contentDescription = "devimg",
                        modifier = Modifier
                            .width(64.dp)
                            .height(64.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        text = "Sourav Das\nhttps://souravdas.dev",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                Text(
                    text = "Designer",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    GlideImage(
                        model = "https://lh3.googleusercontent.com/_DAIFu6wp6wZJBWDR4KVQ6cV4DE3Ov2h9Vvn-3RztW6xJxuGo_sg2gdkZ2dFZTLzANuZ_Bu4EMIKXSIGZAHoYOzqxzY7tisGgq5WpiRNh9kqzh_0WSQZZrGo1d2Vsj2m=w1280",
                        contentDescription = "devimg",
                        modifier = Modifier
                            .width(64.dp)
                            .height(64.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        text = "Raisul Haque\nhttps://www.raishaq.com/",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

    }
}