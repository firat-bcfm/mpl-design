/**
 * Firat Development Pipeline - MODULAR VERSION
 * Uses separate module files for each stage
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
        'smoketest.endpoints': config.'smoketest.endpoints' ?: ['/health', '/api/status', '/api/info']
    ]

    // Pipeline execution
    node {
        // Stage 1: Checkout
        stage('1. Checkout') {
            def moduleCode = libraryResource('com/firat/pipeline/modules/Checkout/DevCheckout.groovy')
            def binding = new Binding([CFG: CFG, env: env])
            new GroovyShell(binding).evaluate(moduleCode)
        }

        // Stage 2: Build
        stage('2. Build') {
            def moduleCode = libraryResource('com/firat/pipeline/modules/Build/DevBuild.groovy')
            def binding = new Binding([CFG: CFG, env: env])
            new GroovyShell(binding).evaluate(moduleCode)
        }

        // Stage 3: Test
        stage('3. Test') {
            def moduleCode = libraryResource('com/firat/pipeline/modules/Test/DevTest.groovy')
            def binding = new Binding([CFG: CFG, env: env])
            new GroovyShell(binding).evaluate(moduleCode)
        }

        // Stage 4: Deploy
        stage('4. Deploy') {
            def moduleCode = libraryResource('com/firat/pipeline/modules/Deploy/DevDeploy.groovy')
            def binding = new Binding([CFG: CFG, env: env])
            new GroovyShell(binding).evaluate(moduleCode)
        }

        // Stage 5: Smoke Test
        stage('5. Smoke Test') {
            def moduleCode = libraryResource('com/firat/pipeline/modules/SmokeTest/DevSmokeTest.groovy')
            def binding = new Binding([CFG: CFG, env: env])
            new GroovyShell(binding).evaluate(moduleCode)
        }

        // Stage 6: Validation
        stage('6. Post-Deploy Validation') {
            def moduleCode = libraryResource('com/firat/pipeline/modules/PostDeployValidation/DevValidation.groovy')
            def binding = new Binding([CFG: CFG, env: env])
            new GroovyShell(binding).evaluate(moduleCode)
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
        echo "════════════════════════════════════════════════"
        echo ""
    }
}
