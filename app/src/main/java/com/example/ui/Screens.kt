package com.example.ui

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.theme.*
import com.example.utils.WirelessTransferServer
import com.example.viewmodel.NexusTab
import com.example.viewmodel.NexusViewModel
import com.example.viewmodel.PingStatus
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NexusMainApp(viewModel: NexusViewModel) {
    val activeTab by viewModel.activeTab.collectAsState()
    val ps4Ip by viewModel.ps4Ip.collectAsState()
    val pingState by viewModel.pingState.collectAsState()
    val latencyMs by viewModel.latencyMs.collectAsState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Drawing custom styled Ω emblem
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(ElectricCyan, RoundedCornerShape(8.dp))
                            ) {
                                Text(
                                    "Ω",
                                    color = DeepViolet,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            Column {
                                Text(
                                    "PS4 NEXUS",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 2.sp,
                                        color = OffWhite
                                    )
                                )
                                Text(
                                    "ALL-IN-ONE SYSTEM TOOLKIT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = ElectricCyan
                                    )
                                )
                            }
                        }
                    },
                    actions = {
                        // Raw Live Latency Stat chip
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CardSlate)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        when (pingState) {
                                            PingStatus.ONLINE -> SuccessGreen
                                            PingStatus.PENDING -> GoldHenGold
                                            PingStatus.OFFLINE -> CyberPink
                                        },
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (pingState) {
                                    PingStatus.ONLINE -> "${latencyMs}ms"
                                    PingStatus.PENDING -> "PINGING..."
                                    PingStatus.OFFLINE -> "OFFLINE"
                                },
                                color = OffWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AbyssBlue,
                        titleContentColor = OffWhite
                    )
                )
                Divider(color = SystemBorder, thickness = 1.dp)
            }
        },
        bottomBar = {
            NexusBottomNavigationBar(
                activeTab = activeTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        },
        containerColor = AbyssBlue,
        contentWindowInsets = WindowInsets.navigationBars
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Background cosmic space dust grid design
            Canvas(modifier = Modifier.fillMaxSize()) {
                val step = 60.dp.toPx()
                for (x in 0..size.width.toInt() step step.toInt()) {
                    drawLine(
                        color = ElectricCyan.copy(alpha = 0.05f),
                        start = Offset(x.toFloat(), 0f),
                        end = Offset(x.toFloat(), size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                for (y in 0..size.height.toInt() step step.toInt()) {
                    drawLine(
                        color = ElectricCyan.copy(alpha = 0.05f),
                        start = Offset(0f, y.toFloat()),
                        end = Offset(size.width, y.toFloat()),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }

            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
                },
                label = "TabContent"
            ) { tab ->
                when (tab) {
                    NexusTab.DASHBOARD -> DashboardScreen(viewModel)
                    NexusTab.PAYLOADS -> PayloadScreen(viewModel)
                    NexusTab.FTP_STATION -> FtpStationScreen(viewModel)
                    NexusTab.WIRELESS -> WirelessTransfersScreen(viewModel)
                    NexusTab.THEME_SECTOR -> ThemesCatalogScreen(viewModel)
                    NexusTab.GAME_HUB -> GameHubScreen(viewModel)
                    NexusTab.CHEATS_DB -> CheatsDbScreen(viewModel)
                    NexusTab.SAVE_RESIGNER -> SaveResignerScreen(viewModel)
                    NexusTab.LINK_SCRAPER -> LinkScraperScreen(viewModel)
                    NexusTab.FILE_BROWSER -> StorageBrowserScreen(viewModel)
                }
            }
        }
    }
}

/* =========================================================
   CORE UI SUB-SCREENS
   ========================================================= */

@Composable
fun DashboardScreen(viewModel: NexusViewModel) {
    val ps4Ip by viewModel.ps4Ip.collectAsState()
    val pingState by viewModel.pingState.collectAsState()
    val cpuTemp by viewModel.cpuTemp.collectAsState()
    val socLoad by viewModel.socLoad.collectAsState()
    val isWirelessRunning by viewModel.isWirelessServerRunning.collectAsState()
    val isFtpRunning by viewModel.isFtpServerRunning.collectAsState()
    val ftpServerUptime by viewModel.ftpServerUptime.collectAsState()
    val ftpServerConnections by viewModel.ftpServerConnections.collectAsState()
    val ftpServerSpeed by viewModel.ftpServerSpeed.collectAsState()
    val ftpServerLogs by viewModel.ftpServerLogs.collectAsState()
    
    val context = LocalContext.current
    var ipInput by remember { mutableStateOf(ps4Ip) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Futuristic Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(NexusNavy, CardSlate),
                            start = Offset(0f, 0f),
                            end = Offset.Infinite
                        )
                    )
                    .border(1.dp, SystemBorder, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Console Terminal",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = OffWhite,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            "Bridge active payload servers and transfer systems",
                            style = MaterialTheme.typography.bodySmall.copy(color = CoolGray)
                        )
                    }
                    // Glowing PlayStation geometric icon rings
                    Canvas(modifier = Modifier.size(50.dp)) {
                        drawCircle(
                            color = ElectricCyan,
                            radius = size.minDimension / 3f,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
            }
        }

        // 2. IP Connection Config Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NexusNavy),
                border = BorderStroke(1.dp, Color(0x15FFFFFF)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "TARGET CONSOLE SPECIFICATION",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = GoldHenGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    OutlinedTextField(
                        value = ipInput,
                        onValueChange = { ipInput = it },
                        label = { Text("PS4 IP Address") },
                        placeholder = { Text("e.g. 192.168.1.15") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("console_ip_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            focusedLabelColor = ElectricCyan,
                            unfocusedBorderColor = CoolGray.copy(0.4f),
                            focusedTextColor = OffWhite,
                            unfocusedTextColor = OffWhite
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            viewModel.updatePs4Ip(ipInput)
                        })
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.updatePs4Ip(ipInput) },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("update_ip_button"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = AbyssBlue)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Map Endpoint", color = AbyssBlue, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.pingConsole() },
                            colors = ButtonDefaults.buttonColors(containerColor = CardSlate),
                            border = BorderStroke(1.dp, SystemBorder),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Trace Console", color = ElectricCyan, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 3. Telemetry Indicators (Gauges)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // CPU Thermal gauge
                TelemetryCard(
                    title = "PS4 Heat Index",
                    value = if (pingState == PingStatus.ONLINE) "$cpuTemp°C" else "--°C",
                    indicatorColor = if (cpuTemp > 64) CyberPink else SuccessGreen,
                    subtext = when {
                        pingState != PingStatus.ONLINE -> "Console Offline"
                        cpuTemp > 64 -> "Warning: Heat High"
                        else -> "Thermal Safe Range"
                    },
                    progress = if (pingState == PingStatus.ONLINE) (cpuTemp - 30) / 50f else 0f,
                    modifier = Modifier.weight(1f)
                )

                // SOC Load gauge
                TelemetryCard(
                    title = "Engine Threads",
                    value = if (pingState == PingStatus.ONLINE) "$socLoad%" else "--%",
                    indicatorColor = ElectricCyan,
                    subtext = if (pingState == PingStatus.ONLINE) "Active Processes" else "No Connection",
                    progress = if (pingState == PingStatus.ONLINE) socLoad / 100f else 0f,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // FTP Host Server Status Dashboard Component
        item {
            // Animated Border & Glow Color
            val animatedBorderColor by animateColorAsState(
                targetValue = if (isFtpRunning) ElectricCyan.copy(alpha = 0.45f) else Color(0x15FFFFFF),
                animationSpec = tween(durationMillis = 600),
                label = "ftpCardBorder"
            )

            // Infinite pulse for active state indicator
            val infiniteTransition = rememberInfiniteTransition(label = "ftpPulseTransition")
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 0.8f,
                animationSpec = infiniteRepeatable(
                    animation = tween(900, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseAlpha"
            )
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 1.0f,
                targetValue = 1.5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(900, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseScale"
            )

            // Connection & transfer visual colors
            val activeConnectionColor by animateColorAsState(
                targetValue = if (ftpServerConnections > 0) SuccessGreen else ElectricCyan,
                animationSpec = tween(durationMillis = 300),
                label = "activeConnectionColor"
            )

            val isTransferring = ftpServerSpeed != "0.0 B/s" && ftpServerConnections > 0
            val speedGaugeColor by animateColorAsState(
                targetValue = if (isTransferring) GoldHenGold else ElectricCyan,
                animationSpec = tween(durationMillis = 300),
                label = "speedGaugeColor"
            )

            val toggleButtonBg by animateColorAsState(
                targetValue = if (isFtpRunning) CyberPink.copy(0.15f) else ElectricCyan.copy(0.1f),
                animationSpec = tween(durationMillis = 300),
                label = "toggleButtonBg"
            )

            val toggleButtonBorder by animateColorAsState(
                targetValue = if (isFtpRunning) CyberPink.copy(0.4f) else ElectricCyan.copy(0.4f),
                animationSpec = tween(durationMillis = 300),
                label = "toggleButtonBorder"
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = NexusNavy),
                border = BorderStroke(1.dp, animatedBorderColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header Row with title and state-toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "INTEGRATED FTP SERVICE",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = GoldHenGold,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(12.dp)
                                ) {
                                    if (isFtpRunning) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    SuccessGreen.copy(alpha = pulseAlpha),
                                                    CircleShape
                                                )
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                if (isFtpRunning) SuccessGreen else CyberPink,
                                                CircleShape
                                            )
                                    )
                                }

                                AnimatedContent(
                                    targetState = isFtpRunning,
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                                    },
                                    label = "ftpStatusText"
                                ) { running ->
                                    Text(
                                        if (running) "RUNNING ON PORT 2121" else "SERVICE OFFLINE",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (running) SuccessGreen else CoolGray,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = { viewModel.toggleFtpHoster() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = toggleButtonBg,
                                contentColor = if (isFtpRunning) CyberPink else ElectricCyan
                            ),
                            border = BorderStroke(1.dp, toggleButtonBorder),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("ftp_dashboard_toggle_button")
                        ) {
                            AnimatedContent(
                                targetState = isFtpRunning,
                                transitionSpec = {
                                    scaleIn() + fadeIn() togetherWith scaleOut() + fadeOut()
                                },
                                label = "toggleButtonContent"
                            ) { running ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (running) Icons.Default.Stop else Icons.Default.PlayArrow,
                                        contentDescription = if (running) "Stop FTP Host" else "Start FTP Host",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        if (running) "STOP" else "START",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        "Enables instant desktop file injections over LAN. Connect using dynamic telemetry gauges beneath:",
                        style = MaterialTheme.typography.bodySmall.copy(color = CoolGray)
                    )

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Uptime gauge
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CardSlate)
                                .border(1.dp, Color(0x0AFFFFFF), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "Uptime Icon",
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "UPTIME",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CoolGray,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    ftpServerUptime,
                                    color = OffWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // 2. Active Connections gauge
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CardSlate)
                                .border(
                                    1.dp,
                                    if (ftpServerConnections > 0) SuccessGreen.copy(0.4f) else Color(0x0AFFFFFF),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.People,
                                    contentDescription = "Active Connections Icon",
                                    tint = activeConnectionColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "CLIENTS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CoolGray,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    "$ftpServerConnections ACT",
                                    color = if (ftpServerConnections > 0) SuccessGreen else OffWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // 3. Transfer Speed gauge
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CardSlate)
                                .border(
                                    1.dp,
                                    if (isTransferring) GoldHenGold.copy(0.4f) else Color(0x0AFFFFFF),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SwapVert,
                                        contentDescription = "Transfer Speed Icon",
                                        tint = speedGaugeColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    if (isTransferring) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(GoldHenGold.copy(alpha = pulseAlpha), CircleShape)
                                        )
                                    }
                                }
                                Text(
                                    "SPEED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CoolGray,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    ftpServerSpeed,
                                    color = if (isTransferring) GoldHenGold else OffWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Micro-console logs telemetry with animated entrance/exit
                    AnimatedVisibility(
                        visible = isFtpRunning,
                        enter = fadeIn(tween(400)) + expandVertically(tween(400)),
                        exit = fadeOut(tween(300)) + shrinkVertically(tween(300))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0C0F17))
                                .border(1.dp, Color(0x1A00E5FF), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(SuccessGreen.copy(alpha = pulseAlpha), CircleShape)
                                )
                                Text(
                                    "HOST SERVICE LOGS:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ElectricCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            if (ftpServerLogs.isEmpty()) {
                                Text(
                                    "No client activity detected yet. Host listening...",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = CoolGray,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    fontSize = 10.sp
                                )
                            } else {
                                ftpServerLogs.take(3).forEach { log ->
                                    Text(
                                        log,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = OffWhite.copy(0.85f),
                                            fontFamily = FontFamily.Monospace
                                        ),
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Creative Feature: Direct Exploit Host Launcher Links
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NexusNavy),
                border = BorderStroke(1.dp, Color(0x15FFFFFF)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "WEB JAILBREAK HOST DIRECTORY",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = GoldHenGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        "Configure these URLs in your PS4 built-in browser to launch standard jailbreak tools offline or online.",
                        style = MaterialTheme.typography.bodySmall.copy(color = CoolGray)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    val clipboardManager = LocalClipboardManager.current
                    val exploitHosts = listOf(
                        "Karo Host (v2.18)" to "https://karo218.ir/",
                        "NightKing Exploit" to "https://nightkinghost.com/",
                        "GoldenHen Offic. Host" to "https://goldhen.github.io/"
                    )

                    exploitHosts.forEach { (name, link) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CardSlate)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(name, color = OffWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(link, color = ElectricCyan, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(onClick = {
                                    clipboardManager.setText(AnnotatedString(link))
                                    Toast.makeText(context, "Copied URL to Clipboard", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(Icons.Default.CopyAll, contentDescription = "Copy Link", tint = CoolGray)
                                }
                                IconButton(onClick = {
                                    viewModel.setScrapingUrl(link)
                                    viewModel.selectTab(NexusTab.LINK_SCRAPER)
                                }) {
                                    Icon(Icons.Default.Search, contentDescription = "Scrape", tint = ElectricCyan)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TelemetryCard(
    title: String,
    value: String,
    indicatorColor: Color,
    subtext: String,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = NexusNavy),
        border = BorderStroke(1.dp, Color(0x15FFFFFF)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                title.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CoolGray,
                    fontWeight = FontWeight.Bold
                )
            )
            Box(
                modifier = Modifier.size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                // Sleek circular track arc
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = CardSlate,
                    strokeWidth = 4.dp,
                )
                CircularProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxSize(),
                    color = indicatorColor,
                    strokeWidth = 5.dp,
                )
                Text(
                    text = value,
                    color = OffWhite,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Text(
                text = subtext,
                fontSize = 11.sp,
                color = indicatorColor.copy(alpha = 0.9f),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

enum class PayloadTransmissionStatus {
    IDLE,
    CONNECTING,
    SENDING,
    SUCCESS,
    FAILED
}

data class PayloadStatusState(
    val status: PayloadTransmissionStatus = PayloadTransmissionStatus.IDLE,
    val message: String = "",
    val timestamp: String = ""
)

private data class PayloadBannerTheme(
    val bg: Color,
    val border: Color,
    val icon: ImageVector,
    val text: Color
)

@Composable
fun PayloadLoaderComponent(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val ps4Ip by viewModel.ps4Ip.collectAsState()
    val pingState by viewModel.pingState.collectAsState()
    val latencyMs by viewModel.latencyMs.collectAsState()

    var ipInput by remember { mutableStateOf(ps4Ip) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }

    var sendingPayloadId by remember { mutableStateOf<String?>(null) }
    var actionLog by remember { mutableStateOf("Ready to send. Configure PS4 IP address and select a payload.") }
    var payloadStatuses by remember { mutableStateOf<Map<String, PayloadStatusState>>(emptyMap()) }

    val categories = remember {
        listOf("ALL", "AIO EXPLOIT", "SYSTEM UI", "UTILITY", "TRAINER", "SYSTEM PROTECTION", "DUMPER", "LINUX", "HARDWARE")
    }

    val filteredPayloads = remember(searchQuery, selectedCategory) {
        PreloadedData.payloads.filter { payload ->
            val matchesCategory = selectedCategory == "ALL" || payload.category.equals(selectedCategory, ignoreCase = true)
            val matchesQuery = searchQuery.isBlank() ||
                    payload.name.contains(searchQuery, ignoreCase = true) ||
                    payload.description.contains(searchQuery, ignoreCase = true) ||
                    payload.category.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        SearchBarHeader(
            title = "PAYLOAD LOADER",
            subtitle = "Directly transmit raw system payload binaries to configured PS4 IP address",
            icon = Icons.Default.Send,
            accentColor = GoldHenGold
        )

        // Target PS4 IP Configuration Bar
        Card(
            colors = CardDefaults.cardColors(containerColor = NexusNavy),
            border = BorderStroke(1.dp, GoldHenGold.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Router,
                            contentDescription = "Target PS4 IP",
                            tint = GoldHenGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "TARGET CONSOLE CONFIGURATION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OffWhite,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        )
                    }

                    // Ping Status Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                when (pingState) {
                                    PingStatus.ONLINE -> SuccessGreen.copy(0.15f)
                                    PingStatus.OFFLINE -> CyberPink.copy(0.15f)
                                    PingStatus.PENDING -> ElectricCyan.copy(0.15f)
                                }
                            )
                            .border(
                                1.dp,
                                when (pingState) {
                                    PingStatus.ONLINE -> SuccessGreen.copy(0.5f)
                                    PingStatus.OFFLINE -> CyberPink.copy(0.5f)
                                    PingStatus.PENDING -> ElectricCyan.copy(0.5f)
                                },
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("payload_ping_status")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        when (pingState) {
                                            PingStatus.ONLINE -> SuccessGreen
                                            PingStatus.OFFLINE -> CyberPink
                                            PingStatus.PENDING -> ElectricCyan
                                        },
                                        CircleShape
                                    )
                            )
                            Text(
                                when (pingState) {
                                    PingStatus.ONLINE -> "ONLINE (${latencyMs}ms)"
                                    PingStatus.OFFLINE -> "OFFLINE"
                                    PingStatus.PENDING -> "PINGING..."
                                },
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (pingState) {
                                    PingStatus.ONLINE -> SuccessGreen
                                    PingStatus.OFFLINE -> CyberPink
                                    PingStatus.PENDING -> ElectricCyan
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = ipInput,
                        onValueChange = {
                            ipInput = it
                            viewModel.updatePs4Ip(it)
                        },
                        label = { Text("PS4 Console IP Address", color = CoolGray, fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldHenGold,
                            unfocusedBorderColor = CardSlate,
                            focusedContainerColor = AbyssBlue,
                            unfocusedContainerColor = AbyssBlue,
                            focusedTextColor = OffWhite,
                            unfocusedTextColor = OffWhite
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("payload_ps4_ip_input"),
                        shape = RoundedCornerShape(8.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Lan, contentDescription = null, tint = CoolGray, modifier = Modifier.size(18.dp))
                        }
                    )

                    Button(
                        onClick = {
                            viewModel.updatePs4Ip(ipInput)
                            viewModel.pingConsole()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldHenGold),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                        modifier = Modifier
                            .height(54.dp)
                            .testTag("payload_ping_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Ping PS4", tint = AbyssBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PING", color = AbyssBlue, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Search & Category Filter Section
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search payloads by name or category...", color = CoolGray, fontSize = 12.sp) },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CoolGray) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = CoolGray)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = CardSlate,
                focusedContainerColor = NexusNavy,
                unfocusedContainerColor = NexusNavy,
                focusedTextColor = OffWhite,
                unfocusedTextColor = OffWhite
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("payload_search_field")
        )

        // Horizontal Category Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = cat },
                    label = {
                        Text(
                            cat,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldHenGold,
                        selectedLabelColor = AbyssBlue,
                        containerColor = CardSlate,
                        labelColor = CoolGray
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) GoldHenGold else Color(0x11FFFFFF)
                    ),
                    modifier = Modifier.testTag("payload_category_chip_$cat")
                )
            }
        }

        // Payload Cards List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredPayloads) { payload ->
                val statusState = payloadStatuses[payload.id] ?: PayloadStatusState()
                val isThisSending = sendingPayloadId == payload.id

                val cardBorderColor = when (statusState.status) {
                    PayloadTransmissionStatus.CONNECTING -> GoldHenGold
                    PayloadTransmissionStatus.SENDING -> ElectricCyan
                    PayloadTransmissionStatus.SUCCESS -> SuccessGreen
                    PayloadTransmissionStatus.FAILED -> CyberPink
                    PayloadTransmissionStatus.IDLE -> if (isThisSending) GoldHenGold else Color(0x15FFFFFF)
                }

                val cardBorderWidth = if (statusState.status != PayloadTransmissionStatus.IDLE || isThisSending) 1.5.dp else 1.dp

                Card(
                    colors = CardDefaults.cardColors(containerColor = NexusNavy),
                    border = BorderStroke(cardBorderWidth, cardBorderColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Header row of payload card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ElectricCyan.copy(0.12f))
                                        .border(1.dp, ElectricCyan.copy(0.3f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        payload.category.uppercase(),
                                        fontSize = 9.sp,
                                        color = ElectricCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CardSlate)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        "v${payload.version}",
                                        fontSize = 9.sp,
                                        color = CoolGray,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Status Chip Badge
                                when (statusState.status) {
                                    PayloadTransmissionStatus.CONNECTING -> {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(GoldHenGold.copy(0.15f))
                                                .border(1.dp, GoldHenGold.copy(0.5f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                .testTag("payload_status_chip_${payload.id}")
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(8.dp),
                                                    color = GoldHenGold,
                                                    strokeWidth = 1.2.dp
                                                )
                                                Text(
                                                    "CONNECTING",
                                                    fontSize = 8.sp,
                                                    color = GoldHenGold,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                        }
                                    }
                                    PayloadTransmissionStatus.SENDING -> {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(ElectricCyan.copy(0.15f))
                                                .border(1.dp, ElectricCyan.copy(0.5f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                .testTag("payload_status_chip_${payload.id}")
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(8.dp),
                                                    color = ElectricCyan,
                                                    strokeWidth = 1.2.dp
                                                )
                                                Text(
                                                    "SENDING",
                                                    fontSize = 8.sp,
                                                    color = ElectricCyan,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                        }
                                    }
                                    PayloadTransmissionStatus.SUCCESS -> {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(SuccessGreen.copy(0.15f))
                                                .border(1.dp, SuccessGreen.copy(0.5f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                .testTag("payload_status_chip_${payload.id}")
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Sent",
                                                    tint = SuccessGreen,
                                                    modifier = Modifier.size(10.dp)
                                                )
                                                Text(
                                                    "SENT",
                                                    fontSize = 8.sp,
                                                    color = SuccessGreen,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                        }
                                    }
                                    PayloadTransmissionStatus.FAILED -> {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(CyberPink.copy(0.15f))
                                                .border(1.dp, CyberPink.copy(0.5f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                .testTag("payload_status_chip_${payload.id}")
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Error,
                                                    contentDescription = "Failed",
                                                    tint = CyberPink,
                                                    modifier = Modifier.size(10.dp)
                                                )
                                                Text(
                                                    "FAILED",
                                                    fontSize = 8.sp,
                                                    color = CyberPink,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                        }
                                    }
                                    PayloadTransmissionStatus.IDLE -> { }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(GoldHenGold.copy(0.12f))
                                        .border(1.dp, GoldHenGold.copy(0.3f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        "PORT ${payload.targetPort}",
                                        fontSize = 9.sp,
                                        color = GoldHenGold,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        // Payload Name & Description
                        Text(
                            payload.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = OffWhite,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Text(
                            payload.description,
                            style = MaterialTheme.typography.bodySmall.copy(color = CoolGray)
                        )

                        // Detailed status indicator banner in card body
                        if (statusState.status != PayloadTransmissionStatus.IDLE) {
                            val bannerTheme = when (statusState.status) {
                                PayloadTransmissionStatus.CONNECTING -> PayloadBannerTheme(
                                    GoldHenGold.copy(alpha = 0.12f),
                                    GoldHenGold.copy(alpha = 0.35f),
                                    Icons.Default.Lan,
                                    GoldHenGold
                                )
                                PayloadTransmissionStatus.SENDING -> PayloadBannerTheme(
                                    ElectricCyan.copy(alpha = 0.12f),
                                    ElectricCyan.copy(alpha = 0.35f),
                                    Icons.Default.CloudUpload,
                                    ElectricCyan
                                )
                                PayloadTransmissionStatus.SUCCESS -> PayloadBannerTheme(
                                    SuccessGreen.copy(alpha = 0.12f),
                                    SuccessGreen.copy(alpha = 0.35f),
                                    Icons.Default.CheckCircle,
                                    SuccessGreen
                                )
                                PayloadTransmissionStatus.FAILED -> PayloadBannerTheme(
                                    CyberPink.copy(alpha = 0.12f),
                                    CyberPink.copy(alpha = 0.35f),
                                    Icons.Default.Warning,
                                    CyberPink
                                )
                                else -> PayloadBannerTheme(
                                    CardSlate,
                                    Color.Transparent,
                                    Icons.Default.Info,
                                    CoolGray
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(bannerTheme.bg)
                                    .border(1.dp, bannerTheme.border, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                    .testTag("payload_status_banner_${payload.id}")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (statusState.status == PayloadTransmissionStatus.CONNECTING || statusState.status == PayloadTransmissionStatus.SENDING) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(12.dp),
                                                color = bannerTheme.text,
                                                strokeWidth = 1.5.dp
                                            )
                                        } else {
                                            Icon(
                                                imageVector = bannerTheme.icon,
                                                contentDescription = null,
                                                tint = bannerTheme.text,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                        Text(
                                            text = statusState.message,
                                            fontSize = 10.sp,
                                            color = bannerTheme.text,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    if (statusState.timestamp.isNotBlank()) {
                                        Text(
                                            text = statusState.timestamp,
                                            fontSize = 9.sp,
                                            color = bannerTheme.text.copy(alpha = 0.7f),
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "FW: ${payload.minFirmware}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = CoolGray)
                                )
                                Text("•", color = CoolGray)
                                Text(
                                    payload.binaryAsset,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CoolGray,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                            }

                            val buttonContainerColor = when (statusState.status) {
                                PayloadTransmissionStatus.SUCCESS -> SuccessGreen
                                PayloadTransmissionStatus.FAILED -> CyberPink
                                PayloadTransmissionStatus.CONNECTING, PayloadTransmissionStatus.SENDING -> CardSlate
                                PayloadTransmissionStatus.IDLE -> GoldHenGold
                            }

                            val buttonContentColor = when (statusState.status) {
                                PayloadTransmissionStatus.FAILED -> OffWhite
                                PayloadTransmissionStatus.SUCCESS -> AbyssBlue
                                else -> AbyssBlue
                            }

                            Button(
                                onClick = {
                                    val initTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                                    sendingPayloadId = payload.id
                                    actionLog = "Initiating payload request '${payload.name}' to $ps4Ip:${payload.targetPort}..."
                                    payloadStatuses = payloadStatuses + (payload.id to PayloadStatusState(
                                        status = PayloadTransmissionStatus.CONNECTING,
                                        message = "Connecting to $ps4Ip:${payload.targetPort}...",
                                        timestamp = initTime
                                    ))

                                    viewModel.injectPayload(payload) { stat ->
                                        val nowTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                                        actionLog = "[$ps4Ip:${payload.targetPort}] $stat"

                                        val updatedStatus = when {
                                            stat.contains("Initiating socket") -> PayloadStatusState(
                                                status = PayloadTransmissionStatus.CONNECTING,
                                                message = "Connecting to $ps4Ip:${payload.targetPort}...",
                                                timestamp = nowTime
                                            )
                                            stat.contains("Uploading payload binary") -> PayloadStatusState(
                                                status = PayloadTransmissionStatus.SENDING,
                                                message = "Transmitting ${payload.name}...",
                                                timestamp = nowTime
                                            )
                                            stat.contains("successfully") -> PayloadStatusState(
                                                status = PayloadTransmissionStatus.SUCCESS,
                                                message = "Payload sent & executed successfully!",
                                                timestamp = nowTime
                                            )
                                            stat.contains("Failed") || stat.contains("timed out") || stat.contains("refused") -> PayloadStatusState(
                                                status = PayloadTransmissionStatus.FAILED,
                                                message = stat,
                                                timestamp = nowTime
                                            )
                                            else -> PayloadStatusState(
                                                status = PayloadTransmissionStatus.CONNECTING,
                                                message = stat,
                                                timestamp = nowTime
                                            )
                                        }

                                        payloadStatuses = payloadStatuses + (payload.id to updatedStatus)

                                        if (stat.contains("successfully") || stat.contains("Failed") || stat.contains("timed out") || stat.contains("refused")) {
                                            sendingPayloadId = null
                                        }
                                    }
                                },
                                enabled = sendingPayloadId == null,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = buttonContainerColor,
                                    disabledContainerColor = CardSlate
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .height(36.dp)
                                    .testTag("payload_send_button_${payload.id}")
                            ) {
                                if (isThisSending) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        color = buttonContentColor,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        if (statusState.status == PayloadTransmissionStatus.SENDING) "SENDING..." else "CONNECTING...",
                                        color = buttonContentColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    val (btnIcon, btnText) = when (statusState.status) {
                                        PayloadTransmissionStatus.SUCCESS -> Icons.Default.CheckCircle to "RE-SEND"
                                        PayloadTransmissionStatus.FAILED -> Icons.Default.Refresh to "RETRY"
                                        else -> Icons.Default.Send to "SEND"
                                    }

                                    Icon(
                                        imageVector = btnIcon,
                                        contentDescription = "Send Payload",
                                        tint = buttonContentColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        btnText,
                                        color = buttonContentColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Network Request Console Log / Feedback Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AbyssBlue)
                .border(1.dp, CardSlate, RoundedCornerShape(8.dp))
                .padding(10.dp)
                .testTag("payload_status_log")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = "Status Log",
                    tint = ElectricCyan,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = actionLog,
                    color = if (actionLog.contains("Failed") || actionLog.contains("timeout")) CyberPink else ElectricCyan,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun PayloadScreen(viewModel: NexusViewModel) {
    PayloadLoaderComponent(viewModel = viewModel)
}


@Composable
fun FtpStationScreen(viewModel: NexusViewModel) {
    val isFtpConnected by viewModel.isFtpConnected.collectAsState()
    val ftpPath by viewModel.ftpCurrentPath.collectAsState()
    val ftpFiles by viewModel.ftpFilesList.collectAsState()
    val clientLogs by viewModel.ftpClientLogs.collectAsState()
    val ps4Ip by viewModel.ps4Ip.collectAsState()
    
    val isHosterRunning by viewModel.isFtpServerRunning.collectAsState()
    val hosterLogs by viewModel.ftpServerLogs.collectAsState()

    var ftpIpInput by remember { mutableStateOf(ps4Ip) }
    var ftpPortInput by remember { mutableStateOf("21") }
    var activeSubOption by remember { mutableStateOf("BROWSER") } // "BROWSER", "HOSTER", or "CLEANER"

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Button(
                onClick = { activeSubOption = "BROWSER" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeSubOption == "BROWSER") ElectricCyan else CardSlate
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
            ) {
                Text(
                    "FTP CLIENT",
                    color = if (activeSubOption == "BROWSER") AbyssBlue else OffWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
            Button(
                onClick = { activeSubOption = "HOSTER" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeSubOption == "HOSTER") ElectricCyan else CardSlate
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
            ) {
                Text(
                    "LOCAL HOST",
                    color = if (activeSubOption == "HOSTER") AbyssBlue else OffWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
            Button(
                onClick = { activeSubOption = "CLEANER" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeSubOption == "CLEANER") ElectricCyan else CardSlate
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1.2f),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
            ) {
                Text(
                    "STORAGE CLEANER",
                    color = if (activeSubOption == "CLEANER") AbyssBlue else OffWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (activeSubOption) {
            "BROWSER" -> {
                // FTP Client Browser UI
            if (!isFtpConnected) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NexusNavy),
                    border = BorderStroke(1.dp, Color(0x15FFFFFF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            "CONNECT TO PLAYSTATION FTP SERVER",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = GoldHenGold,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            "Connect directly to the file system of a jailbroken PS4 running GoldHEN. Browse, patch and transfer files.",
                            style = MaterialTheme.typography.bodySmall.copy(color = CoolGray)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = ftpIpInput,
                                onValueChange = { ftpIpInput = it },
                                label = { Text("PS4 IP Address") },
                                modifier = Modifier.weight(1.8f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricCyan,
                                    focusedTextColor = OffWhite
                                )
                            )
                            OutlinedTextField(
                                value = ftpPortInput,
                                onValueChange = { ftpPortInput = it },
                                label = { Text("Port") },
                                modifier = Modifier.weight(0.8f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricCyan,
                                    focusedTextColor = OffWhite
                                )
                            )
                        }

                        Button(
                            onClick = {
                                val port = ftpPortInput.toIntOrNull() ?: 21
                                viewModel.connectToFtp(ftpIpInput, port)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("connect_ftp_button"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Link, contentDescription = null, tint = AbyssBlue)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Connect to FTP Port", color = AbyssBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Connected File browser
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { viewModel.ftpManager.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = OffWhite)
                    }
                    Text(
                        ftpPath,
                        modifier = Modifier.weight(1f),
                        color = ElectricCyan,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    IconButton(onClick = { viewModel.disconnectFtp() }) {
                        Icon(Icons.Default.LinkOff, contentDescription = "Disconnect", tint = CyberPink)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(NexusNavy)
                        .border(1.dp, Color(0x11FFFFFF))
                ) {
                    item {
                        // Directory Navigation parent CDUP
                        if (ftpPath != "/") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.ftpManager.navigateUp() }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.FolderOpen, contentDescription = null, tint = GoldHenGold)
                                Text(".. [Parent Directory]", color = OffWhite, fontFamily = FontFamily.Monospace)
                            }
                            Divider(color = Color(0x11FFFFFF))
                        }
                    }

                    items(ftpFiles) { file ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (file.isDirectory) {
                                        viewModel.ftpManager.changeFtpDirectory(file.path)
                                    } else {
                                        Toast
                                            .makeText(
                                                context,
                                                "Download trigger: ${file.name}",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile,
                                    contentDescription = null,
                                    tint = if (file.isDirectory) GoldHenGold else OffWhite
                                )
                                Column {
                                    Text(
                                        file.name,
                                        color = OffWhite,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                    Text(
                                        file.permissions,
                                        color = CoolGray,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            if (!file.isDirectory) {
                                val mb = file.sizeBytes / (1024.0 * 1024.0)
                                Text(
                                    text = if (mb > 0) String.format("%.2f MB", mb) else "${file.sizeBytes} B",
                                    fontSize = 11.sp,
                                    color = CoolGray,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Divider(color = Color(0x05FFFFFF))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            SectionLoggerBox(title = "FTP CLI ACTIVITY LOGS", logs = clientLogs)
        }

        "HOSTER" -> {
            // FTP Server Host UI
            Card(
                colors = CardDefaults.cardColors(containerColor = NexusNavy),
                border = BorderStroke(1.dp, Color(0x15FFFFFF)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "MOBILE SYSTEM FTP HOST",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = GoldHenGold,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isHosterRunning) SuccessGreen.copy(0.15f) else CyberPink.copy(0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                if (isHosterRunning) "ACTIVE" else "OFFLINE",
                                color = if (isHosterRunning) SuccessGreen else CyberPink,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Text(
                        "Enables computers or even your PlayStation's native media players to connect directly to this Android phone via FTP protocols.",
                        style = MaterialTheme.typography.bodySmall.copy(color = CoolGray)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Local FTP Binding Port: 2121",
                            color = OffWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { viewModel.toggleFtpHoster() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isHosterRunning) CyberPink else SuccessGreen
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("toggle_ftp_hoster"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            if (isHosterRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = AbyssBlue
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (isHosterRunning) "Stop FTP Host Server" else "Start FTP Host Server",
                            color = AbyssBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            SectionLoggerBox(title = "FTP HOSTER ENGINE TERMINAL", logs = hosterLogs)
        }

        "CLEANER" -> {
            Ps4StorageCleanerScreen(viewModel = viewModel)
        }
    }
}
}

@Composable
fun Ps4StorageCleanerScreen(viewModel: NexusViewModel) {
    val isScanning by viewModel.isCleanerScanning.collectAsState()
    val isCleaning by viewModel.isCleanerCleaning.collectAsState()
    val progress by viewModel.cleanerProgress.collectAsState()
    val logs by viewModel.cleanerLogs.collectAsState()
    val cleanableItems by viewModel.cleanableItems.collectAsState()
    val totalReclaimed by viewModel.totalReclaimedSize.collectAsState()

    var scanExternal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Core Control Card
        Card(
            colors = CardDefaults.cardColors(containerColor = NexusNavy),
            border = BorderStroke(1.dp, Color(0x15FFFFFF)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column {
                    Text(
                        "ORBIS WASTE DIAGNOSTICS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = GoldHenGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "Wipes redundant update payloads, browser logs, temporary cache folders, and core kernel dump registries over the network.",
                        style = MaterialTheme.typography.bodySmall.copy(color = CoolGray)
                    )
                }

                HorizontalDivider(color = Color(0x10FFFFFF), thickness = 1.dp)

                // Sub-Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text("Include Extended Partition", color = OffWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Scan USB mounts (/mnt/ext0/) alongside internal database.", color = CoolGray, fontSize = 9.sp)
                        }
                    }
                    Switch(
                        checked = scanExternal,
                        onCheckedChange = { scanExternal = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AbyssBlue,
                            checkedTrackColor = ElectricCyan,
                            uncheckedThumbColor = CoolGray,
                            uncheckedTrackColor = CardSlate
                        ),
                        enabled = !isScanning && !isCleaning
                    )
                }

                // Analyze Trigger
                Button(
                    onClick = { viewModel.startStorageScan(scanExternal) },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                    enabled = !isScanning && !isCleaning,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = AbyssBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Trigger Space Analyzer", color = AbyssBlue, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }
        }

        // Animated progress
        if (isScanning || isCleaning) {
            Card(
                colors = CardDefaults.cardColors(containerColor = NexusNavy),
                border = BorderStroke(1.dp, Color(0x15FFFFFF)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            if (isScanning) "SCANNING PS4 PARTITION STRUCTURES..." else "WIPING DETECTED DISK JUNK...",
                            color = OffWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${(progress * 100).toInt()}%",
                            color = ElectricCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = ElectricCyan,
                        trackColor = CardSlate
                    )
                }
            }
        }

        // Checklist of identified files
        if (cleanableItems.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = NexusNavy),
                border = BorderStroke(1.dp, Color(0x15FFFFFF)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "RETRIEVABLE DISK CACHES FOUND",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = GoldHenGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )

                    cleanableItems.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CardSlate)
                                .border(1.dp, if (item.isSelected) ElectricCyan.copy(0.3f) else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable { viewModel.toggleCleanableItem(item.id) }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Checkbox(
                                    checked = item.isSelected,
                                    onCheckedChange = { viewModel.toggleCleanableItem(item.id) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = ElectricCyan,
                                        uncheckedColor = CoolGray,
                                        checkmarkColor = AbyssBlue
                                    ),
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text(item.name, color = OffWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(item.description, color = CoolGray, fontSize = 9.sp)
                                    Text("Path: ${item.path}", color = ElectricCyan.copy(0.7f), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                }
                            }
                            Text(
                                item.sizeDisplay,
                                color = if (item.sizeBytes > 0) CyberPink else CoolGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0x08FFFFFF))

                    val selectedBytes = cleanableItems.filter { it.isSelected }.sumOf { it.sizeBytes }
                    val selectedDisplay = if (selectedBytes == 0L) "0 MB" else String.format(Locale.US, "%.1f MB", selectedBytes / (1024f * 1024f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total selected", color = CoolGray, fontSize = 9.sp)
                            Text(selectedDisplay, color = CyberPink, fontSize = 16.sp, fontWeight = FontWeight.Black)
                        }

                        Button(
                            onClick = { viewModel.runStorageCleanup() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberPink),
                            enabled = selectedBytes > 0 && !isCleaning,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = AbyssBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Wipe Selections", color = AbyssBlue, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Summary Banner
        if (totalReclaimed != "0 MB" && totalReclaimed != "0.0 MB") {
            Card(
                colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(0.12f)),
                border = BorderStroke(1.dp, SuccessGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            tint = AbyssBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            "SPACE SECURELY RECLAIMED!",
                            color = SuccessGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            "Cleaned up $totalReclaimed of bloated cache data from target memory partition structures.",
                            color = OffWhite,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Terminal Output
        SectionLoggerBox(title = "CLEANER SYSTEM ENGINE TRACES", logs = logs)
    }
}

@Composable
fun WirelessTransfersScreen(viewModel: NexusViewModel) {
    val isRunning by viewModel.isWirelessServerRunning.collectAsState()
    val logs by viewModel.serverLogs.collectAsState()
    val files by viewModel.receivedFiles.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SearchBarHeader(
            title = "WIRELESS FILE TRANSFER HUB",
            subtitle = "Sleek and easy local PC-to-Android and Android-to-PC sync engine",
            icon = Icons.Default.SwapHorizontalCircle,
            accentColor = ElectricCyan
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = NexusNavy),
            border = BorderStroke(1.dp, Color(0x15FFFFFF)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "HTTP SERVER CONTROLLER",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = GoldHenGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isRunning) SuccessGreen.copy(0.15f) else CyberPink.copy(0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            if (isRunning) "SERVER LIVE" else "OFFLINE",
                            color = if (isRunning) SuccessGreen else CyberPink,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                if (isRunning) {
                    val ip = remember { viewModel.getLocalIpAddress() }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AbyssBlue)
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "ACCESS FROM COMPUTER (WINDOWS EXP/WEB):",
                            fontSize = 10.sp,
                            color = CoolGray,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "http://$ip:8080/",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = ElectricCyan,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Text(
                    "Launch the transfer stream. Once enabled, navigate to the local URL under any browser on your computer network to share PKGs and customized themes seamlessly.",
                    style = MaterialTheme.typography.bodySmall.copy(color = CoolGray)
                )

                Button(
                    onClick = { viewModel.toggleWirelessServer() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) CyberPink else ElectricCyan
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("toggle_server_button"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = AbyssBlue
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        if (isRunning) "Close Server connection" else "Launch Transfer Server",
                        color = AbyssBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Section detailing Hosted / Received Files on device
        Text(
            "SHARED/RECEIVED STORAGE FILES",
            style = MaterialTheme.typography.titleSmall.copy(
                color = GoldHenGold,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(NexusNavy)
                .border(1.dp, Color(0x11FFFFFF))
        ) {
            if (files.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CloudQueue, contentDescription = null, tint = CoolGray, modifier = Modifier.size(36.dp))
                        Text(
                            "No external PC transfers found. Turn on the server to upload PKGs/Themes.",
                            color = CoolGray,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(files) { file ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                file.name,
                                color = OffWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            val mb = file.length() / (1024.0 * 1024.0)
                            Text(
                                text = String.format("%.2f MB | /WirelessTransfers", mb),
                                fontSize = 11.sp,
                                color = CoolGray,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        IconButton(onClick = { viewModel.deleteWirelessFile(file) }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = CyberPink)
                        }
                    }
                    Divider(color = Color(0x05FFFFFF))
                }
            }
        }

        SectionLoggerBox(title = "WIRELESS TRAFFIC ENGINE ACTIVITY LOGS", logs = logs)
    }
}

@Composable
fun ThemesCatalogScreen(viewModel: NexusViewModel) {
    CustomThemesComponent(viewModel = viewModel)
}

@Composable
fun ThemeScreen(viewModel: NexusViewModel) {
    CustomThemesComponent(viewModel = viewModel)
}

@Composable
fun CustomThemePreviewDialog(
    theme: Ps4Theme,
    ps4Ip: String,
    isApplied: Boolean,
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = NexusNavy),
            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Modal Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Tv, contentDescription = null, tint = ElectricCyan)
                        Text(
                            "PS4 HOME HUD SIMULATOR",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = OffWhite,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = CoolGray)
                    }
                }

                // Simulated PS4 Screen TV View
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    border = BorderStroke(1.dp, CardSlate),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Background Image
                        AsyncImage(
                            model = theme.imageUrl,
                            contentDescription = "Simulated Wallpaper",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Dark Vignette Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.5f),
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.7f)
                                        )
                                    )
                                )
                        )

                        // Top Orbis System Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(ElectricCyan, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = AbyssBlue, modifier = Modifier.size(10.dp))
                                }
                                Text("NeoUser", color = OffWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Wifi, contentDescription = null, tint = OffWhite, modifier = Modifier.size(10.dp))
                                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = OffWhite, modifier = Modifier.size(10.dp))
                                Text("10:30 AM", color = OffWhite, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Simulated App Icons Carousel
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val icons = listOf("Library", "Settings", "Browser", "Trophies", "Friends")
                            icons.forEachIndexed { index, app ->
                                val isSelected = index == 0
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) ElectricCyan else Color.White.copy(0.2f))
                                            .border(
                                                1.dp,
                                                if (isSelected) GoldHenGold else Color.Transparent,
                                                RoundedCornerShape(6.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            when (app) {
                                                "Library" -> Icons.Default.Palette
                                                "Settings" -> Icons.Default.Settings
                                                "Browser" -> Icons.Default.Language
                                                "Trophies" -> Icons.Default.EmojiEvents
                                                else -> Icons.Default.People
                                            },
                                            contentDescription = app,
                                            tint = if (isSelected) AbyssBlue else OffWhite,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(app, color = OffWhite, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Theme Info
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(theme.title, style = MaterialTheme.typography.titleMedium.copy(color = OffWhite, fontWeight = FontWeight.Bold))
                    Text(theme.description, style = MaterialTheme.typography.bodySmall.copy(color = CoolGray))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Author: ${theme.author}", fontSize = 10.sp, color = CoolGray)
                        Text("Size: ${theme.sizeMb} MB", fontSize = 10.sp, color = CoolGray, fontFamily = FontFamily.Monospace)
                    }
                }

                // Apply Action Button inside Modal
                Button(
                    onClick = onApply,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isApplied) GoldHenGold else ElectricCyan
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (isApplied) Icons.Default.CheckCircle else Icons.Default.Send,
                        contentDescription = null,
                        tint = AbyssBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        if (isApplied) "CURRENTLY APPLIED TO $ps4Ip" else "APPLY THEME TO $ps4Ip NOW",
                        color = AbyssBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CustomThemeStudioSection(
    viewModel: NexusViewModel,
    studioWallpaper: String,
    studioTitle: String,
    studioAuthor: String,
    studioAccentColor: String,
    studioIcons: Map<String, String>,
    isCompiling: Boolean,
    compileProgress: Float,
    modifier: Modifier = Modifier
) {
    var editorTab by remember { mutableStateOf("BACKGROUND") } // BACKGROUND, ICONS, ACCENT
    var activeIconKeyForPicker by remember { mutableStateOf<String?>(null) }

    // Android Activity Result Launchers for picking images from device library
    val bgPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.updateStudioWallpaper(it.toString())
        }
    }

    val iconPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        val key = activeIconKeyForPicker
        if (uri != null && key != null) {
            viewModel.updateStudioIcon(key, uri.toString())
        }
    }

    val parsedAccentColor = remember(studioAccentColor) {
        try {
            Color(android.graphics.Color.parseColor(if (studioAccentColor.startsWith("#")) studioAccentColor else "#$studioAccentColor"))
        } catch (e: Exception) {
            ElectricCyan
        }
    }

    val uiAppIconsList = listOf(
        Triple("Library", "Game Library", Icons.Default.SportsEsports),
        Triple("Settings", "System Settings", Icons.Default.Settings),
        Triple("Browser", "Web Browser", Icons.Default.Language),
        Triple("Trophies", "Trophies & Badges", Icons.Default.EmojiEvents),
        Triple("Store", "PlayStation Store", Icons.Default.ShoppingCart),
        Triple("Profile", "User Profile & Friends", Icons.Default.Person),
        Triple("Notifications", "System Alerts", Icons.Default.Notifications),
        Triple("Power", "Power Options", Icons.Default.PowerSettingsNew)
    )

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Brush, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                Text(
                    "ORBIS THEME DESIGNER & ASSET EDITOR",
                    color = ElectricCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(GoldHenGold.copy(0.15f))
                    .border(1.dp, GoldHenGold, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("ASSET STUDIO", fontSize = 8.sp, color = GoldHenGold, fontWeight = FontWeight.Bold)
            }
        }

        // Live Simulated PS4 Screen Canvas Preview
        Card(
            colors = CardDefaults.cardColors(containerColor = NexusNavy),
            border = BorderStroke(1.5.dp, parsedAccentColor.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("theme_studio_preview_card")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                // Background Wallpaper (supports content:// URI from gallery or web URL)
                AsyncImage(
                    model = studioWallpaper,
                    contentDescription = "Theme Studio Wallpaper Preview",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Vignette & Contrast Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Black.copy(alpha = 0.65f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // HUD Top Status Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(studioTitle.uppercase(), color = OffWhite, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            Text("By $studioAuthor", color = parsedAccentColor, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Wifi, contentDescription = null, tint = OffWhite, modifier = Modifier.size(12.dp))
                            Text("10:30 AM", color = OffWhite, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        }
                    }

                    // Interactive App Icons Bar Preview
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiAppIconsList) { (appKey, label, defaultVec) ->
                            val iconVal = studioIcons[appKey] ?: ""
                            val isCustomUri = iconVal.startsWith("content://") || iconVal.startsWith("file://") || iconVal.startsWith("http")

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(parsedAccentColor.copy(0.25f))
                                        .border(1.dp, parsedAccentColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCustomUri) {
                                        AsyncImage(
                                            model = iconVal,
                                            contentDescription = label,
                                            modifier = Modifier.size(22.dp).clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            imageVector = defaultVec,
                                            contentDescription = label,
                                            tint = parsedAccentColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(
                                    appKey,
                                    color = OffWhite,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Editor Sub-Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val tabs = listOf(
                Triple("BACKGROUND", "🖼️ Wallpaper", Icons.Default.Image),
                Triple("ICONS", "🎨 UI Icons (${uiAppIconsList.size})", Icons.Default.Widgets),
                Triple("ACCENT", "🏷️ Title & Colors", Icons.Default.Palette)
            )

            tabs.forEach { (key, label, iconVec) ->
                val isSelected = editorTab == key
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) ElectricCyan else CardSlate)
                        .clickable { editorTab = key }
                        .padding(vertical = 8.dp)
                        .testTag("theme_editor_subtab_$key"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            iconVec,
                            contentDescription = null,
                            tint = if (isSelected) AbyssBlue else OffWhite,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = label,
                            color = if (isSelected) AbyssBlue else OffWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        when (editorTab) {
            "BACKGROUND" -> {
                // Background & Wallpaper Selection Panel
                Card(
                    colors = CardDefaults.cardColors(containerColor = NexusNavy),
                    border = BorderStroke(1.dp, CardSlate),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("SELECT BACKGROUND IMAGE FROM DEVICE", color = GoldHenGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                        Text(
                            "Choose any wallpaper or photo from your phone/device gallery to use as the PS4 home screen background.",
                            color = CoolGray,
                            fontSize = 10.sp
                        )

                        // Main Button to Launch Device Media Picker
                        Button(
                            onClick = { bgPickerLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberPink),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("pick_device_wallpaper_btn")
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = AbyssBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("PICK BACKGROUND FROM DEVICE GALLERY", color = AbyssBlue, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }

                        if (studioWallpaper.startsWith("content://") || studioWallpaper.startsWith("file://")) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CardSlate)
                                    .padding(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                                    Text(
                                        "Device Image Selected: $studioWallpaper",
                                        color = SuccessGreen,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = Color(0x10FFFFFF), thickness = 1.dp)

                        Text("OR USE PRESET WALLPAPERS / WEB IMAGE URL:", color = CoolGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = studioWallpaper,
                            onValueChange = { viewModel.updateStudioWallpaper(it) },
                            label = { Text("Image URL or Path") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricCyan,
                                focusedTextColor = OffWhite,
                                unfocusedTextColor = CoolGray
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val presets = listOf(
                                "https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=640" to "Cyber",
                                "https://images.unsplash.com/photo-1511512578047-dfb367046420?q=80&w=640" to "Gamer",
                                "https://images.unsplash.com/photo-1448375240586-882707db888b?q=80&w=640" to "Scenic",
                                "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?q=80&w=640" to "Neon"
                            )
                            presets.forEach { (url, name) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (studioWallpaper == url) ElectricCyan else CardSlate)
                                        .clickable { viewModel.updateStudioWallpaper(url) }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        name,
                                        color = if (studioWallpaper == url) AbyssBlue else OffWhite,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            "ICONS" -> {
                // UI Icons Replacement Panel
                Card(
                    colors = CardDefaults.cardColors(containerColor = NexusNavy),
                    border = BorderStroke(1.dp, CardSlate),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("REPLACE SPECIFIC UI ICONS FROM DEVICE LIBRARY", color = GoldHenGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("${uiAppIconsList.size} icons", color = CoolGray, fontSize = 9.sp)
                        }

                        Text(
                            "Select individual system UI icons to replace with custom PNG/JPEG images from your device.",
                            color = CoolGray,
                            fontSize = 10.sp
                        )

                        uiAppIconsList.forEach { (appKey, label, defaultVec) ->
                            val currentIconVal = studioIcons[appKey] ?: ""
                            val isCustomImage = currentIconVal.startsWith("content://") || currentIconVal.startsWith("file://") || currentIconVal.startsWith("http")

                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardSlate),
                                border = BorderStroke(1.dp, if (isCustomImage) CyberPink.copy(0.5f) else Color(0x05FFFFFF)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // Icon Thumbnail
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(NexusNavy)
                                                .border(1.dp, if (isCustomImage) CyberPink else parsedAccentColor, RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isCustomImage) {
                                                AsyncImage(
                                                    model = currentIconVal,
                                                    contentDescription = label,
                                                    modifier = Modifier.size(28.dp).clip(RoundedCornerShape(6.dp)),
                                                    contentScale = ContentScale.Crop
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = defaultVec,
                                                    contentDescription = label,
                                                    tint = parsedAccentColor,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }

                                        Column {
                                            Text(label, color = OffWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text(
                                                if (isCustomImage) "Custom Device Image" else "Style: ${currentIconVal.ifBlank { "Default" }}",
                                                color = if (isCustomImage) SuccessGreen else CoolGray,
                                                fontSize = 9.sp
                                            )
                                        }
                                    }

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Pick from device button for this specific icon
                                        Button(
                                            onClick = {
                                                activeIconKeyForPicker = appKey
                                                iconPickerLauncher.launch("image/*")
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                            modifier = Modifier
                                                .height(32.dp)
                                                .testTag("pick_icon_btn_$appKey")
                                        ) {
                                            Icon(Icons.Default.FileUpload, contentDescription = null, tint = AbyssBlue, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("PICK IMAGE", color = AbyssBlue, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                        }

                                        if (isCustomImage) {
                                            IconButton(
                                                onClick = { viewModel.updateStudioIcon(appKey, "CLASSIC BLUE") },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.Refresh, contentDescription = "Reset Icon", tint = CoolGray, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "ACCENT" -> {
                // Title, Author & Color Palette Panel
                Card(
                    colors = CardDefaults.cardColors(containerColor = NexusNavy),
                    border = BorderStroke(1.dp, CardSlate),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("THEME METADATA & ACCENT COLOR", color = GoldHenGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = studioTitle,
                            onValueChange = { viewModel.updateStudioTitle(it) },
                            label = { Text("Theme Title") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricCyan,
                                focusedTextColor = OffWhite,
                                unfocusedTextColor = CoolGray
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = studioAuthor,
                            onValueChange = { viewModel.updateStudioAuthor(it) },
                            label = { Text("Author / Creator") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricCyan,
                                focusedTextColor = OffWhite,
                                unfocusedTextColor = CoolGray
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        HorizontalDivider(color = Color(0x10FFFFFF), thickness = 1.dp)

                        Text("ACCENT HIGHLIGHT COLOR PALETTE:", color = CoolGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val colorPresets = listOf(
                                "#00E5FF" to "Cyan",
                                "#FFD700" to "Gold",
                                "#FF007F" to "Pink",
                                "#00FF66" to "Emerald",
                                "#9900FF" to "Purple"
                            )

                            colorPresets.forEach { (hex, colorName) ->
                                val colorVal = Color(android.graphics.Color.parseColor(hex))
                                val isSelected = studioAccentColor.equals(hex, ignoreCase = true)

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(colorVal)
                                        .border(
                                            width = if (isSelected) 2.5.dp else 0.dp,
                                            color = if (isSelected) OffWhite else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { viewModel.updateStudioAccentColor(hex) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = AbyssBlue, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Compile & Add Theme PKG Button
        if (isCompiling) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("COMPILING CUSTOM THEME PKG...", color = CyberPink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                LinearProgressIndicator(progress = { compileProgress }, color = CyberPink, modifier = Modifier.fillMaxWidth())
            }
        } else {
            Button(
                onClick = { viewModel.compileStudioTheme() },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("compile_studio_theme_btn"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = AbyssBlue, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("COMPILE THEME & ADD TO LIBRARY", color = AbyssBlue, fontWeight = FontWeight.Black, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun CustomThemesComponent(
    viewModel: NexusViewModel,
    modifier: Modifier = Modifier
) {
    val ps4Ip by viewModel.ps4Ip.collectAsState()
    val installedThemes by viewModel.installedThemes.collectAsState()
    val onlineThemes by viewModel.onlineThemesList.collectAsState()
    val activeThemeId by viewModel.activeThemeId.collectAsState()
    val searchQuery by viewModel.themeSearchQuery.collectAsState()
    val repoDownloads by viewModel.repoDownloads.collectAsState()
    val activeDownloads by viewModel.activeDownloads.collectAsState()

    // Studio state
    val studioWallpaper by viewModel.studioWallpaperUrl.collectAsState()
    val studioTitle by viewModel.studioThemeTitle.collectAsState()
    val studioAuthor by viewModel.studioThemeAuthor.collectAsState()
    val studioAccentColor by viewModel.studioAccentColorHex.collectAsState()
    val studioIcons by viewModel.studioIconsMap.collectAsState()
    val isCompiling by viewModel.isCompilingTheme.collectAsState()
    val compileProgress by viewModel.themeCompileProgress.collectAsState()

    var activeFilterChip by remember { mutableStateOf("ALL") } // ALL, INSTALLED, STORE, STUDIO
    var selectedCategory by remember { mutableStateOf("ALL") }
    var selectedSortBy by remember { mutableStateOf("RATING") } // RATING, DOWNLOADS, NEWEST, TITLE, SIZE
    var isSortAscending by remember { mutableStateOf(false) }
    var isSortMenuExpanded by remember { mutableStateOf(false) }
    var previewingTheme by remember { mutableStateOf<Ps4Theme?>(null) }
    var actionLog by remember { mutableStateOf("Ready to manage & apply themes. Connected PS4: $ps4Ip") }
    var isApplyingThemeId by remember { mutableStateOf<String?>(null) }

    // Combine list based on filter, category, search query & sorting
    val displayedThemes = remember(
        installedThemes,
        onlineThemes,
        activeFilterChip,
        searchQuery,
        selectedCategory,
        selectedSortBy,
        isSortAscending
    ) {
        val baseList = when (activeFilterChip) {
            "INSTALLED" -> installedThemes
            "STORE" -> onlineThemes
            "STUDIO" -> installedThemes.filter { it.id.startsWith("studio_") }
            else -> (installedThemes + onlineThemes).distinctBy { it.id }
        }

        // 1. Filter by category
        val categoryFiltered = if (selectedCategory == "ALL") {
            baseList
        } else {
            baseList.filter { it.category.contains(selectedCategory, ignoreCase = true) }
        }

        // 2. Filter by search query
        val searchFiltered = if (searchQuery.isBlank()) {
            categoryFiltered
        } else {
            categoryFiltered.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.author.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true)
            }
        }

        // 3. Sort list
        when (selectedSortBy) {
            "RATING" -> if (isSortAscending) searchFiltered.sortedBy { it.rating } else searchFiltered.sortedByDescending { it.rating }
            "DOWNLOADS" -> if (isSortAscending) searchFiltered.sortedBy { it.downloadsCount } else searchFiltered.sortedByDescending { it.downloadsCount }
            "NEWEST" -> if (isSortAscending) searchFiltered.sortedBy { it.dateAddedMs } else searchFiltered.sortedByDescending { it.dateAddedMs }
            "TITLE" -> if (isSortAscending) searchFiltered.sortedBy { it.title.lowercase() } else searchFiltered.sortedByDescending { it.title.lowercase() }
            "SIZE" -> if (isSortAscending) searchFiltered.sortedBy { it.sizeMb } else searchFiltered.sortedByDescending { it.sizeMb }
            else -> searchFiltered
        }
    }

    val activeTheme = remember(installedThemes, onlineThemes, activeThemeId) {
        (installedThemes + onlineThemes).find { it.id == activeThemeId } ?: installedThemes.firstOrNull()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        SearchBarHeader(
            title = "CUSTOM THEMES & ORBIS UI MANAGER",
            subtitle = "View, customize, manage, and apply custom themes to your PS4 console",
            icon = Icons.Default.Palette,
            accentColor = ElectricCyan
        )

        // Currently Applied Active Theme Hero Card
        activeTheme?.let { currentTheme ->
            Card(
                colors = CardDefaults.cardColors(containerColor = NexusNavy),
                border = BorderStroke(1.dp, GoldHenGold.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Background Image Preview Banner
                    AsyncImage(
                        model = currentTheme.imageUrl,
                        contentDescription = "Active Theme Background",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.3f),
                                        NexusNavy
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Active Pulse Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(SuccessGreen.copy(0.2f))
                                    .border(1.dp, SuccessGreen, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(SuccessGreen, CircleShape)
                                    )
                                    Text(
                                        "CURRENTLY APPLIED TO PS4",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Router, contentDescription = null, tint = CoolGray, modifier = Modifier.size(12.dp))
                                Text("TARGET: $ps4Ip", fontSize = 10.sp, color = CoolGray, fontFamily = FontFamily.Monospace)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    currentTheme.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = OffWhite,
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    "Designed by ${currentTheme.author} • ${currentTheme.sizeMb} MB",
                                    style = MaterialTheme.typography.labelSmall.copy(color = CoolGray)
                                )
                            }

                            Button(
                                onClick = { previewingTheme = currentTheme },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldHenGold),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = null, tint = AbyssBlue, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("FULL HUD PREVIEW", color = AbyssBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateThemeSearchQuery(it) },
            placeholder = { Text("Search themes by title, author, or category...", color = CoolGray, fontSize = 12.sp) },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CoolGray) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateThemeSearchQuery("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = CoolGray)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = CardSlate,
                focusedContainerColor = NexusNavy,
                unfocusedContainerColor = NexusNavy,
                focusedTextColor = OffWhite,
                unfocusedTextColor = OffWhite
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("custom_theme_search_input")
        )

        // Primary Source Filter Chips (All, Installed, Store, Studio)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val chips = listOf(
                "ALL" to "All Themes (${installedThemes.size + onlineThemes.size})",
                "INSTALLED" to "Installed (${installedThemes.size})",
                "STORE" to "Online Store (${onlineThemes.size})",
                "STUDIO" to "Create / Studio"
            )

            items(chips) { (key, label) ->
                val isSelected = activeFilterChip == key
                FilterChip(
                    selected = isSelected,
                    onClick = { activeFilterChip = key },
                    label = {
                        Text(
                            label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricCyan,
                        selectedLabelColor = AbyssBlue,
                        containerColor = CardSlate,
                        labelColor = CoolGray
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) ElectricCyan else Color(0x11FFFFFF)
                    ),
                    modifier = Modifier.testTag("theme_filter_chip_$key")
                )
            }
        }

        if (activeFilterChip != "STUDIO") {
            // Category Filter Pills
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "CATEGORY FILTER:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldHenGold,
                    letterSpacing = 0.5.sp
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val categories = listOf(
                        "ALL" to "All Categories",
                        "Gothic & Dark" to "Gothic & Dark",
                        "Cyberpunk & Sci-Fi" to "Cyberpunk & Sci-Fi",
                        "Nature & Scenic" to "Nature & Scenic",
                        "Anime & Gaming" to "Anime & Gaming",
                        "Minimal & HUD" to "Minimal & HUD"
                    )

                    items(categories) { (catKey, catLabel) ->
                        val isCatSelected = selectedCategory == catKey
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCatSelected) CyberPink else CardSlate)
                                .clickable { selectedCategory = catKey }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("theme_category_chip_$catKey"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                catLabel,
                                color = if (isCatSelected) AbyssBlue else OffWhite,
                                fontSize = 10.sp,
                                fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Sort & Filter Toolbar
            Card(
                colors = CardDefaults.cardColors(containerColor = NexusNavy),
                border = BorderStroke(1.dp, CardSlate),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Result Counter Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ElectricCyan.copy(0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "${displayedThemes.size} RESULT${if (displayedThemes.size != 1) "S" else ""}",
                                color = ElectricCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        if (searchQuery.isNotEmpty() || selectedCategory != "ALL" || selectedSortBy != "RATING") {
                            IconButton(
                                onClick = {
                                    viewModel.updateThemeSearchQuery("")
                                    selectedCategory = "ALL"
                                    selectedSortBy = "RATING"
                                    isSortAscending = false
                                },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Reset Filters",
                                    tint = CoolGray,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    // Sort Selector & Order Toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("SORT BY:", color = CoolGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)

                        Box {
                            val currentSortLabel = when (selectedSortBy) {
                                "RATING" -> "Rating"
                                "DOWNLOADS" -> "Downloads"
                                "NEWEST" -> "Date Added"
                                "TITLE" -> "Name"
                                "SIZE" -> "File Size"
                                else -> "Rating"
                            }

                            Button(
                                onClick = { isSortMenuExpanded = true },
                                colors = ButtonDefaults.buttonColors(containerColor = CardSlate),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                modifier = Modifier
                                    .height(30.dp)
                                    .testTag("theme_sort_dropdown_btn")
                            ) {
                                Text(currentSortLabel, color = ElectricCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(14.dp))
                            }

                            DropdownMenu(
                                expanded = isSortMenuExpanded,
                                onDismissRequest = { isSortMenuExpanded = false },
                                modifier = Modifier.background(NexusNavy)
                            ) {
                                val sortOptions = listOf(
                                    "RATING" to "Rating / Popularity",
                                    "DOWNLOADS" to "Most Downloaded",
                                    "NEWEST" to "Date Added (Newest)",
                                    "TITLE" to "Name (A-Z)",
                                    "SIZE" to "File Size"
                                )
                                sortOptions.forEach { (optionKey, optionLabel) ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                optionLabel,
                                                color = if (selectedSortBy == optionKey) ElectricCyan else OffWhite,
                                                fontSize = 11.sp,
                                                fontWeight = if (selectedSortBy == optionKey) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            selectedSortBy = optionKey
                                            isSortMenuExpanded = false
                                        },
                                        modifier = Modifier.testTag("theme_sort_option_$optionKey")
                                    )
                                }
                            }
                        }

                        // Order Toggle (Asc / Desc)
                        IconButton(
                            onClick = { isSortAscending = !isSortAscending },
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(CardSlate)
                                .testTag("theme_sort_direction_btn")
                        ) {
                            Icon(
                                imageVector = if (isSortAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                contentDescription = "Toggle Sort Direction",
                                tint = ElectricCyan,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // Main Content: Grid / List / Studio Creator
        if (activeFilterChip == "STUDIO") {
            // Theme Studio Customizer View
            CustomThemeStudioSection(
                viewModel = viewModel,
                studioWallpaper = studioWallpaper,
                studioTitle = studioTitle,
                studioAuthor = studioAuthor,
                studioAccentColor = studioAccentColor,
                studioIcons = studioIcons,
                isCompiling = isCompiling,
                compileProgress = compileProgress,
                modifier = Modifier.weight(1f)
            )
        } else {
            // Preview Grid of Themes
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(displayedThemes) { theme ->
                    val isApplied = theme.id == activeThemeId
                    val isInstalled = installedThemes.any { it.id == theme.id }
                    val downProgress = repoDownloads[theme.id] ?: activeDownloads[theme.id]
                    val isApplyingThis = isApplyingThemeId == theme.id

                    Card(
                        colors = CardDefaults.cardColors(containerColor = NexusNavy),
                        border = BorderStroke(
                            1.dp,
                            if (isApplied) GoldHenGold else Color(0x15FFFFFF)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            // Thumbnail Preview with Overlay Badge
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(95.dp)
                            ) {
                                AsyncImage(
                                    model = theme.imageUrl,
                                    contentDescription = theme.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.7f)
                                                )
                                            )
                                        )
                                )

                                // Status Badge on Image
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isApplied) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(GoldHenGold)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                "ACTIVE",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Black,
                                                color = AbyssBlue
                                            )
                                        }
                                    } else if (isInstalled) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(SuccessGreen.copy(0.85f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                "INSTALLED",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = OffWhite
                                            )
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(CardSlate.copy(0.85f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                "STORE",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CoolGray
                                            )
                                        }
                                    }

                                    // Rating
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color.Black.copy(alpha = 0.6f))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = GoldHenGold, modifier = Modifier.size(10.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("${theme.rating}", fontSize = 9.sp, color = OffWhite, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Card Body Info & Controls
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    theme.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = OffWhite,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "by ${theme.author} • ${theme.sizeMb} MB",
                                        fontSize = 9.sp,
                                        color = CoolGray,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    // Category Pill Tag
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(CardSlate)
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(theme.category, fontSize = 7.sp, color = GoldHenGold, fontWeight = FontWeight.Bold)
                                    }
                                }

                                HorizontalDivider(color = Color(0x0AFFFFFF))

                                // Grid Item Action Controls
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Preview Button
                                    OutlinedButton(
                                        onClick = { previewingTheme = theme },
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                                        border = BorderStroke(1.dp, CardSlate),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(30.dp)
                                            .testTag("preview_theme_button_${theme.id}")
                                    ) {
                                        Icon(Icons.Default.Visibility, contentDescription = "Preview", tint = CoolGray, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("HUD", fontSize = 9.sp, color = CoolGray)
                                    }

                                    if (isInstalled || isApplied) {
                                        // Apply to PS4 Button
                                        Button(
                                            onClick = {
                                                isApplyingThemeId = theme.id
                                                viewModel.applyThemeToPs4(theme) { result ->
                                                    actionLog = result
                                                    if (result.contains("Success") || result.contains("Error")) {
                                                        isApplyingThemeId = null
                                                    }
                                                }
                                            },
                                            enabled = !isApplyingThis && !isApplied,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = ElectricCyan,
                                                disabledContainerColor = if (isApplied) GoldHenGold.copy(0.2f) else CardSlate
                                            ),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                            modifier = Modifier
                                                .weight(1.3f)
                                                .height(30.dp)
                                                .testTag("apply_theme_button_${theme.id}")
                                        ) {
                                            if (isApplyingThis) {
                                                CircularProgressIndicator(modifier = Modifier.size(12.dp), color = AbyssBlue, strokeWidth = 1.5.dp)
                                            } else {
                                                Text(
                                                    if (isApplied) "APPLIED" else "APPLY",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isApplied) GoldHenGold else AbyssBlue
                                                )
                                            }
                                        }

                                        // Delete / Remove Theme Button
                                        if (!isApplied && theme.id.startsWith("studio_")) {
                                            IconButton(
                                                onClick = { viewModel.deleteInstalledTheme(theme.id) },
                                                modifier = Modifier
                                                    .size(30.dp)
                                                    .testTag("delete_theme_button_${theme.id}")
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CyberPink, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    } else {
                                        // Install / Sync Theme Button
                                        if (downProgress != null) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1.3f)
                                                    .height(30.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(CardSlate)
                                                    .padding(4.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                LinearProgressIndicator(
                                                    progress = { downProgress / 100f },
                                                    color = ElectricCyan,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        } else {
                                            Button(
                                                onClick = { viewModel.triggerThemeDownload(theme) },
                                                colors = ButtonDefaults.buttonColors(containerColor = CyberPink),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                                modifier = Modifier
                                                    .weight(1.3f)
                                                    .height(30.dp)
                                                    .testTag("download_theme_button_${theme.id}")
                                            ) {
                                                Icon(Icons.Default.Download, contentDescription = null, tint = AbyssBlue, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text("SYNC", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AbyssBlue)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Transmission Feedback Log Console
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AbyssBlue)
                .border(1.dp, CardSlate, RoundedCornerShape(8.dp))
                .padding(10.dp)
                .testTag("theme_status_log")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = "Status Log",
                    tint = ElectricCyan,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = actionLog,
                    color = if (actionLog.contains("Error") || actionLog.contains("Failed")) CyberPink else ElectricCyan,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    // Full Interactive PS4 Screen Preview Dialog Modal
    previewingTheme?.let { theme ->
        CustomThemePreviewDialog(
            theme = theme,
            ps4Ip = ps4Ip,
            isApplied = theme.id == activeThemeId,
            onApply = {
                viewModel.applyThemeToPs4(theme) { result ->
                    actionLog = result
                }
                previewingTheme = null
            },
            onDismiss = { previewingTheme = null }
        )
    }
}



@Composable
fun GameHubScreen(viewModel: NexusViewModel) {
    val games by viewModel.filteredGames.collectAsState()
    val search by viewModel.gameSearchQuery.collectAsState()
    var queryInput by remember { mutableStateOf(search) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SearchBarHeader(
            title = "PS4 COMPATIBLE GAMES INDEX",
            subtitle = "Quick check standard CUSA ID codes and pkg reference channels",
            icon = Icons.Default.SportsEsports,
            accentColor = GoldHenGold
        )

        OutlinedTextField(
            value = queryInput,
            onValueChange = {
                queryInput = it
                viewModel.updatePs4Ip(it) // Reusable lookup block
            },
            placeholder = { Text("Search CUSA Code or Title...") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricCyan,
                focusedTextColor = OffWhite
            ),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CoolGray) }
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(games) { game ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = NexusNavy),
                    border = BorderStroke(1.dp, Color(0x15FFFFFF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AsyncImage(
                            model = game.imageUrl,
                            contentDescription = game.title,
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    game.title,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = OffWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    game.code,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GoldHenGold,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Text(
                                "FW target: ${game.requiredFirmware} | Size: ${game.sizeGb} GB",
                                fontSize = 10.sp,
                                color = CoolGray,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                game.developer,
                                fontSize = 10.sp,
                                color = ElectricCyan,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(game.basePkgUrl))
                                        Toast.makeText(context, "Copied Base URL", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CardSlate),
                                    modifier = Modifier.height(26.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("Copy Base URL", color = ElectricCyan, fontSize = 9.sp)
                                }

                                Button(
                                    onClick = {
                                        viewModel.setScrapingUrl("https://orbispatches.com/${game.code}")
                                        viewModel.selectTab(NexusTab.LINK_SCRAPER)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CardSlate),
                                    modifier = Modifier.height(26.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("Scrape Patches", color = GoldHenGold, fontSize = 9.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CheatsDbScreen(viewModel: NexusViewModel) {
    val cheats by viewModel.filteredCheats.collectAsState()
    val onlineCheats by viewModel.onlineCheatsList.collectAsState()
    val repoDownloads by viewModel.repoDownloads.collectAsState()
    val search by viewModel.cheatSearchQuery.collectAsState()

    var searchInput by remember { mutableStateOf(search) }
    var activeSubTab by remember { mutableStateOf("INSTALLED") } // INSTALLED, ONLINE

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SearchBarHeader(
            title = "GOLDHEN CHEATS DB & MEMORY MANAGER",
            subtitle = "Offline trainer database and Apollo memory hacks store",
            icon = Icons.Default.Search,
            accentColor = GoldHenGold
        )

        // Tab Selector Box
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tabs = listOf(
                Pair("INSTALLED", "My Database (${cheats.size})"),
                Pair("ONLINE", "Download Trainers DB")
            )
            tabs.forEach { (mode, label) ->
                val isSelected = activeSubTab == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) GoldHenGold else CardSlate)
                        .clickable { activeSubTab = mode }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) AbyssBlue else OffWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        when (activeSubTab) {
            "INSTALLED" -> {
                OutlinedTextField(
                    value = searchInput,
                    onValueChange = {
                        searchInput = it
                        viewModel.updatePs4Ip(it) // Re-use lookup state
                    },
                    placeholder = { Text("Search My Cheats (e.g. Elden Ring)...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldHenGold,
                        focusedTextColor = OffWhite,
                        unfocusedTextColor = CoolGray
                    ),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CoolGray) }
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(cheats) { cheat ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = NexusNavy),
                            border = BorderStroke(1.dp, Color(0x15FFFFFF)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(cheat.gameTitle, style = MaterialTheme.typography.bodyLarge.copy(color = OffWhite, fontWeight = FontWeight.Bold))
                                        Text(
                                            cheat.gameCode,
                                            fontSize = 11.sp,
                                            color = GoldHenGold,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(CardSlate)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            cheat.category.uppercase(),
                                            fontSize = 8.sp,
                                            color = ElectricCyan,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(cheat.cheatName, fontSize = 13.sp, color = OffWhite, fontWeight = FontWeight.SemiBold)

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AbyssBlue)
                                        .border(1.dp, CardSlate)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        cheat.cheatCodes,
                                        color = OffWhite,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Db Source: ${cheat.author}", fontSize = 10.sp, color = CoolGray)
                                    Button(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(cheat.cheatCodes))
                                            Toast.makeText(context, "Copied Hex Codes to Clipboard", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldHenGold),
                                        modifier = Modifier.height(28.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text("Copy Hex Decs", color = AbyssBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "ONLINE" -> {
                Text(
                    "ORBIS NEOPORTAL ONLINE TRAINER COMPILATIONS",
                    color = GoldHenGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(onlineCheats) { cheat ->
                        val isInstalled = cheats.any { it.cheatName == cheat.cheatName }
                        val progress = repoDownloads[cheat.id]

                        Card(
                            colors = CardDefaults.cardColors(containerColor = NexusNavy),
                            border = BorderStroke(1.dp, Color(0x15FFFFFF)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(cheat.gameTitle, style = MaterialTheme.typography.bodyLarge.copy(color = OffWhite, fontWeight = FontWeight.Bold))
                                        Text(
                                            cheat.gameCode,
                                            fontSize = 11.sp,
                                            color = GoldHenGold,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(CardSlate)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            cheat.category.uppercase(),
                                            fontSize = 8.sp,
                                            color = ElectricCyan,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(cheat.cheatName, fontSize = 13.sp, color = OffWhite, fontWeight = FontWeight.SemiBold)

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AbyssBlue)
                                        .border(1.dp, CardSlate)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        cheat.cheatCodes,
                                        color = OffWhite,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Creator: ${cheat.author}", fontSize = 10.sp, color = CoolGray)

                                    if (isInstalled) {
                                        Box(
                                            modifier = Modifier
                                                .background(SuccessGreen.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .border(1.dp, SuccessGreen, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(10.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("MERGED", color = SuccessGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    } else if (progress != null) {
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Installing... $progress%", color = ElectricCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            LinearProgressIndicator(
                                                progress = { progress / 100f },
                                                modifier = Modifier.width(100.dp).height(4.dp),
                                                color = ElectricCyan
                                            )
                                        }
                                    } else {
                                        Button(
                                            onClick = { viewModel.downloadRepoCheat(cheat) },
                                            colors = ButtonDefaults.buttonColors(containerColor = CyberPink),
                                            modifier = Modifier.height(28.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Icon(Icons.Default.CloudDownload, contentDescription = null, tint = AbyssBlue, modifier = Modifier.size(11.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Download Trainer", color = AbyssBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LinkScraperScreen(viewModel: NexusViewModel) {
    val url by viewModel.scrapingUrl.collectAsState()
    val isScraping by viewModel.isScraping.collectAsState()
    val results by viewModel.scrapedLinks.collectAsState()
    val error by viewModel.scraperError.collectAsState()

    var urlInput by remember { mutableStateOf(url) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SearchBarHeader(
            title = "DYNAMIC WEB LINK CRAWLER",
            subtitle = "Harvest active PKGs and exploit payloads from external URLs",
            icon = Icons.Default.Language,
            accentColor = ElectricCyan
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = urlInput,
                onValueChange = { urlInput = it },
                label = { Text("Crawl URL target") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricCyan,
                    focusedTextColor = OffWhite
                ),
                singleLine = true
            )

            Button(
                onClick = {
                    viewModel.setScrapingUrl(urlInput)
                    viewModel.startScraping()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                modifier = Modifier.align(Alignment.CenterVertically),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isScraping) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = AbyssBlue, strokeWidth = 2.dp)
                } else {
                    Text("Crawl", color = AbyssBlue, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Quick template suggestions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Presets:", fontSize = 11.sp, color = CoolGray)
            val templates = listOf(
                "Karo Exploit" to "https://karo218.ir/",
                "OrbisPatches" to "https://orbispatches.com/CUSA28863",
                "DarkSoftware" to "https://darksoftware.xyz/"
            )
            templates.forEach { (name, link) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CardSlate)
                        .clickable {
                            urlInput = link
                            viewModel.selectScraperTemplate(link)
                        }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(name, color = ElectricCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        error?.let {
            Text(
                "Crawler network failed, loading offline local backups. Info: $it",
                color = GoldHenGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(results) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = NexusNavy),
                    border = BorderStroke(1.dp, Color(0x15FFFFFF)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(CardSlate)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        item.type.uppercase(),
                                        fontSize = 8.sp,
                                        color = GoldHenGold,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Text(
                                    item.title,
                                    color = OffWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                "Source Link: ${item.url}",
                                fontSize = 10.sp,
                                color = CoolGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(item.url))
                                Toast.makeText(context, "Link Copied!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                            modifier = Modifier.height(28.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("Grab Link", color = AbyssBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StorageBrowserScreen(viewModel: NexusViewModel) {
    val localBrowserPath by viewModel.localBrowserPath.collectAsState()
    val localFiles by viewModel.localFiles.collectAsState()
    val context = LocalContext.current

    var showCreateDialog by remember { mutableStateOf(false) }
    var newFilename by remember { mutableStateOf("") }
    var newFileContent by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SearchBarHeader(
            title = "LOCAL ANDROID STORAGE EXPLORER",
            subtitle = "Browse internally saved PKGs, themes and payloads inside cache folders",
            icon = Icons.Default.InsertDriveFile,
            accentColor = ElectricCyan
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { viewModel.navigateLocalUp() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = OffWhite)
            }
            Text(
                localBrowserPath,
                modifier = Modifier.weight(1f),
                color = CoolGray,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.NoteAdd, contentDescription = "Add Custom payload / Note", tint = ElectricCyan)
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(NexusNavy)
                .border(1.dp, Color(0x11FFFFFF))
        ) {
            if (localFiles.isEmpty()) {
                item {
                    Text(
                        "Empty cache folder.",
                        color = CoolGray,
                        modifier = Modifier.padding(20.dp),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else {
                items(localFiles) { file ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (file.isDirectory) {
                                    viewModel.navigateLocalTo(file.absolutePath)
                                } else {
                                    Toast.makeText(context, "${file.name} selected.", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile,
                                contentDescription = null,
                                tint = if (file.isDirectory) GoldHenGold else OffWhite
                            )
                            Column {
                                Text(
                                    file.name,
                                    color = OffWhite,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    if (file.isDirectory) "Directory" else "${file.length() / 1024} KB",
                                    color = CoolGray,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        if (!file.isDirectory) {
                            Button(
                                onClick = {
                                    viewModel.copyLocalFileToWireless(file)
                                    Toast.makeText(context, "Copied file to Server storage", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CardSlate),
                                modifier = Modifier.height(28.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("Map to Server", color = ElectricCyan, fontSize = 9.sp)
                            }
                        }
                    }
                    Divider(color = Color(0x05FFFFFF))
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create custom document", color = OffWhite) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newFilename,
                        onValueChange = { newFilename = it },
                        label = { Text("Filename (e.g. cheats.json)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = OffWhite, unfocusedTextColor = OffWhite)
                    )
                    OutlinedTextField(
                        value = newFileContent,
                        onValueChange = { newFileContent = it },
                        label = { Text("File content") },
                        modifier = Modifier.height(100.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = OffWhite, unfocusedTextColor = OffWhite)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFilename.isNotBlank()) {
                            viewModel.addLocalNoteFile(newFilename, newFileContent)
                            newFilename = ""
                            newFileContent = ""
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                ) {
                    Text("Save Document", color = AbyssBlue)
                }
            },
            dismissButton = {
                Button(onClick = { showCreateDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = CardSlate)) {
                    Text("Cancel", color = OffWhite)
                }
            },
            containerColor = NexusNavy
        )
    }
}

/* =========================================================
   GENERIC HELPER UI CHIPS
   ========================================================= */

@Composable
fun SearchBarHeader(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CardSlate)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(accentColor.copy(0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
        }
        Column {
            Text(title, style = MaterialTheme.typography.titleSmall.copy(color = OffWhite, fontWeight = FontWeight.Bold))
            Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(color = CoolGray, fontSize = 10.sp))
        }
    }
}

@Composable
fun SectionLoggerBox(title: String, logs: List<String>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleSmall.copy(color = GoldHenGold, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.sp))
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(AbyssBlue)
                .border(1.dp, CardSlate)
                .padding(8.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (logs.isEmpty()) {
                    item {
                        Text("Ready. System idling...", color = CoolGray.copy(alpha = 0.5f), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                } else {
                    items(logs) { log ->
                        Text(log, color = ElectricCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

@Composable
fun NexusBottomNavigationBar(
    activeTab: NexusTab,
    onTabSelected: (NexusTab) -> Unit
) {
    NavigationBar(
        containerColor = NexusNavy,
        tonalElevation = 8.dp,
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        val menuItems = listOf(
            Triple(NexusTab.DASHBOARD, Icons.Default.Dashboard, "Console"),
            Triple(NexusTab.PAYLOADS, Icons.Default.NetworkWifi, "Payloads"),
            Triple(NexusTab.FTP_STATION, Icons.Default.CloudSync, "FTP"),
            Triple(NexusTab.WIRELESS, Icons.Default.SwapHorizontalCircle, "Sync"),
            Triple(NexusTab.THEME_SECTOR, Icons.Default.Palette, "Themes"),
            Triple(NexusTab.GAME_HUB, Icons.Default.SportsEsports, "Games"),
            Triple(NexusTab.CHEATS_DB, Icons.Default.Gamepad, "Cheats"),
            Triple(NexusTab.SAVE_RESIGNER, Icons.Default.Save, "Save Data"),
            Triple(NexusTab.LINK_SCRAPER, Icons.Default.Language, "Scraper"),
            Triple(NexusTab.FILE_BROWSER, Icons.Default.Folder, "Storage")
        )

        menuItems.forEach { item ->
            val tab = item.first
            val icon = item.second
            val label = item.third
            val isSelected = activeTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = { Icon(icon, contentDescription = label, tint = if (isSelected) DeepViolet else OffWhite) },
                label = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 8.sp, fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = LavenderContainer,
                    selectedTextColor = ElectricCyan,
                    unselectedTextColor = CoolGray
                )
            )
        }
    }
}

@Composable
fun SaveResignerScreen(viewModel: NexusViewModel) {
    val targetAccountId by viewModel.targetAccountId.collectAsState()
    val savesList by viewModel.savesList.collectAsState()
    val onlineSaves by viewModel.onlineSavesList.collectAsState()
    val repoDownloads by viewModel.repoDownloads.collectAsState()
    val selectedSaveId by viewModel.selectedSaveId.collectAsState()
    val isWorking by viewModel.isResigningWorking.collectAsState()
    val progress by viewModel.resigningProgress.collectAsState()
    val logs by viewModel.resignerLogs.collectAsState()

    var accountIdInput by remember(targetAccountId) { mutableStateOf(targetAccountId) }
    var activeSubTab by remember { mutableStateOf("WORKBENCH") } // WORKBENCH, INSPECTOR, ONLINE, SCANNER
    var searchQuery by remember { mutableStateOf("") }
    var scanPathInput by remember { mutableStateOf("/user/home/savedata/") }

    // Custom Save Entry Dialog State
    var showAddDialog by remember { mutableStateOf(false) }
    var customTitle by remember { mutableStateOf("") }
    var customCusa by remember { mutableStateOf("CUSA-") }
    var customAccount by remember { mutableStateOf(targetAccountId) }
    var customSize by remember { mutableStateOf("10.0 MB") }

    val selectedSave = savesList.find { it.id == selectedSaveId } ?: savesList.firstOrNull()
    var selectedCheatsList by remember(selectedSaveId) { mutableStateOf(setOf<String>()) }

    val filteredSaves = remember(savesList, searchQuery) {
        if (searchQuery.isBlank()) savesList
        else savesList.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.cusa.contains(searchQuery, ignoreCase = true) ||
            it.originalAccountId.contains(searchQuery, ignoreCase = true)
        }
    }

    val filteredOnlineSaves = remember(onlineSaves, searchQuery) {
        if (searchQuery.isBlank()) onlineSaves
        else onlineSaves.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.cusa.contains(searchQuery, ignoreCase = true)
        }
    }

    val isAccountIdHexValid = remember(accountIdInput) {
        accountIdInput.length == 16 && accountIdInput.all { it.isDigit() || it in 'A'..'F' || it in 'a'..'f' }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SearchBarHeader(
            title = "APOLLO SAVE DATA & RE-SIGNER ENGINE",
            subtitle = "Identify PARAM.SFO save structures, re-sign owner Account IDs, inject memory cheat patches & verify PFS seals",
            icon = Icons.Default.Save,
            accentColor = CyberPink
        )

        // Header Engine Quick Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = NexusNavy),
                border = BorderStroke(1.dp, Color(0x15FFFFFF)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(CyberPink.copy(0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.FolderZip, contentDescription = null, tint = CyberPink, modifier = Modifier.size(16.dp))
                    }
                    Column {
                        Text("IDENTIFIED SAVES", color = CoolGray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text("${savesList.size} Files Loaded", color = OffWhite, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            Card(
                modifier = Modifier.weight(1.2f),
                colors = CardDefaults.cardColors(containerColor = NexusNavy),
                border = BorderStroke(1.dp, Color(0x15FFFFFF)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(ElectricCyan.copy(0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountBox, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
                    }
                    Column {
                        Text("TARGET PSN ID", color = CoolGray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text(targetAccountId, color = ElectricCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = NexusNavy),
                border = BorderStroke(1.dp, Color(0x15FFFFFF)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(SuccessGreen.copy(0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                    }
                    Column {
                        Text("PFS KEYSTORE", color = CoolGray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text("RSA-2048 Ready", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }

        // Account ID Management Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NexusNavy),
            border = BorderStroke(1.dp, CyberPink.copy(0.3f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = CyberPink, modifier = Modifier.size(16.dp))
                        Text("ACCOUNT RE-SIGNING PROFILE", color = OffWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isAccountIdHexValid) SuccessGreen.copy(0.15f) else GoldHenGold.copy(0.15f))
                            .border(1.dp, if (isAccountIdHexValid) SuccessGreen else GoldHenGold, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            if (isAccountIdHexValid) "VALID 64-BIT HEX" else "16-CHAR HEX RECOMMENDED",
                            fontSize = 8.sp,
                            color = if (isAccountIdHexValid) SuccessGreen else GoldHenGold,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = accountIdInput,
                        onValueChange = {
                            accountIdInput = it.uppercase()
                            viewModel.updateTargetAccountId(it.uppercase())
                        },
                        label = { Text("Target PSN Account ID (16-Hex String)") },
                        placeholder = { Text("e.g. 4A7B9C3D1F0E21A4") },
                        modifier = Modifier.weight(1f).testTag("save_resigner_account_input"),
                        singleLine = true,
                        maxLines = 1,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberPink,
                            focusedLabelColor = CyberPink,
                            focusedTextColor = OffWhite,
                            unfocusedTextColor = CoolGray
                        )
                    )

                    Button(
                        onClick = { viewModel.autoDetectConsoleAccountId() },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .height(50.dp)
                            .testTag("save_resigner_autodetect_btn")
                    ) {
                        Icon(Icons.Default.Lan, contentDescription = null, tint = AbyssBlue, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AUTO-DETECT", color = AbyssBlue, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }

                // Account ID Actions & Presets Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("PRESETS:", color = CoolGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)

                        val presets = listOf(
                            "Console" to "4A7B9C3D1F0E21A4",
                            "Dev" to "1000000000000000",
                            "User 2" to "8F3A0219C4D5E01B"
                        )
                        presets.forEach { (label, hexVal) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CardSlate)
                                    .clickable {
                                        accountIdInput = hexVal
                                        viewModel.updateTargetAccountId(hexVal)
                                    }
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(label, color = OffWhite, fontSize = 8.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CardSlate)
                                .clickable {
                                    val randomId = (1..16).map { "0123456789ABCDEF".random() }.joinToString("")
                                    accountIdInput = randomId
                                    viewModel.updateTargetAccountId(randomId)
                                    viewModel.addResignerLog("Generated random Account ID: $randomId")
                                }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                .testTag("save_resigner_random_id_btn")
                        ) {
                            Text("🎲 Random", color = GoldHenGold, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = { viewModel.batchResignAllSaves() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPink),
                        enabled = !isWorking && savesList.isNotEmpty(),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("save_resigner_batch_btn")
                    ) {
                        Icon(Icons.Default.DoneAll, contentDescription = null, tint = AbyssBlue, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("RE-SIGN ALL (${savesList.size})", color = AbyssBlue, fontSize = 9.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // Sub Tab Switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val tabs = listOf(
                Pair("WORKBENCH", "🛠️ Workbench (${savesList.size})"),
                Pair("INSPECTOR", "🔍 File Inspector"),
                Pair("ONLINE", "🌐 Apollo Cloud"),
                Pair("SCANNER", "📁 Storage Scanner")
            )
            tabs.forEach { (mode, label) ->
                val isSelected = activeSubTab == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) CyberPink else CardSlate)
                        .clickable { activeSubTab = mode }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) AbyssBlue else OffWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Search Filter for Workbench & Online
        if (activeSubTab == "WORKBENCH" || activeSubTab == "ONLINE") {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Filter saves by title, CUSA code, or Account ID...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CoolGray) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search", tint = CoolGray)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberPink,
                    focusedTextColor = OffWhite,
                    unfocusedTextColor = CoolGray
                )
            )
        }

        when (activeSubTab) {
            "WORKBENCH" -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left Column: List of Saves
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "IDENTIFIED SAVES IN /user/home/savedata/",
                                style = MaterialTheme.typography.labelSmall.copy(color = CoolGray, fontWeight = FontWeight.Bold)
                            )
                            Text("${filteredSaves.size} files", color = CyberPink, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        filteredSaves.forEach { save ->
                            val isSelected = save.id == selectedSave?.id
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.selectSave(save.id) }
                                    .testTag("save_card_${save.id}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) NexusNavy else CardSlate
                                ),
                                border = BorderStroke(1.dp, if (isSelected) CyberPink else Color(0x10FFFFFF)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            save.title,
                                            color = OffWhite,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            save.cusa,
                                            color = CyberPink,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Owner: ${save.originalAccountId}", color = CoolGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                        Text(save.sizeDisplay, color = CoolGray, fontSize = 9.sp)
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .background(
                                                        when (save.status) {
                                                            "Decrypted" -> SuccessGreen
                                                            "Resigned" -> ElectricCyan
                                                            else -> GoldHenGold
                                                        },
                                                        CircleShape
                                                    )
                                            )
                                            Text(
                                                save.status.uppercase(),
                                                color = when (save.status) {
                                                    "Decrypted" -> SuccessGreen
                                                    "Resigned" -> ElectricCyan
                                                    else -> GoldHenGold
                                                },
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = 0.5.sp
                                            )
                                        }

                                        // Quick One-Click Re-Sign Button on Card
                                        if (save.originalAccountId != targetAccountId) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(CyberPink)
                                                    .clickable {
                                                        viewModel.selectSave(save.id)
                                                        viewModel.resignSave(save.id)
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text("QUICK RE-SIGN", color = AbyssBlue, fontSize = 8.sp, fontWeight = FontWeight.Black)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Right Column: Interactive Workbench Panel
                    if (selectedSave != null) {
                        Card(
                            modifier = Modifier.weight(1.2f),
                            colors = CardDefaults.cardColors(containerColor = NexusNavy),
                            border = BorderStroke(1.dp, Color(0x15FFFFFF)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "WORKBENCH: ${selectedSave.title.uppercase()}",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = GoldHenGold,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 0.5.sp
                                        ),
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Text(
                                        selectedSave.cusa,
                                        color = CyberPink,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Text(
                                    "PARAM.SFO Package Files contained:",
                                    color = CoolGray,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    selectedSave.savesList.forEach { f ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(CardSlate)
                                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                        ) {
                                            Text(f, color = OffWhite, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color(0x10FFFFFF), thickness = 1.dp)

                                if (isWorking) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("PROCESSING SECURE PFS BLOCKS...", color = CyberPink, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            Text("${(progress * 100).toInt()}%", color = CyberPink, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                        LinearProgressIndicator(
                                            progress = { progress },
                                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                            color = CyberPink,
                                            trackColor = CardSlate
                                        )
                                    }
                                }

                                // Interactive Pipeline Step Buttons
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("PIPELINE ACTIONS:", color = CoolGray, fontSize = 8.sp, fontWeight = FontWeight.Bold)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { viewModel.decryptSave(selectedSave.id) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (selectedSave.status == "Encrypted & Signed") GoldHenGold else CardSlate
                                            ),
                                            enabled = !isWorking && selectedSave.status == "Encrypted & Signed",
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(38.dp)
                                                .testTag("save_decrypt_btn"),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                                        ) {
                                            Text("1. DECRYPT", color = AbyssBlue, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                        }

                                        Button(
                                            onClick = { viewModel.resignSave(selectedSave.id) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (selectedSave.status == "Decrypted") ElectricCyan else CardSlate
                                            ),
                                            enabled = !isWorking && selectedSave.status == "Decrypted",
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(38.dp)
                                                .testTag("save_resign_btn"),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                                        ) {
                                            Text("2. RE-SIGN ID", color = AbyssBlue, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                        }

                                        Button(
                                            onClick = { viewModel.encryptAndSign(selectedSave.id) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (selectedSave.status == "Resigned" || selectedSave.status == "Decrypted") SuccessGreen else CardSlate
                                            ),
                                            enabled = !isWorking && (selectedSave.status == "Resigned" || selectedSave.status == "Decrypted"),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(38.dp)
                                                .testTag("save_locks_sign_btn"),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                                        ) {
                                            Text("3. LOCK & SIGN", color = AbyssBlue, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color(0x10FFFFFF), thickness = 1.dp)

                                Text(
                                    "APOLLO INTEGRATED MEMORY PATCHES / CHEATS",
                                    style = MaterialTheme.typography.labelSmall.copy(color = GoldHenGold, fontWeight = FontWeight.Bold)
                                )

                                if (selectedSave.status != "Decrypted" && selectedSave.status != "Resigned") {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(CardSlate.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            "Save must be DECRYPTED to inject memory cheat patches.",
                                            color = CoolGray,
                                            fontSize = 10.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                } else {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        selectedSave.availableCheats.forEach { cheat ->
                                            val isChecked = selectedCheatsList.contains(cheat)
                                            val alreadyApplied = selectedSave.appliedCheats.contains(cheat)

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(CardSlate, RoundedCornerShape(6.dp))
                                                    .clickable(enabled = !alreadyApplied) {
                                                        selectedCheatsList = if (isChecked) {
                                                            selectedCheatsList - cheat
                                                        } else {
                                                            selectedCheatsList + cheat
                                                        }
                                                    }
                                                    .padding(6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Checkbox(
                                                    checked = isChecked || alreadyApplied,
                                                    onCheckedChange = {
                                                        if (!alreadyApplied) {
                                                            selectedCheatsList = if (isChecked) {
                                                                selectedCheatsList - cheat
                                                            } else {
                                                                selectedCheatsList + cheat
                                                            }
                                                        }
                                                    },
                                                    enabled = !alreadyApplied,
                                                    colors = CheckboxDefaults.colors(
                                                        checkedColor = CyberPink,
                                                        uncheckedColor = CoolGray,
                                                        checkmarkColor = AbyssBlue
                                                    ),
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Column {
                                                    Text(
                                                        cheat,
                                                        color = if (alreadyApplied) SuccessGreen else OffWhite,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    if (alreadyApplied) {
                                                        Text("PATCH INJECTED OK [0x1C2F0]", color = SuccessGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }

                                        if (selectedCheatsList.isNotEmpty()) {
                                            Button(
                                                onClick = {
                                                    viewModel.applySaveCheats(selectedSave.id, selectedCheatsList.toList())
                                                    selectedCheatsList = emptySet()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = CyberPink),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(36.dp)
                                                    .testTag("apply_cheats_btn"),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(vertical = 4.dp)
                                            ) {
                                                Icon(Icons.Default.Done, contentDescription = null, tint = AbyssBlue, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Apply selected patches (${selectedCheatsList.size})", color = AbyssBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "INSPECTOR" -> {
                if (selectedSave == null) {
                    Text("No save selected for inspection.", color = CoolGray)
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = NexusNavy),
                        border = BorderStroke(1.dp, GoldHenGold.copy(0.3f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.FindInPage, contentDescription = null, tint = GoldHenGold)
                                    Column {
                                        Text("PARAM.SFO & PFS BINARY IDENTIFIER", color = GoldHenGold, fontSize = 13.sp, fontWeight = FontWeight.Black)
                                        Text("Local PS4 Save File Structure Analysis", color = CoolGray, fontSize = 10.sp)
                                    }
                                }

                                Button(
                                    onClick = { viewModel.verifySaveIntegrity(selectedSave.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CardSlate),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.testTag("verify_integrity_btn")
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("VERIFY SEAL", color = OffWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            HorizontalDivider(color = Color(0x10FFFFFF), thickness = 1.dp)

                            // Header Specs Table Grid
                            val specRows = listOf(
                                "Magic Header" to "\\xFFSFO System Format Object [0x00..0x03]",
                                "Game Title" to selectedSave.title,
                                "Title ID (CUSA)" to selectedSave.cusa,
                                "Owner Account ID (0x00A0)" to selectedSave.originalAccountId,
                                "Target Console IDPS" to selectedSave.originalConsoleId,
                                "Container Format" to "Sony Orbis PFS (PlayStation File System)",
                                "Container Size" to selectedSave.sizeDisplay,
                                "Encryption Seal Status" to selectedSave.status
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                specRows.forEach { (field, valText) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(CardSlate, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(field, color = CoolGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            valText,
                                            color = if (field.contains("Account ID")) ElectricCyan else OffWhite,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            Text("PACKAGE FILE MANIFEST & SFO ENTRIES:", color = CoolGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                selectedSave.savesList.forEach { fname ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(CardSlate)
                                            .border(1.dp, CyberPink.copy(0.3f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(fname, color = CyberPink, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "ONLINE" -> {
                Text(
                    "APOLLO COMMUNITY CLOUD SAVE REPOSITORY",
                    color = CyberPink,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )

                filteredOnlineSaves.forEach { os ->
                    val isDownloaded = savesList.any { it.title == os.title }
                    val progressVal = repoDownloads[os.id]

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = NexusNavy),
                        border = BorderStroke(1.dp, Color(0x15FFFFFF)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(os.title, color = OffWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(os.cusa, color = CyberPink, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                }

                                if (isDownloaded) {
                                    Box(
                                        modifier = Modifier
                                            .background(SuccessGreen.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                            .border(1.dp, SuccessGreen, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(10.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("MOUNTED", color = SuccessGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                } else if (progressVal != null) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Downloading... $progressVal%", color = ElectricCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(
                                            progress = { progressVal / 100f },
                                            modifier = Modifier.width(100.dp).height(4.dp),
                                            color = ElectricCyan
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = { viewModel.downloadRepoSave(os) },
                                        colors = ButtonDefaults.buttonColors(containerColor = CyberPink),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.height(32.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                    ) {
                                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = AbyssBlue, modifier = Modifier.size(11.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Mount & Auto-Re-sign", color = AbyssBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Text("Original Owner PSN ID: ${os.originalAccountId}", color = CoolGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)

                            Text("Packaged filesystem files:", color = CoolGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                os.savesList.forEach { fl ->
                                    Box(
                                        modifier = Modifier
                                            .background(CardSlate, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(fl, color = OffWhite, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }

                            HorizontalDivider(color = Color(0x10FFFFFF), thickness = 1.dp)

                            Text("Apollo Preloaded Modification Cheats available:", color = GoldHenGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                os.availableCheats.forEach { ch ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(4.dp).background(GoldHenGold, CircleShape))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(ch, color = OffWhite, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "SCANNER" -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = NexusNavy),
                    border = BorderStroke(1.dp, Color(0x15FFFFFF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, tint = CyberPink)
                            Text("LOCAL STORAGE & USB SAVE DIRECTORY SCANNER", color = OffWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            "Scan local PS4 USB drives or console storage (/user/home/savedata/) to identify PARAM.SFO containers automatically.",
                            color = CoolGray,
                            fontSize = 10.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = scanPathInput,
                                onValueChange = { scanPathInput = it },
                                label = { Text("Directory Path") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyberPink,
                                    focusedTextColor = OffWhite,
                                    unfocusedTextColor = CoolGray
                                )
                            )

                            Button(
                                onClick = { viewModel.scanSaveDirectory(scanPathInput) },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberPink),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .height(50.dp)
                                    .testTag("scan_dir_btn")
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = AbyssBlue, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("SCAN DIRECTORY", color = AbyssBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        HorizontalDivider(color = Color(0x10FFFFFF), thickness = 1.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("MANUALLY IMPORT SAVE ENTRY:", color = GoldHenGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)

                            Button(
                                onClick = { showAddDialog = !showAddDialog },
                                colors = ButtonDefaults.buttonColors(containerColor = CardSlate),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.testTag("add_custom_save_btn")
                            ) {
                                Icon(
                                    imageVector = if (showAddDialog) Icons.Default.Close else Icons.Default.Add,
                                    contentDescription = null,
                                    tint = GoldHenGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (showAddDialog) "CANCEL" else "ADD ENTRY", color = OffWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (showAddDialog) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CardSlate, RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Import Save File Metadata", color = OffWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                                OutlinedTextField(
                                    value = customTitle,
                                    onValueChange = { customTitle = it },
                                    label = { Text("Game Title") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = customCusa,
                                        onValueChange = { customCusa = it },
                                        label = { Text("Title ID (CUSA-XXXXX)") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = customSize,
                                        onValueChange = { customSize = it },
                                        label = { Text("Save Size") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                }

                                Button(
                                    onClick = {
                                        viewModel.addCustomSave(customTitle, customCusa, customAccount, customSize)
                                        showAddDialog = false
                                        customTitle = ""
                                        customCusa = "CUSA-"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("IMPORT TO WORKBENCH", color = AbyssBlue, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        SectionLoggerBox(title = "APOLLO ENGINE TRACES", logs = logs)
    }
}
