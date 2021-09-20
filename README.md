# ZermiaV7

20/09/2021
How to use 
--------------------------------------------------------

Ficheiros a configurar antes de correr

PARTE DO CLIENTE (ZermiaClient) (Copiar a pasta toda para cada replica)

--zermiaRuntime.properties dentro da pasta do bftsmart (library-master)

----meter o ip e porta do coordenador para troca de informação

----meter o numero de rondas totais (tem de ser igual ao numero de rondas quando vão iniciar um cliente)

----O resto não é preciso, era experimentos meus

--configurar os ficheiros na pasta config do bftsmart de acordo com as especificações que quiser

----hosts.config e system.config (não esquecer apagar o currentView quando correr para novas configurações, ex: f=1/f=2.... )

PARTE DO SERVER(ZermiaServer)

--Entrar na pasta ConfigurationProperties

----configurar zermia.properties

------A porta no qual o coordenador vai iniciar

------Os ID's das replicas

------Os ID dos clientes

------Meter o numero de replicas e clientes que vão fazer parte do experimento (igual á configuração do bftsmart).

--Entrar na pasta ConfigureProperties

---configurar faultyReplicas.properties

---modificar parametros de acordo com o objectivo, sendo que aqui se configura quais os tipos de mensagens que as replicas faltosas vão injectar falhas. (as replicas faltosas tem de ter os mesmos ids quando executado no terminal senão dá erro) <- IMPORTANTE

JAVA STUFF
-----------------------------------------------------------------------
Antes de correr o Zermia é preciso o Oracle Java JDK 15/16/17 instalado (modificar o numero abaixo)

--caso não tenha instalado, faça o seguinte:

----sudo add-apt-repository ppa:linuxuprising/java

----sudo apt install oracle-java15-installer

----sudo apt install orcale-java15-set-default

INICIAR SERVER
------------------------------------------------------------------------

O servidor tem de correr sempre primeiro

--Ir para pasta ZermiaServer\

---- java -jar Zermia.jar [parametros]

------ Exemplo de um comando java -jar Zermia.jar Replica 0 Flood 1000 200 800 Flood 2000 2000 1000

------ No exemplo acima esta executar um flood attack desde o round 200 até 1000 (1000 mensages extras), e corre outro flood attack desde 2000 até 3000 (2000 mensagens extras)

------ Exemplo de multiplas replicas : java -jar Zermia.jar Replica 0 TdelayAll 100 1000 1000 Replica 3 TdelayAll 100 1000 1000

------ Exemplo de multiplas replicas e falhas : java -jar Zermia.jar Replica 0 TdelayAll 100 Load 50 1000 1000 Crash 1 5000 1 Replica 3 0Packet 10 800 1200  

------ No exemplo acima, Replica 0 vai correr duas falhas no mesmo intervalo, começa na ronda de consensus 1000 até ronda 2000, sendo que corre um delayer e um CPU_loader ao mesmo tempo. Mais tarde crasha na ronda 5000. Na replica 3 é corrido uma falha para enviar mensages vazias, começando na ronda 800 até 2000.


------ Exemplo de correr replicas e clientes :  java -jar Zermia.jar Replica 0 TdelayAll 200 2500 2500 Client 50 PNRS 1 50 50

------ No exemplo em cima é corrido um schedule para replica 0 e depois um schedule é corrido para 50 clientes do protocolo. Esta falha faz com que não sejam enviados requests para primaria. É de notar que o numero de rondas funcionam de forma diferente nos clientes. Sendo que por exemplo quando a replica chega á ronda 2500 o cliente estara a chegar a ronda 50 (50*50 = 2500)


Correr replicas e clients do bftsmart
----------------------------------------------------------

Ir para a pasta do ZermiaClient\library-master\

-- correr ./runscripts/smartrun.sh bftsmart.demo.counter.CounterServer [numero] (modificar este parametro com base no numero da replica)

-- depois de todas as replicas carregadas (esperar até qu estejam ready to process)

-- correr client com o mesmo numero de rondas estipulado anteriormente

-- ./runscripts/smartrun.sh bftsmart.demo.counter.CounterClient 1001 1 [numero] (modificar este parametro para o numeero de rondas).

--------------------------------------------------------------------------------------

Mal todas as replicas terminem as suas operações (faltosas podem demorar mais em alguns casos), o servidor mostra stats do teste para cada replica, bem como grava os dados de debito de cada na pasta replicastats no servidor.

--------------------------------------------------------------------------------------

Correr replicas e clients do bftsmart pelo YCSB
----------------------------------------------------------

-- Correr o zermia como normal (com seus parametros)

-- Ir para a pasta do ZermiaClient\library-master\

-- Replicas no terminal(linux) por YCSB

--- java -Djava.security.properties=config/java.security -Dlogback.configurationFile=config/logback.xml  -cp bin/:lib/* bftsmart.demo.ycsb.YCSBServer 0 (modificar este "0" aqui por cada instancia da replica)

-- Clientes no terminal(linux) por YCSB 

--- java -Djava.security.properties=config/java.security -Dlogback.configurationFile=config/logback.xml  -cp bin/:lib/* com.yahoo.ycsb.Client -threads 10 -P config/workloads/workloada -p measurementtype=timeseries -p timeseries.granularity=1000 -db bftsmart.demo.ycsb.YCSBClient -s >> output.txt

--- O output é escrito para um ficheiro na mesma pasta do client sob o nome output.txt. Modificar o numero de clientes em cima em "-threads 10" para outro para sem ser 10. Modificar granularidade de acordo com o que se quer, default é 1 segundo.

