package com.example.billbuddy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import java.text.SimpleDateFormat
import java.util.*

// Tidak perlu lagi mengimpor font family atau warna secara individual di sini.
// Mereka diakses melalui MaterialTheme.typography dan MaterialTheme.colorScheme.

@Composable
fun MonthlyBarChart(monthlyTotals: Map<String, Long>) {
    val density = LocalDensity.current

    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())

    // Define a palette of colors for the bars using MaterialTheme.colorScheme
    // Ini akan mengambil warna yang Anda definisikan di Color.kt dan dipetakan di Theme.kt
    val barColors = listOf(
        MaterialTheme.colorScheme.primary,    // -> PinkPrimary
        MaterialTheme.colorScheme.tertiary,   // -> Pink80 (Dark) / Pink40 (Light)
        MaterialTheme.colorScheme.onBackground, // -> DarkGreyText
        MaterialTheme.colorScheme.surface,      // -> PinkBackground (Light) / White (Dark)
        MaterialTheme.colorScheme.onSurface,    // -> DarkGreyText
        MaterialTheme.colorScheme.secondary     // -> PinkPrimary
    )
    // Anda bisa menyesuaikan urutan atau menambahkan properti colorScheme lainnya di sini
    // untuk variasi warna yang lebih banyak jika dibutuhkan.

    val monthsToShow = mutableListOf<String>()

    for (i in 0 until 6) { // Displaying last 6 months
        val monthDate = Calendar.getInstance()
        monthDate.add(Calendar.MONTH, -i)
        monthsToShow.add(0, dateFormat.format(monthDate.time))
    }

    val chartData = monthsToShow.associateWith { month ->
        monthlyTotals[month] ?: 0L
    }.entries.sortedBy { it.key }

    val maxTotal = if (chartData.isNotEmpty()) chartData.maxOfOrNull { it.value } ?: 1L else 1L

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Monthly Spending Overview",
            // Menggunakan gaya tipografi dari tema yang sudah dikonfigurasi fontnya
            // Asumsi: titleMedium di Type.kt menggunakan RobotoBold
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.primary // Menggunakan warna primary (PinkPrimary)
            ),
            // Tidak perlu FontWeight.Bold lagi di sini jika sudah ada di TextStyle
            // fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom
        ) {
            items(chartData.size) { index ->
                val (monthYear, totalAmount) = chartData[index]
                val barColor = barColors[index % barColors.size]

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(48.dp)
                        .padding(horizontal = 4.dp)
                ) {
                    if (totalAmount > 0) {
                        Text(
                            text = "$totalAmount",
                            // Asumsi: labelMedium di Type.kt menggunakan RobotoMedium
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onBackground // Menggunakan warna onBackground (DarkGreyText)
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(with(density) { 14.sp.toDp() }))
                    }

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(((totalAmount.toFloat() / maxTotal) * 160).dp)
                            .background(barColor, RoundedCornerShape(4.dp))
                            // Menggunakan warna onBackground (DarkGreyText) untuk border
                            .border(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    )
                    Text(
                        text = monthYear.takeLast(2),
                        // Asumsi: labelSmall di Type.kt menggunakan KhulaRegular
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onBackground // Menggunakan warna onBackground (DarkGreyText)
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
        if (monthlyTotals.isEmpty()) {
            Text(
                text = "No split bill transactions for this month yet.",
                // Asumsi: bodySmall di Type.kt menggunakan KhulaRegular
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}