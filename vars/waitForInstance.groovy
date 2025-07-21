// vars/waitForInstance.groovy

/**
 * Waits until an EC2 instance is running and retrieves its public IP
 *
 * @param Map params (all optional—defaults read from env)
 *   - credentialsId  : Jenkins AWS creds ID (defaults to env.AWS_CREDENTIALS)
 *   - instanceId     : EC2 instance ID (defaults to env.INSTANCE_ID)
 *   - region         : AWS region (defaults to env.AWS_REGION)
 *   - outputVar      : name of env var to set with the IP (defaults to 'PUBLIC_IP')
 */
def call(Map params = [:]) {
    // Pick up params or fallback to environment variables
    String creds      = params.credentialsId ?: env.AWS_CREDENTIALS
    String region     = params.region        ?: env.AWS_REGION
    String instanceId = params.instanceId    ?: env.INSTANCE_ID
    String outputVar  = params.outputVar     ?: 'PUBLIC_IP'

    withCredentials([[
        $class: 'AmazonWebServicesCredentialsBinding',
        credentialsId: creds,
        accessKeyVariable: 'AWS_ACCESS_KEY_ID',
        secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
    ]]) {
        // Wait for the instance to enter the 'running' state
        sh "aws ec2 wait instance-running --instance-ids ${instanceId} --region ${region}"

        // Fetch the public IP
        def ip = sh(
            script: """
                aws ec2 describe-instances \\
                  --instance-ids ${instanceId} \\
                  --region ${region} \\
                  --query 'Reservations[0].Instances[0].PublicIpAddress' \\
                  --output text
            """.stripIndent(),
            returnStdout: true
        ).trim()

        // Export into an environment variable for downstream stages
        env."${outputVar}" = ip
        echo "Instance ${instanceId} is running with Public IP: ${ip}"
    }
}
