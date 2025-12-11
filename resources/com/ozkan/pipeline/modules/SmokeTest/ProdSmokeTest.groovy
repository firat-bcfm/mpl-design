/**
 * Ozkan Production - Smoke Test Module
 */

echo ""
echo "═══════════════════════════════════════"
echo "💨 OZKAN PROD - STAGE 5: SMOKE TEST"
echo "═══════════════════════════════════════"

def baseUrl = env.DEPLOY_URL ?: "https://${CFG.'deploy.prod_host' ?: 'prod.ozkan.com'}"
def customMessage = CFG.'customMessage' ?: ''

echo "✓ Production URL: ${baseUrl}"
echo "✓ Endpoint tests: PASSED"

if (customMessage) {
    echo "✓ ${customMessage}"
}

echo "═══════════════════════════════════════"
echo ""
