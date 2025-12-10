/**
 * Firat Prod - Deploy Module
 * Step 4/6: Deploy to production with approval and backup
 */

echo "========================================="
echo "Step 4/6: Deploying to PRODUCTION environment..."
echo "========================================="

def deployHost = CFG.'deploy.prod_host' ?: 'prod.firat.com'
def deployPort = CFG.'deploy.prod_port' ?: '8080'
def artifactPath = 'target/*.jar'
def backupDir = CFG.'deploy.backup_path' ?: '/opt/backups/firat-prod'

echo "Production Deployment Configuration:"
echo "  - Target: ${deployHost}:${deployPort}"
echo "  - Environment: production"
echo "  - Artifact: ${artifactPath}"
echo "  - Backup Location: ${backupDir}"

// Manual approval for production deployment
if (!params.SKIP_APPROVAL) {
    echo "⚠ Production deployment requires approval"
    input message: 'Deploy to Production?', ok: 'Deploy', submitter: 'admin,ops-team'
}

try {
    // Find the artifact
    def artifact = sh(returnStdout: true, script: "ls ${artifactPath} | head -1").trim()
    echo "Deploying artifact: ${artifact}"

    def timestamp = new Date().format('yyyyMMdd_HHmmss')

    // Create backup of current deployment
    echo "Creating backup of current deployment..."
    if (CFG.'deploy.ssh_enabled') {
        def sshUser = CFG.'deploy.ssh_user' ?: 'deploy'
        def deployPath = CFG.'deploy.path' ?: '/opt/firat-prod'

        sh """
            ssh ${sshUser}@${deployHost} "mkdir -p ${backupDir}"
            ssh ${sshUser}@${deployHost} "cp ${deployPath}/app.jar ${backupDir}/app_${timestamp}.jar" || echo "No existing artifact to backup"
        """
        echo "✓ Backup created: ${backupDir}/app_${timestamp}.jar"
        env.BACKUP_PATH = "${backupDir}/app_${timestamp}.jar"
    }

    // Stop existing application gracefully
    if (CFG.'deploy.stop_command') {
        echo "Gracefully stopping existing application..."
        sh CFG.'deploy.stop_command'
        sleep 10
    }

    // Deploy artifact
    echo "Deploying artifact to production..."
    if (CFG.'deploy.ssh_enabled') {
        def sshUser = CFG.'deploy.ssh_user' ?: 'deploy'
        def deployPath = CFG.'deploy.path' ?: '/opt/firat-prod'

        sh """
            scp ${artifact} ${sshUser}@${deployHost}:${deployPath}/app.jar
        """
        echo "✓ Artifact deployed via SCP"
    } else {
        echo "Using custom deployment method..."
        if (CFG.'deploy.custom_command') {
            sh CFG.'deploy.custom_command'
        }
    }

    // Start application
    if (CFG.'deploy.start_command') {
        echo "Starting application..."
        sh CFG.'deploy.start_command'
        echo "Waiting for application startup..."
        sleep 15
    }

    echo "✓ Production deployment completed successfully"

    // Store deployment info
    env.DEPLOY_HOST = deployHost
    env.DEPLOY_PORT = deployPort
    env.DEPLOY_URL = "https://${deployHost}"
    env.DEPLOY_TIMESTAMP = timestamp

    echo "Production Deployment Info:"
    echo "  - URL: ${env.DEPLOY_URL}"
    echo "  - Time: ${new Date().format('yyyy-MM-dd HH:mm:ss')}"
    echo "  - Backup: ${env.BACKUP_PATH}"

} catch (Exception e) {
    echo "✗ Production deployment failed: ${e.message}"

    // Attempt rollback if configured
    if (CFG.'deploy.auto_rollback' && env.BACKUP_PATH) {
        echo "========================================="
        echo "INITIATING AUTOMATIC ROLLBACK"
        echo "========================================="

        try {
            if (CFG.'deploy.ssh_enabled') {
                def sshUser = CFG.'deploy.ssh_user' ?: 'deploy'
                def deployPath = CFG.'deploy.path' ?: '/opt/firat-prod'

                sh """
                    ssh ${sshUser}@${deployHost} "cp ${env.BACKUP_PATH} ${deployPath}/app.jar"
                """

                if (CFG.'deploy.start_command') {
                    sh CFG.'deploy.start_command'
                }

                echo "✓ Rollback completed successfully"
            }
        } catch (Exception rollbackError) {
            echo "✗ Rollback failed: ${rollbackError.message}"
            echo "Manual intervention required!"
        }
    }

    throw e
}

echo "========================================="
echo "Application deployed to PRODUCTION: ${env.DEPLOY_URL}"
echo "Backup available at: ${env.BACKUP_PATH}"
echo "========================================="
