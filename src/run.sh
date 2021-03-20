#! /bin/bash

cd Gramatica/
antlr4-build -visitor -no-listener
cd ../Logica
antlr4-build
cd ..

java Gramatica.ALFA_DMain $1&& echo -e "\n\nPrograma Output\n\n" && javac Output.java && java Output

cd Gramatica/
antlr4-clean &>/dev/null
cd ../Logica
antlr4-clean &>/dev/null
cd ..

rm Output.class &>/dev/null
