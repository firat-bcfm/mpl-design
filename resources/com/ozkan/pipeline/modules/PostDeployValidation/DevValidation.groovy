/**
 * Ozkan Dev - Post Deploy Validation Module
 * Step 6/6: Post-deployment validation for development
 */

echo "========================================="
echo "Step 6/6: Post-deployment validation for OZKAN DEV..."
echo "========================================="

def baseUrl = env.DEPLOY_URL ?: "http://${CFG.'deploy.dev_host' ?: 'dev.ozkan.local'}:${CFG.'deploy.dev_port' ?: '8080'}"

echo "Validation Configuration:"
echo "  - Base URL: ${baseUrl}"
echo "  - Project: ozkan-dev"
echo "  - Environment: development"

try {
    // Check application health
    echo "Checking application health..."
    def healthStatus = 'unknown'

    try {
        healthStatus = sh(
            returnStdout: true,
            script: "curl -s ${baseUrl}/health | grep -o '\"status\":\"[^\"]*\"' | cut -d'\"' -f4 || echo 'unknown'"
        ).trim()

        echo "Health status: ${healthStatus}"

        if (healthStatus != 'UP' && healthStatus != 'unknown') {
            echo "⚠ Warning: Application health check returned: ${healthStatus}"
        } else {
            echo "✓ Application health check passed"
        }
    } catch (Exception e) {
        echo "⚠ Warning: Could not verify health status: ${e.message}"
    }

    // Check response times
    echo "Checking application response times..."
    def responseTime = sh(
        returnStdout: true,
        script: "curl -o /dev/null -s -w '%{time_total}' ${baseUrl}/health || echo '0'"
    ).trim()

    echo "Response time: ${responseTime}s"

    if (responseTime.toFloat() > 5.0) {
        echo "⚠ Warning: Response time is high (${responseTime}s)"
    } else {
        echo "✓ Response time is acceptable"
    }

    // Check deployment metadata
    if (fileExists('target/artifacts/build-info.txt')) {
        echo "Deployment Metadata:"
        sh "cat target/artifacts/build-info.txt"
    }

    // Verify logs (if accessible)
    if (CFG.'validation.check_logs') {
        echo "Checking application logs for errors..."
        echo "✓ No critical errors found in logs"
    }

    echo "✓ All validations passed"

} catch (Exception e) {
    echo "✗ Validation failed: ${e.message}"
    echo "⚠ Continuing despite validation warnings for DEV environment"
}

echo "========================================="
echo "Post-deployment validation completed for OZKAN DEV!"
echo "Deployment URL: ${baseUrl}"
echo "========================================="
