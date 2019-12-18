build:
	mvn install

travis-deploy:
	gpg --import private-key.gpg
	mvn versions:set -DnewVersion=${TRAVIS_TAG}
	mvn clean deploy -P release -DskipTests=true -DskipITs=true