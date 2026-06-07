package com.example.model

import java.io.Serializable

data class Payload(
    val id: String,
    val name: String,
    val description: String,
    val version: String,
    val targetPort: Int = 9020,
    val category: String,
    val minFirmware: String = "9.00",
    val binaryAsset: String, // Mock payload raw filename or type
    val detailInfo: String = ""
) : Serializable

data class Ps4Theme(
    val id: String,
    val title: String,
    val description: String,
    val author: String,
    val sizeMb: Double,
    val imageUrl: String,
    val rating: Float,
    val version: String = "1.00",
    val pkgUrl: String = ""
) : Serializable

data class Ps4Game(
    val id: String,
    val title: String,
    val code: String, // e.g., CUSA-12345
    val sizeGb: Double,
    val requiredFirmware: String = "9.00",
    val imageUrl: String,
    val developer: String,
    val description: String,
    val basePkgUrl: String = "",
    val patchPkgUrl: String = "",
    val rating: Float = 4.8f
) : Serializable

data class CheatItem(
    val id: String,
    val gameTitle: String,
    val gameCode: String,
    val category: String,
    val cheatName: String,
    val cheatCodes: String, // Memory offset and bytes representation
    val author: String = "GoldHEN DB"
) : Serializable

data class ScrapedLink(
    val title: String,
    val url: String,
    val sizeString: String,
    val type: String, // "PKG", "Update", "Theme", "Payload"
    val sourceUrl: String
)

data class FtpFileItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val sizeBytes: Long,
    val permissions: String = "drwxrwxrwx"
)

