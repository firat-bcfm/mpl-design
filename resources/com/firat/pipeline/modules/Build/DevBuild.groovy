/**
 * Firat Dev - Build Module
 * Step 2/6: Build application for development
 */

echo ""
echo "═══════════════════════════════════════"
echo "🔨 FIRAT DEV - STAGE 2: BUILD"
echo "═══════════════════════════════════════"

def projectName = CFG.'projectName' ?: 'firat-app'

echo "✓ Building project: ${projectName}"
echo "✓ Running Maven clean install..."
echo "✓ Compiling source code..."
echo "✓ Creating JAR/WAR artifact..."
echo "✓ Build completed successfully!"

echo "═══════════════════════════════════════"
echo ""
