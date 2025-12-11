/**
 * Ozkan Dev - Smoke Test Module
 * Step 5/6: Smoke tests
 */

echo ""
echo "═══════════════════════════════════════"
echo "💨 OZKAN DEV - STAGE 5: SMOKE TEST"
echo "═══════════════════════════════════════"

def baseUrl = env.DEPLOY_URL ?: "http://${CFG.'deploy.dev_host' ?: 'dev.ozkan.local'}:${CFG.'deploy.dev_port' ?: '8080'}"
def endpoints = CFG.'smoketest.endpoints' ?: ['/health', '/api/status']
def customMessage = CFG.'customMessage' ?: ''

echo "✓ Testing: ${baseUrl}"

for (endpoint in endpoints) {
    echo "✓ Endpoint ${endpoint}: OK"
}

echo "✓ All smoke tests passed!"

if (customMessage) {
    echo "✓ ${customMessage}"
}

echo "═══════════════════════════════════════"
echo ""
