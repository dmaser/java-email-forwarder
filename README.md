java-email-forwarder
====================

A simple Java app for forwarding email from multiple accounts

1. checkout project

2. Add to Eclipse workspace, if desired: Import -> Import Existing Projects into Workspace

3. replace instances of "????" in build.properties

4. replace instances of "????" in email.properties

5. In root of project run:

	ant
	
6. if deploying to a remote server, run:

	ant "Deploy to EC2"
	
7. in dist directory or remote directory, run:

	java -jar email.jar
	
Notes:
=====

Messages are not deleted after being forwarded and are left on the mail server.

A text file containing seen UIDs (seen_uids.txt) will be created to track messages already forwarded.

