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
            post {
                always {
                    junit '**/build/test-results/test/*.xml'
                }
            }
       }

       stage('Coverage') {
            steps {
                recordCoverage(
                tools: [[parser: 'JACOCO', pattern: '**/build/reports/jacoco/test/jacocoTestReport.xml']],
                id: 'jacoco',
                name: 'JaCoCo Coverage',
                sourceCodeRetention: 'EVERY_BUILD',
                enabledForFailure: true,
                qualityGates: [
                    [threshold: 60.0, metric: 'LINE', baseline: 'PROJECT', unstable: true],
                    [threshold: 60.0, metric: 'BRANCH', baseline: 'PROJECT', unstable: true]
                ]
                )
            }
       }

       stage('Sonarqube') {
            steps {
                withSonarQubeEnv('sonarcloud') {
                    withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')]) {
                                sh '''
                                    ./gradlew sonar \
                                        -Dsonar.scanner.skipJreProvisioning=true \
                                        -Dsonar.organization=justjournal \
                                        -Dsonar.projectKey=JustJournal_xml-rpc \
                                        -Dsonar.coverage.jacoco.xmlReportPaths=build/reports/jacoco/test/jacocoTestReport.xml \
                                        -Dsonar.token=$SONAR_TOKEN
                                '''
                    }
                }
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
       }
    }
}
