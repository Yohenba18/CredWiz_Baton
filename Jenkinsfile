pipeline {
  agent {label 'ec2'}
  
  stages {
    stage('Build') {
      steps {
        echo 'Demo Build Message'
        sh "mvn clean install"
      }
    }

    stage('Linux Tests') {
      parallel {
        stage('Linux Tests') {
          steps {
            echo 'Linux Test'

          }
        }

        stage('Windows Tests') {
          steps {
            echo 'This is windows test'
            sh 'echo "windows test shell"'
          }
        }

      }
    }

    stage('Deploy Staging') {
      steps {
        echo 'Deploy to staging environment'

      }
    }

    stage('Deploy Production') {
      steps {
        echo 'Deploy to Prod'
      }
    }

    stage('Docker Build') {
      steps {
        sh "docker-compose up"
      }
    }
  }
}
