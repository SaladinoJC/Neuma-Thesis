[CmdletBinding()]
param(
    [string]$Serial,
    [switch]$SkipAndroid
)
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression.FileSystem
$platformRoot = Split-Path $PSScriptRoot -Parent
if (-not $env:JAVA_HOME) { $env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr' }
if (-not $env:ANDROID_HOME) { $env:ANDROID_HOME = Join-Path $env:LOCALAPPDATA 'Android\Sdk' }
$adb = Join-Path $env:ANDROID_HOME 'platform-tools\adb.exe'
$api = $null
if (-not (Test-Path (Join-Path $env:JAVA_HOME 'bin\java.exe'))) { throw 'Configurá JAVA_HOME con un JDK 17 o superior.' }
if (-not (Test-Path $adb)) { throw 'Configurá ANDROID_HOME con el SDK instalado.' }
if (-not $SkipAndroid) {
    if (-not $Serial) { throw 'Indicá -Serial emulator-5554 o usá -SkipAndroid.' }
    $deviceState = & $adb -s $Serial get-state 2>&1
    if ($LASTEXITCODE -ne 0 -or $deviceState -notcontains 'device') { throw "Dispositivo no disponible: $Serial" }
    $emulated = (& $adb -s $Serial shell getprop ro.kernel.qemu).Trim()
    if ($emulated -ne '1') { throw 'Este comando solo ejecuta pruebas sobre emuladores; las pruebas reinician datos de la app.' }
    $env:ANDROID_SERIAL = $Serial
    $api = (& $adb -s $Serial shell getprop ro.build.version.sdk).Trim()
}
$stamp = Get-Date -Format 'yyyyMMdd-HHmmss-fff'
$destination = Join-Path $platformRoot "build\verification\$stamp"
New-Item -ItemType Directory -Path $destination -Force | Out-Null
$summary = [ordered]@{
    startedAt = (Get-Date).ToString('o'); serial = $Serial; api = $api
    androidSkipped = [bool]$SkipAndroid; passed = $false; apk = $null
}
$reportPaths = @('app\build\test-results\testDebugUnitTest', 'app\build\reports\tests\testDebugUnitTest',
    'app\build\reports\lint-results-debug.xml', 'app\build\reports\lint-results-debug.html')
try {
    $gradleTasks = @(':app:testDebugUnitTest', ':app:lintDebug', ':app:assembleDebug')
    if (-not $SkipAndroid) {
        $gradleTasks += ':app:connectedDebugAndroidTest'
        $reportPaths += @('app\build\outputs\androidTest-results\connected', 'app\build\reports\androidTests\connected')
    }
    # Windows PowerShell trata stderr nativo como ErrorRecord, incluso si la primera línea está vacía.
    $previousErrorPreference = $ErrorActionPreference
    try {
        $ErrorActionPreference = 'Continue'
        & (Join-Path $platformRoot 'gradlew.bat') -p $platformRoot @gradleTasks --console=plain 2>&1 |
            ForEach-Object { $_.ToString() } | Tee-Object -FilePath (Join-Path $destination 'gradle.log')
        $gradleExit = $LASTEXITCODE
    } finally { $ErrorActionPreference = $previousErrorPreference }
    if ($gradleExit -ne 0) { throw "Gradle terminó con código $gradleExit. Consultá gradle.log." }
    $apk = Join-Path $destination 'entrenamiento-auditivo-debug.apk'
    Copy-Item -LiteralPath (Join-Path $platformRoot 'app\build\outputs\apk\debug\app-debug.apk') -Destination $apk
    $summary.apk = [ordered]@{ file = (Split-Path $apk -Leaf); bytes = (Get-Item $apk).Length; sha256 = (Get-FileHash $apk -Algorithm SHA256).Hash }
    $summary.passed = $true
} catch {
    $summary.error = $_.Exception.Message
    throw
} finally {
    $summary.finishedAt = (Get-Date).ToString('o')
    foreach ($relative in $reportPaths) {
        $source = Join-Path $platformRoot $relative
        if (Test-Path $source) {
            try {
                $name = $relative.Replace('\', '-')
                if (Test-Path $source -PathType Container) {
                    [System.IO.Compression.ZipFile]::CreateFromDirectory($source, (Join-Path $destination "$name.zip"))
                } else { Copy-Item -LiteralPath $source -Destination (Join-Path $destination $name) }
            } catch { $summary.reportCopyError = $_.Exception.Message }
        }
    }
    $summary | ConvertTo-Json -Depth 5 | Set-Content -Encoding UTF8 (Join-Path $destination 'summary.json')
    Write-Host "Evidencia: $destination"
}
