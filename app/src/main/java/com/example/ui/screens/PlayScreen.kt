package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DoodleStroke
import com.example.ui.PncViewModel
import com.example.ui.components.LivingCatView
import com.example.ui.theme.LocalPncTheme
import com.example.ui.theme.parseHexColor

@Composable
fun PlayScreen(
    viewModel: PncViewModel,
    modifier: Modifier = Modifier
) {
    val theme = LocalPncTheme.current
    var selectedTab by remember { mutableStateOf(0) } // 0: Doodle Canvas, 1: Cat Playroom

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = theme.accentColor,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = theme.accentColor
                )
            },
            divider = {}
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Brush, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Doodle Canvas", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Pets, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Cat Playroom", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            )
        }

        if (selectedTab == 0) {
            DoodleCanvasSection(viewModel = viewModel)
        } else {
            CatPlayroomSection(viewModel = viewModel)
        }
    }
}

@Composable
fun DoodleCanvasSection(viewModel: PncViewModel) {
    val theme = LocalPncTheme.current
    val strokes by viewModel.strokes.collectAsState()
    val currentBrushColorHex by viewModel.currentBrushColorHex.collectAsState()
    val currentBrushWidth by viewModel.currentBrushWidth.collectAsState()

    val currentPoints = remember { mutableStateListOf<Pair<Float, Float>>() }

    val colorPalette = listOf(
        "#E05368", // Rose Accent
        "#FFFFFF", // White
        "#F39C12", // Gold
        "#3498DB", // Sky Blue
        "#2ECC71", // Emerald
        "#9B59B6"  // Purple
    )

    val brushSizes = listOf(4f to "Fine", 8f to "Medium", 16f to "Bold")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF161B22))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(20.dp)
                )
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPoints.clear()
                            currentPoints.add(offset.x to offset.y)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            currentPoints.add(change.position.x to change.position.y)
                        },
                        onDragEnd = {
                            if (currentPoints.size > 1) {
                                viewModel.addStroke(
                                    DoodleStroke(
                                        points = currentPoints.toList(),
                                        colorHex = currentBrushColorHex,
                                        strokeWidth = currentBrushWidth
                                    )
                                )
                            }
                            currentPoints.clear()
                        },
                        onDragCancel = {
                            currentPoints.clear()
                        }
                    )
                }
                .testTag("doodle_canvas_area")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                strokes.forEach { stroke ->
                    if (stroke.points.size > 1) {
                        val path = Path().apply {
                            moveTo(stroke.points.first().first, stroke.points.first().second)
                            for (i in 1 until stroke.points.size) {
                                lineTo(stroke.points[i].first, stroke.points[i].second)
                            }
                        }
                        drawPath(
                            path = path,
                            color = parseHexColor(stroke.colorHex),
                            style = Stroke(
                                width = stroke.strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }

                if (currentPoints.size > 1) {
                    val activePath = Path().apply {
                        moveTo(currentPoints.first().first, currentPoints.first().second)
                        for (i in 1 until currentPoints.size) {
                            lineTo(currentPoints[i].first, currentPoints[i].second)
                        }
                    }
                    drawPath(
                        path = activePath,
                        color = parseHexColor(currentBrushColorHex),
                        style = Stroke(
                            width = currentBrushWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            if (strokes.isEmpty() && currentPoints.isEmpty()) {
                Text(
                    text = "Draw something together...",
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                colorPalette.forEach { hex ->
                    val isSelected = currentBrushColorHex == hex
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(parseHexColor(hex))
                            .border(
                                width = if (isSelected) 2.dp else 0.5.dp,
                                color = if (isSelected) Color.White else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { viewModel.currentBrushColorHex.value = hex }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                brushSizes.forEach { (width, label) ->
                    val isSelected = currentBrushWidth == width
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) theme.accentColor.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { viewModel.currentBrushWidth.value = width }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) theme.accentColor else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { viewModel.undoStroke() },
                    enabled = strokes.isNotEmpty(),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.clearCanvas() },
                    enabled = strokes.isNotEmpty(),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Button(
                onClick = { viewModel.shareDoodleToChat() },
                enabled = strokes.isNotEmpty(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = theme.accentColor),
                modifier = Modifier.testTag("doodle_send_chat_btn")
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share to Chat", fontSize = 12.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun CatPlayroomSection(viewModel: PncViewModel) {
    val theme = LocalPncTheme.current
    val cat by viewModel.cat.collectAsState()
    val session by viewModel.session.collectAsState()
    val catName = cat?.name ?: session?.catName ?: "Mochi"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        0.5.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        RoundedCornerShape(22.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$catName's Cozy Corner",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    LivingCatView(
                        cat = cat,
                        onTap = { viewModel.interactWithCat() },
                        size = 200.dp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Tap to say hello and pet your shared companion",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
