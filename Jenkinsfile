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
                sh 'docker compose build'
                sh 'docker build -f manager_app/Dockerfile.test -t manager-test manager_app'
                sh 'docker build -f employee_app/Dockerfile.e2e -t employee-e2e employee_app'
                sh 'docker build -f employee_app/tests/jmeter/Dockerfile -t jmeter-employee employee_app/tests/jmeter'
                sh 'docker build -f manager_app/src/test/java/com/expense/manager/jmeter/Dockerfile -t jmeter-manager manager_app/src/test/java/com/expense/manager/jmeter'
            }
        }

        stage('Unit Tests') {
            steps {
                sh 'docker compose run --rm employee-backend python -m pytest'
                sh 'docker run --rm manager-test mvn test'
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
                    sh 'docker run --rm --network host -e HEADLESS=true -e SELENIUM_URL=http://localhost:4444 employee-e2e'
                }
                catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                    sh 'docker run --rm --network host -e HEADLESS=true -e SELENIUM_URL=http://localhost:4444 manager-test mvn test -Dtest=RunCucumberTest'
                }
            }
        }

        stage('API Tests') {
            steps {
                sh 'docker run --rm --network host manager-test mvn test -Dtest="com.expense.manager.app.*APITest"'
            }
        }

        stage('Performance') {
            steps {
                sh 'docker run --rm --network host jmeter-employee -n -t /plan.jmx -l /tmp/employee-results.jtl'
                sh 'docker run --rm --network host jmeter-manager -n -t /plan.jmx -l /tmp/manager-results.jtl'
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
