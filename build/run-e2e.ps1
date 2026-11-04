$root = Split-Path -Parent $PSScriptRoot
New-Item -ItemType Directory -Force -Path (Join-Path $root "build\reports") | Out-Null
Push-Location (Join-Path $root "tests")
try {
    mvn test
    exit $LASTEXITCODE
} finally {
    Pop-Location
}
