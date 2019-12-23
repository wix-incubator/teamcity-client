build:
	mvn install

travis-deploy:
    gpg --import private-key.gpg
	mvn deploy -P publish -DskipTests=true -DskipITs=true