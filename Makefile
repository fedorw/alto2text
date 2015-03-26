all: compile
	mvn assembly:assembly

clean:
	mvn clean

mvn compile:
	mvn compile
