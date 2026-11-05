$root = Split-Path -Parent $PSScriptRoot
$reports = Join-Path $root "build\reports"
New-Item -ItemType Directory -Force -Path $reports | Out-Null

function Invoke-Step {
    param(
        [string]$Label,
        [string]$Directory,
        [string]$Command
    )
    Write-Host "== $Label ($Directory): $Command"
    Push-Location (Join-Path $root $Directory)
    try {
        $output = & cmd /c $Command 2>&1
        $code = $LASTEXITCODE
        $output | ForEach-Object { Write-Host $_ }
        return [int]$code
    } finally {
        Pop-Location
    }
}

function New-Check {
    param(
        [string]$Name,
        [int]$ExitCode,
        [string]$PassedDetails
    )
    if ($ExitCode -eq 0) {
        return [ordered]@{
            name = $Name
            status = "PASSED"
            details = $PassedDetails
        }
    }
    return [ordered]@{
        name = $Name
        status = "FAILED"
        details = "exit code $ExitCode"
    }
}

$lintCode = Invoke-Step -Label "lint" -Directory "frontend" -Command "npm run lint"
$frontendTest = Invoke-Step -Label "frontend test" -Directory "frontend" -Command "npm test"
$backendTest = Invoke-Step -Label "backend test" -Directory "backend" -Command "mvn -B test"
$testCode = if ($frontendTest -eq 0 -and $backendTest -eq 0) { 0 } else { 1 }
$frontendBuild = Invoke-Step -Label "frontend build" -Directory "frontend" -Command "npm run build"
$backendBuild = Invoke-Step -Label "backend build" -Directory "backend" -Command "mvn -B -DskipTests package"
$buildCode = if ($frontendBuild -eq 0 -and $backendBuild -eq 0) { 0 } else { 1 }

$checks = @(
    (New-Check -Name "lint" -ExitCode $lintCode -PassedDetails "frontend: npm run lint"),
    (New-Check -Name "test" -ExitCode $testCode -PassedDetails "frontend: npm test; backend: mvn test"),
    (New-Check -Name "build" -ExitCode $buildCode -PassedDetails "frontend: npm run build; backend: mvn -DskipTests package")
)

$reportPath = Join-Path $reports "checks.json"
$checks | ConvertTo-Json | Set-Content -Path $reportPath -Encoding utf8
Write-Host ""
Get-Content $reportPath

$failed = @($checks | Where-Object { $_.status -ne "PASSED" })
if ($failed.Count -gt 0) {
    exit 1
}
exit 0