// Preloaded Lists to live within the data layer
object PreloadedData {
    val payloads = listOf(
        Payload(
            id = "goldhen_24_900",
            name = "GoldHEN v2.4b17",
            description = "Homebrew Enabler, PS4 cheats support, FTP server, Debug settings, Payload loader.",
            version = "2.4b17",
            targetPort = 9020,
            category = "AIO Exploit",
            minFirmware = "9.00",
            binaryAsset = "goldhen_2.4b17.bin",
            detailInfo = "The gold standard homebrew payload by SISiCo. Features cheat database interface, plug-in loader, package manager, and custom FTP host (port 2121)."
        ),
        Payload(
            id = "goldhen_23_900",
            name = "GoldHEN v2.3",
            description = "Legacy and highly stable Homebrew Enabler for 9.00 firmware consoles.",
            version = "2.3.0",
            targetPort = 9020,
            category = "AIO Exploit",
            minFirmware = "9.00",
            binaryAsset = "goldhen_2.3.bin",
            detailInfo = "Provides stable PKG installation, debug menus, cheat overlays, and virtual trophies."
        ),
        Payload(
            id = "orbis_toolbox",
            name = "Orbis Toolbox",
            description = "System modification toolbox. Modifies the shell, shows real-time temperatures and CPU loads.",
            version = "1.00",
            targetPort = 9021,
            category = "System UI",
            minFirmware = "9.00",
            binaryAsset = "orbis_toolbox.bin",
            detailInfo = "A UI modification payload that embeds hardware specs, temperatures, and dynamic RAM counters directly in the PS4 Home Screen top bar."
        ),
        Payload(
            id = "ftp_server",
            name = "FTP Server v1.8",
            description = "Enables full write/read access to the entire root directory structure of the PS4.",
            version = "1.8",
            targetPort = 9020,
            category = "Utility",
            minFirmware = "5.05 - 9.00",
            binaryAsset = "ps4_ftp.bin",
            detailInfo = "Starts an FTP server on port 1337 of your PS4. Connect using the FTP Client tab in this app to transfer game saves and themes."
        ),
        Payload(
            id = "webrte_900",
            name = "WebRTE Trainer",
            description = "Web Real-Time Editor payload for launching game trainer networks and remote cheat tools.",
            version = "1.7.0",
            targetPort = 9020,
            category = "Trainer",
            minFirmware = "9.00",
            binaryAsset = "webrte.bin",
            detailInfo = "Sets up an HTTP cheat injection server on port 2801. Interfacing directly with online trainer lists and matching PS4 cheats."
        ),
        Payload(
            id = "disable_updates",
            name = "Disable System Updates",
            description = "Creates dummy update folders to prevent the PS4 from matching and downloading newer system firmware.",
            version = "1.0",
            targetPort = 9020,
            category = "System Protection",
            minFirmware = "5.05 - 9.00",
            binaryAsset = "disable_updates.bin",
            detailInfo = "Permanently blocks official update pings by creating write-blocked system directory skeletons on user storage partition."
        ),
        Payload(
            id = "enable_updates",
            name = "Enable System Updates",
            description = "Removes dummy update blockers, allowing normal system firmware downloads if desired.",
            version = "1.0",
            targetPort = 9020,
            category = "System Protection",
            minFirmware = "5.05 - 9.00",
            binaryAsset = "enable_updates.bin"
        ),
        Payload(
            id = "app_dumper",
            name = "App Dumper / PS4 Ripper",
            description = "Dumps installed game discs and digital games to an attached USB drive in PKG decrypt format.",
            version = "1.9",
            targetPort = 9020,
            category = "Dumper",
            minFirmware = "9.00",
            binaryAsset = "app_dumper.bin",
            detailInfo = "Dumps decrypted fpkg chunks directly to a plugged FAT32/exFAT USB stick. Includes configuration config file setup on internal disk."
        ),
        Payload(
            id = "linux_1gb",
            name = "Linux Loader (1GB VRAM)",
            description = "Boots the Linux Kernel via Orbis. Allocates 1GB of processing RAM to the graphics controller.",
            version = "5.15",
            targetPort = 9020,
            category = "Linux",
            minFirmware = "9.00",
            binaryAsset = "linux_1gb.bin"
        ),
        Payload(
            id = "linux_3gb",
            name = "Linux Loader (3GB VRAM)",
            description = "Boots Linux with a larger 3GB graphic frame allocation for gaming/emulators like Steam.",
            version = "5.15",
            targetPort = 9020,
            category = "Linux",
            minFirmware = "9.00",
            binaryAsset = "linux_3gb.bin"
        ),
        Payload(
            id = "todex",
            name = "ToDEX developer switch",
            description = "Changes target ID configuration to developers/debug, unlocking extra system menus and developer options.",
            version = "1.0",
            targetPort = 9020,
            category = "Utility",
            minFirmware = "5.05 - 9.00",
            binaryAsset = "todex.bin"
        ),
        Payload(
            id = "fan_control",
            name = "Fan Speed Control",
            description = "Dynamically overrides PS4 fan targets. Keeps temperature low during intense rendering.",
            version = "2.1",
            targetPort = 9020,
            category = "Hardware",
            minFirmware = "5.05 - 9.00",
            binaryAsset = "fan_control.bin",
            detailInfo = "Locks your PS4 fan curves. Safe targets set at 65°C trigger point relative to stock 79°C thermal throttle threshold."
        )
    )

    val themes = listOf(
        Ps4Theme(
            id = "t1",
            title = "Bloodborne Gothic",
            description = "Step into Yharnam with an ambient custom background, customized gothic UI icons, and dark retro orchestral theme sounds.",
            author = "Sony Studio Japan",
            sizeMb = 48.5,
            imageUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=640",
            rating = 4.9f,
            pkgUrl = "https://nexus-ps4themes.example/BloodborneClassic.theme.pkg"
        ),
        Ps4Theme(
            id = "t2",
            title = "Persona 5 Royal - Phantom Thieves",
            description = "Glows with a sharp, dynamic red UI theme, rotating phantom thief cards, and full interactive background animations.",
            author = "Atlus Co.",
            sizeMb = 65.2,
            imageUrl = "https://images.unsplash.com/photo-1511512578047-dfb367046420?q=80&w=640",
            rating = 4.8f,
            pkgUrl = "https://nexus-ps4themes.example/Persona5Royal.theme.pkg"
        ),
        Ps4Theme(
            id = "t3",
            title = "The Last of Us Part II Scenic",
            description = "An amazing calm forest theme. Displays Seattle's green ruins, water reflections, and matches comforting home screen acoustic guitar audio.",
            author = "Naughty Dog",
            sizeMb = 32.1,
            imageUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?q=80&w=640",
            rating = 4.7f,
            pkgUrl = "https://nexus-ps4themes.example/TLoUScenic.theme.pkg"
        ),
        Ps4Theme(
            id = "t4",
            title = "Cyberpunk 2077 Night City Neon",
            description = "A brutalist cyberpunk console layout. Offers neon golden UI boxes, glitch graphics, and synthwave ambient transitions.",
            author = "CD Projekt RED",
            sizeMb = 78.4,
            imageUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?q=80&w=640",
            rating = 4.5f,
            pkgUrl = "https://nexus-ps4themes.example/CyberpunkNeon.theme.pkg"
        ),
        Ps4Theme(
            id = "t5",
            title = "NieR: Automata YoRHa System UI",
            description = "Classic sand-gray militaristic HUD mirroring Pod UI layouts. Features minimalistic icons and custom key sound effects.",
            author = "Square Enix",
            sizeMb = 54.0,
            imageUrl = "https://images.unsplash.com/photo-1538481199705-c710c4e965fc?q=80&w=640",
            rating = 4.9f,
            pkgUrl = "https://nexus-ps4themes.example/NierYorha.theme.pkg"
        ),
        Ps4Theme(
            id = "t6",
            title = "Ghost of Tsushima - Floating Leaves",
            description = "Beautiful samurai backdrop depicting turning maple leaves falling under wind. Features peaceful shakuhachi flute scores.",
            author = "Sucker Punch",
            sizeMb = 29.8,
            imageUrl = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?q=80&w=640",
            rating = 4.9f,
            pkgUrl = "https://nexus-ps4themes.example/GhostTsushima.theme.pkg"
        )
    )

