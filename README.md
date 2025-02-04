per testare il sistema c'è bisogno del server MySQL e del comando MysqlDump

- istanziare una connessione al server MySQL
- creare nuovo schema vuoto nominato 'organigrammadb'
- modificare i parametri di accesso al DB nella classe "db connection" presente nella cartella src/db
- modificare path di output del file
- avviare l'app dalla classe "Main"
- scegliere file .sql presente nella cartella "src" del progetto

esecuzione :
il sistema riceve un file .sql, crea una connessione col db MySQL e accede al db 'organigrammadb',
se le tabelle esistono già (relative ad un vecchio organigramma), fa una DROP e istanzia le nuove tabelle,
popolandole con le nuove informazioni contenute nel file, per salvare le modifiche utilizza il comando DUMP fornito da MySQL,
per creare un nuovo file .sql basato sulle tabelle e sui record che in quel momento saranno presenti nel db mysql, mantenendo nel db il salvataggio relativo all'ultimo organigramma che il sistema ha lavorato.
