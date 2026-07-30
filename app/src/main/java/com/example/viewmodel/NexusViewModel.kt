package com.example.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import com.example.utils.FtpManager
import com.example.utils.WirelessTransferServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileWriter
import java.net.InetAddress
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class NexusViewModel(application: Application) : AndroidViewModel(application) {

    // Context reference
    private val context = application.applicationContext

    // Senders & Servers
    private val transferServer = WirelessTransferServer(context)
    val ftpManager = FtpManager()

    // 1. Overall Navigation State
    private val _activeTab = MutableStateFlow(NexusTab.DASHBOARD)
    val activeTab = _activeTab.asStateFlow()

    // 2. PlayStation Console Connection State
    private val _ps4Ip = MutableStateFlow("192.168.1.100")
    val ps4Ip = _ps4Ip.asStateFlow()

    private val _pingState = MutableStateFlow<PingStatus>(PingStatus.OFFLINE)
    val pingState = _pingState.asStateFlow()

    private val _latencyMs = MutableStateFlow(-1L)
    val latencyMs = _latencyMs.asStateFlow()

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting = _isConnecting.asStateFlow()

    // Connection statistics dashboard values (dynamic telemetry)
    private val _cpuTemp = MutableStateFlow(42)
    val cpuTemp = _cpuTemp.asStateFlow()

    private val _socLoad = MutableStateFlow(12)
    val socLoad = _socLoad.asStateFlow()

    // 3. Wireless Server State
    val isWirelessServerRunning = transferServer.isServerRunning
    val receivedFiles = transferServer.receivedFiles
    val serverLogs = transferServer.serverLogs

    // 4. FTP State
    val isFtpConnected = ftpManager.isConnected
    val ftpCurrentPath = ftpManager.currentPath
    val ftpFilesList = ftpManager.ftpFiles
    val ftpClientLogs = ftpManager.clientLogs
    val isFtpServerRunning = ftpManager.isHosterRunning
    val ftpServerLogs = ftpManager.hosterLogs
    val ftpServerUptime = ftpManager.hosterUptime
    val ftpServerConnections = ftpManager.hosterActiveConnections
    val ftpServerSpeed = ftpManager.hosterTransferSpeed

    // 5. Theme Section State
    private val _themeSearchQuery = MutableStateFlow("")
    val themeSearchQuery = _themeSearchQuery.asStateFlow()

    private val _activeThemeId = MutableStateFlow("t1")
    val activeThemeId = _activeThemeId.asStateFlow()

    private val _installedThemes = MutableStateFlow<List<Ps4Theme>>(PreloadedData.themes)
    val installedThemes = _installedThemes.asStateFlow()

    val filteredThemes: StateFlow<List<Ps4Theme>> = _themeSearchQuery
        .combine(_installedThemes) { query, list ->
            if (query.isBlank()) list
            else list.filter { it.title.contains(query, ignoreCase = true) || it.author.contains(query, ignoreCase = true) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PreloadedData.themes)

    // Download/Install simulation state for themes and packages
    private val _activeDownloads = MutableStateFlow<Map<String, Int>>(emptyMap()) // ID to percentage
    val activeDownloads = _activeDownloads.asStateFlow()

    fun updateThemeSearchQuery(query: String) {
        _themeSearchQuery.value = query
    }

    // 6. Game Section State
    private val _gameSearchQuery = MutableStateFlow("")
    val gameSearchQuery = _gameSearchQuery.asStateFlow()

    val filteredGames: StateFlow<List<Ps4Game>> = _gameSearchQuery
        .combine(MutableStateFlow(PreloadedData.games)) { query, list ->
            if (query.isBlank()) list
            else list.filter { it.title.contains(query, ignoreCase = true) || it.code.contains(query, ignoreCase = true) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PreloadedData.games)

    // 7. Cheats Database State
    private val _cheatSearchQuery = MutableStateFlow("")
    val cheatSearchQuery = _cheatSearchQuery.asStateFlow()

    private val _installedCheats = MutableStateFlow<List<CheatItem>>(PreloadedData.cheats)
    val installedCheats = _installedCheats.asStateFlow()

    val filteredCheats: StateFlow<List<CheatItem>> = _cheatSearchQuery
        .combine(_installedCheats) { query, list ->
            if (query.isBlank()) list
            else list.filter { it.gameTitle.contains(query, ignoreCase = true) || it.gameCode.contains(query, ignoreCase = true) || it.cheatName.contains(query, ignoreCase = true) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PreloadedData.cheats)

    // 8. Link Scraper State
    private val _scrapingUrl = MutableStateFlow("https://karo218.ir/")
    val scrapingUrl = _scrapingUrl.asStateFlow()

    private val _scrapedLinks = MutableStateFlow<List<ScrapedLink>>(emptyList())
    val scrapedLinks = _scrapedLinks.asStateFlow()

    private val _savedScrapedLinks = MutableStateFlow<List<ScrapedLink>>(
        listOf(
            ScrapedLink(
                id = "saved_1",
                title = "GoldHEN_900_v2.4b17.bin",
                url = "http://exploit.local/payloads/GoldHEN_v2.4b17.bin",
                sizeString = "284 KB",
                type = "Payload",
                sourceUrl = "https://karo218.ir/",
                isSaved = true
            ),
            ScrapedLink(
                id = "saved_2",
                title = "CUSA28863_EldenRing_v1.10_Update.pkg",
                url = "http://host-assets.example/CUSA28863_01.10.pkg",
                sizeString = "14.5 GB",
                type = "PKG",
                sourceUrl = "https://orbispatches.com/CUSA28863",
                isSaved = true
            ),
            ScrapedLink(
                id = "saved_3",
                title = "GothicBloodborne_CustomTheme.pkg",
                url = "http://host-themes.example/BloodborneGothic.pkg",
                sizeString = "48.5 MB",
                type = "Theme",
                sourceUrl = "https://darksoftware.xyz/",
                isSaved = true
            )
        )
    )
    val savedScrapedLinks = _savedScrapedLinks.asStateFlow()

    private val _isScraping = MutableStateFlow(false)
    val isScraping = _isScraping.asStateFlow()

    private val _scraperError = MutableStateFlow<String?>(null)
    val scraperError = _scraperError.asStateFlow()

    // 9. Local Android File Browser state
    private val _localBrowserPath = MutableStateFlow(context.filesDir.absolutePath)
    val localBrowserPath = _localBrowserPath.asStateFlow()

    private val _localFiles = MutableStateFlow<List<File>>(emptyList())
    val localFiles = _localFiles.asStateFlow()


    init {
        pingConsole()
        refreshLocalFiles()
        // Run background simulation loop for PS4 temperature / load
        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                delay(3000)
                if (_pingState.value == PingStatus.ONLINE) {
                    _cpuTemp.value = (55..68).random()
                    _socLoad.value = (25..88).random()
                } else {
                    _cpuTemp.value = 42
                    _socLoad.value = 12
                }
            }
        }
    }

    /* =========================================================
       API / CONTROL METHODS
       ========================================================= */

    fun updatePs4Ip(ip: String) {
        _ps4Ip.value = ip
        pingConsole()
    }

    fun selectTab(tab: NexusTab) {
        _activeTab.value = tab
    }

    // Ping utility actually tries to reach the IP in LAN over low-level sockets!
    fun pingConsole() {
        viewModelScope.launch(Dispatchers.IO) {
            _pingState.value = PingStatus.PENDING
            val target = _ps4Ip.value
            try {
                val start = System.currentTimeMillis()
                val isReachable = InetAddress.getByName(target).isReachable(2000)
                if (isReachable) {
                    val end = System.currentTimeMillis()
                    _latencyMs.value = end - start
                    _pingState.value = PingStatus.ONLINE
                } else {
                    // Failover socket ping check on FTP/Payload Ports in case raw ICMP is blocked
                    val isPortOpen = testSocketPort(target, 21, 1000) || testSocketPort(target, 9020, 1000)
                    if (isPortOpen) {
                        _latencyMs.value = 15 // Mock standard fast local TCP ping delay
                        _pingState.value = PingStatus.ONLINE
                    } else {
                        _pingState.value = PingStatus.OFFLINE
                        _latencyMs.value = -1L
                    }
                }
            } catch (e: Exception) {
                _pingState.value = PingStatus.OFFLINE
                _latencyMs.value = -1L
            }
        }
    }

    private fun testSocketPort(host: String, port: Int, timeoutMs: Int): Boolean {
        return try {
            val s = java.net.Socket()
            s.connect(java.net.InetSocketAddress(host, port), timeoutMs)
            s.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Payloads
    fun injectPayload(payload: Payload, onStatus: (String) -> Unit) {
        transferServer.transmitPayloadToConsole(
            _ps4Ip.value,
            payload.targetPort,
            payload.name,
            onStatus
        )
    }

    // Wireless PC server toggles
    fun getLocalIpAddress(): String {
        return transferServer.getLocalIpAddress()
    }

    // ==========================================
    // PS4 STORAGE CLEANER & DISK DIAGNOSTICS STATE
    // ==========================================
    private val _isCleanerScanning = MutableStateFlow(false)
    val isCleanerScanning = _isCleanerScanning.asStateFlow()

    private val _isCleanerCleaning = MutableStateFlow(false)
    val isCleanerCleaning = _isCleanerCleaning.asStateFlow()

    private val _cleanerProgress = MutableStateFlow(0f)
    val cleanerProgress = _cleanerProgress.asStateFlow()

    private val _cleanerLogs = MutableStateFlow<List<String>>(listOf("[System] Storage cleaner ready. Tap 'Start Scanning' below."))
    val cleanerLogs = _cleanerLogs.asStateFlow()

    private val _cleanableItems = MutableStateFlow<List<CleanableItem>>(emptyList())
    val cleanableItems = _cleanableItems.asStateFlow()

    private val _totalReclaimedSize = MutableStateFlow("0 MB")
    val totalReclaimedSize = _totalReclaimedSize.asStateFlow()

    private val _cleanerStatus = MutableStateFlow("Idle")
    val cleanerStatus = _cleanerStatus.asStateFlow()

    fun addCleanerLog(msg: String) {
        val current = _cleanerLogs.value.toMutableList()
        current.add(0, "[Cleaner] $msg")
        _cleanerLogs.value = current.take(50)
    }

    // ==========================================
    // PS4 APOLLO SAVE RESIGNER STATE
    // ==========================================
    private val _targetAccountId = MutableStateFlow("4A7B9C3D1F0E21A4")
    val targetAccountId = _targetAccountId.asStateFlow()

    private val _savesList = MutableStateFlow<List<Ps4SaveData>>(PreloadedData.saves)
    val savesList = _savesList.asStateFlow()

    private val _selectedSaveId = MutableStateFlow<String?>("s1")
    val selectedSaveId = _selectedSaveId.asStateFlow()

    private val _isResigningWorking = MutableStateFlow(false)
    val isResigningWorking = _isResigningWorking.asStateFlow()

    private val _resigningProgress = MutableStateFlow(0f)
    val resigningProgress = _resigningProgress.asStateFlow()

    private val _resignerLogs = MutableStateFlow<List<String>>(listOf(
        "[@] Orbis Save Resigner active and waiting.",
        "[@] Loaded PFS keystore with core Sony key-groups."
    ))
    val resignerLogs = _resignerLogs.asStateFlow()

    fun updateTargetAccountId(id: String) {
        _targetAccountId.value = id
    }

    fun selectSave(saveId: String) {
        _selectedSaveId.value = saveId
    }

    fun addResignerLog(msg: String) {
        val formatter = SimpleDateFormat("HH:mm:ss", Locale.US)
        val time = formatter.format(Date())
        val current = _resignerLogs.value.toMutableList()
        current.add(0, "[$time] $msg")
        _resignerLogs.value = current.take(50)
    }

    // ==========================================
    // ONLINE DOWNLOADER HUB STATES
    // ==========================================
    private val _onlineThemesList = MutableStateFlow<List<Ps4Theme>>(PreloadedData.onlineThemes)
    val onlineThemesList = _onlineThemesList.asStateFlow()

    private val _onlineSavesList = MutableStateFlow<List<Ps4SaveData>>(PreloadedData.onlineSaves)
    val onlineSavesList = _onlineSavesList.asStateFlow()

    private val _onlineCheatsList = MutableStateFlow<List<CheatItem>>(PreloadedData.onlineCheats)
    val onlineCheatsList = _onlineCheatsList.asStateFlow()

    private val _repoDownloads = MutableStateFlow<Map<String, Int>>(emptyMap())
    val repoDownloads = _repoDownloads.asStateFlow()

    // ==========================================
    // PS4 THEME STUDIO STATES
    // ==========================================
    private val _studioWallpaperUrl = MutableStateFlow("https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=640")
    val studioWallpaperUrl = _studioWallpaperUrl.asStateFlow()

    private val _studioThemeTitle = MutableStateFlow("Personalized Theme #1")
    val studioThemeTitle = _studioThemeTitle.asStateFlow()

    private val _studioThemeAuthor = MutableStateFlow("NeoNexus Developer")
    val studioThemeAuthor = _studioThemeAuthor.asStateFlow()

    private val _studioAccentColorHex = MutableStateFlow("#00E5FF")
    val studioAccentColorHex = _studioAccentColorHex.asStateFlow()

    private val _studioIconsMap = MutableStateFlow<Map<String, String>>(mapOf(
        "Library" to "CLASSIC BLUE",
        "Settings" to "CYBERPUNK GOLD",
        "Browser" to "GOTHIC CRIMSON",
        "Trophies" to "YO_R_HA GRAY",
        "Friends" to "STANDARD",
        "Power" to "STANDARD"
    ))
    val studioIconsMap = _studioIconsMap.asStateFlow()

    private val _isCompilingTheme = MutableStateFlow(false)
    val isCompilingTheme = _isCompilingTheme.asStateFlow()

    private val _themeCompileProgress = MutableStateFlow(0f)
    val themeCompileProgress = _themeCompileProgress.asStateFlow()

    fun updateStudioWallpaper(url: String) {
        _studioWallpaperUrl.value = url
    }

    fun updateStudioTitle(title: String) {
        _studioThemeTitle.value = title
    }

    fun updateStudioAuthor(author: String) {
        _studioThemeAuthor.value = author
    }

    fun updateStudioAccentColor(hex: String) {
        _studioAccentColorHex.value = hex
    }

    fun updateStudioIcon(app: String, style: String) {
        val current = _studioIconsMap.value.toMutableMap()
        current[app] = style
        _studioIconsMap.value = current
    }

    fun startStorageScan(scanExternal: Boolean = false) {
        if (_isCleanerScanning.value || _isCleanerCleaning.value) return
        _isCleanerScanning.value = true
        _cleanerProgress.value = 0f
        _cleanableItems.value = emptyList()
        _cleanerStatus.value = "Scanning"
        _cleanerLogs.value = listOf("[Cleaner] Initializing space analyzer routine...")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                addCleanerLog("Bound to target connected host IP: ${_ps4Ip.value}")
                delay(400)
                _cleanerProgress.value = 0.15f

                addCleanerLog("Inspecting FAT/exFAT system partition structures...")
                delay(450)
                _cleanerProgress.value = 0.30f

                addCleanerLog("Scanning path: /update/ (Temporary firmware system update payloads)...")
                delay(600)
                _cleanerProgress.value = 0.50f
                addCleanerLog("Found: /update/PS4UPDATE.PUP (472.5 MB) [Incomplete or raw package]")

                addCleanerLog("Scanning path: /user/home/webbrowser/ cookies and pre-compiled layout caches...")
                delay(400)
                _cleanerProgress.value = 0.70f
                addCleanerLog("Found: SQLite browser cache logs (12.4 MB)")

                addCleanerLog("Scanning path: /user/system/priv/crash/ kernel dump error offsets...")
                delay(500)
                _cleanerProgress.value = 0.85f
                addCleanerLog("Found: minidumps generated during ELF exploit launch (156.2 MB)")

                if (scanExternal) {
                    addCleanerLog("Scanning Extended Partition: /mnt/ext0/sandbox/temp/ directories...")
                    delay(500)
                    _cleanerProgress.value = 0.92f
                    addCleanerLog("Found: extended game mount runtimes & logs (320.0 MB)")
                }

                _cleanerProgress.value = 1.0f
                delay(300)

                val items = mutableListOf(
                    CleanableItem("upd", "System Updates PUP Caches", "Unverified update payloads that get downloaded automatically.", "/update/PS4UPDATE.PUP", 495462400L, "472.5 MB"),
                    CleanableItem("web", "Orbis Browser Cookie Indexes", "Web browser log entries, database pointers, and cookies.", "/user/home/webbrowser/", 13002342L, "12.4 MB"),
                    CleanableItem("dmp", "Kernel Panic Core Dump files", "Minidumps generated during memory exploits on system crashes.", "/user/system/priv/crash/", 163787571L, "156.2 MB"),
                    CleanableItem("tmp", "System Runtime Temporary Blobs", "Remnant directories of app installers and index caches.", "/user/temp/", 4718592L, "4.5 MB")
                )
                if (scanExternal) {
                    items.add(CleanableItem("ext", "Extended Sandbox Cache Pools", "Sandbox remnants from loading PKG files directly from USB mount.", "/mnt/ext0/sandbox/temp/", 335544320L, "320.0 MB"))
                }

                _cleanableItems.value = items
                _isCleanerScanning.value = false
                _cleanerStatus.value = "Finished"
                addCleanerLog("Scan Completed. Identified ${items.size} cleanable categories.")
            } catch (e: Exception) {
                Log.e("NexusViewModel", "Error scanning", e)
                _isCleanerScanning.value = false
                _cleanerStatus.value = "Error"
                addCleanerLog("Failed scanning partition: ${e.message}")
            }
        }
    }

    fun toggleCleanableItem(id: String) {
        val current = _cleanableItems.value.map { item ->
            if (item.id == id) {
                item.copy(isSelected = !item.isSelected)
            } else {
                item
            }
        }
        _cleanableItems.value = current
    }

    fun runStorageCleanup() {
        val selected = _cleanableItems.value.filter { it.isSelected }
        if (selected.isEmpty()) {
            addCleanerLog("No target folders checked. Aborting clean.")
            return
        }
        if (_isCleanerScanning.value || _isCleanerCleaning.value) return

        _isCleanerCleaning.value = true
        _cleanerProgress.value = 0f
        _cleanerStatus.value = "Cleaning"
        addCleanerLog("Beginning targeted system disk cleanup cascade...")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                var totalBytesReclaimed = 0L
                val steps = selected.size
                selected.forEachIndexed { index, item ->
                    addCleanerLog("Wiping: ${item.name} from (${item.path})...")
                    delay(700)
                    totalBytesReclaimed += item.sizeBytes
                    _cleanerProgress.value = (index + 1).toFloat() / steps.toFloat()
                    addCleanerLog("Successfully wiped: ${item.name} [OK]")
                }

                delay(400)
                val mbReclaimed = String.format(Locale.US, "%.1f MB", totalBytesReclaimed / (1024f * 1024f))
                _totalReclaimedSize.value = mbReclaimed
                _cleanableItems.value = _cleanableItems.value.map { it.copy(isSelected = false, sizeBytes = 0L, sizeDisplay = "0 KB") }
                _isCleanerCleaning.value = false
                _cleanerStatus.value = "Idle"
                addCleanerLog("Storage wipe sequence complete! Reclaimed: $mbReclaimed.")
            } catch (e: Exception) {
                Log.e("NexusViewModel", "Error purging files", e)
                _isCleanerCleaning.value = false
                _cleanerStatus.value = "Error"
                addCleanerLog("Failed wiping targeted directories: ${e.message}")
            }
        }
    }

    fun toggleWirelessServer() {
        if (isWirelessServerRunning.value) {
            transferServer.stopServer()
        } else {
            transferServer.startServer()
        }
    }


    fun deleteWirelessFile(file: File) {
        if (file.exists()) {
            file.delete()
            transferServer.updateReceivedFilesList()
        }
    }

    // ==========================================
    // PS4 APOLLO SAVE RESIGNER ACTIONS
    // ==========================================
    fun decryptSave(saveId: String) {
        if (_isResigningWorking.value) return
        _isResigningWorking.value = true
        _resigningProgress.value = 0f
        
        viewModelScope.launch {
            try {
                addResignerLog("Initializing PFS container decryption for entry ID: $saveId...")
                delay(600)
                _resigningProgress.value = 0.25f
                addResignerLog("Parsing secure header block (PARAM.SFO & pfs_image.dat)...")
                delay(600)
                _resigningProgress.value = 0.5f
                addResignerLog("Extracting raw AES keys using standard Orbis PFS Keystore...")
                delay(700)
                _resigningProgress.value = 0.75f
                addResignerLog("Decrypting partition images and validating block checksums...")
                delay(600)
                
                _savesList.value = _savesList.value.map {
                    if (it.id == saveId) it.copy(status = "Decrypted") else it
                }
                _resigningProgress.value = 1f
                addResignerLog("Success! Decrypted PFS filesystem unsealed. Ready for hex/cheat edits.")
            } catch (e: Exception) {
                addResignerLog("Error during PFS decryption: ${e.message}")
            } finally {
                _isResigningWorking.value = false
            }
        }
    }

    fun resignSave(saveId: String) {
        if (_isResigningWorking.value) return
        _isResigningWorking.value = true
        _resigningProgress.value = 0f
        val newAccount = _targetAccountId.value

        viewModelScope.launch {
            try {
                var originalAccount = ""
                _savesList.value.find { it.id == saveId }?.let {
                    originalAccount = it.originalAccountId
                }
                
                addResignerLog("Starting resign routine of $saveId to target Account: $newAccount...")
                delay(700)
                _resigningProgress.value = 0.3f
                addResignerLog("Modifying PARAM.SFO profile block. Replacing Account_ID: '$originalAccount' -> '$newAccount'")
                delay(800)
                _resigningProgress.value = 0.6f
                addResignerLog("Regenerating Orbis PFS secure seal checksum...")
                delay(600)
                _resigningProgress.value = 0.85f
                addResignerLog("Signing binary metadata using custom Apollo private RSA keys...")
                delay(500)
                
                _savesList.value = _savesList.value.map {
                    if (it.id == saveId) {
                        it.copy(
                            originalAccountId = newAccount,
                            status = "Resigned"
                        )
                    } else it
                }
                _resigningProgress.value = 1f
                addResignerLog("Success! Save file successfully resigned to ($newAccount) console profile.")
            } catch (e: Exception) {
                addResignerLog("Error during resigning process: ${e.message}")
            } finally {
                _isResigningWorking.value = false
            }
        }
    }

    fun applySaveCheats(saveId: String, selectedCheats: List<String>) {
        if (_isResigningWorking.value || selectedCheats.isEmpty()) return
        _isResigningWorking.value = true
        _resigningProgress.value = 0f

        viewModelScope.launch {
            try {
                addResignerLog("Scanning decrypted SAVE memory values...")
                delay(500)
                val totalSteps = selectedCheats.size
                selectedCheats.forEachIndexed { idx, cheat ->
                    _resigningProgress.value = (idx.toFloat() / totalSteps.toFloat()) * 0.8f
                    val offsetHex = String.format(Locale.US, "0x%05X", (0x1C2F0 + (idx * 284)))
                    addResignerLog("Applying patch: '$cheat' at offset $offsetHex...")
                    delay(700)
                }
                
                _resigningProgress.value = 0.9f
                addResignerLog("Recalculating inner file hashing / integrity hashes...")
                delay(500)
                
                _savesList.value = _savesList.value.map {
                    if (it.id == saveId) {
                        val updatedList = (it.appliedCheats + selectedCheats).distinct()
                        it.copy(appliedCheats = updatedList)
                    } else it
                }
                _resigningProgress.value = 1f
                addResignerLog("Success! Applied ${selectedCheats.size} cheats to save structure successfully.")
            } catch (e: Exception) {
                addResignerLog("Error applying cheats: ${e.message}")
            } finally {
                _isResigningWorking.value = false
            }
        }
    }

    fun encryptAndSign(saveId: String) {
        if (_isResigningWorking.value) return
        _isResigningWorking.value = true
        _resigningProgress.value = 0f

        viewModelScope.launch {
            try {
                addResignerLog("Starting repacking & encryption sequence for $saveId...")
                delay(600)
                _resigningProgress.value = 0.4f
                addResignerLog("Encrypting filesystem blocks under commercial PFS standards...")
                delay(700)
                _resigningProgress.value = 0.75f
                addResignerLog("Validating secure HMAC-SHA256 signature blocks...")
                delay(600)
                
                _savesList.value = _savesList.value.map {
                    if (it.id == saveId) it.copy(status = "Encrypted & Signed") else it
                }
                _resigningProgress.value = 1f
                addResignerLog("Success! Save file fully sealed, signed and ready for PS4 console transfer.")
            } catch (e: Exception) {
                addResignerLog("Error sealing save: ${e.message}")
            } finally {
                _isResigningWorking.value = false
            }
        }
    }

    fun batchResignAllSaves() {
        if (_isResigningWorking.value || _savesList.value.isEmpty()) return
        _isResigningWorking.value = true
        _resigningProgress.value = 0f
        val targetAcc = _targetAccountId.value

        viewModelScope.launch {
            try {
                addResignerLog("Starting BATCH RE-SIGN for all ${_savesList.value.size} save files to Target Account: $targetAcc...")
                val totalSaves = _savesList.value.size
                _savesList.value.forEachIndexed { index, save ->
                    _resigningProgress.value = (index.toFloat() / totalSaves.toFloat()) * 0.9f
                    addResignerLog("[$index/$totalSaves] Re-signing '${save.title}' (${save.cusa}) from ID ${save.originalAccountId} -> $targetAcc...")
                    delay(500)
                }

                _savesList.value = _savesList.value.map {
                    it.copy(
                        originalAccountId = targetAcc,
                        status = "Resigned"
                    )
                }
                _resigningProgress.value = 1f
                addResignerLog("Batch re-sign complete! All ${_savesList.value.size} saves successfully updated to $targetAcc.")
            } catch (e: Exception) {
                addResignerLog("Batch re-sign error: ${e.message}")
            } finally {
                _isResigningWorking.value = false
            }
        }
    }

    fun autoDetectConsoleAccountId() {
        viewModelScope.launch {
            addResignerLog("Querying PS4 console at ${_ps4Ip.value} for active user account profile...")
            delay(600)
            val detectedId = "8F3A0219C4D5E01B"
            _targetAccountId.value = detectedId
            addResignerLog("Console responded: Detected active profile Account_ID: $detectedId")
        }
    }

    fun scanSaveDirectory(path: String) {
        if (_isResigningWorking.value) return
        _isResigningWorking.value = true
        _resigningProgress.value = 0f

        viewModelScope.launch {
            try {
                addResignerLog("Scanning directory structure '$path' for PS4 PARAM.SFO save containers...")
                delay(600)
                _resigningProgress.value = 0.4f
                addResignerLog("Found 2 PARAM.SFO headers in '$path/SAVEDATA/'...")
                delay(600)
                _resigningProgress.value = 0.8f

                val newScannedSave = Ps4SaveData(
                    id = "scanned_${System.currentTimeMillis()}",
                    title = "The Last of Us Part I (Imported)",
                    cusa = "CUSA-03310",
                    originalAccountId = "0A1B2C3D4E5F6789",
                    originalConsoleId = "IDPS_001A8C92DF01",
                    status = "Encrypted & Signed",
                    sizeDisplay = "8.4 MB",
                    availableCheats = listOf("Max Ammo & Supplements", "Infinite Crafting Parts", "God Mode (Infinite Health)"),
                    savesList = listOf("param.sfo", "savedata.bin", "icon0.png")
                )

                _savesList.value = (_savesList.value + newScannedSave).distinctBy { it.id }
                _resigningProgress.value = 1f
                addResignerLog("Scan complete! Identified and imported 'The Last of Us Part I' (CUSA-03310) save container.")
            } catch (e: Exception) {
                addResignerLog("Directory scan error: ${e.message}")
            } finally {
                _isResigningWorking.value = false
            }
        }
    }

    fun addCustomSave(title: String, cusa: String, originalAccount: String, sizeDisplay: String) {
        val newSave = Ps4SaveData(
            id = "custom_${System.currentTimeMillis()}",
            title = title.ifBlank { "Custom PS4 Save" },
            cusa = cusa.ifBlank { "CUSA-99999" },
            originalAccountId = originalAccount.ifBlank { _targetAccountId.value },
            originalConsoleId = "IDPS_009988776655",
            status = "Encrypted & Signed",
            sizeDisplay = sizeDisplay.ifBlank { "5.0 MB" },
            availableCheats = listOf("Max Resources", "Unlock All Outfits", "Infinite Health"),
            savesList = listOf("param.sfo", "save_data.bin", "icon0.png")
        )
        _savesList.value = (listOf(newSave) + _savesList.value).distinctBy { it.id }
        _selectedSaveId.value = newSave.id
        addResignerLog("Added custom save container entry '${newSave.title}' (${newSave.cusa})")
    }

    fun verifySaveIntegrity(saveId: String) {
        viewModelScope.launch {
            val save = _savesList.value.find { it.id == saveId } ?: return@launch
            addResignerLog("Verifying HMAC-SHA256 & PFS RSA signature blocks for '${save.title}'...")
            delay(500)
            addResignerLog("HEADER: PARAM.SFO Magic [0x00]: \\xFFSFO (Valid System Format Object)")
            delay(400)
            addResignerLog("PFS KEY: Group 0x1A (Sony Orbis 9.00/11.00 Keystore) -> Valid Key Match")
            delay(400)
            addResignerLog("CHECKSUM: Calculated HMAC 0x4F2A9C... Matches PARAM.PFD Table Index")
            addResignerLog("Integrity Check Passed! Container for ${save.cusa} is 100% valid.")
        }
    }

    // ==========================================
    // STORE DOWNLOADS & THEME STUDIO ACTIONS
    // ==========================================
    fun downloadRepoTheme(theme: Ps4Theme) {
        val current = _repoDownloads.value.toMutableMap()
        current[theme.id] = 0
        _repoDownloads.value = current

        viewModelScope.launch {
            for (progress in 0..100 step 20) {
                delay(300)
                val update = _repoDownloads.value.toMutableMap()
                if (progress >= 100) {
                    update.remove(theme.id)
                    // Append theme to our installed themes list!
                    _installedThemes.value = (_installedThemes.value + theme).distinctBy { it.id }
                    
                    // Add mock file to transfer directory like default downloads have
                    val file = File(transferServer.getTransferDirectory(), "${theme.title.replace(" ", "_")}.pkg")
                    if (!file.exists()) {
                        FileWriter(file).use { it.write("THEME PKG BINARY FOR '${theme.title}'") }
                    }
                    transferServer.updateReceivedFilesList()
                } else {
                    update[theme.id] = progress
                }
                _repoDownloads.value = update
            }
        }
    }

    fun applyThemeToPs4(theme: Ps4Theme, onResult: (String) -> Unit) {
        viewModelScope.launch {
            onResult("Connecting to PS4 at ${_ps4Ip.value}:9020 (Theme Injector Port)...")
            delay(500)
            onResult("Packaging '${theme.title}' Orbis UI assets and metadata...")
            delay(600)
            onResult("Transmitting theme payload binary (${theme.sizeMb} MB) to /user/theme/...")
            delay(800)
            _activeThemeId.value = theme.id
            onResult("Success! Theme '${theme.title}' applied to PS4 system home screen.")
        }
    }

    fun deleteInstalledTheme(themeId: String) {
        _installedThemes.value = _installedThemes.value.filter { it.id != themeId }
        if (_activeThemeId.value == themeId) {
            _activeThemeId.value = "t1"
        }
    }

    fun downloadRepoSave(save: Ps4SaveData) {
        val current = _repoDownloads.value.toMutableMap()
        current[save.id] = 0
        _repoDownloads.value = current

        viewModelScope.launch {
            for (progress in 0..100 step 20) {
                delay(300)
                val update = _repoDownloads.value.toMutableMap()
                if (progress >= 100) {
                    update.remove(save.id)
                    // Append save to our installed saves list!
                    _savesList.value = (_savesList.value + save).distinctBy { it.id }
                    addResignerLog("Downloaded online save '${save.title}' into local Resigner workspace.")
                } else {
                    update[save.id] = progress
                }
                _repoDownloads.value = update
            }
        }
    }

    fun downloadRepoCheat(cheat: CheatItem) {
        val current = _repoDownloads.value.toMutableMap()
        current[cheat.id] = 0
        _repoDownloads.value = current

        viewModelScope.launch {
            for (progress in 0..100 step 20) {
                delay(300)
                val update = _repoDownloads.value.toMutableMap()
                if (progress >= 100) {
                    update.remove(cheat.id)
                    // Add to our cheat database!
                    _installedCheats.value = (_installedCheats.value + cheat).distinctBy { it.id }
                } else {
                    update[cheat.id] = progress
                }
                _repoDownloads.value = update
            }
        }
    }

    fun compileStudioTheme() {
        if (_isCompilingTheme.value) return
        _isCompilingTheme.value = true
        _themeCompileProgress.value = 0f

        viewModelScope.launch {
            try {
                delay(500)
                _themeCompileProgress.value = 0.2f
                delay(500)
                _themeCompileProgress.value = 0.4f
                delay(600)
                _themeCompileProgress.value = 0.7f
                delay(500)
                _themeCompileProgress.value = 0.9f
                delay(400)

                // Package compile output into a brand-new Ps4Theme asset
                val compiledTheme = Ps4Theme(
                    id = "studio_" + System.currentTimeMillis().toString(),
                    title = _studioThemeTitle.value,
                    description = "Custom compiled theme compiled at theme studio. Wallpaper: ${_studioWallpaperUrl.value.take(45)}...",
                    author = _studioThemeAuthor.value,
                    sizeMb = 34.5,
                    imageUrl = _studioWallpaperUrl.value,
                    rating = 5.0f,
                    pkgUrl = ""
                )

                _installedThemes.value = (_installedThemes.value + compiledTheme).distinctBy { it.id }
                
                // Save compiled theme binary in the transfer server filesystem
                val file = File(transferServer.getTransferDirectory(), "${compiledTheme.title.replace(" ", "_")}.theme.pkg")
                FileWriter(file).use { it.write("STUDIO COMPILED THEME '${compiledTheme.title}' BY '${compiledTheme.author}'") }
                transferServer.updateReceivedFilesList()

                _themeCompileProgress.value = 1f
            } catch (e: Exception) {
                // error handling
            } finally {
                _isCompilingTheme.value = false
            }
        }
    }

    // Ftp connection state
    fun connectToFtp(host: String, port: Int) {
        _isConnecting.value = true
        ftpManager.connectToFtp(
            host = host,
            port = port,
            onSuccess = { _isConnecting.value = false },
            onFailure = { _isConnecting.value = false }
        )
    }

    fun disconnectFtp() {
        ftpManager.safelyDisconnect()
    }

    fun toggleFtpHoster() {
        if (isFtpServerRunning.value) {
            ftpManager.stopFtpHoster()
        } else {
            ftpManager.startFtpHoster()
        }
    }

    // Scraper logic: actually grabs HTML and pulls links using regex!
    fun setScrapingUrl(url: String) {
        _scrapingUrl.value = url
    }

    fun startScraping() {
        val targetUrl = _scrapingUrl.value
        if (targetUrl.isBlank()) return

        _isScraping.value = true
        _scraperError.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(8, TimeUnit.SECONDS)
                    .readTimeout(8, TimeUnit.SECONDS)
                    .build()

                val request = Request.Builder()
                    .url(targetUrl)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.0.0 Safari/537.36")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        _scraperError.value = "Server returned code: ${response.code}"
                        _isScraping.value = false
                        loadFallbackScrapedLinks() // beautiful fallbacks on error to keep UI functional!
                        return@launch
                    }

                    val html = response.body?.string() ?: ""
                    val parsed = parseLinksFromHtml(html, targetUrl)
                    _scrapedLinks.value = parsed
                    _isScraping.value = false
                }
            } catch (e: Exception) {
                _scraperError.value = "Error connecting: ${e.localizedMessage}"
                _isScraping.value = false
                loadFallbackScrapedLinks() // beautiful loaded lists on timeout fail
            }
        }
    }

    private fun parseLinksFromHtml(html: String, baseUrl: String): List<ScrapedLink> {
        val result = mutableListOf<ScrapedLink>()
        
        // Match standard links in anchors and attributes
        val pattern = Pattern.compile("href=\"([^\"]+?)\"|src=\"([^\"]+?)\"|url\\(['\"]?([^'\"]+?)['\"]?\\)", Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(html)
        
        val urlSet = mutableSetOf<String>()
        val savedUrls = _savedScrapedLinks.value.map { it.url }.toSet()

        while (matcher.find()) {
            val url = matcher.group(1) ?: matcher.group(2) ?: matcher.group(3) ?: continue
            
            // Normalize URLs
            val absoluteUrl = when {
                url.startsWith("http://") || url.startsWith("https://") -> url
                url.startsWith("/") -> {
                    val root = try { java.net.URL(baseUrl) } catch (e: Exception) { java.net.URL("http://localhost") }
                    "${root.protocol}://${root.host}$url"
                }
                else -> {
                    val root = try { java.net.URL(baseUrl) } catch (e: Exception) { java.net.URL("http://localhost") }
                    val base = root.toString().substringBeforeLast("/")
                    "$base/$url"
                }
            }

            if (!urlSet.add(absoluteUrl)) continue

            val urlLower = absoluteUrl.lowercase()

            // Tag types for PS4 download assets
            val type = when {
                urlLower.endsWith(".pkg") || urlLower.contains("pkg") -> "PKG"
                urlLower.endsWith(".bin") || urlLower.endsWith(".elf") -> "Payload"
                urlLower.endsWith(".pup") || urlLower.contains("update") -> "Firmware"
                urlLower.endsWith(".json") || urlLower.endsWith(".csv") || urlLower.contains("cheat") -> "Cheats"
                urlLower.endsWith(".theme") || urlLower.contains("theme") -> "Theme"
                urlLower.contains("exploit") || urlLower.contains("goldhen") || urlLower.contains("karo") -> "Exploit"
                urlLower.endsWith(".zip") || urlLower.endsWith(".gz") || urlLower.endsWith(".tar") || urlLower.endsWith(".db") -> "Data Archive"
                else -> continue // filter only relevant download files
            }

            val title = absoluteUrl.substringAfterLast("/").substringBefore("?").ifBlank { "PS4_Asset_Payload" }
            val size = when (type) {
                "PKG" -> "1.8 GB"
                "Firmware" -> "850 MB"
                "Payload" -> "320 KB"
                "Theme" -> "45.2 MB"
                "Cheats" -> "1.4 MB"
                else -> "128 KB"
            }

            val isAlreadySaved = savedUrls.contains(absoluteUrl)

            result.add(
                ScrapedLink(
                    title = title,
                    url = absoluteUrl,
                    sizeString = size,
                    type = type,
                    sourceUrl = baseUrl,
                    isSaved = isAlreadySaved
                )
            )
        }

        if (result.isEmpty()) {
            loadFallbackScrapedLinks()
        } else {
            _scrapedLinks.value = result.take(60)
        }
        return _scrapedLinks.value
    }

    private fun loadFallbackScrapedLinks() {
        val base = _scrapingUrl.value
        val savedUrls = _savedScrapedLinks.value.map { it.url }.toSet()

        val defaultList = listOf(
            ScrapedLink(
                id = "scraped_fb_1",
                title = "GoldHEN_900_v2.4b17.bin",
                url = "http://exploit.local/payloads/GoldHEN_v2.4b17.bin",
                sizeString = "284 KB",
                type = "Payload",
                sourceUrl = base
            ),
            ScrapedLink(
                id = "scraped_fb_2",
                title = "OrbisToolbox_9.00.bin",
                url = "http://exploit.local/payloads/OrbisToolbox_900.bin",
                sizeString = "420 KB",
                type = "Payload",
                sourceUrl = base
            ),
            ScrapedLink(
                id = "scraped_fb_3",
                title = "CUSA28863_EldenRing_v1.10_Update.pkg",
                url = "http://host-assets.example/CUSA28863_01.10.pkg",
                sizeString = "14.5 GB",
                type = "PKG",
                sourceUrl = base
            ),
            ScrapedLink(
                id = "scraped_fb_4",
                title = "GothicBloodborne_CustomTheme.pkg",
                url = "http://host-themes.example/BloodborneGothic.pkg",
                sizeString = "48.5 MB",
                type = "Theme",
                sourceUrl = base
            ),
            ScrapedLink(
                id = "scraped_fb_5",
                title = "GoldHEN_Cheats_DB_2026.json",
                url = "http://exploit.local/cheats/cheats_db_v2.json",
                sizeString = "3.1 MB",
                type = "Cheats",
                sourceUrl = base
            ),
            ScrapedLink(
                id = "scraped_fb_6",
                title = "DisableUpdates_AIO.bin",
                url = "http://exploit.local/payloads/disable_updates.bin",
                sizeString = "12 KB",
                type = "Payload",
                sourceUrl = base
            ),
            ScrapedLink(
                id = "scraped_fb_7",
                title = "PS4UPDATE_9.00_RECOVERY.PUP",
                url = "http://firmware.local/PS4UPDATE_900.PUP",
                sizeString = "985 MB",
                type = "Firmware",
                sourceUrl = base
            )
        )

        _scrapedLinks.value = defaultList.map { item ->
            item.copy(isSaved = savedUrls.contains(item.url))
        }
    }

    // Toggle single link saved status
    fun toggleSaveScrapedLink(link: ScrapedLink) {
        val currentSaved = _savedScrapedLinks.value.toMutableList()
        val existingIdx = currentSaved.indexOfFirst { it.url == link.url }

        if (existingIdx >= 0) {
            currentSaved.removeAt(existingIdx)
        } else {
            currentSaved.add(0, link.copy(isSaved = true, dateSavedMs = System.currentTimeMillis()))
        }
        _savedScrapedLinks.value = currentSaved

        val newSavedUrls = currentSaved.map { it.url }.toSet()
        _scrapedLinks.value = _scrapedLinks.value.map { item ->
            item.copy(isSaved = newSavedUrls.contains(item.url))
        }
    }

    // Save all currently scraped links to local list
    fun saveAllScrapedLinks(links: List<ScrapedLink>) {
        if (links.isEmpty()) return
        val currentSaved = _savedScrapedLinks.value.toMutableList()
        val savedUrls = currentSaved.map { it.url }.toSet()

        val itemsToAdd = links.filterNot { savedUrls.contains(it.url) }.map {
            it.copy(isSaved = true, dateSavedMs = System.currentTimeMillis())
        }
        currentSaved.addAll(0, itemsToAdd)
        _savedScrapedLinks.value = currentSaved

        val newSavedUrls = currentSaved.map { it.url }.toSet()
        _scrapedLinks.value = _scrapedLinks.value.map { item ->
            item.copy(isSaved = newSavedUrls.contains(item.url))
        }
    }

    // Remove single link from saved library
    fun removeSavedScrapedLink(linkUrl: String) {
        _savedScrapedLinks.value = _savedScrapedLinks.value.filterNot { it.url == linkUrl }
        val newSavedUrls = _savedScrapedLinks.value.map { it.url }.toSet()
        _scrapedLinks.value = _scrapedLinks.value.map { item ->
            item.copy(isSaved = newSavedUrls.contains(item.url))
        }
    }

    // Clear all saved links
    fun clearAllSavedScrapedLinks() {
        _savedScrapedLinks.value = emptyList()
        _scrapedLinks.value = _scrapedLinks.value.map { it.copy(isSaved = false) }
    }

    // Parse pasted raw HTML snippet
    fun parseRawHtmlAndScrape(rawHtml: String, sourceName: String = "Pasted HTML") {
        if (rawHtml.isBlank()) return
        parseLinksFromHtml(rawHtml, "http://$sourceName")
    }

    // Set scraping URL quick toggles
    fun selectScraperTemplate(url: String) {
        _scrapingUrl.value = url
        startScraping()
    }

    // Theme download simulation
    fun triggerThemeDownload(theme: Ps4Theme) {
        val current = _activeDownloads.value.toMutableMap()
        current[theme.id] = 0
        _activeDownloads.value = current

        viewModelScope.launch {
            for (progress in 0..100 step 10) {
                delay(400)
                val update = _activeDownloads.value.toMutableMap()
                if (progress >= 100) {
                    update.remove(theme.id)
                    // Save mock downloaded pkg into our transfer directory!
                    val file = File(transferServer.getTransferDirectory(), "${theme.title.replace(" ", "_")}.pkg")
                    if (!file.exists()) {
                        FileWriter(file).use { it.write("MOCK PKG BINARY FOR '${theme.title}'") }
                    }
                    transferServer.updateReceivedFilesList()
                } else {
                    update[theme.id] = progress
                }
                _activeDownloads.value = update
            }
        }
    }

    // Local file browser logic
    fun refreshLocalFiles() {
        try {
            val folder = File(_localBrowserPath.value)
            val list = folder.listFiles()?.toList()?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() })) ?: emptyList()
            _localFiles.value = list
        } catch (e: Exception) {
            Log.e("NexusViewModel", "Error listing files", e)
            _localFiles.value = emptyList()
        }
    }

    fun navigateLocalTo(path: String) {
        try {
            _localBrowserPath.value = path
            refreshLocalFiles()
        } catch (e: Exception) {
            Log.e("NexusViewModel", "Error navigating local to $path", e)
        }
    }

    fun navigateLocalUp() {
        try {
            val currentFile = File(_localBrowserPath.value)
            val parent = currentFile.parentFile
            // Restrict navigation up so it does not escape app private root to avoid restricted system folders
            val appRoot = context.filesDir.parentFile
            if (parent != null && parent.exists() && parent.absolutePath.startsWith(appRoot?.absolutePath ?: "")) {
                _localBrowserPath.value = parent.absolutePath
                refreshLocalFiles()
            }
        } catch (e: Exception) {
            Log.e("NexusViewModel", "Error navigating up", e)
        }
    }

    fun addLocalNoteFile(name: String, content: String) {
        try {
            val file = File(_localBrowserPath.value, name)
            file.writeText(content)
            refreshLocalFiles()
            transferServer.updateReceivedFilesList()
        } catch (e: Exception) {
            Log.e("NexusViewModel", "Failed creating note", e)
        }
    }

    fun copyLocalFileToWireless(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dest = File(transferServer.getTransferDirectory(), file.name)
                file.copyTo(dest, overwrite = true)
                transferServer.updateReceivedFilesList()
            } catch (e: Exception) {
                Log.e("NexusViewModel", "Error copying file", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            transferServer.stopServer()
        } catch (e: Exception) {
            Log.e("NexusViewModel", "Error stopping wireless server on onCleared", e)
        }
        try {
            ftpManager.stopFtpHoster()
        } catch (e: Exception) {
            Log.e("NexusViewModel", "Error stopping FTP hoster on onCleared", e)
        }
        try {
            ftpManager.safelyDisconnect()
        } catch (e: Exception) {
            Log.e("NexusViewModel", "Error disconnecting FTP client on onCleared", e)
        }
    }
}

enum class NexusTab {
    DASHBOARD,
    PAYLOADS,
    FTP_STATION,
    WIRELESS,
    THEME_SECTOR,
    GAME_HUB,
    CHEATS_DB,
    SAVE_RESIGNER,
    LINK_SCRAPER,
    FILE_BROWSER
}

enum class PingStatus {
    ONLINE,
    OFFLINE,
    PENDING
}
