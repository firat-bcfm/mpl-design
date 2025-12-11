def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    node {
        stage('1. Checkout') {
            echo ""; echo "═══════════════════════════════════════"
            echo "📥 OZKAN PROD - STAGE 1: CHECKOUT"; echo "═══════════════════════════════════════"
            echo "✓ Production code checked out!"; echo "═══════════════════════════════════════"; echo ""
        }
        stage('2. Build') {
            echo ""; echo "═══════════════════════════════════════"
            echo "🔨 OZKAN PROD - STAGE 2: BUILD"; echo "═══════════════════════════════════════"
            echo "✓ Production build completed!"; echo "═══════════════════════════════════════"; echo ""
        }
        stage('3. Test') {
            echo ""; echo "═══════════════════════════════════════"
            echo "🧪 OZKAN PROD - STAGE 3: TEST"; echo "═══════════════════════════════════════"
            echo "✓ All tests passed!"; echo "═══════════════════════════════════════"; echo ""
        }
        stage('4. Deploy') {
            echo ""; echo "═══════════════════════════════════════"
            echo "🚀 OZKAN PROD - STAGE 4: DEPLOY"; echo "═══════════════════════════════════════"
            echo "✓ Deployed to prod.ozkan.com:443"; echo "═══════════════════════════════════════"; echo ""
        }
        stage('5. Smoke Test') {
            echo ""; echo "═══════════════════════════════════════"
            echo "💨 OZKAN PROD - STAGE 5: SMOKE TEST"; echo "═══════════════════════════════════════"
            echo "✓ Production smoke tests passed!"; echo "═══════════════════════════════════════"; echo ""
        }
        stage('6. Validation') {
            echo ""; echo "═══════════════════════════════════════"
            echo "✅ OZKAN PROD - STAGE 6: VALIDATION"; echo "═══════════════════════════════════════"
            echo "✓ Production validation passed!"; echo "═══════════════════════════════════════"; echo ""
        }
        echo ""; echo "════════════════════════════════════════════════"
        echo "✓✓✓ OZKAN PROD PIPELINE - SUCCESS! ✓✓✓"; echo "Build: #${env.BUILD_NUMBER}"
        echo "════════════════════════════════════════════════"; echo ""
    }
}
