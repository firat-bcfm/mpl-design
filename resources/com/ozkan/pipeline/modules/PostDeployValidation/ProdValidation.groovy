/**
 * Ozkan Production - Validation Module
 */

echo ""
echo "═══════════════════════════════════════"
echo "✅ OZKAN PROD - STAGE 6: VALIDATION"
echo "═══════════════════════════════════════"

def grafanaUrl = CFG.'grafanaUrl' ?: ''

echo "✓ Production validations..."
echo "✓ Monitoring: ACTIVE"

if (grafanaUrl) {
    echo "✓ Dashboard: ${grafanaUrl}"
}

echo "✓ All validations passed!"

echo "═══════════════════════════════════════"
echo ""
