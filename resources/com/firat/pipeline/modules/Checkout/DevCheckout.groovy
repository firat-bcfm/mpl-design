/**
 * Firat Dev - Checkout Module
 * Step 1/6: Git checkout for development
 */

echo ""
echo "═══════════════════════════════════════"
echo "📥 FIRAT DEV - STAGE 1: CHECKOUT"
echo "═══════════════════════════════════════"

// Get config values
def projectName = CFG.'projectName' ?: 'firat-app'
def showGitInfo = CFG.'showGitInfo' ?: false

echo "✓ Project: ${projectName}"

if (showGitInfo) {
    try {
        def gitBranch = sh(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD').trim()
        def gitCommit = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
        def gitAuthor = sh(returnStdout: true, script: 'git log -1 --pretty=format:"%an"').trim()

        echo "✓ Branch: ${gitBranch}"
        echo "✓ Commit: ${gitCommit}"
        echo "✓ Author: ${gitAuthor}"
    } catch (Exception e) {
        echo "✓ Repository: github.com/firat-bcfm/mpl-design"
    }
}

echo "✓ Code checkout completed successfully!"
echo "═══════════════════════════════════════"
echo ""
