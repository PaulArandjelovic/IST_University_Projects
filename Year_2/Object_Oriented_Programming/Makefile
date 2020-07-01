all: compile run

compile:
	javac -cp po-uilib.jar:. `find m19 -name *.java`
run:
	java -Dimport=tests/A-07-10-M-ok.import -cp po-uilib.jar:. m19.app.App
clean:
	rm `find m19 -name *.class`
