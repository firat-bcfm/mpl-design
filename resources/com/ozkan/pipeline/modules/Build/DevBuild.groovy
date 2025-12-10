/**
 * Ozkan Dev - Build Module
 * Step 2/6: Maven build for development
 */

echo "========================================="
echo "Step 2/6: Building application for OZKAN DEV..."
echo "========================================="

def mavenHome = tool(CFG.'maven.tool_version' ?: 'Maven 3')
env.PATH = "${mavenHome}/bin:${env.PATH}"

echo "Maven Version:"
sh "mvn -version"

echo "Starting Maven build..."
def settings = CFG.'maven.settings_path' ? "-s '${CFG.'maven.settings_path'}'" : ''

try {
    sh """
        mvn -B ${settings} clean package -DskipTests=false
    """

    echo "✓ Maven build completed successfully"

    // Create artifact metadata
    sh """
        mkdir -p target/artifacts
        echo "Build Time: ${env.BUILD_TIMESTAMP}" > target/artifacts/build-info.txt
        echo "Git Commit: ${env.GIT_COMMIT_SHORT}" >> target/artifacts/build-info.txt
        echo "Project: ozkan-dev" >> target/artifacts/build-info.txt
        echo "Environment: development" >> target/artifacts/build-info.txt
    """

    echo "Build artifacts prepared in target/"

} catch (Exception e) {
    echo "✗ Build failed: ${e.message}"
    throw e
}

echo "========================================="
echo "Build completed successfully for OZKAN DEV!"
echo "========================================="
