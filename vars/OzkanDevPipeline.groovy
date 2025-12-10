/**
 * Ozkan Development Pipeline
 * 6-stage pipeline for development environment deployment
 *
 * Usage:
 * @Library('mpl') _
 * OzkanDevPipeline {
 *   maven.tool_version = 'Maven 3'
 *   deploy.dev_host = 'dev.ozkan.local'
 *   deploy.dev_port = '8080'
 * }
 */
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    // Set default CFG values
    def CFG = [
        'maven.tool_version': config.'maven.tool_version' ?: 'Maven 3',
        'maven.settings_path': config.'maven.settings_path' ?: '',
        'deploy.dev_host': config.'deploy.dev_host' ?: 'dev.ozkan.local',
        'deploy.dev_port': config.'deploy.dev_port' ?: '8080',
        'deploy.ssh_enabled': config.'deploy.ssh_enabled' ?: false,
        'deploy.ssh_user': config.'deploy.ssh_user' ?: 'deploy',
        'deploy.path': config.'deploy.path' ?: '/opt/ozkan-dev',
        'deploy.stop_command': config.'deploy.stop_command' ?: '',
        'deploy.start_command': config.'deploy.start_command' ?: '',
        'deploy.custom_command': config.'deploy.custom_command' ?: '',
        'smoketest.endpoints': config.'smoketest.endpoints' ?: ['/health', '/api/status', '/api/info'],
        'smoketest.max_retries': config.'smoketest.max_retries' ?: 10,
        'smoketest.retry_delay': config.'smoketest.retry_delay' ?: 5,
        'validation.check_logs': config.'validation.check_logs' ?: false,
        'test.integration_enabled': config.'test.integration_enabled' ?: false
    ]

    pipeline {
        agent any

        environment {
            PROJECT_NAME = 'ozkan-dev'
            ENVIRONMENT = 'development'
        }

        options {
            buildDiscarder(logRotator(numToKeepStr: '10'))
            timestamps()
        }

        stages {
            stage('Checkout') {
                steps {
                    script {
                        echo "═══════════════════════════════════════"
                        echo "OZKAN DEV PIPELINE - Starting"
                        echo "═══════════════════════════════════════"

                        def modulePath = 'resources/com/ozkan/pipeline/modules/Checkout/DevCheckout.groovy'
                        def moduleCode = libraryResource(modulePath)
                        evaluate(moduleCode)
                    }
                }
            }

            stage('Build') {
                steps {
                    script {
                        def modulePath = 'resources/com/ozkan/pipeline/modules/Build/DevBuild.groovy'
                        def moduleCode = libraryResource(modulePath)
                        evaluate(moduleCode)
                    }
                }
            }

            stage('Test') {
                steps {
                    script {
                        def modulePath = 'resources/com/ozkan/pipeline/modules/Test/DevTest.groovy'
                        def moduleCode = libraryResource(modulePath)
                        evaluate(moduleCode)
                    }
                }
            }

            stage('Deploy') {
                steps {
                    script {
                        def modulePath = 'resources/com/ozkan/pipeline/modules/Deploy/DevDeploy.groovy'
                        def moduleCode = libraryResource(modulePath)
                        evaluate(moduleCode)
                    }
                }
            }

            stage('Smoke Test') {
                steps {
                    script {
                        def modulePath = 'resources/com/ozkan/pipeline/modules/SmokeTest/DevSmokeTest.groovy'
                        def moduleCode = libraryResource(modulePath)
                        evaluate(moduleCode)
                    }
                }
            }

            stage('Post-Deploy Validation') {
                steps {
                    script {
                        def modulePath = 'resources/com/ozkan/pipeline/modules/PostDeployValidation/DevValidation.groovy'
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
                echo "✓ OZKAN DEV PIPELINE - SUCCESS"
                echo "═══════════════════════════════════════"
                echo "Build: #${env.BUILD_NUMBER}"
                echo "Duration: ${currentBuild.durationString.replace(' and counting', '')}"
                if (env.DEPLOY_URL) {
                    echo "Deployment: ${env.DEPLOY_URL}"
                }
                echo "═══════════════════════════════════════"
            }
            failure {
                echo ""
                echo "═══════════════════════════════════════"
                echo "✗ OZKAN DEV PIPELINE - FAILED"
                echo "═══════════════════════════════════════"
                echo "Build: #${env.BUILD_NUMBER}"
                echo "Check logs for details"
                echo "═══════════════════════════════════════"
            }
            always {
                cleanWs()
            }
        }
    }
}
