/**
 * Ozkan Dev - Checkout Module
 * Step 1/6: Source code checkout
 */

echo "========================================="
echo "Step 1/6: Checking out source code for OZKAN DEV..."
echo "========================================="

checkout scm

// Get git info
def gitCommit = sh(returnStdout: true, script: 'git log -1 --pretty=format:"%h - %an, %ar : %s"').trim()
echo "Git Commit: ${gitCommit}"

env.GIT_COMMIT_SHORT = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
env.BUILD_TIMESTAMP = sh(returnStdout: true, script: 'date -u +"%Y-%m-%dT%H:%M:%SZ"').trim()

echo "Build Info:"
echo "  - Commit: ${env.GIT_COMMIT_SHORT}"
echo "  - Timestamp: ${env.BUILD_TIMESTAMP}"
echo "  - Branch: ${env.GIT_BRANCH ?: 'main'}"

echo "✓ Checkout completed successfully"
echo "========================================="
