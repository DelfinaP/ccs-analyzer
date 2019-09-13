-- Readme per la compilazione ed esecuzione del tool
Passi da eseguire:
1. Installare Ant;
2. Se sei su Windows, configura Ant:
2.1. Aggiungere alla variabile d'ambiente "Path" il percorso in cui abbiamo messo Ant (ad esempio "C:\MyFolder\apache-ant-1.10.7\bin");
2.2. Creare (se non esiste già) una nuova variabile d'ambiente "JAVA_HOME" e settarla al path della JDK (ad esempio "C:\Program Files\Java\jdk1.8.0_201");
2.3. Chiudere e riavviare il prompt.
3. Verifica che Ant sia configurato correttamente eseguendo il comando che restituisce il numero di versione di Ant:
ant -v
4. Apri il terminale ed esegui:
cd <uni-sicur-reti\intellij\01-implem_sicur_reti>
5. Esegui il comando per compilare ed eseguire il tool:
ant compile jar run