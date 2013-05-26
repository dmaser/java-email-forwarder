java-email-forwarder
====================

A simple Java app for forwarding email from multiple accounts

# checkout project

# Add to Eclipse workspace, if desired: Import -> Import Existing Projects into Workspace

# replace "????" in build.properties

# replace "????" in email.properties

# In root of project run:

	ant
	
#  if deploying to a remote server, run:

	run ant "Deploy to EC2"
	
# in dist directory or remote directory, run:

	java -jar email.jar