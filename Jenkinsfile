node {
  stage 'Build and Test'
  env.PATH = "${tool 'Maven'}/bin:${env.PATH}"
  checkout scm
   sh 'mvn clean test'
}