INSTALLDIR="/opt/altorender"

all: compile
	mvn assembly:assembly

clean:
	mvn clean

mvn compile:
	mvn compile

install:
	mkdir -p $(INSTALLDIR)
	cp target/*with-dependencies*jar $(INSTALLDIR)
	cp altorender $(INSTALLDIR)
	

