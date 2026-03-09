package com.familyplate.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.familyplate.app.domain.model.Family
import com.familyplate.app.domain.model.MealCategory
import com.familyplate.app.domain.model.User
import com.familyplate.app.domain.repository.AuthRepository
import com.familyplate.app.domain.repository.FamilyRepository
import androidx.compose.runtime.collectAsState
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    onSignOut: () -> Unit
) {
    val authRepository: AuthRepository = koinInject()
    val familyRepository: FamilyRepository = koinInject()
    val authUser by authRepository.authState.collectAsState(initial = null)

    var user by remember { mutableStateOf<User?>(null) }
    var family by remember { mutableStateOf<Family?>(null) }

    LaunchedEffect(authUser) {
        authUser?.let { auth ->
            user = familyRepository.getUserProfile(auth.id).getOrNull()
            user?.familyId?.let { familyId ->
                family = familyRepository.getFamily(familyId).getOrNull()
            }
        }
    }

    val displayName = user?.displayName?.takeIf { it.isNotBlank() } ?: user?.email ?: "there"
    val greeting = when (Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour) {
        in 0..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "$greeting, $displayName!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        family?.let { fam ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = fam.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${fam.memberIds.size} family members",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = kotlinx.datetime.Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
                .toString(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        MealCategory.entries.forEach { category ->
            MealSlotCard(
                category = category,
                onClick = { /* Phase 2 */ }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        TextButton(onClick = onSignOut) {
            Text("Sign Out")
        }
    }
}

@Composable
private fun MealSlotCard(
    category: MealCategory,
    onClick: () -> Unit
) {
    val icon = when (category) {
        MealCategory.BREAKFAST -> Icons.Filled.FreeBreakfast
        MealCategory.LUNCH -> Icons.Filled.LunchDining
        MealCategory.DINNER -> Icons.Filled.DinnerDining
        MealCategory.SNACK -> Icons.Filled.Cookie
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.padding(horizontal = 16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Tap to add",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
