// vars/startNexusEC2.groovy

/**
 * Starts (and waits for) the Nexus EC2 instance defined in env.NEXUS_INSTANCE_ID
 *
 * @param Map params (all optional—fallbacks read from env)
 *   - credentialsId : AWS creds ID (defaults to env.AWS_CREDENTIALS)
 *   - instanceIdEnv : name of env var holding the instance ID (default: 'NEXUS_INSTANCE_ID')
 *   - regionEnv     : name of env var holding the region (default: 'AWS_REGION')
 */
def call(Map params = [:]) {
    String creds     = params.credentialsId ?: env.AWS_CREDENTIALS
    String instEnv    = params.instanceIdEnv ?: 'NEXUS_INSTANCE_ID'
    String regionEnv  = params.regionEnv    ?: 'AWS_REGION'
    String instanceId = env."${instEnv}"
    String region     = env."${regionEnv}"

    withCredentials([[
      $class: 'AmazonWebServicesCredentialsBinding',
      credentialsId: creds,
      accessKeyVariable: 'AWS_ACCESS_KEY_ID',
      secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
    ]]) {
        sh """
          aws ec2 start-instances \
            --instance-ids ${instanceId} \
            --region ${region}

          aws ec2 wait instance-running \
            --instance-ids ${instanceId} \
            --region ${region}
        """.stripIndent()
        echo "Started Nexus EC2: ${instanceId}"
    }
}
