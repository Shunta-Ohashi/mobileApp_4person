# install_temurin_user.ps1
# Downloads Temurin JDK17 ZIP to user Downloads, extracts to %USERPROFILE%\Adoptium\jdk-17,
# sets user JAVA_HOME via setx, updates current session, prints java -version and runs gradlew.

$ErrorActionPreference = 'Stop'

$tempZip = Join-Path $env:USERPROFILE "Downloads\\temurin17.zip"
$installDir = Join-Path $env:USERPROFILE "Adoptium\\jdk-17"
# try multiple download URLs (GitHub release redirect may not exist for every build)
$downloadUrlCandidates = @(
    'https://github.com/adoptium/temurin17-binaries/releases/latest/download/OpenJDK17U-jdk_x64_windows_hotspot_latest.zip',
    'https://api.adoptium.net/v3/binary/latest/17/ga/windows/x64/jdk/hotspot/normal/adoptium'
)

Write-Output "Temp zip: $tempZip"
Write-Output "Install dir: $installDir"

# Ensure Downloads exists
$dlDir = Split-Path $tempZip
if (-not (Test-Path $dlDir)) { New-Item -ItemType Directory -Path $dlDir | Out-Null }

# Download (try candidates until one succeeds)
Write-Output 'Downloading... (this may take a while)'
$downloadSucceeded = $false
foreach ($downloadUrl in $downloadUrlCandidates) {
    Write-Output "Attempting download from: $downloadUrl"
    try {
        Invoke-WebRequest -Uri $downloadUrl -OutFile $tempZip -UseBasicParsing -Verbose
        $downloadSucceeded = $true
        break
    } catch {
        Write-Output "Download from $downloadUrl failed: $($_.Exception.Message)"
    }
}
if (-not $downloadSucceeded) {
    Write-Output 'All download attempts failed.'
    exit 2
}

# Prepare install dir
if (Test-Path $installDir) {
    Write-Output "Removing existing folder $installDir"
    Remove-Item -Recurse -Force $installDir
}
New-Item -ItemType Directory -Path $installDir | Out-Null

# Extract
Write-Output 'Extracting...'
try {
    Expand-Archive -Path $tempZip -DestinationPath $installDir -Force
} catch {
    Write-Output "Extract failed: $($_.Exception.Message)"
    exit 3
}

# The zip usually contains one top-level folder (jdk-17.x). Detect it.
$child = Get-ChildItem -Path $installDir -Directory | Select-Object -First 1
if ($child) { $jdkHome = $child.FullName } else { $jdkHome = $installDir }

Write-Output "Detected JDK home: $jdkHome"

# Set user JAVA_HOME (setx affects new terminals)
Write-Output 'Setting user JAVA_HOME via setx (applies to new terminals)'
setx JAVA_HOME $jdkHome | Out-Null

# Update current session
$env:JAVA_HOME = $jdkHome
$env:Path = (Join-Path $jdkHome 'bin') + ';' + $env:Path

Write-Output 'java -version (current session):'
try { & (Join-Path $jdkHome 'bin\java.exe') -version } catch { Write-Output "java -version failed: $($_.Exception.Message)" }

# Run Gradle build if gradlew exists
if (Test-Path '.\\gradlew') {
    Write-Output 'Running gradlew clean assembleDebug --no-daemon'
    & '.\\gradlew' clean assembleDebug --no-daemon
} else {
    Write-Output 'gradlew not found in project root; skipping Gradle build'
}

Write-Output 'Script finished.'
