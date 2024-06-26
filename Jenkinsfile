pipeline {
    agent any
    environment {
        DOCKER_COMPOSE_VERSION = '1.25.0' // 사용할 Docker Compose의 버전
        GITLAB_TOKEN = credentials('wns1915_cherry') // Jenkins에 저장된 GitLab Token의 ID
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'release', credentialsId: 'wns1915_cherry', url: 'https://lab.ssafy.com/2_yewon/chelitalk.git'
            }
        }
		stage('Update Local Repository') {
            steps {
                script {
						withCredentials([usernamePassword(credentialsId: 'wns1915_cherry', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
							sh '''
							ENCODED_USERNAME=$(echo $GIT_USERNAME | sed 's/@/%40/g')
							cd /home/ubuntu/chelitalk
							git pull https://$ENCODED_USERNAME:$GIT_PASSWORD@lab.ssafy.com/2_yewon/chelitalk.git release
							'''
						}
                }
            }
        }
        stage('Build Docker Images') {
            steps {
                script {
                    sh 'docker-compose -f /home/ubuntu/chelitalk/Backend/docker-compose.yml build --no-cache app_cherry'
                    sh 'docker-compose -f /home/ubuntu/chelitalk/Backend/docker-compose.yml up -d app_cherry'
					sh 'docker-compose -f /home/ubuntu/oringe/devway/docker-compose.yml build nginx'
                    sh 'docker-compose -f /home/ubuntu/oringe/devway/docker-compose.yml build certbot'
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    sh 'docker-compose -f /home/ubuntu/oringe/devway/docker-compose.yml up -d nginx'
                    sh 'docker-compose -f /home/ubuntu/oringe/devway/docker-compose.yml up -d certbot'
                }
            }
        }
    }
}
