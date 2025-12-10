/**
 * Firat Prod - Checkout Module
 * Step 1/6: Source code checkout with validation
 */

echo "========================================="
echo "Step 1/6: Checking out source code for PRODUCTION..."
echo "========================================="

checkout scm

// Get git info
def gitCommit = sh(returnStdout: true, script: 'git log -1 --pretty=format:"%h - %an, %ar : %s"').trim()
echo "Git Commit: ${gitCommit}"

env.GIT_COMMIT_SHORT = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
env.BUILD_TIMESTAMP = sh(returnStdout: true, script: 'date -u +"%Y-%m-%dT%H:%M:%SZ"').trim()

// Verify this is a production-ready branch
def currentBranch = env.GIT_BRANCH ?: 'main'
echo "Current Branch: ${currentBranch}"

if (!currentBranch.contains('main') && !currentBranch.contains('release')) {
    echo "⚠ WARNING: Building from non-production branch: ${currentBranch}"
    input message: "Continue with non-production branch ${currentBranch}?", ok: 'Continue'
}

echo "Build Info:"
echo "  - Commit: ${env.GIT_COMMIT_SHORT}"
echo "  - Timestamp: ${env.BUILD_TIMESTAMP}"
echo "  - Branch: ${currentBranch}"

echo "✓ Checkout completed and validated"
echo "========================================="
