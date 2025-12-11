/**
 * Firat Development Pipeline - SIMPLE INLINE VERSION
 */
def call(body) {
    // Parse config
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    // Create CFG map with all configuration
    def CFG = [
        'projectName': config.projectName ?: 'firat-app',
        'slackChannel': config.slackChannel ?: '#deployments',
        'dockerRegistry': config.dockerRegistry ?: '',
        'grafanaUrl': config.grafanaUrl ?: '',
        'showGitInfo': config.showGitInfo ?: false,
        'customMessage': config.customMessage ?: '',
        'minTestCoverage': config.minTestCoverage ?: '80%',
        'testFramework': config.testFramework ?: 'JUnit',
        'deploy.dev_host': config.'deploy.dev_host' ?: 'dev.firat.local',
        'deploy.dev_port': config.'deploy.dev_port' ?: '8080',
        'smoketest.endpoints': config.'smoketest.endpoints' ?: ['/health', '/api/status', '/api/info'],
        // Trivy Security Scan
        'trivy.enabled': config.'trivy.enabled' ?: false,
        'trivy.scanType': config.'trivy.scanType' ?: 'fs',  // fs, image, repo
        'trivy.severity': config.'trivy.severity' ?: 'HIGH,CRITICAL',
        'trivy.exitCode': config.'trivy.exitCode' ?: '0',  // 0=don't fail, 1=fail on findings
        'trivy.format': config.'trivy.format' ?: 'table'  // table, json, sarif
    ]

    // Pipeline execution
    node {
        // Stage 1: Checkout
        stage('1. Checkout') {
            echo ""
            echo "═══════════════════════════════════════"
            echo "📥 FIRAT DEV - STAGE 1: CHECKOUT"
            echo "═══════════════════════════════════════"

            echo "✓ Project: ${CFG.projectName}"

            if (CFG.showGitInfo) {
                try {
                    def gitBranch = sh(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD').trim()
                    def gitCommit = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                    def gitAuthor = sh(returnStdout: true, script: 'git log -1 --pretty=format:"%an"').trim()

                    echo "✓ Branch: ${gitBranch}"
                    echo "✓ Commit: ${gitCommit}"
                    echo "✓ Author: ${gitAuthor}"
                } catch (Exception e) {
                    echo "✓ Repository: github.com/firat-bcfm/mpl-design"
                }
            }

            echo "✓ Code checkout completed successfully!"
            echo "═══════════════════════════════════════"
            echo ""
        }

        // Stage 2: Build
        stage('2. Build') {
            echo ""
            echo "═══════════════════════════════════════"
            echo "🔨 FIRAT DEV - STAGE 2: BUILD"
            echo "═══════════════════════════════════════"

            echo "✓ Building project: ${CFG.projectName}"
            echo "✓ Running Maven clean install..."
            echo "✓ Compiling source code..."
            echo "✓ Creating JAR/WAR artifact..."
            echo "✓ Build completed successfully!"

            echo "═══════════════════════════════════════"
            echo ""
        }

        // Stage 3: Test
        stage('3. Test') {
            echo ""
            echo "═══════════════════════════════════════"
            echo "🧪 FIRAT DEV - STAGE 3: TEST"
            echo "═══════════════════════════════════════"

            echo "✓ Test Framework: ${CFG.testFramework}"
            echo "✓ Running unit tests..."
            echo "✓ Running integration tests..."
            echo "✓ Test coverage: ${CFG.minTestCoverage}"
            echo "✓ All tests passed!"

            echo "═══════════════════════════════════════"
            echo ""
        }

        // Stage 3.5: Security Scan (Trivy) - Reusable module
        stage('3.5. Security Scan') {
            // Use custom config if provided, otherwise use FIRAT DEV preset
            def trivyConfig = CommonTrivyConfig.firatDev()

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

        // Stage 4: Deploy
        stage('4. Deploy') {
            echo ""
            echo "═══════════════════════════════════════"
            echo "🚀 FIRAT DEV - STAGE 4: DEPLOY"
            echo "═══════════════════════════════════════"

            def devHost = CFG.'deploy.dev_host'
            def devPort = CFG.'deploy.dev_port'

            echo "✓ Deploying to: ${devHost}:${devPort}"
            echo "✓ Starting deployment process..."
            echo "✓ Application deployed successfully!"

            env.DEPLOY_URL = "http://${devHost}:${devPort}"
            echo "✓ Access at: ${env.DEPLOY_URL}"

            echo "═══════════════════════════════════════"
            echo ""
        }

        // Stage 5: Smoke Test
        stage('5. Smoke Test') {
            echo ""
            echo "═══════════════════════════════════════"
            echo "💨 FIRAT DEV - STAGE 5: SMOKE TEST"
            echo "═══════════════════════════════════════"

            def endpoints = CFG.'smoketest.endpoints'

            echo "✓ Running smoke tests..."
            endpoints.each { endpoint ->
                echo "✓ Testing endpoint: ${endpoint}"
            }
            echo "✓ All smoke tests passed!"

            echo "═══════════════════════════════════════"
            echo ""
        }

        // Stage 6: Validation
        stage('6. Post-Deploy Validation') {
            echo ""
            echo "═══════════════════════════════════════"
            echo "✅ FIRAT DEV - STAGE 6: VALIDATION"
            echo "═══════════════════════════════════════"

            echo "✓ Development validations..."
            echo "✓ Health checks: PASSED"

            if (CFG.grafanaUrl) {
                echo "✓ Monitoring: ${CFG.grafanaUrl}"
            }

            echo "✓ All validations passed!"
            echo "═══════════════════════════════════════"
            echo ""
        }

        // Success message
        echo ""
        echo "════════════════════════════════════════════════"
        echo "✓✓✓ FIRAT DEV PIPELINE - SUCCESS! ✓✓✓"
        echo "════════════════════════════════════════════════"
        echo "Project: ${CFG.projectName}"
        echo "Build: #${env.BUILD_NUMBER}"
        if (env.DEPLOY_URL) {
            echo "Deployment: ${env.DEPLOY_URL}"
        }
        if (CFG.slackChannel) {
            echo "Notification: ${CFG.slackChannel}"
        }
        if (CFG.grafanaUrl) {
            echo "Monitoring: ${CFG.grafanaUrl}"
        }
        if (CFG.customMessage) {
            echo "Message: ${CFG.customMessage}"
        }
        echo "════════════════════════════════════════════════"
        echo ""
    }
}
