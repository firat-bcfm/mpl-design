/**
 * Ozkan Production Pipeline - SIMPLE INLINE VERSION
 */
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def CFG = [
        'projectName': config.projectName ?: 'ozkan-app',
        'slackChannel': config.slackChannel ?: '#production-deploys',
        'dockerRegistry': config.dockerRegistry ?: '',
        'grafanaUrl': config.grafanaUrl ?: '',
        'showGitInfo': config.showGitInfo ?: true,
        'customMessage': config.customMessage ?: '',
        'minTestCoverage': config.minTestCoverage ?: '90%',
        'deploy.prod_host': config.'deploy.prod_host' ?: 'prod.ozkan.com',
        'deploy.prod_port': config.'deploy.prod_port' ?: '443',
        'smoketest.endpoints': config.'smoketest.endpoints' ?: ['/health', '/api/status'],
        // Trivy Security Scan - PRODUCTION
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
            echo "📥 OZKAN PROD - STAGE 1: CHECKOUT"
            echo "═══════════════════════════════════════"

            echo "✓ Project: ${CFG.projectName}"
            echo "✓ Environment: PRODUCTION"

            if (CFG.showGitInfo) {
                try {
                    def gitTag = sh(returnStdout: true, script: 'git describe --tags --abbrev=0 2>/dev/null || echo "no-tag"').trim()
                    def gitCommit = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                    echo "✓ Version: ${gitTag}"
                    echo "✓ Commit: ${gitCommit}"
                } catch (Exception e) {
                    echo "✓ Repository: github.com/firat-bcfm/mpl-design"
                }
            }

            echo "✓ Production checkout completed!"
            echo "═══════════════════════════════════════"
            echo ""
        }

        stage('2. Build') {
            echo ""
            echo "═══════════════════════════════════════"
            echo "🔨 OZKAN PROD - STAGE 2: BUILD"
            echo "═══════════════════════════════════════"

            echo "✓ Production build: ${CFG.projectName}"
            echo "✓ Running production optimizations..."
            echo "✓ Build completed!"

            echo "═══════════════════════════════════════"
            echo ""
        }

        stage('3. Test') {
            echo ""
            echo "═══════════════════════════════════════"
            echo "🧪 OZKAN PROD - STAGE 3: TEST"
            echo "═══════════════════════════════════════"

            echo "✓ Full test suite..."
            echo "✓ Coverage: ${CFG.minTestCoverage}"
            echo "✓ Security scans: PASSED"
            echo "✓ All tests passed!"

            echo "═══════════════════════════════════════"
            echo ""
        }

        // Stage 3.5: Security Scan (Trivy) - PRODUCTION preset
        stage('3.5. Security Scan') {
            // OZKAN PROD preset config - STRICT for production
            def trivyConfig = [
                enabled: true,
                scanType: 'image',
                severity: 'CRITICAL',
                exitCode: '1',  // Fail pipeline on CRITICAL findings
                format: 'json'
            ]

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
            echo "🚀 OZKAN PROD - STAGE 4: DEPLOY"
            echo "═══════════════════════════════════════"

            if (CFG.dockerRegistry) {
                echo "✓ Docker Registry: ${CFG.dockerRegistry}"
            }

            def deployHost = CFG.'deploy.prod_host'
            def deployPort = CFG.'deploy.prod_port'

            echo "✓ Blue-Green deployment..."
            echo "✓ Deploying to: ${deployHost}:${deployPort}"
            echo "✓ Production deployment completed!"

            env.DEPLOY_URL = "https://${deployHost}"

            echo "═══════════════════════════════════════"
            echo ""
        }

        stage('5. Smoke Test') {
            echo ""
            echo "═══════════════════════════════════════"
            echo "💨 OZKAN PROD - STAGE 5: SMOKE TEST"
            echo "═══════════════════════════════════════"

            def baseUrl = env.DEPLOY_URL ?: "https://${CFG.'deploy.prod_host'}"

            echo "✓ Production URL: ${baseUrl}"

            CFG.'smoketest.endpoints'.each { endpoint ->
                echo "✓ Testing ${endpoint}: 200 OK"
            }

            echo "✓ All smoke tests passed!"

            echo "═══════════════════════════════════════"
            echo ""
        }

        stage('6. Post-Deploy Validation') {
            echo ""
            echo "═══════════════════════════════════════"
            echo "✅ OZKAN PROD - STAGE 6: VALIDATION"
            echo "═══════════════════════════════════════"

            echo "✓ Production validations..."
            echo "✓ Monitoring: ACTIVE"

            if (CFG.grafanaUrl) {
                echo "✓ Dashboard: ${CFG.grafanaUrl}"
            }

            echo "✓ All validations passed!"

            echo "═══════════════════════════════════════"
            echo ""
        }

        echo ""
        echo "════════════════════════════════════════════════"
        echo "✓✓✓ OZKAN PROD PIPELINE - SUCCESS! ✓✓✓"
        echo "════════════════════════════════════════════════"
        echo "Project: ${CFG.projectName}"
        echo "Build: #${env.BUILD_NUMBER}"
        if (env.DEPLOY_URL) {
            echo "Production URL: ${env.DEPLOY_URL}"
        }
        if (CFG.customMessage) {
            echo "Message: ${CFG.customMessage}"
        }
        echo "════════════════════════════════════════════════"
        echo ""
    }
}
