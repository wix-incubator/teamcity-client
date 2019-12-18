build:
	mvn install

travis-deploy:
	gpg --import private-key.gpg
	mvn versions:set -DnewVersion=${TRAVIS_TAG}
	git config --global user.signingkey 324C864A
	mvn clean deploy -P release --settings settings.xml -DskipTests=true -DskipITs=true