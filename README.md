# spring-boot-microservices
Spring-Boot micro-services for BDX I/O demo


To import CSV into mongo:
mongoimport --type csv --headerline -d aoc_aop -c communes_aires < communes_aires.csv
mongoimport --type csv --headerline -d aoc_aop -c aires_produits < aires_produits.csv
