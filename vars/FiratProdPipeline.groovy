/**
 * Firat Production Pipeline
 * 6-stage pipeline with manual approval for production environment deployment
 *
 * Usage:
 * @Library('mpl') _
 * FiratProdPipeline {
 *   maven.tool_version = 'Maven 3'
 *   deploy.prod_host = 'prod.firat.com'
 *   deploy.ssh_enabled = true
 *   deploy.auto_rollback = true
 * }
 */
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    // Set default CFG values for production
    def CFG = [
        'maven.tool_version': config.'maven.tool_version' ?: 'Maven 3',
        'maven.settings_path': config.'maven.settings_path' ?: '',
        'deploy.prod_host': config.'deploy.prod_host' ?: 'prod.firat.com',
        'deploy.prod_port': config.'deploy.prod_port' ?: '8080',
        'deploy.ssh_enabled': config.'deploy.ssh_enabled' ?: false,
        'deploy.ssh_user': config.'deploy.ssh_user' ?: 'deploy',
        'deploy.path': config.'deploy.path' ?: '/opt/firat-prod',
        'deploy.backup_path': config.'deploy.backup_path' ?: '/opt/backups/firat-prod',
        'deploy.stop_command': config.'deploy.stop_command' ?: '',
        'deploy.start_command': config.'deploy.start_command' ?: '',
        'deploy.custom_command': config.'deploy.custom_command' ?: '',
        'deploy.auto_rollback': config.'deploy.auto_rollback' != false,
        'smoketest.endpoints': config.'smoketest.endpoints' ?: ['/health', '/api/status', '/api/info'],
        'smoketest.max_retries': config.'smoketest.max_retries' ?: 20,
        'smoketest.retry_delay': config.'smoketest.retry_delay' ?: 10,
        'smoketest.business_checks_enabled': config.'smoketest.business_checks_enabled' ?: false,
        'validation.check_database': config.'validation.check_database' ?: false,
        'validation.check_dependencies': config.'validation.check_dependencies' ?: false,
        'validation.check_logs': config.'validation.check_logs' ?: false,
        'validation.check_metrics': config.'validation.check_metrics' ?: false,
        'validation.auto_rollback': config.'validation.auto_rollback' != false,
        'security.enabled': config.'security.enabled' ?: false,
        'test.integration_enabled': config.'test.integration_enabled' ?: false,
        'test.coverage_threshold': config.'test.coverage_threshold' ?: 0
    ]

    pipeline {
        agent any

        parameters {
            booleanParam(
                name: 'SKIP_APPROVAL',
                defaultValue: false,
                description: 'Skip manual approval for deployment (use with caution)'
            )
        }

        environment {
            PROJECT_NAME = 'firat-prod'
            ENVIRONMENT = 'production'
        }

        options {
            buildDiscarder(logRotator(numToKeepStr: '30'))
            timestamps()
        }

        stages {
            stage('Checkout') {
                steps {
                    script {
                        echo "═══════════════════════════════════════"
                        echo "FIRAT PRODUCTION PIPELINE - Starting"
                        echo "═══════════════════════════════════════"

                        def modulePath = 'resources/com/firat/pipeline/modules/Checkout/ProdCheckout.groovy'
                        def moduleCode = libraryResource(modulePath)
                        evaluate(moduleCode)
                    }
                }
            }

            stage('Build') {
                steps {
                    script {
                        def modulePath = 'resources/com/firat/pipeline/modules/Build/ProdBuild.groovy'
                        def moduleCode = libraryResource(modulePath)
                        evaluate(moduleCode)
                    }
                }
            }

            stage('Test') {
                steps {
                    script {
                        def modulePath = 'resources/com/firat/pipeline/modules/Test/ProdTest.groovy'
                        def moduleCode = libraryResource(modulePath)
                        evaluate(moduleCode)
                    }
                }
            }

            stage('Deploy') {
                steps {
                    script {
                        def modulePath = 'resources/com/firat/pipeline/modules/Deploy/ProdDeploy.groovy'
                        def moduleCode = libraryResource(modulePath)
                        evaluate(moduleCode)
                    }
                }
            }

            stage('Smoke Test') {
                steps {
                    script {
                        def modulePath = 'resources/com/firat/pipeline/modules/SmokeTest/ProdSmokeTest.groovy'
                        def moduleCode = libraryResource(modulePath)
                        evaluate(moduleCode)
                    }
                }
            }

            stage('Post-Deploy Validation') {
                steps {
                    script {
                        def modulePath = 'resources/com/firat/pipeline/modules/PostDeployValidation/ProdValidation.groovy'
                        def moduleCode = libraryResource(modulePath)
                        evaluate(moduleCode)
                    }
                }
            }
        }

        post {
            success {
                echo ""
                echo "═══════════════════════════════════════"
                echo "✓ FIRAT PRODUCTION PIPELINE - SUCCESS"
                echo "═══════════════════════════════════════"
                echo "Build: #${env.BUILD_NUMBER}"
                echo "Duration: ${currentBuild.durationString.replace(' and counting', '')}"
                echo "Commit: ${env.GIT_COMMIT_SHORT}"
                if (env.DEPLOY_URL) {
                    echo "Deployment: ${env.DEPLOY_URL}"
                }
                if (env.BACKUP_PATH) {
                    echo "Backup: ${env.BACKUP_PATH}"
                }
                echo "═══════════════════════════════════════"

                // Send success notifications
                // Add Slack/email notification here if needed
            }
            failure {
                echo ""
                echo "═══════════════════════════════════════"
                echo "✗ FIRAT PRODUCTION PIPELINE - FAILED"
                echo "═══════════════════════════════════════"
                echo "Build: #${env.BUILD_NUMBER}"
                echo "⚠ PRODUCTION DEPLOYMENT FAILED"
                echo "Check logs and verify system status"
                if (env.BACKUP_PATH) {
                    echo "Backup available: ${env.BACKUP_PATH}"
                }
                echo "═══════════════════════════════════════"

                // Send failure notifications
                // Add Slack/email notification here if needed
            }
            always {
                cleanWs()
            }
        }
    }
}
