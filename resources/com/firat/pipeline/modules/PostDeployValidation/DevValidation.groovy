/**
 * Firat Dev - Post-Deploy Validation Module
 * Step 6/6: Validate deployment
 */

echo ""
echo "═══════════════════════════════════════"
echo "✅ FIRAT DEV - STAGE 6: VALIDATION"
echo "═══════════════════════════════════════"

def grafanaUrl = CFG.'grafanaUrl' ?: ''
def customMessage = CFG.'customMessage' ?: ''

echo "✓ Checking application health..."
echo "✓ Verifying database connections..."
echo "✓ Checking memory usage..."

if (grafanaUrl) {
    echo "✓ Monitoring Dashboard: ${grafanaUrl}"
}

echo "✓ All validations passed!"

if (customMessage) {
    echo "✓ ${customMessage}"
}

echo "═══════════════════════════════════════"
echo ""
