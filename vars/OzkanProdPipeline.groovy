/**
 * Ozkan Production Pipeline - MODULAR VERSION
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
        'smoketest.endpoints': config.'smoketest.endpoints' ?: ['/health', '/api/status']
    ]

    node {
        stage('1. Checkout') {
            def moduleCode = libraryResource('com/ozkan/pipeline/modules/Checkout/ProdCheckout.groovy')
            evaluate(moduleCode)
        }

        stage('2. Build') {
            def moduleCode = libraryResource('com/ozkan/pipeline/modules/Build/ProdBuild.groovy')
            evaluate(moduleCode)
        }

        stage('3. Test') {
            def moduleCode = libraryResource('com/ozkan/pipeline/modules/Test/ProdTest.groovy')
            evaluate(moduleCode)
        }

        stage('4. Deploy') {
            def moduleCode = libraryResource('com/ozkan/pipeline/modules/Deploy/ProdDeploy.groovy')
            evaluate(moduleCode)
        }

        stage('5. Smoke Test') {
            def moduleCode = libraryResource('com/ozkan/pipeline/modules/SmokeTest/ProdSmokeTest.groovy')
            evaluate(moduleCode)
        }

        stage('6. Post-Deploy Validation') {
            def moduleCode = libraryResource('com/ozkan/pipeline/modules/PostDeployValidation/ProdValidation.groovy')
            evaluate(moduleCode)
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
        echo "════════════════════════════════════════════════"
        echo ""
    }
}
