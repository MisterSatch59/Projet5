[![Build Status](https://travis-ci.com/Oltenos/Projet5.svg?branch=master)](https://travis-ci.com/Oltenos/Projet5)

# MyERP

## Organisation du répertoire

*   `doc` : documentation
*   `docker` : répertoire relatifs aux conteneurs _docker_ utiles pour le projet
    *   `dev` : environnement de développement
*   `src` : code source de l'application


## Environnement de développement

Les composants nécessaires lors du développement sont disponibles via des conteneurs _docker_.
L'environnement de développement est assemblé grâce à _docker-compose_
(cf docker/dev/docker-compose.yml).

Il comporte :

*   une base de données _PostgreSQL_ contenant un jeu de données de démo (`postgresql://127.0.0.1:9032/db_myerp`)


### Lancement

    cd docker/dev
    docker-compose up


### Arrêt

    cd docker/dev
    docker-compose stop


### Remise à zero

    cd docker/dev
    docker-compose stop
    docker-compose rm -v
    docker-compose up

# Rapports
Il est possible d'obtenir les rapports suivant : 
 - Site Maven contenant (dosser site-doc) : 
 	- Javadoc
 	- Résultats des test
 - Rapport de couverture du code par les test unitaires par modules (dans site-doc/module/cobertura et via SonarQube)
 - Rapports d'analyse avec SonarQube

Pour celà il faut lancer les commandes suivantes :
 - mvn clean install -PtestCoverage
 - mvn test package site site-deploy
 - mvn sonar:sonar (après avoir lancé le serveur sonar)

 
