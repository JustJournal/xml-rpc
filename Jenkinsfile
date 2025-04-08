pipeline {
    agent any
    tools {
        maven 'maven'
        jdk 'jdk17'
    }
    stages {
        stage('Build') {
            steps {
                sh './gradlew build -x test'
            }
        }

       stage('Test with Coverage') {
            steps {
                sh './gradlew test jacocoTestReport'
            }
        }
       stage('Coverage') {
            steps {
                recordCoverage(tools: [[parser: 'JACOCO']], id: 'jacoco', name: 'JaCoCo Coverage', sourceCodeRetention: 'EVERY_BUILD', enabledForFailure: true,
        qualityGates: [
                [threshold: 60.0, metric: 'LINE', baseline: 'PROJECT', unstable: true],
                [threshold: 60.0, metric: 'BRANCH', baseline: 'PROJECT', unstable: true]])
            }
        }
    }
}
