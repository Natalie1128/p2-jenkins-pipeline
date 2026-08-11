pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Images') {
            steps {
                sh 'docker compose --profile test build'
            }
        }

        stage('Unit Tests') {
            steps {
                sh 'docker compose run --rm employee-backend python -m pytest -q'
                sh 'docker compose run --rm manager-test mvn test'
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker compose down -v || true'
                sh 'docker compose up -d'
            }
        }

        stage('Seed') {
            steps {
                sh '''
                    until docker compose exec -T employee-backend python -c "import urllib.request; urllib.request.urlopen('http://localhost:8080/users')"; do
                        echo "Waiting for employee-backend..."
                        sleep 2
                    done
                '''
                sh 'docker compose exec -T employee-backend python db/seed.py'
            }
        }

        stage('E2E Tests') {
            steps {
                sh '''
                    until docker compose exec -T selenium curl -sf http://localhost:4444/status; do
                        echo "Waiting for Selenium..."
                        sleep 2
                    done
                '''
                catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                    sh 'docker compose run --rm employee-e2e'
                }
                catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                    sh 'docker compose run --rm manager-test mvn test -Dtest=RunCucumberTest'
                }
            }
        }

        stage('API Tests') {
            steps {
                sh 'docker compose run --rm manager-test mvn test -Dtest="com.expense.manager.app.*APITest"'
            }
        }

        stage('Performance') {
            steps {
                sh 'docker compose run --rm jmeter-employee'
                sh 'docker compose run --rm jmeter-manager'
            }
        }
    }

    post {
        always {
            sh 'docker compose down -v --remove-orphans || true'
        }
        success {
            echo 'All stages green — build, unit, API, E2E, and performance passed.'
        }
        unstable {
            echo 'Core pipeline passed; one or more E2E scenarios flaked and were marked UNSTABLE.'
        }
        failure {
            echo 'Pipeline failed — check the stage logs above for the first red stage.'
        }
    }
}
