// DeployOnK8s shared library (Lab 23, GitOps version).
// Jenkins no longer deploys to the cluster itself: it edits the deployment file and pushes it
// to GitHub, and Argo CD applies the change to the cluster.

def updateImage(String image, String tag, String file = 'k8s/deployment.yaml') {
    // Only the application image line changes (the MySQL init container is not touched)
    sh """
        sed -i "s#image: ${image}:.*#image: ${image}:${tag}#" ${file}
        grep -n "image:" ${file}
    """
}

def pushToGit(String repoPath, String tag, String file = 'k8s/deployment.yaml',
              String branch = 'main', String credentialsId = 'github-creds') {
    // repoPath example: github.com/YosraMamdouh/Ivolve-app-cicd.git
    withCredentials([usernamePassword(credentialsId: credentialsId,
                                      usernameVariable: 'GIT_USER',
                                      passwordVariable: 'GIT_PASS')]) {
        sh """
            git config user.name "jenkins"
            git config user.email "jenkins@ivolve.local"
            git add ${file}
            git diff --cached --quiet || git commit -m "Update image to ${tag}"
            git push "https://\$GIT_USER:\$GIT_PASS@${repoPath}" HEAD:${branch}
        """
    }
}
