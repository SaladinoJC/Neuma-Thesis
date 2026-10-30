param([switch]$Bibliografia)

$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    foreach ($tool in @('xelatex')) {
        if (-not (Get-Command $tool -ErrorAction SilentlyContinue)) {
            throw "Falta $tool en PATH. Instalar/configurar MiKTeX o TeX Live."
        }
    }
    if ($Bibliografia -and -not (Get-Command biber -ErrorAction SilentlyContinue)) {
        throw 'Falta biber en PATH.'
    }
    New-Item -ItemType Directory -Path build -Force | Out-Null
    $latexArgs = @('--disable-installer', '-interaction=nonstopmode', '-halt-on-error',
                   '-no-shell-escape', '-output-directory=build', 'main.tex')
    function Invoke-Latex {
        & xelatex @latexArgs *> build/compilacion.txt
        if ($LASTEXITCODE -ne 0) {
            Get-Content -LiteralPath build/compilacion.txt -Tail 45
            throw 'Fallo XeLaTeX. Revisar Informe/build/main.log.'
        }
    }
    Invoke-Latex
    if ($Bibliografia) {
        # Cache local regenerable y locale compatible con Perl en Windows.
        $biberEnvironment = @{}
        foreach ($name in @('LC_ALL', 'LC_CTYPE', 'LANG', 'PAR_GLOBAL_TMPDIR')) {
            $biberEnvironment[$name] = [Environment]::GetEnvironmentVariable($name, 'Process')
        }
        try {
            $env:LC_ALL = 'C'
            $env:LC_CTYPE = 'C'
            $env:LANG = 'C'
            $env:PAR_GLOBAL_TMPDIR = Join-Path $PSScriptRoot 'build/biber-cache'
            & biber --input-directory build --output-directory build main *> build/bibliografia.txt
            if ($LASTEXITCODE -ne 0) {
                Get-Content -LiteralPath build/bibliografia.txt -Tail 45
                throw 'Fallo Biber. Revisar Informe/build/main.blg.'
            }
        } finally {
            foreach ($name in $biberEnvironment.Keys) {
                [Environment]::SetEnvironmentVariable($name, $biberEnvironment[$name], 'Process')
            }
        }
    }
    Invoke-Latex
    Invoke-Latex
    Write-Output "PDF generado: $PSScriptRoot/build/main.pdf"
} finally {
    Pop-Location
}
