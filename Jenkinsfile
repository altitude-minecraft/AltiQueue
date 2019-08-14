node {
    stage('Checkout') {
        checkout changelog: true
    }
    stage('Test') {
        try {
            sh './gradlew clean test --no-daemon' //run a gradle task
        } finally {
            junit '**/build/test-results/test/*.xml' //make the junit test results available in any case (success & failure)
        }
    }
    stage ('Build') {
        sh './gradlew build -x test'
    }
}