    val games = listOf(
        Ps4Game(
            id = "g1",
            title = "Elden Ring",
            code = "CUSA-28863",
            sizeGb = 44.8,
            requiredFirmware = "9.00",
            imageUrl = "https://images.unsplash.com/photo-1612287230202-1bf1d85d1bdf?q=80&w=640",
            developer = "FromSoftware",
            description = "Rise, Tarnished, and be guided by grace to brandish the power of the Elden Ring and become an Elden Lord in the Lands Between.",
            basePkgUrl = "http://pkg-host.local/base/CUSA28863_01.00.pkg",
            patchPkgUrl = "http://pkg-host.local/patch/CUSA28863_01.10.pkg",
            rating = 4.91f
        ),
        Ps4Game(
            id = "g2",
            title = "God of War Ragnarök",
            code = "CUSA-34384",
            sizeGb = 84.1,
            requiredFirmware = "9.00",
            imageUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?q=80&w=640",
            developer = "Santa Monica Studio",
            description = "Kratos and Atreus must journey to each of the Nine Realms in search of answers as Asgardian forces prepare for a prophesied battle.",
            basePkgUrl = "http://pkg-host.local/base/CUSA34384_01.00.pkg",
            patchPkgUrl = "http://pkg-host.local/patch/CUSA34384_02.00.pkg",
            rating = 4.88f
        ),
        Ps4Game(
            id = "g3",
            title = "Red Dead Redemption 2",
            code = "CUSA-03041",
            sizeGb = 105.4,
            requiredFirmware = "5.05",
            imageUrl = "https://images.unsplash.com/photo-1533142261314-a95728a49df3?q=80&w=640",
            developer = "Rockstar Games",
            description = "America, 1899. Arthur Morgan and the Van der Linde gang are outlaws on the run. With federal agents on their heels, they must rob and steal to survive.",
            basePkgUrl = "http://pkg-host.local/base/CUSA03041_01.00.pkg",
            patchPkgUrl = "http://pkg-host.local/patch/CUSA03041_01.29.pkg",
            rating = 4.95f
        ),
        Ps4Game(
            id = "g4",
            title = "Bloodborne",
            code = "CUSA-00299",
            sizeGb = 27.2,
            requiredFirmware = "5.05",
            imageUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=640",
            developer = "FromSoftware",
            description = "A lone traveler arrives in the ancient gothic city of Yharnam, now plagued by a horrific endemic illness, seeking the mysterious Paleblood.",
            basePkgUrl = "http://pkg-host.local/base/CUSA00299_01.00.pkg",
            patchPkgUrl = "http://pkg-host.local/patch/CUSA00299_01.09.pkg",
            rating = 4.93f
        ),
        Ps4Game(
            id = "g5",
            title = "Marvel's Spider-Man",
            code = "CUSA-11995",
            sizeGb = 40.5,
            requiredFirmware = "6.72",
            imageUrl = "https://images.unsplash.com/photo-1604871000636-074fa5117945?q=80&w=640",
            developer = "Insomniac Games",
            description = "An original, action-packed story starring an experienced Peter Parker battling rogue elements across New York City.",
            basePkgUrl = "http://pkg-host.local/base/CUSA11995_01.00.pkg",
            patchPkgUrl = "http://pkg-host.local/patch/CUSA11995_01.19.pkg",
            rating = 4.79f
        )
    )

