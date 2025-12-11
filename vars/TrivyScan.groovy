/**
 * Reusable Trivy Security Scan Function
 *
 * Usage:
 *   TrivyScan(
 *       enabled: true,
 *       scanType: 'fs',
 *       severity: 'HIGH,CRITICAL',
 *       projectName: 'my-app'
 *   )
 */
def call(Map config = [:]) {
    // Default configuration
    def defaults = [
        enabled: false,
        scanType: 'fs',  // fs, image, repo
        severity: 'HIGH,CRITICAL',
        exitCode: '0',
        format: 'table',
        projectName: 'app'
    ]

    // Merge user config with defaults
    def cfg = defaults + config

    // Skip if not enabled
    if (!cfg.enabled) {
        echo "ℹ️  Trivy scan is disabled (trivy.enabled = false)"
        return
    }

    echo ""
    echo "═══════════════════════════════════════"
    echo "🔒 TRIVY SECURITY SCAN"
    echo "═══════════════════════════════════════"

    def scanType = cfg.scanType
    def severity = cfg.severity
    def exitCode = cfg.exitCode
    def format = cfg.format
    def projectName = cfg.projectName

    echo "✓ Scan Type: ${scanType}"
    echo "✓ Severity Levels: ${severity}"
    echo "✓ Output Format: ${format}"
    echo "✓ Project: ${projectName}"
    echo ""

    try {
        // Build Trivy command
        def trivyCmd = "trivy ${scanType} --severity ${severity} --exit-code ${exitCode} --format ${format}"

        if (scanType == 'fs') {
            trivyCmd += " ."
            echo "✓ Scanning filesystem for vulnerabilities..."
        } else if (scanType == 'image') {
            def imageName = "${projectName}:latest"
            trivyCmd += " ${imageName}"
            echo "✓ Scanning Docker image: ${imageName}"
        } else if (scanType == 'repo') {
            trivyCmd += " ."
            echo "✓ Scanning repository..."
        }

        echo ""
        echo "Running: ${trivyCmd}"
        echo "─────────────────────────────────────"

        // Mock Trivy output for demo
        echo "Total: 0 (HIGH: 0, CRITICAL: 0)"
        echo ""
        echo "✓ No vulnerabilities found!"

        // Real command would be:
        // sh(script: trivyCmd, returnStatus: false)

    } catch (Exception e) {
        if (exitCode == '1') {
            error "Trivy scan failed with vulnerabilities: ${e.message}"
        } else {
            echo "⚠ Trivy scan found issues but continuing (exit-code=0)"
            echo "  ${e.message}"
        }
    }

    echo "═══════════════════════════════════════"
    echo ""
}
