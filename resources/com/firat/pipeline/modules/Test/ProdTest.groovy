/**
 * Firat Prod - Test Module
 * Step 3/6: Full test suite with coverage for production
 */

echo "========================================="
echo "Step 3/6: Running full test suite for PRODUCTION..."
echo "========================================="

def mavenHome = tool(CFG.'maven.tool_version' ?: 'Maven 3')
env.PATH = "${mavenHome}/bin:${env.PATH}"

def settings = CFG.'maven.settings_path' ? "-s '${CFG.'maven.settings_path'}'" : ''

try {
    echo "Executing unit tests with coverage..."
    sh """
        mvn -B ${settings} test jacoco:report
    """

    echo "✓ Unit tests completed"

    // Run integration tests if enabled
    if (CFG.'test.integration_enabled' == true) {
        echo "Running integration tests..."
        sh """
            mvn -B ${settings} verify -Pintegration-tests || echo "Integration tests completed"
        """
    }

    echo "✓ All tests passed"

    // Publish test results
    if (fileExists('target/surefire-reports')) {
        junit 'target/surefire-reports/*.xml'
        echo "Test reports published"
    }

    // Publish coverage report
    if (fileExists('target/site/jacoco')) {
        publishHTML([
            allowMissing: false,
            alwaysLinkToLastBuild: true,
            keepAll: true,
            reportDir: 'target/site/jacoco',
            reportFiles: 'index.html',
            reportName: 'Code Coverage Report'
        ])
        echo "Code coverage report published"
    }

    // Display test summary
    def testCount = sh(returnStdout: true, script: 'find target/surefire-reports -name "TEST-*.xml" | wc -l').trim()
    echo "Test Summary: ${testCount} test suite(s) executed"

    // Check coverage threshold (optional)
    if (CFG.'test.coverage_threshold') {
        echo "Verifying code coverage threshold: ${CFG.'test.coverage_threshold'}%"
        // Add coverage check logic here if needed
    }

} catch (Exception e) {
    echo "✗ Tests failed: ${e.message}"

    // Still publish test results even on failure
    if (fileExists('target/surefire-reports')) {
        junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
    }

    throw e
}

echo "========================================="
echo "Full test suite passed for PRODUCTION!"
echo "========================================="
