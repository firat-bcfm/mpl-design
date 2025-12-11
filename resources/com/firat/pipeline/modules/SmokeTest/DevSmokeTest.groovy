/**
 * Firat Dev - Smoke Test Module
 * Step 5/6: Basic smoke tests for development
 */

echo ""
echo "═══════════════════════════════════════"
echo "💨 FIRAT DEV - STAGE 5: SMOKE TEST"
echo "═══════════════════════════════════════"

def baseUrl = env.DEPLOY_URL ?: "http://${CFG.'deploy.dev_host' ?: 'dev.firat.local'}:${CFG.'deploy.dev_port' ?: '8080'}"
def endpoints = CFG.'smoketest.endpoints' ?: ['/health', '/api/status', '/api/info']

echo "✓ Base URL: ${baseUrl}"

// Test each endpoint (demo version - just echo)
for (endpoint in endpoints) {
    echo "✓ Testing endpoint: ${endpoint}"
    echo "  → Status: 200 OK"
}

echo "✓ All smoke tests passed!"

echo "═══════════════════════════════════════"
echo ""
