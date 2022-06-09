# Guião de Demonstração


## 1. Preparação do sistema

Para testar o sistema e todos os seus componentes, é necessário preparar um ambiente com dados para proceder à verificação dos testes.

### 1.1. Lançar o *registry*

Para lançar o *ZooKeeper*, ir à pasta `zookeeper/bin` e correr o comando  
`./zkServer.sh start` (Linux) ou `zkServer.cmd` (Windows).

É possível também lançar a consola de interação com o *ZooKeeper*, novamente na pasta `zookeeper/bin` e correr `./zkCli.sh` (Linux) ou `zkCli.cmd` (Windows).

### 1.2. Compilar o projeto

Primeiramente, é necessário compilar e instalar todos os módulos e suas dependências --  *rec*, *hub*, *app*, etc.
Para isso, basta ir à pasta *root* do projeto e correr o seguinte comando:

```sh
$ mvn clean install -DskipTests
```

### 1.3. Lançar e testar o *rec*

Para proceder aos testes, é preciso em primeiro lugar lançar o servidor *rec* .
Para isso basta ir à pasta *rec* e executar:

```sh
$ mvn compile exec:java
```

Este comando vai colocar o *rec* no endereço *localhost* e na porta *8091*.

Para lançar outros, em terminais diferentes:
```shell
$ ./target/appassembler/bin/rec localhost 2181 localhost 8092 2 
$ ./target/appassembler/bin/rec localhost 2181 localhost 8093 3
```

Para confirmar o funcionamento do servidor com um *ping*, fazer:

```sh
$ cd rec-tester
$ mvn compile exec:java
```

Para executar toda a bateria de testes de integração, fazer:

```sh
$ mvn verify
```

Todos os testes devem ser executados sem erros.


### 1.4. Lançar e testar o *hub*


Em seguida é necessário lançar o servidor *hub* .
Para isso basta ir à pasta *hub* e executar:

```sh
$ mvn compile exec:java
```

Este comando vai colocar o *hub* no endereço *localhost* e na porta *8081*.

Para confirmar o funcionamento do servidor com um *ping*, fazer:

```sh
$ cd hub-tester
$ mvn compile exec:java
```

Para executar toda a bateria de testes de integração, fazer:

```sh
$ mvn verify
```

Todos os testes devem ser executados sem erros.


### 1.5. *App*

Caso tenham sido corridos os testes, devemos desligar e voltar a ligar o hub.

Iniciar a aplicação com a utilizadora alice:

```sh
$ cd app
$ mvn compile exec:java
```

**Nota:** Para poder correr o script *app* diretamente é necessário fazer `mvn install` e adicionar ao *PATH* ou utilizar diretamente os executáveis gerados na pasta `target/appassembler/bin/`.

Para alterar o nome de utilziador é necessário editar o ficheiro `app/pom.xml`
Depois de lançar todos os componentes, tal como descrito acima, já temos o que é necessário para usar o sistema através dos comandos.

## 2. Sequência de comandos
Para experimentar vários comandos automaticamente podemos lançar o ficheiro de comandos em dois terminais diferentes usando:

```shell
$ ./target/appassembler/bin/app localhost 2181 alice +35191102030 38.7380 -9.3000 < '../demo/comandos.txt'
$ ./target/appassembler/bin/app localhost 2181 bruno +35193334444 38.7380 -9.3000 < '../demo/comandos.txt'
```

A falta silenciosa dos processos pode ser desencadeada terminando o programa abruptamente (por exemplo, fechando a 
janela do terminal, ou fazendo o comando `kill -9` que envia um `SIGKILL` que, ao contrário do `SIGTERM`, não pode ser 
tratado).  
É também possível colocar o processo em pausa (utilizando a combinação teclas `Ctrl+Z` no terminal do servidor, ou o 
comando `kill -20` que envia um `SIGTSTP` ao processo) e resumindo o seu estado posteriormente (escrevendo `fg` no 
terminal do servidor ou utilizando o comando `kill -18` que envia o signal `SIGCONT`).

## 3. Teste dos comandos

Nesta secção vamos correr os comandos necessários para testar todas as operações do sistema.
Cada subsecção é respetiva a cada operação presente no *hub*.

