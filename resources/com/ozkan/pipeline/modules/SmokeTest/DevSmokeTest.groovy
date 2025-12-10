/**
 * Ozkan Dev - Smoke Test Module
 * Step 5/6: Basic smoke tests for development
 */

echo "========================================="
echo "Step 5/6: Running smoke tests for OZKAN DEV..."
echo "========================================="

def baseUrl = env.DEPLOY_URL ?: "http://${CFG.'deploy.dev_host' ?: 'dev.ozkan.local'}:${CFG.'deploy.dev_port' ?: '8080'}"
def maxRetries = CFG.'smoketest.max_retries' ?: 10
def retryDelay = CFG.'smoketest.retry_delay' ?: 5

def endpoints = CFG.'smoketest.endpoints' ?: ['/health', '/api/status', '/api/info']

echo "Smoke Test Configuration:"
echo "  - Base URL: ${baseUrl}"
echo "  - Max Retries: ${maxRetries}"
echo "  - Endpoints: ${endpoints.size()}"

def testEndpoint(String endpoint, String expectedStatus = '200') {
    echo "Testing endpoint: ${baseUrl}${endpoint}"

    for (int i = 1; i <= maxRetries; i++) {
        try {
            def response = sh(
                returnStdout: true,
                script: "curl -s -o /dev/null -w '%{http_code}' ${baseUrl}${endpoint} || echo '000'"
            ).trim()

            if (response == expectedStatus) {
                echo "✓ ${endpoint} is responding (${response} OK)"
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

    // Test each endpoint
    for (endpoint in endpoints) {
        if (!testEndpoint(endpoint)) {
            allPassed = false
        }
    }

    if (!allPassed) {
        error("One or more smoke tests failed")
    }

    echo "✓ All smoke tests passed"

} catch (Exception e) {
    echo "✗ Smoke tests failed: ${e.message}"
    throw e
}

echo "========================================="
echo "All smoke tests passed for OZKAN DEV!"
echo "========================================="
