
travis-deploy:
	gpg --import private-key.gpg
	mvn deploy -P publish --settings settings.xml -DskipTests=true -DskipITs=true