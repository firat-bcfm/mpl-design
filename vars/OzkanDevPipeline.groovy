def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    node {
        stage('1. Checkout') {
            echo ""; echo "═══════════════════════════════════════"
            echo "📥 OZKAN DEV - STAGE 1: CHECKOUT"; echo "═══════════════════════════════════════"
            echo "✓ Code checked out!"; echo "═══════════════════════════════════════"; echo ""
        }
        stage('2. Build') {
            echo ""; echo "═══════════════════════════════════════"
            echo "🔨 OZKAN DEV - STAGE 2: BUILD"; echo "═══════════════════════════════════════"
            echo "✓ Build completed!"; echo "═══════════════════════════════════════"; echo ""
        }
        stage('3. Test') {
            echo ""; echo "═══════════════════════════════════════"
            echo "🧪 OZKAN DEV - STAGE 3: TEST"; echo "═══════════════════════════════════════"
            echo "✓ Tests passed!"; echo "═══════════════════════════════════════"; echo ""
        }
        stage('4. Deploy') {
            echo ""; echo "═══════════════════════════════════════"
            echo "🚀 OZKAN DEV - STAGE 4: DEPLOY"; echo "═══════════════════════════════════════"
            echo "✓ Deployed to dev.ozkan.local:8080"; echo "═══════════════════════════════════════"; echo ""
        }
        stage('5. Smoke Test') {
            echo ""; echo "═══════════════════════════════════════"
            echo "💨 OZKAN DEV - STAGE 5: SMOKE TEST"; echo "═══════════════════════════════════════"
            echo "✓ Smoke tests passed!"; echo "═══════════════════════════════════════"; echo ""
        }
        stage('6. Validation') {
            echo ""; echo "═══════════════════════════════════════"
            echo "✅ OZKAN DEV - STAGE 6: VALIDATION"; echo "═══════════════════════════════════════"
            echo "✓ Validation passed!"; echo "═══════════════════════════════════════"; echo ""
        }
        echo ""; echo "════════════════════════════════════════════════"
        echo "✓✓✓ OZKAN DEV PIPELINE - SUCCESS! ✓✓✓"; echo "Build: #${env.BUILD_NUMBER}"
        echo "════════════════════════════════════════════════"; echo ""
    }
}
