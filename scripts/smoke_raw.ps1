param(
    [string]$BaseUrl = "http://127.0.0.1:8080",
    [long]$Sid = 71236848,
    [string]$LogDate = "2026-03-25",
    [int]$Limit = 3
)

$ErrorActionPreference = "Stop"

function Invoke-RawGet {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Url
    )

    $raw = (Invoke-WebRequest $Url -UseBasicParsing).Content
    if ([string]::IsNullOrWhiteSpace($raw)) {
        throw "Empty response from: $Url"
    }

    return $raw.Trim()
}

function Invoke-JsonGet {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Url
    )

    $raw = Invoke-RawGet -Url $Url
    return ($raw | ConvertFrom-Json)
}

function Assert-Success {
    param(
        [Parameter(Mandatory = $true)]
        $Response,
        [Parameter(Mandatory = $true)]
        [string]$Name
    )

    if ($null -eq $Response) {
        throw "$Name returned null response"
    }

    if ($Response.success -ne $true) {
        $msg = $Response.message
        if ([string]::IsNullOrWhiteSpace($msg)) {
            $msg = "unknown error"
        }
        throw "$Name failed: $msg"
    }
}

Write-Host ""
Write-Host "=== yunbian_ads_scoring_java smoke start ==="
Write-Host "BaseUrl : $BaseUrl"
Write-Host "Sid     : $Sid"
Write-Host "LogDate : $LogDate"
Write-Host "Limit   : $Limit"
Write-Host ""

$healthUrl = "$BaseUrl/health"
$dbPingUrl = "$BaseUrl/db/ping"
$dbPingMybatisUrl = "$BaseUrl/db/ping-mybatis"
$rawSmokeUrl = "$BaseUrl/smoke/raw?sid=$Sid&logDate=$LogDate&limit=$Limit"

$healthRaw = Invoke-RawGet -Url $healthUrl
if ($healthRaw -ne "ok") {
    throw "/health failed: expected 'ok', actual '$healthRaw'"
}
Write-Host "[OK] /health"

$dbPing = Invoke-JsonGet -Url $dbPingUrl
Assert-Success -Response $dbPing -Name "/db/ping"
Write-Host "[OK] /db/ping          mode=$($dbPing.data.mode) db=$($dbPing.data.name)"

$dbPingMybatis = Invoke-JsonGet -Url $dbPingMybatisUrl
Assert-Success -Response $dbPingMybatis -Name "/db/ping-mybatis"
Write-Host "[OK] /db/ping-mybatis  mode=$($dbPingMybatis.data.mode) db=$($dbPingMybatis.data.name)"

$rawSmoke = Invoke-JsonGet -Url $rawSmokeUrl
Assert-Success -Response $rawSmoke -Name "/smoke/raw"

if ($null -eq $rawSmoke.data) {
    throw "/smoke/raw missing data"
}

if ($null -eq $rawSmoke.data.campaign -or $null -eq $rawSmoke.data.adgroup -or $null -eq $rawSmoke.data.bidword) {
    throw "/smoke/raw missing campaign/adgroup/bidword sections"
}

$campaignCount = [int]$rawSmoke.data.campaign.count
$adgroupCount = [int]$rawSmoke.data.adgroup.count
$bidwordCount = [int]$rawSmoke.data.bidword.count

Write-Host "[OK] /smoke/raw"
Write-Host "     campaign count = $campaignCount"
Write-Host "     adgroup  count = $adgroupCount"
Write-Host "     bidword  count = $bidwordCount"
Write-Host ""

Write-Host "=== smoke passed ==="