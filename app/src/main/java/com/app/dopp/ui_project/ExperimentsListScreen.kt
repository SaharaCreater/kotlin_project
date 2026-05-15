package com.app.dopp.ui_project

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.dopp.physics.ExperimentCategory
import com.app.dopp.physics.ExperimentType
import com.app.dopp.ui.theme.Violet600
import com.app.dopp.ui.theme.Violet800
import com.app.dopp.ui_project.components.OfflineBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperimentsListScreen(
    onExperimentSelected: (ExperimentType) -> Unit,
    completedIds: Set<String> = emptySet(),
    isOnline: Boolean = true,
    initialCategory: ExperimentCategory? = null
) {
    var selectedCategory by remember { mutableStateOf<ExperimentCategory?>(initialCategory) }
    var searchQuery      by remember { mutableStateOf("") }

    val filteredExperiments = remember(selectedCategory, searchQuery) {
        val base = if (selectedCategory == null) ExperimentType.entries
        else ExperimentType.entries.filter { it.category == selectedCategory }
        if (searchQuery.isBlank()) base
        else base.filter {
            it.displayName.contains(searchQuery, ignoreCase = true) ||
            it.description.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Эксперименты", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OfflineBanner(isOffline = !isOnline)
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Поиск экспериментов...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, null)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(28.dp)),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor   = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor  = Color.Transparent,
                    focusedTextColor        = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor      = MaterialTheme.colorScheme.onSurface,
                )
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("Все") },
                        leadingIcon = if (selectedCategory == null) {
                            { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Violet800,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )
                }
                items(ExperimentCategory.entries) { category ->
                    val catColor = getCategoryColor(category)
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = if (selectedCategory == category) null else category },
                        label = { Text(category.displayName) },
                        leadingIcon = if (selectedCategory == category) {
                            { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
                        } else {
                            { Icon(getCategoryIcon(category), null, Modifier.size(16.dp)) }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = catColor,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (selectedCategory == null) {
                    ExperimentCategory.entries.forEach { category ->
                        val categoryExperiments = filteredExperiments.filter { it.category == category }
                        if (categoryExperiments.isNotEmpty()) {
                            item { CategoryHeader(category) }
                            items(categoryExperiments) { experiment ->
                                ExperimentCard(
                                    experiment = experiment,
                                    isCompleted = completedIds.contains(experiment.name),
                                    onClick = { onExperimentSelected(experiment) }
                                )
                            }
                            item { Spacer(Modifier.height(4.dp)) }
                        }
                    }
                } else {
                    items(filteredExperiments) { experiment ->
                        ExperimentCard(
                            experiment = experiment,
                            isCompleted = completedIds.contains(experiment.name),
                            onClick = { onExperimentSelected(experiment) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryHeader(category: ExperimentCategory) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.linearGradient(
                        listOf(getCategoryColor(category), getCategoryColor(category).copy(0.6f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(getCategoryIcon(category), null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Column {
            Text(
                category.displayName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                category.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ExperimentCard(
    experiment: ExperimentType,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    val categoryColor = getCategoryColor(experiment.category)
    val doneGreen = Color(0xFF059669)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) Color(0xFFF0FDF4) else Color.White
        ),
        elevation = CardDefaults.cardElevation(if (isCompleted) 0.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(if (isCompleted) 96.dp else 80.dp)
                    .background(
                        if (isCompleted)
                            Brush.verticalGradient(listOf(doneGreen, Color(0xFF34D399)))
                        else
                            Brush.verticalGradient(listOf(categoryColor, categoryColor.copy(0.4f)))
                    )
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isCompleted)
                                    Brush.linearGradient(listOf(doneGreen.copy(0.75f), Color(0xFF34D399).copy(0.75f)))
                                else
                                    Brush.linearGradient(listOf(categoryColor, categoryColor.copy(0.65f)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            getExperimentIcon(experiment),
                            null,
                            modifier = Modifier.size(26.dp),
                            tint = Color.White
                        )
                    }
                    if (isCompleted) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .offset(x = 3.dp, y = 3.dp)
                                .clip(CircleShape)
                                .background(doneGreen)
                                .border(2.dp, Color(0xFFF0FDF4), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Check,
                                null,
                                modifier = Modifier.size(10.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        experiment.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) Color(0xFF065F46) else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        experiment.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isCompleted) Color(0xFF065F46).copy(alpha = 0.65f)
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                    if (isCompleted) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(doneGreen.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                "✓  Пройдено",
                                style = MaterialTheme.typography.labelSmall,
                                color = doneGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Icon(
                    Icons.Default.ChevronRight,
                    null,
                    tint = if (isCompleted) doneGreen.copy(alpha = 0.4f)
                           else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

private fun getCategoryIcon(category: ExperimentCategory): ImageVector = when (category) {
    ExperimentCategory.MECHANICS      -> Icons.Default.Settings
    ExperimentCategory.ELECTRICITY    -> Icons.Default.Bolt
    ExperimentCategory.OPTICS         -> Icons.Default.Lightbulb
    ExperimentCategory.THERMODYNAMICS -> Icons.Default.Whatshot
}

private fun getCategoryColor(category: ExperimentCategory): Color = when (category) {
    ExperimentCategory.MECHANICS      -> Color(0xFF2563EB)
    ExperimentCategory.ELECTRICITY    -> Color(0xFFD97706)
    ExperimentCategory.OPTICS         -> Color(0xFF7C3AED)
    ExperimentCategory.THERMODYNAMICS -> Color(0xFFDC2626)
}

private fun getExperimentIcon(experiment: ExperimentType): ImageVector = when (experiment) {
    ExperimentType.PENDULUM        -> Icons.Default.SwapVert
    ExperimentType.FREE_FALL       -> Icons.Default.ArrowDownward
    ExperimentType.COLLISION       -> Icons.Default.Compress
    ExperimentType.ELECTRIC_CIRCUIT -> Icons.Default.Cable
    ExperimentType.MAGNETIC_FIELD  -> Icons.Default.Radar
    ExperimentType.LIGHT_REFRACTION -> Icons.Default.Flare
    ExperimentType.LENS            -> Icons.Default.CenterFocusWeak
    ExperimentType.BROWNIAN_MOTION -> Icons.Default.Grain
    ExperimentType.GAS_EXPANSION   -> Icons.Default.Air
}
