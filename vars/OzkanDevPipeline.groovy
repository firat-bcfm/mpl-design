/**
 * Ozkan Development Pipeline - MODULAR VERSION
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
        'projectName': config.projectName ?: 'ozkan-app',
        'slackChannel': config.slackChannel ?: '#deployments',
        'dockerRegistry': config.dockerRegistry ?: '',
        'grafanaUrl': config.grafanaUrl ?: '',
        'showGitInfo': config.showGitInfo ?: false,
        'customMessage': config.customMessage ?: '',
        'minTestCoverage': config.minTestCoverage ?: '80%',
        'deploy.dev_host': config.'deploy.dev_host' ?: 'dev.ozkan.local',
        'deploy.dev_port': config.'deploy.dev_port' ?: '8080',
        'smoketest.endpoints': config.'smoketest.endpoints' ?: ['/health', '/api/status']
    ]

    // Pipeline execution
    node {
        // Make CFG available to evaluated modules
        binding.setVariable('CFG', CFG)

        // Stage 1: Checkout
        stage('1. Checkout') {
            def moduleCode = libraryResource('com/ozkan/pipeline/modules/Checkout/DevCheckout.groovy')
            evaluate(moduleCode)
        }

        // Stage 2: Build
        stage('2. Build') {
            def moduleCode = libraryResource('com/ozkan/pipeline/modules/Build/DevBuild.groovy')
            evaluate(moduleCode)
        }

        // Stage 3: Test
        stage('3. Test') {
            def moduleCode = libraryResource('com/ozkan/pipeline/modules/Test/DevTest.groovy')
            evaluate(moduleCode)
        }

        // Stage 4: Deploy
        stage('4. Deploy') {
            def moduleCode = libraryResource('com/ozkan/pipeline/modules/Deploy/DevDeploy.groovy')
            evaluate(moduleCode)
        }

        // Stage 5: Smoke Test
        stage('5. Smoke Test') {
            def moduleCode = libraryResource('com/ozkan/pipeline/modules/SmokeTest/DevSmokeTest.groovy')
            evaluate(moduleCode)
        }

        // Stage 6: Validation
        stage('6. Post-Deploy Validation') {
            def moduleCode = libraryResource('com/ozkan/pipeline/modules/PostDeployValidation/DevValidation.groovy')
            evaluate(moduleCode)
        }

        // Success message
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
        echo "════════════════════════════════════════════════"
        echo ""
    }
}
