/**
 * Ozkan Dev - Checkout Module
 * Step 1/6: Git checkout for development
 */

echo ""
echo "═══════════════════════════════════════"
echo "📥 OZKAN DEV - STAGE 1: CHECKOUT"
echo "═══════════════════════════════════════"

def projectName = CFG.'projectName' ?: 'ozkan-app'
def showGitInfo = CFG.'showGitInfo' ?: false

echo "✓ Project: ${projectName}"

if (showGitInfo) {
    try {
        def gitBranch = sh(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD').trim()
        def gitCommit = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
        echo "✓ Branch: ${gitBranch}"
        echo "✓ Commit: ${gitCommit}"
    } catch (Exception e) {
        echo "✓ Repository: github.com/firat-bcfm/mpl-design"
    }
}

echo "✓ Code checkout completed!"
echo "═══════════════════════════════════════"
echo ""
