// BuildImage shared library: build, scan, push and delete the Docker image.

def build(String image, String tag) {
    sh "docker build -t ${image}:${tag} -t ${image}:latest ."
}

def scan(String image, String tag) {
    // Reports HIGH and CRITICAL vulnerabilities. exit-code 0 keeps the pipeline going.
    sh """
        docker run --rm \\
            -v /var/run/docker.sock:/var/run/docker.sock \\
            -v trivy-cache:/root/.cache/ \\
            aquasec/trivy:latest image \\
            --severity HIGH,CRITICAL --exit-code 0 --no-progress \\
            ${image}:${tag}
    """
}

def push(String image, String tag, String credentialsId = 'dockerhub-creds') {
    withCredentials([usernamePassword(credentialsId: credentialsId,
                                      usernameVariable: 'DH_USER',
                                      passwordVariable: 'DH_PASS')]) {
        sh """
            echo "\$DH_PASS" | docker login -u "\$DH_USER" --password-stdin
            docker push ${image}:${tag}
            docker push ${image}:latest
        """
    }
}

def delete(String image, String tag) {
    sh "docker rmi ${image}:${tag} ${image}:latest"
}

def cleanup(String image, String tag) {
    // Used in post actions: never fails
    sh """
        docker rmi ${image}:${tag} ${image}:latest || true
        docker logout || true
    """
}
