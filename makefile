build:
	mvn install

travis-deploy:
	mvn versions:set -DnewVersion=${TRAVIS_TAG}
	mvn clean deploy -P sign-artifacts -DskipTests=true -DskipITs=true