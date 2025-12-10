/**
 * Ozkan Dev - Test Module
 * Step 3/6: Unit tests for development
 */

echo "========================================="
echo "Step 3/6: Running unit tests for OZKAN DEV..."
echo "========================================="

def mavenHome = tool(CFG.'maven.tool_version' ?: 'Maven 3')
env.PATH = "${mavenHome}/bin:${env.PATH}"

def settings = CFG.'maven.settings_path' ? "-s '${CFG.'maven.settings_path'}'" : ''

try {
    echo "Executing unit tests..."
    sh """
        mvn -B ${settings} test
    """

    echo "✓ All unit tests passed"

    // Publish test results
    if (fileExists('target/surefire-reports')) {
        junit 'target/surefire-reports/*.xml'
        echo "Test reports published"
    }

    // Display test summary
    def testCount = sh(returnStdout: true, script: 'find target/surefire-reports -name "TEST-*.xml" | wc -l').trim()
    echo "Test Summary: ${testCount} test suite(s) executed"

} catch (Exception e) {
    echo "✗ Unit tests failed: ${e.message}"

    // Still publish test results even on failure
    if (fileExists('target/surefire-reports')) {
        junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
    }

    throw e
}

echo "========================================="
echo "All unit tests passed for OZKAN DEV!"
echo "========================================="