    val cheats = listOf(
        CheatItem(
            id = "c1",
            gameTitle = "Elden Ring",
            gameCode = "CUSA-28863",
            category = "HP/FP",
            cheatName = "Infinite HP (God Mode)",
            cheatCodes = "Offset: 0x2A15B90\nValue: 41 8B 87 28 02 00 00 -> 90 90 90 90 90 90 90\nDescription: Freeze health bars from decreasing on impact."
        ),
        CheatItem(
            id = "c2",
            gameTitle = "Elden Ring",
            gameCode = "CUSA-28863",
            category = "Stats",
            cheatName = "Infinite Runes / Souls on Spend",
            cheatCodes = "Offset: 0x3BC8E10\nValue: 29 7B 40 -> 90 90 90\nDescription: Spending any amount of runes will make the counter max out."
        ),
        CheatItem(
            id = "c3",
            gameTitle = "God of War Ragnarök",
            gameCode = "CUSA-34384",
            category = "Combat",
            cheatName = "Infinite Rage Gauge",
            cheatCodes = "Offset: 0x1F2A7E1\nValue: F3 11 40 18 -> F3 11 40 00\nDescription: Spartan Rage gauge remains 100% full permanently."
        ),
        CheatItem(
            id = "c4",
            gameTitle = "Bloodborne",
            gameCode = "CUSA-00299",
            category = "HP/FP",
            cheatName = "Infinite Health (GOD MODE)",
            cheatCodes = "Offset: 0x1B82CC4\nValue: 89 83 F0 01 00 00 -> 90 90 90 90 90 90\nDescription: Makes player strictly invulnerable to lethal standard and fall damage."
        ),
        CheatItem(
            id = "c5",
            gameTitle = "Red Dead Redemption 2",
            gameCode = "CUSA-03041",
            category = "Finance",
            cheatName = "Infinite Cash ($99,999)",
            cheatCodes = "Offset: 0x4CA10FF\nValue: 44 8B 01 C3 -> B8 9F 86 01 B0\nDescription: Forces internal purse variable to write the max balance on transaction trigger."
        )
    )

    val saves = listOf(
        Ps4SaveData(
            id = "s1",
            title = "Bloodborne Gothic Save",
            cusa = "CUSA-00299",
            originalAccountId = "1EF4A03B4C5D2C1A",
            originalConsoleId = "IDPS_00192A48C8B5",
            status = "Encrypted & Signed",
            sizeDisplay = "4.8 MB",
            availableCheats = listOf("Infinite Blood Echoes", "99 Quicksilver Bullets & Vials", "Unlock All Hunter Badges", "Max Character Stat Levels"),
            savesList = listOf("param.sfo", "sddata.bin", "icon0.png")
        ),
        Ps4SaveData(
            id = "s2",
            title = "Elden Ring Endgame Save",
            cusa = "CUSA-28863",
            originalAccountId = "5F02B94C2E4CBA1A",
            originalConsoleId = "IDPS_00918C4DDFE1",
            status = "Encrypted & Signed",
            sizeDisplay = "15.2 MB",
            availableCheats = listOf("999,999,999 Runes", "God Mode (Infinite HP)", "Unlock All Ashes of War", "Max Attributes Level 99"),
            savesList = listOf("param.sfo", "ER0000.bin")
        ),
        Ps4SaveData(
            id = "s3",
            title = "God of War Ragnarök Complete Save",
            cusa = "CUSA-34384",
            originalAccountId = "7A03D2E4F9C2B81F",
            originalConsoleId = "IDPS_01A39BDE8F26",
            status = "Encrypted & Signed",
            sizeDisplay = "12.4 MB",
            availableCheats = listOf("99,999,999 Hacksilver", "Max Sovereign Coals", "Unlock All Rune Attacks", "Infinite Spartan Rage"),
            savesList = listOf("param.sfo", "game_save_0.bin")
        ),
        Ps4SaveData(
            id = "s4",
            title = "Red Dead Redemption 2 100% Save",
            cusa = "CUSA-03041",
            originalAccountId = "B4E32C9810AD8E1D",
            originalConsoleId = "IDPS_00BCE32A15EE",
            status = "Encrypted & Signed",
            sizeDisplay = "32.1 MB",
            availableCheats = listOf("Max Ammo & Provisions", "Infinite Cash ($999,999)", "Max Deadeye Core", "Unlock All Camp Cosmetics"),
            savesList = listOf("param.sfo", "SRDR30000")
        )
    )

