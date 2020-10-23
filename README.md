# ZermiaV3

23/10/2020
How to use 
--------------------------------------------------------

Ficheiros a configurar antes de correr

PARTE DO CLIENTE (ZermiaClient)

--zermiaRuntime.properties dentro da pasta do bftsmart (library-master)

----meter o ip e porta do coordendor para troca de informação

----meter o numero de rondas totais (tem de ser igual ao numero de rondas quando vão iniciar um cliente)

----O resto não é preciso, era experimentos meus

--configurar os ficheiros na pasta config do bftsmart de acordo com as especificações que quiser

----hosts.config e system.config (não esquecer apagar o currentView quando correr para novas configurações, ex: f=1/f=2.... )

PARTE DO SERVER(ZermiaMor)

--Entrar na pasta ConfigurationProperties

----configurar zermia.properties

------A porta no qual o coordenador vai iniciar

------Os ID's das replicas (o IP não é preciso, era outra experimentação minha da qual vou limpar na proxima versão)

------O ID do client (IP não é preciso)

------Meter o numero de replicas e clientes que vão fazer parte do experimento (igual á configuração do bftsmart), sendo que é preciso digitar duas vezes ambos valores, um para o numberOfReplica e outro para o numberOfAvailableReplicas (o mesmo para o cliente). Isto vai ser limpo tambem na proxima versão(outro experimento meu por causa de novas replicas acederem ao grupo durante runtime, pelo que vou descontinuar esse processo todo de inicio).

JAVA STUFF
-----------------------------------------------------------------------
Antes de correr o Zermia é preciso o Oracle Java JDK 15 instalado

--caso não tenha instalado, faça o seguinte:

----sudo add-apt-repository ppa:linuxuprising/java

----sudo apt install oracle-java15-installer

----sudo apt install orcale-java15-set-default

INICIAR SERVER
------------------------------------------------------------------------

O servidor tem de correr sempre primeiro

--Ir para pasta ZermiaMor\

---- java -jar Zermia.jar [parametros]

------ Exemplo de um comando java -jar Zermia.jar Replica 0 Flood 1000 200 800 Flood 2000 2000 1000

------ No exemplo acima esta executar um flood attack desde o round 200 até 1000 (1000 mensages extras), e corre outro flood attack desde 2000 até 3000 (2000 mensagens extras)

------ Exemplo de multiplas replicas : java -jar Zermia.jar Replica 0 TdelayOnce 100 1000 1000 Replica 3 TdelayOnce 100 1000 1000

------ Exemplo de multiplas replicas e falhas : java -jar Zermia.jar Replica 0 TdelayAll 100 Load 50 1000 1000 Crash 1 5000 1 Replica 3 0Packet 10 800 1200  

------ No exemplo acima, Replica 0 vai correr duas falhas no mesmo intervalo, começa na ronda 1000 até ronda 2000, sendo que corre um delayer e um CPU_loader ao mesmo tempo. Mais tarde crasha na ronda 5000. Na replica 3 é corrido uma falha para enviar mensages vazias, começando na ronda 800 até 2000.

------------------------- Correr Replicas do bftsmart ---------------------------------

Ir para a pasta do ZermiaClient\library-master\

-- correr ./runscripts/smartrun.sh bftsmart.demo.counter.CounterServer [numero] (modificar este parametro com base no numero da replica)

-- depois de todas as replicas carregadas (esperar até qu estejam ready to process)

-- correr client com o mesmo numero de rondas estipulado anteriormente

-- ./runscripts/smartrun.sh bftsmart.demo.counter.CounterClient 1001 1 [numero] (modificar este parametro para o numeero de rondas).

--------------------------------------------------------------------------------------

Mal todas as replicas terminem as suas operações (faltosas podem demorar mais em alguns casos), o servidor mostra stats do teste para cada replica.


