pipeline {
    
    agent any
    
	triggers {
        githubPush()
    }
    
    stages {
        stage ('Init') {
            steps {
                echo 'Initializing...'
            }
        }

	stage ('Create docker image'){
		steps {
			sh 'docker build -t tailorw/fin-admin:dev .'
				withDockerRegistry(credentialsId: 'Tailorw-DockerCredentials', url: "") {
                			sh 'docker push tailorw/fin-admin:dev'
                		}
		}
	}

	stage ('Publish to Kubernetes') {
            steps {
                withKubeConfig(caCertificate: '', clusterName: '', contextName: '', credentialsId: 'KubernetesCredential', namespace: 'finamer', restrictKubeConfigAccess: false, serverUrl: 'https://172.20.41.100:5443') {
                    sh 'kubectl apply -f finamer-admin.yaml'
                }
            }
        }
    }
}
