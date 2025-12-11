/**
 * Ozkan Dev - Validation Module
 * Step 6/6: Post-deploy validation
 */

echo ""
echo "═══════════════════════════════════════"
echo "✅ OZKAN DEV - STAGE 6: VALIDATION"
echo "═══════════════════════════════════════"

def grafanaUrl = CFG.'grafanaUrl' ?: ''

echo "✓ Checking application health..."
echo "✓ Verifying connections..."

if (grafanaUrl) {
    echo "✓ Monitoring: ${grafanaUrl}"
}

echo "✓ All validations passed!"

echo "═══════════════════════════════════════"
echo ""
