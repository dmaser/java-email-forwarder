java-email-forwarder
====================

A simple Java app for forwarding email from multiple accounts

1. checkout project

2. Add to Eclipse workspace, if desired: Import -> Import Existing Projects into Workspace

3. replace "????" in build.properties

4. replace "????" in email.properties

5. In root of project run:

	ant
	
6. if deploying to a remote server, run:

	run ant "Deploy to EC2"
	
7. in dist directory or remote directory, run:

	java -jar email.jar