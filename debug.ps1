Write-Host "=== Make Space Scheduler - Debug Script ===" -ForegroundColor Cyan
Write-Host "===========================================" -ForegroundColor Cyan

# 1. Show current directory
Write-Host "`n[1] Current directory:" -ForegroundColor Yellow
$currentDir = Get-Location
Write-Host "   $currentDir"

# 2. Check Java installation
Write-Host "`n[2] Java version check:" -ForegroundColor Yellow
java -version 2>&1 | Out-Host

# 3. List source files
Write-Host "`n[3] Source files found:" -ForegroundColor Yellow
$sourceFiles = Get-ChildItem -Path "src/main/java/com/makespace" -Filter "*.java" -Recurse -File
foreach ($file in $sourceFiles) {
    Write-Host "   $($file.FullName.Substring($currentDir.Path.Length + 1))"
}

# 4. Clean and compile
Write-Host "`n[4] Cleaning bin directory..." -ForegroundColor Yellow
Remove-Item -Path "bin" -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path "bin" -Force | Out-Null

# 5. Show the compile command
Write-Host "`n[5] Compiling with command:" -ForegroundColor Yellow
Write-Host "   javac -d ../../../bin com/makespace/*.java com/makespace/models/*.java"

# 6. Execute compile command
Set-Location "src/main/java"
$compileResult = javac -d ../../../bin com/makespace/*.java com/makespace/models/*.java 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✅ Compilation successful" -ForegroundColor Green
} else {
    Write-Host "   ❌ Compilation failed:" -ForegroundColor Red
    Write-Host "   $compileResult" -ForegroundColor Red
}

# 7. Return to project root and check results
Set-Location $currentDir

Write-Host "`n[6] Checking generated class files:" -ForegroundColor Yellow
if (Test-Path "bin") {
    $classFiles = Get-ChildItem -Path "bin" -Filter "*.class" -Recurse -File
    if ($classFiles.Count -gt 0) {
        Write-Host "   Found $($classFiles.Count) class files:" -ForegroundColor Green
        foreach ($file in $classFiles) {
            $relativePath = $file.FullName.Substring($currentDir.Path.Length + 1)
            Write-Host "   - $relativePath" -ForegroundColor Gray
        }
        
        # Check for Main.class specifically
        $mainClassPath = "bin/com/makespace/Main.class"
        if (Test-Path $mainClassPath) {
            Write-Host "`n   ✅ Found Main.class at: $mainClassPath" -ForegroundColor Green
            
            # Try to run
            Write-Host "`n[7] Attempting to run application..." -ForegroundColor Cyan
            java -cp bin com.makespace.Main
        } else {
            Write-Host "`n   ❌ Main.class NOT found at expected location" -ForegroundColor Red
            Write-Host "   Expected: $mainClassPath" -ForegroundColor Red
            Write-Host "`n   Actual bin structure:" -ForegroundColor Yellow
            tree bin /f
        }
    } else {
        Write-Host "   ❌ No class files found in bin directory" -ForegroundColor Red
        Write-Host "   Bin directory contents:" -ForegroundColor Yellow
        Get-ChildItem -Path "bin" -Recurse
    }
} else {
    Write-Host "   ❌ Bin directory was not created" -ForegroundColor Red
}

Write-Host "`n=== Debug complete ===" -ForegroundColor Cyan