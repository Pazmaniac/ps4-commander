package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
            Card(
                colors = CardDefaults.cardColors(containerColor = NexusNavy),
                border = BorderStroke(1.dp, Color(0x15FFFFFF)),
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
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            if (isFtpRunning) SuccessGreen else CyberPink,
                                            CircleShape
                                        )
                                )
                                Text(
                                    if (isFtpRunning) "RUNNING ON PORT 2121" else "SERVICE OFFLINE",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isFtpRunning) SuccessGreen else CoolGray,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.toggleFtpHoster() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFtpRunning) CyberPink.copy(0.15f) else ElectricCyan.copy(0.1f),
                                contentColor = if (isFtpRunning) CyberPink else ElectricCyan
                            ),
                            border = BorderStroke(1.dp, if (isFtpRunning) CyberPink.copy(0.4f) else ElectricCyan.copy(0.4f)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("ftp_dashboard_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (isFtpRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = if (isFtpRunning) "Stop FTP Host" else "Start FTP Host",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (isFtpRunning) "STOP" else "START",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
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
                                .border(1.dp, Color(0x0AFFFFFF), RoundedCornerShape(8.dp))
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
                                    tint = ElectricCyan,
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
                                    color = OffWhite,
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
                                .border(1.dp, Color(0x0AFFFFFF), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapVert,
                                    contentDescription = "Transfer Speed Icon",
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "SPEED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CoolGray,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    ftpServerSpeed,
                                    color = OffWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Micro-console logs telemetry
                    if (isFtpRunning) {
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
                                        .background(SuccessGreen, CircleShape)
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

@Composable
fun PayloadScreen(viewModel: NexusViewModel) {
    val context = LocalContext.current
    var selectedPayload by remember { mutableStateOf<Payload?>(null) }
    var actionStatus by remember { mutableStateOf("Ready to inject. Connect your PS4 to Port 9020/9021.") }
    var isInjecting by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBarHeader(
            title = "PAYLOAD MODULES",
            subtitle = "Transmitting raw system payload binaries to PS4 exploit portals",
            icon = Icons.Default.NetworkWifi,
            accentColor = GoldHenGold
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(PreloadedData.payloads) { payload ->
                val isSelected = selectedPayload?.id == payload.id
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) CardSlate else NexusNavy)
                        .border(
                            1.dp,
                            if (isSelected) GoldHenGold else Color(0x11FFFFFF),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            selectedPayload = payload
                            actionStatus = "Payload '${payload.name}' selected. Ready to inject."
                        }
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                payload.category.uppercase(),
                                fontSize = 9.sp,
                                color = ElectricCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CardSlate)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "v${payload.version}",
                                    fontSize = 8.sp,
                                    color = CoolGray,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Text(
                            payload.name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = OffWhite,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            payload.description,
                            style = MaterialTheme.typography.labelSmall.copy(color = CoolGray),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Expanded Control Panel detail
        selectedPayload?.let { payload ->
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = NexusNavy),
                border = BorderStroke(1.dp, GoldHenGold.copy(0.4f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(payload.name, style = MaterialTheme.typography.titleMedium.copy(color = OffWhite, fontWeight = FontWeight.Bold))
                        Text(
                            "PORT ${payload.targetPort}",
                            style = MaterialTheme.typography.titleSmall.copy(color = GoldHenGold, fontFamily = FontFamily.Monospace)
                        )
                    }

                    Text(
                        payload.detailInfo.ifBlank { payload.description },
                        style = MaterialTheme.typography.bodySmall.copy(color = OffWhite)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Min Firmware: ${payload.minFirmware}", style = MaterialTheme.typography.labelSmall.copy(color = CoolGray))
                        Text("|", color = CoolGray.copy(0.3f))
                        Text("Binary: ${payload.binaryAsset}", style = MaterialTheme.typography.labelSmall.copy(color = CoolGray))
                    }

                    Divider(color = Color(0x11FFFFFF))

                    // Logger screen in card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(AbyssBlue)
                            .border(1.dp, CardSlate)
                            .padding(8.dp)
                    ) {
                        Text(
                            actionStatus,
                            color = if (actionStatus.contains("Failed") || actionStatus.contains("timeout")) CyberPink else ElectricCyan,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Button(
                        onClick = {
                            isInjecting = true
                            viewModel.injectPayload(payload) { stat ->
                                actionStatus = stat
                                if (stat.contains("sent") || stat.contains("Failed")) {
                                    isInjecting = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldHenGold),
                        enabled = !isInjecting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("inject_payload_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isInjecting) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = AbyssBlue, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Upload, contentDescription = null, tint = AbyssBlue)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Inject payload via TCP Socket", color = AbyssBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
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
    val localThemes by viewModel.filteredThemes.collectAsState()
    val onlineThemes by viewModel.onlineThemesList.collectAsState()
    val repoDownloads by viewModel.repoDownloads.collectAsState()
    val activeDownloads by viewModel.activeDownloads.collectAsState()
    val search by viewModel.themeSearchQuery.collectAsState()

    // Studio selection binds
    val studioWallpaper by viewModel.studioWallpaperUrl.collectAsState()
    val studioTitle by viewModel.studioThemeTitle.collectAsState()
    val studioAuthor by viewModel.studioThemeAuthor.collectAsState()
    val studioAccentColor by viewModel.studioAccentColorHex.collectAsState()
    val studioIcons by viewModel.studioIconsMap.collectAsState()
    val isCompiling by viewModel.isCompilingTheme.collectAsState()
    val compileProgress by viewModel.themeCompileProgress.collectAsState()

    var searchQuery by remember { mutableStateOf(search) }
    var activeSubTab by remember { mutableStateOf("CATALOG") } // CATALOG, ONLINE, STUDIO

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SearchBarHeader(
            title = "ORBIS GRAPHICS & THEME SUITE",
            subtitle = "Surgical design workspace: download retail themes or customize system assets",
            icon = Icons.Default.Palette,
            accentColor = ElectricCyan
        )

        // Subtabs Selector
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val subTabs = listOf(
                Triple("CATALOG", Icons.Default.Palette, "My Themes (${localThemes.size})"),
                Triple("ONLINE", Icons.Default.CloudDownload, "Theme Store"),
                Triple("STUDIO", Icons.Default.Brush, "Theme Studio")
            )
            subTabs.forEach { (mode, icon, label) ->
                val isSelected = activeSubTab == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) ElectricCyan else CardSlate)
                        .clickable { activeSubTab = mode }
                        .padding(vertical = 10.dp)
                        .testTag("theme_sub_tab_$mode"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = if (isSelected) AbyssBlue else CoolGray
                        )
                        Spacer(modifier = Modifier.width(6.dp))
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

        when (activeSubTab) {
            "CATALOG" -> {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.updatePs4Ip(it) // Re-use update block
                    },
                    placeholder = { Text("Search Installed Themes...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        focusedTextColor = OffWhite,
                        unfocusedTextColor = CoolGray
                    ),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CoolGray) }
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(localThemes) { theme ->
                        val progress = activeDownloads[theme.id]
                        Card(
                            colors = CardDefaults.cardColors(containerColor = NexusNavy),
                            border = BorderStroke(1.dp, Color(0x15FFFFFF)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column {
                                AsyncImage(
                                    model = theme.imageUrl,
                                    contentDescription = theme.title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp),
                                    contentScale = ContentScale.Crop
                                )

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
                                            theme.title,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                color = OffWhite,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = GoldHenGold, modifier = Modifier.size(14.dp))
                                            Text(
                                                "${theme.rating}",
                                                fontSize = 11.sp,
                                                color = OffWhite,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Text(theme.description, fontSize = 11.sp, color = CoolGray)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Author: ${theme.author}", fontSize = 10.sp, color = CoolGray)
                                            Text("Size: ${theme.sizeMb} MB", fontSize = 10.sp, color = CoolGray, fontFamily = FontFamily.Monospace)
                                        }

                                        if (progress != null) {
                                            Box(
                                                modifier = Modifier
                                                    .width(110.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(CardSlate)
                                                    .padding(6.dp)
                                            ) {
                                                LinearProgressIndicator(
                                                    progress = { progress / 100f },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    color = ElectricCyan
                                                )
                                            }
                                        } else {
                                            Button(
                                                onClick = { viewModel.triggerThemeDownload(theme) },
                                                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                                                modifier = Modifier.height(32.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text("Sync PKG File", color = AbyssBlue, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "ONLINE" -> {
                Text(
                    "ORBIS NEOPORTAL THEME REPOSITORY",
                    color = GoldHenGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(onlineThemes) { theme ->
                        val isInstalled = localThemes.any { it.title == theme.title }
                        val downProgress = repoDownloads[theme.id]

                        Card(
                            colors = CardDefaults.cardColors(containerColor = NexusNavy),
                            border = BorderStroke(1.dp, Color(0x15FFFFFF)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column {
                                AsyncImage(
                                    model = theme.imageUrl,
                                    contentDescription = theme.title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    contentScale = ContentScale.Crop
                                )

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
                                            theme.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = OffWhite,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = GoldHenGold, modifier = Modifier.size(12.dp))
                                            Text(
                                                "${theme.rating}",
                                                fontSize = 10.sp,
                                                color = OffWhite,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Text(theme.description, fontSize = 11.sp, color = CoolGray)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Author: ${theme.author}", fontSize = 10.sp, color = CoolGray)
                                            Text("PKG Down size: ${theme.sizeMb} MB", fontSize = 10.sp, color = CoolGray, fontFamily = FontFamily.Monospace)
                                        }

                                        if (isInstalled) {
                                            Box(
                                                modifier = Modifier
                                                    .background(SuccessGreen.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                                    .border(1.dp, SuccessGreen, RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(11.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("INSTALLED", color = SuccessGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        } else if (downProgress != null) {
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("Downloading... $downProgress%", color = ElectricCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                LinearProgressIndicator(
                                                    progress = { downProgress / 100f },
                                                    modifier = Modifier.width(100.dp).height(4.dp),
                                                    color = ElectricCyan
                                                )
                                            }
                                        } else {
                                            Button(
                                                onClick = { viewModel.downloadRepoTheme(theme) },
                                                colors = ButtonDefaults.buttonColors(containerColor = CyberPink),
                                                modifier = Modifier.height(30.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Icon(Icons.Default.CloudDownload, contentDescription = null, tint = AbyssBlue, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Install Sync", color = AbyssBlue, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "STUDIO" -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "ORBIS ENGINE THEME DESIGN STUDIO",
                        color = ElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )

                    // LIVE PREVIEW INTERACTIVE DESKTOP
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NexusNavy),
                        border = BorderStroke(1.dp, Color(0x30FFFFFF)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                            ) {
                                AsyncImage(
                                    model = studioWallpaper,
                                    contentDescription = "Studio Live Wallpaper",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Dark overlay
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.4f))
                                )

                                // HUD Overlay
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Top Row: Title & Author info
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                studioTitle.uppercase(),
                                                color = OffWhite,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                            Text(
                                                "CREATED BY ${studioAuthor.uppercase()}",
                                                color = ElectricCyan,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text(
                                            "10:30 AM",
                                            color = OffWhite,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Dynamic custom icons row based on selection style!
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        studioIcons.forEach { (app, style) ->
                                            val colorTint = when (style) {
                                                "CLASSIC BLUE" -> Color(0xFF005FF7)
                                                "CYBERPUNK GOLD" -> GoldHenGold
                                                "GOTHIC CRIMSON" -> CyberPink
                                                "YO_R_HA GRAY" -> Color(0xFFC0C0C0)
                                                "RETRO PIXEL" -> SuccessGreen
                                                else -> OffWhite
                                            }

                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .background(colorTint.copy(alpha = 0.25f), CircleShape)
                                                        .border(1.dp, colorTint, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        when (app) {
                                                            "Library" -> Icons.Default.Palette
                                                            "Settings" -> Icons.Default.Search
                                                            "Browser" -> Icons.Default.Palette
                                                            "Trophies" -> Icons.Default.Star
                                                            "Friends" -> Icons.Default.Palette
                                                            else -> Icons.Default.Palette
                                                        },
                                                        contentDescription = null,
                                                        modifier = Modifier.size(12.dp),
                                                        tint = colorTint
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(app, color = OffWhite, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Configuration entries
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.7f)),
                        border = BorderStroke(1.dp, Color(0x10FFFFFF)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("1. TEXT IDENTITY METADATA", color = GoldHenGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)

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
                                label = { Text("Designer / Creator Author") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricCyan,
                                    focusedTextColor = OffWhite,
                                    unfocusedTextColor = CoolGray
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.7f)),
                        border = BorderStroke(1.dp, Color(0x10FFFFFF)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("2. CUSTOM BACKGROUND PICTURE", color = GoldHenGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)

                            OutlinedTextField(
                                value = studioWallpaper,
                                onValueChange = { viewModel.updateStudioWallpaper(it) },
                                label = { Text("Custom Picture/Image URL") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricCyan,
                                    focusedTextColor = OffWhite,
                                    unfocusedTextColor = CoolGray
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Text("Quick Presets:", color = CoolGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val presets = listOf(
                                    "https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=640" to "Cyberpunk",
                                    "https://images.unsplash.com/photo-1511512578047-dfb367046420?q=80&w=640" to "Gamer Red",
                                    "https://images.unsplash.com/photo-1448375240586-882707db888b?q=80&w=640" to "Lush Valley"
                                )
                                presets.forEach { (url, label) ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (studioWallpaper == url) ElectricCyan else CardSlate)
                                            .clickable { viewModel.updateStudioWallpaper(url) }
                                            .padding(6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(label, color = if (studioWallpaper == url) AbyssBlue else OffWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // INDIVIDUAL APP ICON REPLACEMENT
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardSlate.copy(alpha = 0.7f)),
                        border = BorderStroke(1.dp, Color(0x10FFFFFF)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("3. INDIVIDUAL APP ICON REPLACEMENTS", color = GoldHenGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)

                            listOf("Library", "Settings", "Browser", "Trophies", "Friends", "Power").forEach { appName ->
                                val currentAppStyle = studioIcons[appName] ?: "STANDARD"
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(appName, color = OffWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                                    var expandedDropdown by remember { mutableStateOf(false) }
                                    Box {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(NexusNavy)
                                                .clickable { expandedDropdown = !expandedDropdown }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(currentAppStyle, color = ElectricCyan, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                                        }

                                        DropdownMenu(
                                            expanded = expandedDropdown,
                                            onDismissRequest = { expandedDropdown = false },
                                            modifier = Modifier.background(CardSlate)
                                        ) {
                                            listOf("STANDARD", "CLASSIC BLUE", "CYBERPUNK GOLD", "GOTHIC CRIMSON", "YO_R_HA GRAY", "RETRO PIXEL").forEach { styleOpt ->
                                                DropdownMenuItem(
                                                    text = { Text(styleOpt, color = OffWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                                    onClick = {
                                                        viewModel.updateStudioIcon(appName, styleOpt)
                                                        expandedDropdown = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // COMPILE SECTOR
                    if (isCompiling) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "COMPILING SYSTEM PKG...",
                                    color = CyberPink,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${(compileProgress * 100).toInt()}%",
                                    color = CyberPink,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            LinearProgressIndicator(
                                progress = { compileProgress },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = CyberPink,
                                trackColor = CardSlate
                            )
                        }
                    } else {
                        Button(
                            onClick = { viewModel.compileStudioTheme() },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Brush, contentDescription = null, tint = AbyssBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("COMPILE & INSTALL THEME PKG", color = AbyssBlue, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
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
            Triple(NexusTab.SAVE_RESIGNER, Icons.Default.Save, "Saves"),
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

    var accountIdInput by remember { mutableStateOf(targetAccountId) }
    var activeSubTab by remember { mutableStateOf("WORKBENCH") } // WORKBENCH, ONLINE

    val selectedSave = savesList.find { it.id == selectedSaveId }
    var selectedCheatsList by remember(selectedSaveId) { mutableStateOf(setOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SearchBarHeader(
            title = "APOLLO SAVE RESIGNER SUITE",
            subtitle = "Modify owner Account IDs, apply raw cheat patches, and sign PFS secure seals",
            icon = Icons.Default.Save,
            accentColor = CyberPink
        )

        // Sub Tab Switcher
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tabs = listOf(
                Pair("WORKBENCH", "Apollo Workbench (${savesList.size})"),
                Pair("ONLINE", "Apollo Cloud Saves")
            )
            tabs.forEach { (mode, label) ->
                val isSelected = activeSubTab == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) CyberPink else CardSlate)
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
            "WORKBENCH" -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = accountIdInput,
                        onValueChange = {
                            accountIdInput = it
                            viewModel.updateTargetAccountId(it)
                        },
                        label = { Text("Target PSN Account ID (64-bit Hex)") },
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
                        onClick = {
                            val randomId = (1..16).map { "0123456789ABCDEF".random() }.joinToString("")
                            accountIdInput = randomId
                            viewModel.updateTargetAccountId(randomId)
                            viewModel.addResignerLog("Generated random target Account ID: $randomId")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CardSlate),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .height(50.dp)
                            .testTag("save_resigner_random_id_btn")
                    ) {
                        Text("RANDOM ID", color = OffWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            "SAVES FOUND IN /user/home/savedata/",
                            style = MaterialTheme.typography.labelSmall.copy(color = CoolGray, fontWeight = FontWeight.Bold)
                        )

                        savesList.forEach { save ->
                            val isSelected = save.id == selectedSaveId
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.selectSave(save.id) }
                                    .testTag("save_card_${save.id}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) NexusNavy else CardSlate
                                ),
                                border = BorderStroke(1.dp, if (isSelected) CyberPink else Color(0x05FFFFFF)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
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
                                            overflow = TextOverflow.Ellipsis
                                        )
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
                                        Text("Owner ID: ${save.originalAccountId}", color = CoolGray, fontSize = 9.sp)
                                        Text(save.sizeDisplay, color = CoolGray, fontSize = 9.sp)
                                    }
                                    
                                    Spacer(modifier = Modifier.height(2.dp))
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
                                }
                            }
                        }
                    }

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
                                Text(
                                    "WORKBENCH: ${selectedSave.title.uppercase()}",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = GoldHenGold,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    )
                                )

                                Text(
                                    "Secure files contained inside container:",
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

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
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
                                            Text("DECRYPT", color = AbyssBlue, fontSize = 10.sp, fontWeight = FontWeight.Black)
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
                                            Text("RESIGN ID", color = AbyssBlue, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
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
                                            Text("LOCK & SIGN", color = AbyssBlue, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color(0x10FFFFFF), thickness = 1.dp)

                                Text(
                                    "APOLLO INTEGRATED GAME PATCDHES / CHEATS",
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
                                            "Save must be in DECRYPTED state to inject memory cheat patches.",
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

            "ONLINE" -> {
                Text(
                    "ORBIS NEOPORTAL CLOUD SAVES DATABASE",
                    color = CyberPink,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )

                onlineSaves.forEach { os ->
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
                                        modifier = Modifier.height(30.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                    ) {
                                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = AbyssBlue, modifier = Modifier.size(11.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Mount Save", color = AbyssBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Text("Original Owner PSN ID: ${os.originalAccountId}", color = CoolGray, fontSize = 10.sp)

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
        }

        SectionLoggerBox(title = "APOLLO ENGINE TRACES", logs = logs)
    }
}
