/**
 * Firat Dev - Deploy Module
 * Step 4/6: Deploy to development environment
 */

echo "========================================="
echo "Step 4/6: Deploying to DEV environment..."
echo "========================================="

def deployHost = CFG.'deploy.dev_host' ?: 'dev.firat.local'
def deployPort = CFG.'deploy.dev_port' ?: '8080'
def artifactPath = 'target/*.jar'

echo "Deployment Configuration:"
echo "  - Target: ${deployHost}:${deployPort}"
echo "  - Environment: development"
echo "  - Artifact: ${artifactPath}"

try {
    // Find the artifact
    def artifact = sh(returnStdout: true, script: "ls ${artifactPath} | head -1").trim()
    echo "Deploying artifact: ${artifact}"

    // Stop existing application (if configured)
    if (CFG.'deploy.stop_command') {
        echo "Stopping existing application..."
        sh CFG.'deploy.stop_command'
        sleep 5
    }

    // Deploy artifact
    echo "Deploying artifact to ${deployHost}..."

    // If SSH deployment is configured
    if (CFG.'deploy.ssh_enabled') {
        def sshUser = CFG.'deploy.ssh_user' ?: 'deploy'
        def deployPath = CFG.'deploy.path' ?: '/opt/firat-dev'

        sh """
            scp ${artifact} ${sshUser}@${deployHost}:${deployPath}/app.jar
        """
        echo "✓ Artifact deployed via SCP"
    } else {
        // Local or custom deployment
        echo "Using custom deployment method..."
        if (CFG.'deploy.custom_command') {
            sh CFG.'deploy.custom_command'
        }
    }

    // Start application (if configured)
    if (CFG.'deploy.start_command') {
        echo "Starting application..."
        sh CFG.'deploy.start_command'
        sleep 10
    }

    echo "✓ Deployment completed successfully"

    // Store deployment info
    env.DEPLOY_HOST = deployHost
    env.DEPLOY_PORT = deployPort
    env.DEPLOY_URL = "http://${deployHost}:${deployPort}"

    echo "Deployment Info:"
    echo "  - URL: ${env.DEPLOY_URL}"
    echo "  - Time: ${new Date().format('yyyy-MM-dd HH:mm:ss')}"

} catch (Exception e) {
    echo "✗ Deployment failed: ${e.message}"
    throw e
}

echo "========================================="
echo "Application deployed to DEV: ${env.DEPLOY_URL}"
echo "========================================="
