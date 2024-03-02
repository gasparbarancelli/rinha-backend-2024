## Gaspar Barancell Junior


## Rinha de Backend 2024

Aplicação desenvolvida em Java compilada para código de máquina com a GraalVM, sem utilizar nenhum framework, apenas as libs de jdbc do postgres e do hikari para pool de conexão com banco de dados.

Inicialmente estava utilizando a lib do gson, depois mudei para a jackson, mas removendo a lib e fazendo o parse manualmente, melhorei a performance.

Utilizei o banco de dados H2, onde compilei o mesmo para código de máquina com a GraalVM.

Para proxy fiz testes com o HAProxy, Envoy e Nginx o qual teve melhor performance.


##### BUILD

mvn -Pnative -Dagent package

docker build -t gasparbarancelli/rinha-2024-java-nativo-22:latest .


##### Repositorio Oficial

https://github.com/gasparbarancelli/rinha-backend-2024/tree/feature/nativo
https://twitter.com/gasparbjr
https://br.linkedin.com/in/gaspar-barancelli-junior-77681881




