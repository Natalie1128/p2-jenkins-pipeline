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
                sh 'docker compose up -d'
            }
        }

        stage('Seed') {
            steps {
                sh 'sleep 20'
                sh 'docker compose exec -T employee-backend python db/seed.py'
            }
        }

        stage('E2E Tests') {
            steps {
                sh 'docker rm -f selenium || true'
                sh 'docker run -d --network host --name selenium selenium/standalone-chrome'
                sh 'sleep 10'
                catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                    sh 'docker run --rm --network host -e HEADLESS=true -e SELENIUM_URL=http://localhost:4444 employee-e2e'
                }
                catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                    sh 'docker run --rm --network host -e HEADLESS=true -e DBUS_SESSION_BUS_ADDRESS=/dev/null manager-test mvn test -Dtest=RunCucumberTest'
                }
                sh 'docker rm -f selenium || true'
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
}
