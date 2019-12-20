# testeAtomix

Comando Cliente:
>mvn exec:java -Dexec.mainClass="stateMachine.client.cliente" -Dexec.args="127.0.0.1 6000 127.0.0.1 6001 127.0.0.1 6002" 
Comando pra Réplica: 
>mvn exec:java -Dexec.mainClass="stateMachine.server.KeyValueStore" -Dexec.args="0 127.0.0.1 6000 127.0.0.1 6001 127.0.0.1 6002"
