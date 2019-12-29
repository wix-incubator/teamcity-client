build:
	mvn install

travis-deploy:
	mvn versions:set -DnewVersion=${TRAVIS_TAG}
	mvn deploy -P publish --settings settings.xml -DskipTests=true -DskipITs=true

