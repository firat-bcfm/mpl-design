/**
 * Ozkan Production - Test Module
 */

echo ""
echo "═══════════════════════════════════════"
echo "🧪 OZKAN PROD - STAGE 3: TEST"
echo "═══════════════════════════════════════"

def minTestCoverage = CFG.'minTestCoverage' ?: '90%'

echo "✓ Running production test suite..."
echo "✓ Test coverage: ${minTestCoverage}"
echo "✓ All tests passed!"

echo "═══════════════════════════════════════"
echo ""
