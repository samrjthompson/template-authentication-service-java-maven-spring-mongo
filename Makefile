artifact_name	:= template
version			:= unversioned

.PHONY: test
test:
	mvn clean
	mvn verify -Dskip.unit.tests=false -Dskip.integration.tests=false

.PHONY: unit-test
unit-test:
	mvn clean
	mvn test

.PHONY: i-test
i-test:
	mvn clean
	mvn verify -Dskip.unit.tests=true -Dskip.integration.tests=false

.PHONY: build
build:
	mvn clean
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -Dmaven.test.skip=true
	cp ./target/$(artifact_name)-$(version).jar ./$(artifact_name).jar