/**
 * Common Configuration for all pipelines
 * FIRAT and OZKAN both use these settings
 */
def getCommonConfig() {
    return [
        // Notification
        'slack.channel': '#team-deployments',
        'slack.enabled': true,

        // Git Integration
        'git.showInfo': true,
        'git.showAuthor': true,

        // Docker
        'docker.registry': 'docker.io/company',
        'docker.enabled': true,

        // Monitoring
        'monitoring.grafana': 'https://grafana.company.com',
        'monitoring.prometheus': 'https://prometheus.company.com',

        // Testing
        'test.minCoverage': '80%',
        'test.framework': 'JUnit',

        // Quality Gates
        'sonar.enabled': true,
        'sonar.url': 'https://sonar.company.com',

        // Deployment
        'deploy.timeout': '10m',
        'deploy.retries': 3
    ]
}

return this