### 3.1. *balance*
Para correr este comando basta escrever `balance` no standard input da App.
```
> balance
alice 0 BIC
```
O único caso de erro possível é quando a app tem um username que não está registado com o hub.
Para criarmos esta situação temos de lançar a app com um username inválido.
Para fazer isso é necessário alterar username `app/pom.xml` para um inválido.

O output esperado nesta situação é:
```
> balance
User does not exist
```

### 3.2 *top-up*
A sintaxe deste comando é `top-up <valor:unsigned int>
```
> top-up 21
alice 210 BIC
> top-up notANumber
Invalid argument, try again.
alice 210 BIC
> top-up -4
Cannot Top up negative value
alice 210 BIC
> top-up 0.1234 
Invalid argument, try again.
alice 210 BIC
```

### 3.3 *tag*
Para criar um alias para outra localização corremos
```shell
> tag 38.7376 -9.3031 loc1
OK
```
Agora podemos usar `loc1` em vez das suas coordenadas, por exemplo
```
> move loc1
alice em https://www.google.com/maps/place/38.7376,-9.3031
> move 21.21 22.22
alice em https://www.google.com/maps/place/21.21,22.22
```
### 3.4 *at*
Imprime a localização do utilizador
```shell
> at
alice em https://www.google.com/maps/place/38.7376,-9.3031
```

### 3.5 *scan*
Lista as *n* estações mais próximas (*n*=3 no exemplo seguinte):
```shell
> scan 3
istt, lat 38.7372, -9.3023 long, 20 docas, 4 BIC prémio, 12 bicicletas, a 82 metros
stao, lat 38.6867, -9.3124 long, 30 docas, 3 BIC prémio, 20 bicicletas, a 5717 metros
jero, lat 38.6972, -9.2064 long, 30 docas, 3 BIC prémio, 20 bicicletas, a 9517 metros
```

### 3.6 *info*
Lista a informação de uma estação:
```shell
> info istt
IST Taguspark, lat 38.7372, -9.3023 long, 20 docas, 4 BIC prémio, 12 bicicletas, 22 levantamentos, 7 devoluções, https://www.google.com/maps/place/38.7372,-9.3023
```

### 3.7 *bike-up*
Levanta a bicicleta da estação indicada e pedalar para outra posição:
```
> bike-up istt
OK
> move 38.6867 -9.3117
alice em https://www.google.com/maps/place/38.6867,-9.3117
```

### 3.8 *bike-down*
Devolve a bicicleta (é necessário a menos de 200 metros):
```shell
> bike-down istt
ERRO fora de alcance
> bike-down stao
OK
```

### 3.9 *ping*
Verifica se o hub está online.
```shell
> ping
OK
```

### 3.10 *sys-status*
Contacta todos os hub e rec registados no serviço de nomes.
Para cada servidor deve ser indicado o seu nome (path) e se está a responder (up) ou não (down).

```shell
> sys-status
/grpc/bicloin/hub/1 is up.
/grpc/bicloin/rec/1 is up.
```

### 3.10 *help*
Indica ao utilizador onde está disponível documentação.
```shell
> help 
Complete instructions for running this system available at the README.md file
```

### 3.11 *zzz*
Espera o nùmero de milisegundos correspondente ao seu argumento.
```shell
> zzz 500
```
----

## 3. Tolerância a faltas
Para testar a tolerância de faltas podemos enviar um *SIGKILL* para um rec de modo a terminá-lo abruptamente.
Por exemplo vamos desligar o rec que está a usar o porto 8093.
```shell
fuser -k 8093/tcp
```
Como ainda temos um quorum de recs, podemos verificar que a app não alterou o seu comportamento para o utilizador.
```shell
$ ./target/appassembler/bin/app localhost 2181 carlos +34203040 38.7380 -9.3000 < '../demo/comandos.txt'
```
O sistema também tolera quando recs mudam de porto ou de host. Para esta demonstração vamos relançar o rec que
acabámos de terminar no porto 8094.

```shell
$ ./target/appassembler/bin/rec localhost 2181 localhost 8094 4
```
Lançamos novamente a app e observamos que também funciona.
```shell
$ ./target/appassembler/bin/app localhost 2181 diana +34010203 38.7380 -9.3000 < '../demo/comandos.txt'
```



## 4. Considerações Finais

Estes testes não cobrem tudo, pelo que devem ter sempre em conta os testes de integração e o código.