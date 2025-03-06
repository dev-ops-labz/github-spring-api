@Library('devops-library') _

pipeline {
    agent any
    tools {
                git 'Default'
                gradle 'Default'
        }
        parameters {
            string(name: 'environment', defaultValue: 'dev', description: 'Application Environment')
            booleanParam(name: 'deployToEks', defaultValue: false, description: 'EKS Environment')
        }
    environment {
            AWS_REGION = 'us-east-2'
            EKS_ENABLED = false
            EKS_CLUSTER_NAME = 'alphatech-cluster'
            DOCKER_USER_NAME = "dhananjay01"
            SONARQUBE_TOKEN = credentials('sonar-token')
            SONARQUBE_URL="http://localhost:9000"
            DOCKER_CREDENTIALS = 'dockerhub'
            KUBECONFIG = "${HOME}/.kube/config"
            MINIKUBE_HOME = "/tmp/minikube"
            DEPLOYMENT_FILE = "k8s/deployment.yaml"
            DOCKER_IMAGE_NAME = "github-app" // Docker image name
            DOCKER_TAG = "latest"  // Docker tag (can be dynamic, like commit hash or build number)
            KUBERNETES_DEPLOYMENT_NAME = "github-spring-app"  // Kubernetes deployment name
            KUBERNETES_NAMESPACE = "default"  // Kubernetes namespace
            K8S_CLUSTER_CONFIG = "/path/to/kube/config"  // Path to Kubernetes config file (if needed)
            REGISTRY_CREDENTIALS = 'docker-hub-credentials'  // Jenkins credentials for Docker registry
       }

    stages {
        stage("clone") {
            steps {
                echo 'Clone repository'
                git 'git@github.com:dev-ops-labz/github-spring-api.git'
            }
        }
          stage('Build Spring Boot Application') {
                                    steps {
                                          echo 'Building application..'
                                          script {
                                                 sh './gradlew clean'
                                                 sh './gradlew clean build'
                                          }
                                    }
          }

        stage('SonarQube Analysis') {
                    when {
                               expression {
                                                 //taking from params
                                                  params.environment != 'dev'
                                            }
                                         }
                    steps {
                        script {
                            // Perform SonarQube analysis
                            withSonarQubeEnv('SonarQube') { // 'SonarQube' is the name of your SonarQube server configuration in Jenkins
                                sh  '''
                                     ./gradlew sonar -Dsonar.projectKey=my-app -Dsonar.host.url=${SONARQUBE_URL} -Dsonar.token=${SONARQUBE_TOKEN} --stacktrace
                                '''
                            }
                        }
                    }
                }

        stage('Test Spring Boot Application') {
                  when {
                     expression {
                         params.environment != 'STG'
                         }
                  }
                         steps {
                                           echo "Running Tests in ${params.environment} for application.."
                                           script {
                                                  sh './gradlew test'
                                           }
                         }
                  }
                    stage('Test Spring Boot Application in Stage') {
                      when {
                                expression {
                                    params.environment == 'STG'
                                    }
                             }
                                  steps {
                                                    echo 'Running Tests in STG for application..'
                                                    script {
                                                           sh './gradlew test'
                                                    }
                                  }
                           }
        stage('Build Docker Image') {
              steps {
                        echo 'Building docker file..'
                        script {

                            sh """
                                docker buildx build -t ${DOCKER_USER_NAME}/${DOCKER_IMAGE_NAME}:${DOCKER_TAG} .
                            """
                        }
              }
        }

         stage('Push Docker Image to Registry') {
               steps {
                        echo 'Pushing docker file to docker hub..'
                        uploadArtifactToDockerHub('${DOCKER_IMAGE_NAME}','${DOCKER_TAG}',DOCKER_USER_NAME )
                       /* script {
                               withCredentials([usernamePassword(credentialsId: DOCKER_CREDENTIALS, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASSWORD')]) {
                                     sh '''
                                          echo $DOCKER_PASSWORD | docker login -u $DOCKER_USER --password-stdin
                                          docker push ${DOCKER_USER_NAME}/${DOCKER_IMAGE_NAME}:${DOCKER_TAG}
                                     '''
                                }
                        } */
               }
         }

         stage('Configure kubectl') {
               when {
                         expression {
                                  EKS_ENABLED == true
                    }
               }
               steps {
                   echo "Logging to EKS K8..${EKS_ENABLED}"


                    sh """
                             aws eks update-kubeconfig --name ${EKS_CLUSTER_NAME} --region ${AWS_REGION}
                    """

               }
         }

         stage('Deploy to EKS') {
               when {
                       expression {
                            //taking from params
                              params.deployToEks
                       }
                    }
               steps {

                      echo 'Deploying application to K8..'

                      script {
                               deployToEKS('k8s/deployment.yaml', EKS_CLUSTER_NAME, AWS_REGION)
                             }
                      }
             }

             stage('Deploy to Minikube in local') {
                            when {
                                    expression {
                                         //taking from params
                                          params.environment == 'dev'
                                    }
                                 }
                            steps {

                                   echo 'Deploying application to Minikube..'
                                   dockerPull('${DOCKER_IMAGE_NAME}','${DOCKER_TAG}',DOCKER_USER_NAME )
                                   script {

                                           sh "kubectl apply -f ${DEPLOYMENT_FILE}"
                                          }
                                   }
                          }

    }

    post {
            success {
                echo "Deployment successful!"
            }
            failure {
                echo "Deployment failed. Please check the logs!"
            }
    }

}