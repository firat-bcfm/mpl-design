/**
 * Ozkan Production - Checkout Module
 */

echo ""
echo "═══════════════════════════════════════"
echo "📥 OZKAN PROD - STAGE 1: CHECKOUT"
echo "═══════════════════════════════════════"

def projectName = CFG.'projectName' ?: 'ozkan-app'
echo "✓ Project: ${projectName}"
echo "✓ Environment: PRODUCTION"
echo "✓ Production checkout completed!"

echo "═══════════════════════════════════════"
echo ""