    val onlineThemes = listOf(
        Ps4Theme(
            id = "ot1",
            title = "The Witcher 3: Wild Hunt Portal",
            description = "Scenic landscape featuring Geralt, ambient campfire animations, and custom metallic UI icon designs.",
            author = "CD Projekt RED",
            sizeMb = 112.5,
            imageUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=640",
            rating = 4.93f,
            pkgUrl = "https://nexus-repo.orbis/Witcher3WildHunt.pkg"
        ),
        Ps4Theme(
            id = "ot2",
            title = "Spider-Man Miles Morales - Neon Brooklyn",
            description = "Bright spray-paint UI style, Miles swinging under snow effects, and hip-hop custom system tunes.",
            author = "Insomniac Games",
            sizeMb = 84.8,
            imageUrl = "https://images.unsplash.com/photo-1608889174637-3c44f6326f2a?q=80&w=640",
            rating = 4.85f,
            pkgUrl = "https://nexus-repo.orbis/SpiderManMilesMorales.pkg"
        ),
        Ps4Theme(
            id = "ot3",
            title = "Yakuza Like a Dragon Arcade Theme",
            description = "Glows with Yokohama neon light style, customized RPG combat sound effects, and karaoke instrumental background track.",
            author = "SEGA",
            sizeMb = 44.0,
            imageUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=640",
            rating = 4.76f,
            pkgUrl = "https://nexus-repo.orbis/YakuzaLikeADragon.pkg"
        ),
        Ps4Theme(
            id = "ot4",
            title = "Hollow Knight: Dirtmouth Ambient",
            description = "Deep dark moody theme, soft acoustic melancholic sound loop, and hand-drawn insect icon replacement masks.",
            author = "Team Cherry",
            sizeMb = 28.5,
            imageUrl = "https://images.unsplash.com/photo-1511512578047-dfb367046420?q=80&w=640",
            rating = 4.91f,
            pkgUrl = "https://nexus-repo.orbis/HollowKnightDirtmouth.pkg"
        ),
        Ps4Theme(
            id = "ot5",
            title = "Death Stranding: Low Roar Horizon",
            description = "Panoramic post-apocalyptic mountains, black tar particle overlays, and soothing low-roar cinematic synth.",
            author = "Kojima Productions",
            sizeMb = 94.2,
            imageUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?q=80&w=640",
            rating = 4.88f,
            pkgUrl = "https://nexus-repo.orbis/DeathStrandingLowRoar.pkg"
        )
    )

