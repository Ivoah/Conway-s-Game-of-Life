OUTPUT="Conway's Game of Life.jar"

all:
	javac *.java
	jar cvfe $(OUTPUT) GameOfLife *.class

run:
	java -jar $(OUTPUT)