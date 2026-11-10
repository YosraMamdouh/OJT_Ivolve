// DeployOnK8s shared library: edit the deployment file and deploy it to the cluster.

def updateImage(String image, String tag, String file = 'k8s/deployment.yaml') {
    // Only the application image line changes (the MySQL init container is not touched)
    sh """
        sed -i "s#image: ${image}:.*#image: ${image}:${tag}#" ${file}
        grep -n "image:" ${file}
    """
}

def deploy(String server, String namespace, String file = 'k8s/deployment.yaml',
           String credentialsId = 'k8s-jenkins-token') {
    // jenkins-sa (Lab 19) may only create and update Deployments, so "replace" is used.
    withCredentials([string(credentialsId: credentialsId, variable: 'K8S_TOKEN')]) {
        sh """
            kubectl --kubeconfig=/dev/null --server="${server}" \\
                --insecure-skip-tls-verify=true --token="\$K8S_TOKEN" \\
                -n "${namespace}" replace -f ${file}
        """
    }
}
