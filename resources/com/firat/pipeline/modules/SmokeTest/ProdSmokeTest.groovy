/**
 * Firat Prod - Smoke Test Module
 * Step 5/6: Comprehensive smoke tests for production
 */

echo "========================================="
echo "Step 5/6: Running smoke tests for PRODUCTION..."
echo "========================================="

def baseUrl = env.DEPLOY_URL ?: "https://${CFG.'deploy.prod_host' ?: 'prod.firat.com'}"
def maxRetries = CFG.'smoketest.max_retries' ?: 20
def retryDelay = CFG.'smoketest.retry_delay' ?: 10

def endpoints = CFG.'smoketest.endpoints' ?: ['/health', '/api/status', '/api/info']

echo "Production Smoke Test Configuration:"
echo "  - Base URL: ${baseUrl}"
echo "  - Max Retries: ${maxRetries}"
echo "  - Retry Delay: ${retryDelay}s"
echo "  - Endpoints: ${endpoints.size()}"

def testEndpoint(String endpoint, String expectedStatus = '200') {
    echo "Testing endpoint: ${baseUrl}${endpoint} (expecting HTTP ${expectedStatus})"

    for (int i = 1; i <= maxRetries; i++) {
        try {
            def response = sh(
                returnStdout: true,
                script: "curl -f -s -o /dev/null -w '%{http_code}' ${baseUrl}${endpoint} || echo '000'"
            ).trim()

            if (response == expectedStatus) {
                echo "✓ ${endpoint} is responding (${response} OK)"

                // Measure response time
                def responseTime = sh(
                    returnStdout: true,
                    script: "curl -s -o /dev/null -w '%{time_total}' ${baseUrl}${endpoint}"
                ).trim()

                echo "  Response time: ${responseTime}s"

                return true
            } else {
                echo "Attempt ${i}/${maxRetries}: Got HTTP ${response}, retrying in ${retryDelay}s..."
                sleep retryDelay
            }
        } catch (Exception e) {
            echo "Attempt ${i}/${maxRetries} failed: ${e.message}"
            if (i < maxRetries) {
                sleep retryDelay
            }
        }
    }

    echo "✗ ${endpoint} failed after ${maxRetries} attempts"
    return false
}

try {
    def allPassed = true
    def failedEndpoints = []

    echo "Testing critical production endpoints..."

    // Test each endpoint
    for (endpoint in endpoints) {
        if (!testEndpoint(endpoint)) {
            allPassed = false
            failedEndpoints.add(endpoint)
        }
    }

    if (!allPassed) {
        echo "========================================="
        echo "SMOKE TESTS FAILED"
        echo "Failed endpoints: ${failedEndpoints.join(', ')}"
        echo "========================================="
        error("Critical smoke tests failed for production")
    }

    echo "✓ All smoke tests passed"

    // Additional business-critical checks (if configured)
    if (CFG.'smoketest.business_checks_enabled') {
        echo "Running business-critical endpoint checks..."
        // Add custom business logic checks here
    }

} catch (Exception e) {
    echo "✗ Production smoke tests failed: ${e.message}"
    throw e
}

echo "========================================="
echo "All smoke tests passed for PRODUCTION!"
echo "========================================="
