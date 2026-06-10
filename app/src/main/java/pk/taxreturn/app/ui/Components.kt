@file:OptIn(ExperimentalMaterial3Api::class)

package pk.taxreturn.app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

fun fmt(v: Long): String = NumberFormat.getNumberInstance(Locale.US).format(v)

/** Rupee input field â shows digits with thousand separators. */
@Composable
fun MoneyField(label: String, value: Long, modifier: Modifier = Modifier, onChange: (Long) -> Unit) {
    var text by remember(value) { mutableStateOf(if (value == 0L) "" else fmt(value)) }
    OutlinedTextField(
        value = text,
        onValueChange = { input ->
            val digits = input.filter { it.isDigit() }.take(15)
            val parsed = digits.toLongOrNull() ?: 0L
            text = if (digits.isEmpty()) "" else fmt(parsed)
            onChange(parsed)
        },
        label = { Text(label) },
        prefix = { Text("Rs ") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)
    )
}

/** Section card with title and body. */
@Composable
fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            content()
        }
    }
}

/** Income source selector card â colored, tappable, shows checkmark when selected. */
@Composable
fun SourceCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg by animateColorAsState(
        if (selected) accentColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
        animationSpec = tween(200), label = "bg"
    )
    val borderColor by animateColorAsState(
        if (selected) accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
        animationSpec = tween(200), label = "border"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 4.dp else 1.dp)
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(44.dp)
                    .background(accentColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(subtitle, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (selected) {
                Box(
                    Modifier
                        .size(22.dp)
                        .background(accentColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("â", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/** Summary label + amount row. */
@Composable
fun SummaryRow(label: String, amount: Long, bold: Boolean = false, negativeInBrackets: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(label, Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
        val txt = if (negativeInBrackets && amount < 0) "(${fmt(-amount)})" else fmt(amount)
        Text(txt, style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun Divider() = HorizontalDivider(Modifier.padding(vertical = 6.dp))

/** Expandable section with a header that shows/hides content. */
@Composable
fun ExpandableSection(title: String, expanded: Boolean, onToggle: () -> Unit, content: @Composable () -> Unit) {
    SectionCard(title = "") {
        Row(
            Modifier.fillMaxWidth().clickable(onClick = onToggle),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, Modifier.weight(1f), fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium)
            Text(if (expanded) "â²" else "â¼", color = MaterialTheme.colorScheme.primary)
        }
        if (expanded) {
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

/** Inline profit/loss display row. */
@Composable
fun PLRow(label: String, amount: Long, isResult: Boolean = false, isNegative: Boolean = false) {
    val color = when {
        isResult && amount < 0 -> MaterialTheme.colorScheme.error
        isResult -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(label, Modifier.weight(1f),
            style = if (isResult) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isResult) FontWeight.Bold else FontWeight.Normal,
            color = color)
        val display = if (isNegative) "(${fmt(amount)})" else fmt(amount)
        Text("Rs $display",
            style = if (isResult) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isResult) FontWeight.Bold else FontWeight.Normal,
            color = color)
    }
}

/** ScreenColumn helper for scrollable screens. */
@Composable
fun ScreenColumn(content: @Composable () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        content()
        Spacer(Modifier.height(90.dp))
    }
}

/** Warning card for validation messages. */
@Composable
fun WarningsCard(warnings: List<String>) {
    if (warnings.isEmpty()) return
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("Review before filing", fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer)
            warnings.forEach {
                Text("â¢ $it", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

/** Dropdown selector. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(label: String, selected: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable).padding(vertical = 4.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = { onSelect(opt); expanded = false }
                )
            }
        }
    }
}
