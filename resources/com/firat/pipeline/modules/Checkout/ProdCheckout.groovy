/**
 * Firat Production - Checkout Module
 */

echo ""
echo "═══════════════════════════════════════"
echo "📥 FIRAT PROD - STAGE 1: CHECKOUT"
echo "═══════════════════════════════════════"

def projectName = CFG.'projectName' ?: 'firat-app'
def showGitInfo = CFG.'showGitInfo' ?: false

echo "✓ Project: ${projectName}"
echo "✓ Environment: PRODUCTION"

if (showGitInfo) {
    try {
        def gitTag = sh(returnStdout: true, script: 'git describe --tags --abbrev=0 2>/dev/null || echo "no-tag"').trim()
        def gitCommit = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
        echo "✓ Version: ${gitTag}"
        echo "✓ Commit: ${gitCommit}"
    } catch (Exception e) {
        echo "✓ Repository: github.com/firat-bcfm/mpl-design"
    }
}

echo "✓ Production checkout completed!"
echo "═══════════════════════════════════════"
echo ""
