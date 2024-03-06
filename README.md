## Gaspar Barancell Junior


## Rinha de Backend 2024

Aplicação desenvolvida em Java compilada para código de máquina com a GraalVM, sem utilizar nenhum framework nem mesmo libs.

Os dados estao sendo persistidos em outro projeto backend desenvolvido por mim, onde armazeno tudo em CSV (https://github.com/gasparbarancelli/rinha-backend-2024/tree/feature/persistence).

Para proxy utilizo o nginx.


##### BUILD

mvn -Pnative -Dagent package

docker build -t gasparbarancelli/rinha-2024-java-nativo-persistence:latest .


##### Repositorio Oficial

https://github.com/gasparbarancelli/rinha-backend-2024/tree/feature/nativo
https://twitter.com/gasparbjr
https://br.linkedin.com/in/gaspar-barancelli-junior-77681881




