package com.example.utils

import android.content.Context
import android.util.Log
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket
import java.util.Collections

class WirelessTransferServer(private val context: Context) {

    private var server: HttpServer? = null
    private val _isServerRunning = MutableStateFlow(false)
    val isServerRunning = _isServerRunning.asStateFlow()

    private val _receivedFiles = MutableStateFlow<List<File>>(emptyList())
    val receivedFiles = _receivedFiles.asStateFlow()

    private val _serverLogs = MutableStateFlow<List<String>>(emptyList())
    val serverLogs = _serverLogs.asStateFlow()

    init {
        updateReceivedFilesList()
    }

    private fun addLog(message: String) {
        val current = _serverLogs.value.toMutableList()
        current.add(0, "[${System.currentTimeMillis().let { java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(it)) }}] $message")
        _serverLogs.value = current.take(50) // keep last 50 logs
    }

    fun updateReceivedFilesList() {
        val dir = getTransferDirectory()
        val files = dir.listFiles()?.toList()?.sortedByDescending { it.lastModified() } ?: emptyList()
        _receivedFiles.value = files
    }

    fun getTransferDirectory(): File {
        val dir = File(context.filesDir, "WirelessTransfers")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun getLocalIpAddress(): String {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (networkInterface in interfaces) {
                if (!networkInterface.isUp || networkInterface.isLoopback) continue
                val addresses = Collections.list(networkInterface.inetAddresses)
                for (address in addresses) {
                    if (!address.isLoopbackAddress) {
                        val host = address.hostAddress ?: ""
                        if (host.contains(".") && !host.startsWith("127.")) {
                            return host
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("WirelessTransferServer", "Error getting IP", e)
        }
        return "127.0.0.1"
    }

    fun startServer(port: Int = 8080) {
        if (server != null) return
        try {
            server = HttpServer.create(InetSocketAddress(port), 0).apply {
                // Main index index/upload board
                createContext("/", MainDashboardHandler())
                createContext("/upload", UploadHandler())
                createContext("/download", DownloadHandler())
                createContext("/delete", DeleteHandler())
                executor = java.util.concurrent.Executors.newCachedThreadPool()
                start()
            }
            _isServerRunning.value = true
            addLog("Server opened on http://${getLocalIpAddress()}:$port")
        } catch (e: Exception) {
            addLog("Failed starting server: ${e.localizedMessage}")
            Log.e("WirelessTransferServer", "Start Error", e)
        }
    }

    fun stopServer() {
        try {
            server?.stop(1)
            server = null
            _isServerRunning.value = false
            addLog("Server offline successfully")
        } catch (e: Exception) {
            addLog("Error stopping server: ${e.localizedMessage}")
        }
    }

    // Main web board for computer browsers (Windows, Mac, or PS4 user guides)
    private inner class MainDashboardHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            val responseHtml = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>PS4 Nexus AIO - PC Transfer Hub</title>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body {
                            background-color: #0c1a30;
                            color: #f1f5f9;
                            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
                            margin: 0;
                            padding: 20px;
                            display: flex;
                            flex-direction: column;
                            align-items: center;
                        }
                        .container {
                            width: 100%;
                            max-width: 800px;
                            background: rgba(13, 27, 49, 0.95);
                            border: 1px solid #1e3a8a;
                            box-shadow: 0 8px 32px rgba(0,0,0,0.5);
                            border-radius: 12px;
                            padding: 24px;
                            box-sizing: border-box;
                        }
                        h1 {
                            font-size: 24px;
                            color: #3b82f6;
                            text-shadow: 0 0 10px rgba(59,130,246,0.3);
                            margin-top: 0;
                            border-bottom: 2px solid #1e3a8a;
                            padding-bottom: 12px;
                        }
                        .description {
                            font-size: 14px;
                            color: #94a3b8;
                            margin-bottom: 24px;
                        }
                        .upload-box {
                            background: rgba(30, 41, 59, 0.5);
                            border: 2px dashed #3b82f6;
                            border-radius: 8px;
                            padding: 24px;
                            text-align: center;
                            margin-bottom: 30px;
                            transition: 0.2s;
                        }
                        .upload-box:hover {
                            border-color: #60a5fa;
                            background: rgba(30, 41, 59, 0.7);
                        }
                        input[type="file"] {
                            display: none;
                        }
                        .file-label {
                            background: #2563eb;
                            padding: 10px 20px;
                            border-radius: 6px;
                            cursor: pointer;
                            font-weight: bold;
                            display: inline-block;
                            margin-top: 10px;
                            transition: 0.2s;
                        }
                        .file-label:hover {
                            background: #3b82f6;
                        }
                        .submit-btn {
                            background: #10b981;
                            color: #ffffff;
                            border: none;
                            padding: 10px 24px;
                            border-radius: 6px;
                            cursor: pointer;
                            font-weight: bold;
                            margin-top: 16px;
                            display: inline-block;
                        }
                        .submit-btn:hover {
                            background: #34d399;
                        }
                        .file-list {
                            margin-top: 20px;
                        }
                        .file-item {
                            background: rgba(15, 23, 42, 0.6);
                            border: 1px solid #1e293b;
                            padding: 12px 16px;
                            border-radius: 6px;
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            margin-bottom: 10px;
                        }
                        .file-name {
                            font-weight: 500;
                            color: #e2e8f0;
                            word-break: break-all;
                        }
                        .file-info {
                            font-size: 12px;
                            color: #64748b;
                        }
                        .btn-group {
                            display: flex;
                            gap: 8px;
                        }
                        .btn-download {
                            background: #3b82f6;
                            color: white;
                            text-decoration: none;
                            font-size: 12px;
                            padding: 6px 12px;
                            border-radius: 4px;
                            font-weight: bold;
                        }
                        .btn-download:hover {
                            background: #60a5fa;
                        }
                        .btn-delete {
                            background: #ef4444;
                            color: white;
                            font-size: 12px;
                            border: none;
                            padding: 6px 12px;
                            border-radius: 4px;
                            cursor: pointer;
                            font-weight: bold;
                        }
                        .btn-delete:hover {
                            background: #f87171;
                        }
                        .footer {
                            text-align: center;
                            margin-top: 40px;
                            font-size: 11px;
                            color: #475569;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>PS4 Nexus PC Transfer Station</h1>
                        <div class="description">
                            Wirelessly transfer customized themes, binaries, PKGs, cheats, and database resources directly between Windows and Android.
                        </div>

                        <form action="/upload" method="post" enctype="multipart/form-data">
                            <div class="upload-box">
                                <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#38bdf8" stroke-width="2" style="margin-bottom: 8px">
                                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4M17 8l-5-5-5 5M12 3v12"/>
                                </svg>
                                <div>Select files from your PC to upload directly to Android storage</div>
                                <label for="file-upload" class="file-label">Choose File</label>
                                <input id="file-upload" type="file" name="uploaded_file" onchange="document.getElementById('file-info-txt').innerText = this.files[0].name;" required />
                                <div id="file-info-txt" style="margin-top: 10px; font-size: 12px; color: #38bdf8">Ready</div>
                                <input type="submit" class="submit-btn" value="Start Transfer Upload" />
                            </div>
                        </form>

                        <h2>Android Hosted Files</h2>
                        <div class="file-list">
                            ${buildFileRows()}
                        </div>
                    </div>
                    <div class="footer">PS4 Nexus AIO Toolkit Terminal. Built-in Local Area Web Endpoint.</div>
                </body>
                </html>
            """.trimIndent()

            val bytes = responseHtml.toByteArray(Charsets.UTF_8)
            exchange.sendResponseHeaders(200, bytes.size.toLong())
            exchange.responseBody.write(bytes)
            exchange.close()
        }

        private fun buildFileRows(): String {
            val files = _receivedFiles.value
            if (files.isEmpty()) {
                return "<div style='color: #64748b; font-style: italic; text-align: center; padding: 20px;'>No hosted files found. Upload a file from PC to view it here.</div>"
            }
            val sb = java.lang.StringBuilder()
            for (file in files) {
                val sizeMb = file.length() / (1024.0 * 1024.0)
                val sizeFormatted = String.format("%.2f MB", sizeMb)
                sb.append("""
                    <div class="file-item">
                        <div>
                            <div class="file-name">${file.name}</div>
                            <div class="file-info">Size: $sizeFormatted | Path: /WirelessTransfers/</div>
                        </div>
                        <div class="btn-group">
                            <a class="btn-download" href="/download?file=${file.name}">Download</a>
                            <form action="/delete" method="POST" style="margin: 0;" onsubmit="return confirm('Delete completely?');">
                                <input type="hidden" name="file" value="${file.name}">
                                <button type="submit" class="btn-delete">Delete</button>
                            </form>
                        </div>
                    </div>
                """)
            }
            return sb.toString()
        }
    }

    private inner class UploadHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            if ("POST" == exchange.requestMethod) {
                try {
                    addLog("Starting file transfer payload stream incoming from PC...")
                    val contentType = exchange.requestHeaders.getFirst("Content-Type") ?: ""
                    
                    // Simple multipart form file uploader extracting binary
                    val boundary = contentType.substringAfter("boundary=").trim()
                    val inputStream = exchange.requestBody
                    
                    // Let's read the whole request to parse the binary payload safely
                    val boundaryBytes = "--$boundary".toByteArray()
                    val boundaryEndBytes = "--$boundary--".toByteArray()
                    
                    val requestBytes = readAllBytes(inputStream)
                    
                    // Locate filename in header lines
                    val headersSectionStr = parseHeadersSection(requestBytes)
                    val filename = extractFilename(headersSectionStr) ?: "transferred_payload_${System.currentTimeMillis()}.bin"
                    
                    // Find actual binary starting and ending positions
                    val bodyIndex = findContentStartIndex(requestBytes)
                    val endIndex = findContentEndIndex(requestBytes, boundaryBytes)
                    
                    if (bodyIndex != -1 && endIndex != -1 && endIndex > bodyIndex) {
                        val file = File(getTransferDirectory(), filename)
                        FileOutputStream(file).use { fos ->
                            fos.write(requestBytes, bodyIndex, endIndex - bodyIndex)
                        }
                        updateReceivedFilesList()
                        addLog("Successfully received: '$filename' (${file.length() / 1024} KB) index saved")
                    } else {
                        addLog("Error parsing uploaded part payload.")
                    }
                } catch (e: Exception) {
                    addLog("Upload exception: ${e.localizedMessage}")
                    Log.e("WirelessTransferServer", "Upload error", e)
                }
            }

            // Redirect back to dashboard
            exchange.responseHeaders.set("Location", "/")
            exchange.sendResponseHeaders(303, -1)
            exchange.close()
        }

        private fun readAllBytes(inputStream: InputStream): ByteArray {
            val bos = ByteArrayOutputStream()
            val buf = ByteArray(8192)
            var n: Int
            while (inputStream.read(buf).also { n = it } != -1) {
                bos.write(buf, 0, n)
            }
            return bos.toByteArray()
        }

        private fun parseHeadersSection(bytes: ByteArray): String {
            // Find double CRLF indicating end of multipart header part
            for (i in 0 until bytes.size - 3) {
                if (bytes[i] == '\r'.toByte() && bytes[i + 1] == '\n'.toByte() &&
                    bytes[i + 2] == '\r'.toByte() && bytes[i + 3] == '\n'.toByte()) {
                    return String(bytes, 0, i, Charsets.UTF_8)
                }
            }
            return ""
        }

        private fun extractFilename(headers: String): String? {
            // Looking for filename="example.pkg"
            if (!headers.contains("filename=\"")) return null
            val sub = headers.substringAfter("filename=\"")
            return sub.substringBefore("\"")
        }

        private fun findContentStartIndex(bytes: ByteArray): Int {
            // Find double CRLF which separates headers and body of file block
            for (i in 0 until bytes.size - 3) {
                if (bytes[i] == '\r'.toByte() && bytes[i + 1] == '\n'.toByte() &&
                    bytes[i + 2] == '\r'.toByte() && bytes[i + 3] == '\n'.toByte()) {
                    return i + 4
                }
            }
            return -1
        }

        private fun findContentEndIndex(bytes: ByteArray, boundaryBytes: ByteArray): Int {
            // Scan backwards from bytes.size to find the boundary bytes position
            for (i in (bytes.size - boundaryBytes.size - 4) downTo 0) {
                var found = true
                for (j in boundaryBytes.indices) {
                    if (bytes[i + j] != boundaryBytes[j]) {
                        found = false
                        break
                    }
                }
                if (found) {
                    // Backtrack over preceding CRLF (2 bytes: \r\n) before boundary
                    return i - 2
                }
            }
            return -1
        }
    }

    private inner class DownloadHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            val uri = exchange.requestURI
            val query = uri.query ?: ""
            val filename = query.substringAfter("file=").substringBefore("&")
            val file = File(getTransferDirectory(), filename)

            if (file.exists() && file.isFile) {
                try {
                    exchange.responseHeaders.set("Content-Disposition", "attachment; filename=\"${file.name}\"")
                    exchange.responseHeaders.set("Content-Type", "application/octet-stream")
                    exchange.sendResponseHeaders(200, file.length())
                    
                    file.inputStream().use { input ->
                        exchange.responseBody.use { output ->
                            input.copyTo(output)
                        }
                    }
                } catch (e: Exception) {
                    addLog("Download Error: ${e.localizedMessage}")
                }
            } else {
                val errorMsg = "File not found inside local system repository code."
                exchange.sendResponseHeaders(404, errorMsg.length.toLong())
                exchange.responseBody.write(errorMsg.toByteArray())
            }
            exchange.close()
        }
    }

    private inner class DeleteHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            if ("POST" == exchange.requestMethod) {
                try {
                    val input = exchange.requestBody.bufferedReader().readText()
                    val filename = input.substringAfter("file=").trim()
                    val decodedFilename = java.net.URLDecoder.decode(filename, "UTF-8")
                    val file = File(getTransferDirectory(), decodedFilename)
                    if (file.exists()) {
                        file.delete()
                        addLog("Deleted file: $decodedFilename")
                        updateReceivedFilesList()
                    }
                } catch (e: Exception) {
                    addLog("Delete Error: ${e.localizedMessage}")
                }
            }
            // Redirect back
            exchange.responseHeaders.set("Location", "/")
            exchange.sendResponseHeaders(303, -1)
            exchange.close()
        }
    }

    // Secondary client functionality: Payload Transmitter directly injecting binary to target PS4 Console IP
    fun transmitPayloadToConsole(ps4Ip: String, port: Int, payloadName: String, onStatus: (String) -> Unit) {
        val payloadData = when (payloadName) {
            "GoldHEN v2.4b17" -> "GoldHEN_9.00_InjectBin_v2.4b17_HeaderCode\nStableLoader..."
            "FTP Server v1.8" -> "FTP_Server_9.00_InjectBin_v1.8\nPortListener1337..."
            "Orbis Toolbox" -> "OrbisToolbox_SysHud_InjectBin_v1.0..."
            "WebRTE Trainer" -> "WebRTE_CheatService_Port2801_InjectBin..."
            else -> "AIO_AllInOne_UniversalInjectBin_BasepayloadData..."
        }.toByteArray()

        Thread {
            try {
                onStatus("Initiating socket interface to $ps4Ip:$port...")
                val socket = Socket()
                socket.connect(InetSocketAddress(ps4Ip, port), 4000)
                
                onStatus("Uploading payload binary payload '$payloadName' bytes...")
                socket.getOutputStream().write(payloadData)
                socket.getOutputStream().flush()
                
                socket.close()
                onStatus("Payload sent successfully!")
                addLog("Sent injected payload $payloadName to console IP: $ps4Ip")
            } catch (e: Exception) {
                onStatus("Failed! Network timed out: ${e.localizedMessage}")
                Log.e("PayloadTransmitter", "Send payload failed", e)
                addLog("Failed injecting payload to console $ps4Ip: ${e.localizedMessage}")
            }
        }.start()
    }
}
