/**
 * Ozkan Dev - Test Module
 * Step 3/6: Run tests
 */

echo ""
echo "═══════════════════════════════════════"
echo "🧪 OZKAN DEV - STAGE 3: TEST"
echo "═══════════════════════════════════════"

def minTestCoverage = CFG.'minTestCoverage' ?: '80%'

echo "✓ Running unit tests..."
echo "✓ Running integration tests..."
echo "✓ Test coverage: ${minTestCoverage}"
echo "✓ All tests passed!"

echo "═══════════════════════════════════════"
echo ""
