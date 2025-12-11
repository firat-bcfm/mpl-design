/**
 * Ozkan Development Pipeline - DEMO VERSION
 */
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    pipeline {
        agent { label 'built-in' }
        stages {
            stage('1. Checkout') {
                steps {
                    script {
                        echo ""
                        echo "═══════════════════════════════════════"
                        echo "📥 OZKAN DEV - STAGE 1: CHECKOUT"
                        echo "═══════════════════════════════════════"
                        echo "✓ Code checked out successfully!"
                        echo "═══════════════════════════════════════"
                        echo ""
                    }
                }
            }
            stage('2. Build') {
                steps {
                    script {
                        echo ""
                        echo "═══════════════════════════════════════"
                        echo "🔨 OZKAN DEV - STAGE 2: BUILD"
                        echo "═══════════════════════════════════════"
                        echo "✓ Build completed successfully!"
                        echo "═══════════════════════════════════════"
                        echo ""
                    }
                }
            }
            stage('3. Test') {
                steps {
                    script {
                        echo ""
                        echo "═══════════════════════════════════════"
                        echo "🧪 OZKAN DEV - STAGE 3: TEST"
                        echo "═══════════════════════════════════════"
                        echo "✓ All tests passed!"
                        echo "═══════════════════════════════════════"
                        echo ""
                    }
                }
            }
            stage('4. Deploy') {
                steps {
                    script {
                        echo ""
                        echo "═══════════════════════════════════════"
                        echo "🚀 OZKAN DEV - STAGE 4: DEPLOY"
                        echo "═══════════════════════════════════════"
                        echo "✓ Deployed to dev.ozkan.local:8080"
                        echo "═══════════════════════════════════════"
                        echo ""
                    }
                }
            }
            stage('5. Smoke Test') {
                steps {
                    script {
                        echo ""
                        echo "═══════════════════════════════════════"
                        echo "💨 OZKAN DEV - STAGE 5: SMOKE TEST"
                        echo "═══════════════════════════════════════"
                        echo "✓ Smoke tests passed!"
                        echo "═══════════════════════════════════════"
                        echo ""
                    }
                }
            }
            stage('6. Validation') {
                steps {
                    script {
                        echo ""
                        echo "═══════════════════════════════════════"
                        echo "✅ OZKAN DEV - STAGE 6: VALIDATION"
                        echo "═══════════════════════════════════════"
                        echo "✓ All validations passed!"
                        echo "═══════════════════════════════════════"
                        echo ""
                    }
                }
            }
        }
        post {
            success {
                script {
                    echo ""
                    echo "════════════════════════════════════════════════"
                    echo "✓✓✓ OZKAN DEV PIPELINE - SUCCESS! ✓✓✓"
                    echo "════════════════════════════════════════════════"
                    echo "Build: #${env.BUILD_NUMBER}"
                    echo "════════════════════════════════════════════════"
                    echo ""
                }
            }
        }
    }
}
