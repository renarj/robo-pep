node {
    git url: 'git@github.com:renarj/robo-pep.git'
    def mvnHome = tool 'M3'

    stage 'checkout'
    checkout scm

    stage 'build'
    sh "${mvnHome}/bin/mvn -B clean install"

    stage 'archive'
    step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
}
