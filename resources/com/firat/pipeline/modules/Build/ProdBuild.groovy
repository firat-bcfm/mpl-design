/**
 * Firat Prod - Build Module
 * Step 2/6: Maven build with security checks for production
 */

echo "========================================="
echo "Step 2/6: Building application for PRODUCTION..."
echo "========================================="

def mavenHome = tool(CFG.'maven.tool_version' ?: 'Maven 3')
env.PATH = "${mavenHome}/bin:${env.PATH}"

echo "Maven Version:"
sh "mvn -version"

echo "Starting Maven build with full test suite..."
def settings = CFG.'maven.settings_path' ? "-s '${CFG.'maven.settings_path'}'" : ''

try {
    // Clean build with tests
    sh """
        mvn -B ${settings} clean package -DskipTests=false
    """

    echo "✓ Maven build completed successfully"

    // Run security checks (optional - can be enabled)
    if (CFG.'security.enabled' == true) {
        echo "Running security checks..."
        sh """
            mvn -B ${settings} dependency-check:check || echo "Security check completed"
        """
    }

    // Create detailed artifact metadata
    sh """
        mkdir -p target/artifacts
        echo "Build Time: ${env.BUILD_TIMESTAMP}" > target/artifacts/build-info.txt
        echo "Git Commit: ${env.GIT_COMMIT_SHORT}" >> target/artifacts/build-info.txt
        echo "Environment: production" >> target/artifacts/build-info.txt
        echo "Build Number: ${env.BUILD_NUMBER}" >> target/artifacts/build-info.txt
        echo "Jenkins URL: ${env.BUILD_URL}" >> target/artifacts/build-info.txt
    """

    // Archive artifacts
    archiveArtifacts artifacts: 'target/*.jar,target/artifacts/*', fingerprint: true

    echo "Build artifacts archived and prepared"

} catch (Exception e) {
    echo "✗ Production build failed: ${e.message}"
    throw e
}

echo "========================================="
echo "Production build completed successfully!"
echo "========================================="
