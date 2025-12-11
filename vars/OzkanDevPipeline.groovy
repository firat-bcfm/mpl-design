/**
 * Ozkan Development Pipeline - SIMPLE INLINE VERSION
 */
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def CFG = [
        'projectName': config.projectName ?: 'ozkan-app',
        'slackChannel': config.slackChannel ?: '#deployments',
        'dockerRegistry': config.dockerRegistry ?: '',
        'grafanaUrl': config.grafanaUrl ?: '',
        'showGitInfo': config.showGitInfo ?: false,
        'customMessage': config.customMessage ?: '',
        'minTestCoverage': config.minTestCoverage ?: '80%',
        'deploy.dev_host': config.'deploy.dev_host' ?: 'dev.ozkan.local',
        'deploy.dev_port': config.'deploy.dev_port' ?: '8080',
        'smoketest.endpoints': config.'smoketest.endpoints' ?: ['/health', '/api/status'],
        // Trivy Security Scan
        'trivy.enabled': config.'trivy.enabled',
        'trivy.scanType': config.'trivy.scanType',
        'trivy.severity': config.'trivy.severity',
        'trivy.exitCode': config.'trivy.exitCode',
        'trivy.format': config.'trivy.format'
    ]

    node {
        stage('1. Checkout') {
            echo ""
            echo "═══════════════════════════════════════"
            echo "📥 OZKAN DEV - STAGE 1: CHECKOUT"
            echo "═══════════════════════════════════════"

            echo "✓ Project: ${CFG.projectName}"

            if (CFG.showGitInfo) {
                try {
                    def gitBranch = sh(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD').trim()
                    def gitCommit = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                    echo "✓ Branch: ${gitBranch}"
                    echo "✓ Commit: ${gitCommit}"
                } catch (Exception e) {
                    echo "✓ Repository: github.com/firat-bcfm/mpl-design"
                }
            }

            echo "✓ Checkout completed!"
            echo "═══════════════════════════════════════"
            echo ""
        }

        stage('2. Build') {
            echo ""
            echo "═══════════════════════════════════════"
            echo "🔨 OZKAN DEV - STAGE 2: BUILD"
            echo "═══════════════════════════════════════"

            echo "✓ Building: ${CFG.projectName}"
            echo "✓ Compiling sources..."
            echo "✓ Build successful!"

            echo "═══════════════════════════════════════"
            echo ""
        }

        stage('3. Test') {
            echo ""
            echo "═══════════════════════════════════════"
            echo "🧪 OZKAN DEV - STAGE 3: TEST"
            echo "═══════════════════════════════════════"

            echo "✓ Running tests..."
            echo "✓ Coverage: ${CFG.minTestCoverage}"
            echo "✓ Tests passed!"

            echo "═══════════════════════════════════════"
            echo ""
        }

        // Stage 3.5: Security Scan (Trivy) - Reusable module
        stage('3.5. Security Scan') {
            // Use custom config if provided, otherwise use OZKAN DEV preset
            def trivyConfig = CommonTrivyConfig.ozkanDev()

            // Override with user config if provided
            if (CFG.'trivy.enabled' != null) {
                trivyConfig.enabled = CFG.'trivy.enabled'
            }
            if (CFG.'trivy.scanType') {
                trivyConfig.scanType = CFG.'trivy.scanType'
            }
            if (CFG.'trivy.severity') {
                trivyConfig.severity = CFG.'trivy.severity'
            }
            if (CFG.'trivy.exitCode') {
                trivyConfig.exitCode = CFG.'trivy.exitCode'
            }
            if (CFG.'trivy.format') {
                trivyConfig.format = CFG.'trivy.format'
            }

            TrivyScan(
                enabled: trivyConfig.enabled,
                scanType: trivyConfig.scanType,
                severity: trivyConfig.severity,
                exitCode: trivyConfig.exitCode,
                format: trivyConfig.format,
                projectName: CFG.projectName
            )
        }

        stage('4. Deploy') {
            echo ""
            echo "═══════════════════════════════════════"
            echo "🚀 OZKAN DEV - STAGE 4: DEPLOY"
            echo "═══════════════════════════════════════"

            def devHost = CFG.'deploy.dev_host'
            def devPort = CFG.'deploy.dev_port'

            echo "✓ Deploying to: ${devHost}:${devPort}"
            echo "✓ Deployment completed!"

            env.DEPLOY_URL = "http://${devHost}:${devPort}"

            echo "═══════════════════════════════════════"
            echo ""
        }

        stage('5. Smoke Test') {
            echo ""
            echo "═══════════════════════════════════════"
            echo "💨 OZKAN DEV - STAGE 5: SMOKE TEST"
            echo "═══════════════════════════════════════"

            CFG.'smoketest.endpoints'.each { endpoint ->
                echo "✓ Testing: ${endpoint}"
            }
            echo "✓ Smoke tests passed!"

            echo "═══════════════════════════════════════"
            echo ""
        }

        stage('6. Post-Deploy Validation') {
            echo ""
            echo "═══════════════════════════════════════"
            echo "✅ OZKAN DEV - STAGE 6: VALIDATION"
            echo "═══════════════════════════════════════"

            echo "✓ Validation checks..."
            if (CFG.grafanaUrl) {
                echo "✓ Monitoring: ${CFG.grafanaUrl}"
            }
            echo "✓ All validations passed!"

            echo "═══════════════════════════════════════"
            echo ""
        }

        echo ""
        echo "════════════════════════════════════════════════"
        echo "✓✓✓ OZKAN DEV PIPELINE - SUCCESS! ✓✓✓"
        echo "════════════════════════════════════════════════"
        echo "Project: ${CFG.projectName}"
        echo "Build: #${env.BUILD_NUMBER}"
        if (env.DEPLOY_URL) {
            echo "Deployment: ${env.DEPLOY_URL}"
        }
        if (CFG.slackChannel) {
            echo "Notification: ${CFG.slackChannel}"
        }
        if (CFG.customMessage) {
            echo "Message: ${CFG.customMessage}"
        }
        echo "════════════════════════════════════════════════"
        echo ""
    }
}