    val onlineSaves = listOf(
        Ps4SaveData(
            id = "os1",
            title = "Bloodborne Max Stats Start Save",
            cusa = "CUSA-00299",
            originalAccountId = "4F52C06DA01EBEF2",
            originalConsoleId = "IDPS_00192A48C8B5",
            status = "Encrypted & Signed",
            sizeDisplay = "4.2 MB",
            availableCheats = listOf("999,999,999 Blood Echoes", "All Hunter Tools Unlocked", "99 INSIGHT Maxed Out", "Max Level (544) Master State"),
            savesList = listOf("param.sfo", "sddata.bin")
        ),
        Ps4SaveData(
            id = "os2",
            title = "Monster Hunter: Iceborne Complete Save",
            cusa = "CUSA-07713",
            originalAccountId = "9D038C4FA10BC912",
            originalConsoleId = "IDPS_00998FCDE1A3",
            status = "Encrypted & Signed",
            sizeDisplay = "21.6 MB",
            availableCheats = listOf("Max Research Points", "Unlock All High-Rank Palico Gear", "Max Zenny Coins ($9,999,999)", "Infinite Jewels / Decos"),
            savesList = listOf("param.sfo", "SAVEDATA00")
        ),
        Ps4SaveData(
            id = "os3",
            title = "Sekiro: Shadows Die Twice 100% Core Save",
            cusa = "CUSA-13456",
            originalAccountId = "3A1F4C9D2E8B9023",
            originalConsoleId = "IDPS_01A39BDE8F26",
            status = "Encrypted & Signed",
            sizeDisplay = "3.1 MB",
            availableCheats = listOf("Unlock All Prosthetic Upgrades", "99 Attack Power Maxed", "Max Vitality & Posture (99)", "99,999 Sen Coins"),
            savesList = listOf("param.sfo", "S_GAME_0")
        ),
        Ps4SaveData(
            id = "os4",
            title = "Horizon Forbidden West NG+ Ready",
            cusa = "CUSA-24545",
            originalAccountId = "BC81E29F3D0C2A48",
            originalConsoleId = "IDPS_00BCD21AE159",
            status = "Encrypted & Signed",
            sizeDisplay = "11.8 MB",
            availableCheats = listOf("999,999 Metal Shards", "Infinite Crafted Resource Items", "Max Health Points (Level 60)", "Unlock All Legendary Outfits"),
            savesList = listOf("param.sfo", "horizon_save_01")
        )
    )

    val onlineCheats = listOf(
        CheatItem(
            id = "oc1",
            gameTitle = "Elden Ring",
            gameCode = "CUSA-28863",
            category = "XP Multiplier",
            cheatName = "Max Rune Multiplier (100x)",
            cheatCodes = "Offset: 0x2A19F20\nValue: 8B 44 24 10 -> B8 64 00 00 00\nDescription: Multiplies every raw item rune reward by index 100."
        ),
        CheatItem(
            id = "oc2",
            gameTitle = "God of War Ragnarök",
            gameCode = "CUSA-34384",
            category = "Stat Gauge",
            cheatName = "One Hit Shield Stagger Limit",
            cheatCodes = "Offset: 0x1C2F200\nValue: 45 8B 01 C3 -> B8 9F 86 01 B0\nDescription: Forces posture block values of enemy targets to break on any light contact."
        ),
        CheatItem(
            id = "oc3",
            gameTitle = "Marvel's Spider-Man",
            gameCode = "CUSA-11995",
            category = "Skill Points",
            cheatName = "Infinite Focus Gauge & Air Velocity Boost",
            cheatCodes = "Offset: 0x3E291F0\nValue: D1 B1 AF -> C1 FF FF\nDescription: Fills the mechanical Focus block layout and boosts speed limits on air swings."
        ),
        CheatItem(
            id = "oc4",
            gameTitle = "Resident Evil 4 Remake",
            gameCode = "CUSA-32431",
            category = "Item Supply",
            cheatName = "Infinite Ammo & Spinel Jewels",
            cheatCodes = "Offset: 0x1F2A79E\nValue: 89 83 F0 01 -> 90 90 90 90\nDescription: Keeps any weapon's ammunition gauge locked at max. Standard knife never wears out."
        ),
        CheatItem(
            id = "oc5",
            gameTitle = "Grand Theft Auto V",
            gameCode = "CUSA-00419",
            category = "Wallet Lock",
            cheatName = "Max Money Hack ($9,999,999,999)",
            cheatCodes = "Offset: 0x4CA1F98\nValue: 4B 8B -> B8 9F 86\nDescription: Sets story mode bank account balance of all three protagonists to maximum."
        )
    )
}

data class Ps4SaveData(
    val id: String,
    val title: String,
    val cusa: String,
    val originalAccountId: String,
    val originalConsoleId: String,
    val status: String, // "Encrypted & Signed", "Decrypted", "Resigned"
    val sizeDisplay: String,
    val availableCheats: List<String> = emptyList(),
    val appliedCheats: List<String> = emptyList(),
    val savesList: List<String> = emptyList()
) : Serializable

data class CleanableItem(
    val id: String,
    val name: String,
    val description: String,
    val path: String,
    val sizeBytes: Long,
    val sizeDisplay: String,
    val isSelected: Boolean = true
)

