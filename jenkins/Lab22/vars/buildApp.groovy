// BuildApp shared library: unit test and build of the Node.js application.
// The agent has no Node.js, so each command runs in a temporary node container.
// The workspace lives in the Docker volume "agent_work" (mounted at /home/jenkins/agent on the agent).

def run(String cmd) {
    sh """
        docker run --rm -u 1000:1000 \\
            -v agent_work:/home/jenkins/agent \\
            -w "\$WORKSPACE" \\
            -e npm_config_cache=/tmp/.npm \\
            node:18-alpine \\
            sh -c '${cmd}'
    """
}

def test() {
    // --if-present: the project has no test script yet, so the step passes instead of failing
    run('npm install && npm test --if-present')
}

def build() {
    run('npm install --omit=dev && node --check server.js')
}
