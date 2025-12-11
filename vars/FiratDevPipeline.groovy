/**
 * Firat Development Pipeline - DEMO VERSION
 * Simple 6-stage demo pipeline that only prints messages
 */
def call(body) {
    // Parse config
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    // Get config values
    def projectName = config.projectName ?: 'firat-app'
    def slackChannel = config.slackChannel ?: '#deployments'
    def dockerRegistry = config.dockerRegistry ?: ''
    def grafanaUrl = config.grafanaUrl ?: ''
    def showGitInfo = config.showGitInfo ?: false
    def customMessage = config.customMessage ?: ''

    // Simple script-based pipeline without agent
    node {
        stage('1. Checkout') {
            echo ""
            echo "═══════════════════════════════════════"
            echo "📥 FIRAT DEV - STAGE 1: CHECKOUT"
            echo "═══════════════════════════════════════"
            echo "✓ Project: ${projectName}"
            if (showGitInfo) {
                echo "✓ Branch: main"
                echo "✓ Repository: github.com/firat-bcfm/mpl-design"
            }
            echo "✓ Checkout completed successfully!"
            echo "═══════════════════════════════════════"
            echo ""
        }

        stage('2. Build') {
            echo ""
            echo "═══════════════════════════════════════"
            echo "🔨 STAGE 2: BUILD"
            echo "═══════════════════════════════════════"
            echo "✓ Starting Maven build..."
            echo "✓ Compiling source code..."
            echo "✓ Creating JAR/WAR file..."
            echo "✓ Build completed successfully!"
            echo "═══════════════════════════════════════"
            echo ""
        }

        stage('3. Test') {
            echo ""
            echo "═══════════════════════════════════════"
            echo "🧪 STAGE 3: TEST"
            echo "═══════════════════════════════════════"
            echo "✓ Running unit tests..."
            echo "✓ Running integration tests..."
            echo "✓ All tests passed!"
            echo "✓ Test coverage: 85%"
            echo "═══════════════════════════════════════"
            echo ""
        }

        stage('4. Deploy') {
            echo ""
            echo "═══════════════════════════════════════"
            echo "🚀 FIRAT DEV - STAGE 4: DEPLOY"
            echo "═══════════════════════════════════════"
            if (dockerRegistry) {
                echo "✓ Docker Registry: ${dockerRegistry}"
                echo "✓ Pushing image to registry..."
            }
            echo "✓ Deploying to development environment..."
            echo "✓ Target: dev.firat.local:8080"
            echo "✓ Deployment completed!"
            echo "═══════════════════════════════════════"
            echo ""
        }

        stage('5. Smoke Test') {
            echo ""
            echo "═══════════════════════════════════════"
            echo "💨 STAGE 5: SMOKE TEST"
            echo "═══════════════════════════════════════"
            echo "✓ Testing endpoint: /health"
            echo "  → Status: 200 OK"
            echo "✓ Testing endpoint: /api/status"
            echo "  → Status: 200 OK"
            echo "✓ Testing endpoint: /api/info"
            echo "  → Status: 200 OK"
            echo "✓ All smoke tests passed!"
            echo "═══════════════════════════════════════"
            echo ""
        }

        stage('6. Post-Deploy Validation') {
            echo ""
            echo "═══════════════════════════════════════"
            echo "✅ FIRAT DEV - STAGE 6: VALIDATION"
            echo "═══════════════════════════════════════"
            echo "✓ Checking application health..."
            if (grafanaUrl) {
                echo "✓ Monitoring: ${grafanaUrl}"
            }
            echo "✓ All validations passed!"
            if (customMessage) {
                echo "✓ ${customMessage}"
            }
            echo "═══════════════════════════════════════"
            echo ""
        }

        // Success message
        echo ""
        echo "════════════════════════════════════════════════"
        echo "✓✓✓ FIRAT DEV PIPELINE - SUCCESS! ✓✓✓"
        echo "════════════════════════════════════════════════"
        echo "Project: ${projectName}"
        echo "Build: #${env.BUILD_NUMBER}"
        echo "Deployment: http://dev.firat.local:8080"
        if (slackChannel) {
            echo "Notification: ${slackChannel}"
        }
        if (grafanaUrl) {
            echo "Monitoring: ${grafanaUrl}"
        }
        echo "════════════════════════════════════════════════"
        echo ""
    }
}
