# Projet Réseau d'ordinateur : Communication UDP clients/serveur

## Description

Nous avons développé en Java un serveur UDP et un client permettant à plusieurs clients de communiquer entre eux via le serveur. Une fois le serveur lancé, il est passif et peut recevoir des demandes de connexion de la part de clients via un port générique que connaît chaque client. Il dédie un port d'écoute spécifique pour chaque nouveau client.
Lorsqu’un nouveau client est lancé, il renseigne son nom d’utilisateur sachant qu’il ne faut pas que ce nom soit déjà utilisé. Si c’est le cas, le serveur l'avertit et il devra choisir un autre nom. Si le nom choisi est valide, c’est-à-dire s’il n’est pas déjà utilisé par un autre client, l’utilisateur sera connecté au serveur via un port dédié.
Une fois connecté au serveur, un client pourra envoyer des messages privés aux autres clients connectés au serveur et à lui-même, mais aussi des messages publics à tous les autres clients connectés. Il pourra quitter la conversation et les autres clients seront avertis de son départ.

## Comment utiliser le projet

Dans le répertoire Sockets_UDP\src, compiler puis exécuter le fichier Serveur.java pour lancer le serveur UDP. Ensuite dans le même répertoire, compiler puis exécuter le fichier Client.java autant de fois que de clients voulus.
Au niveau des clients, il faut rentrer son nom d’utilisateur, puis vous aurez la possibilité d’envoyer un message à tous les autres clients lancés en entrant ‘all’ ou à seulement un autre client en entrant son nom. Ensuite il faudra renseigner le message que vous voulez envoyer puis valider en appuyant sur le bouton ‘Entrée’ du clavier. Vous pourrez constater dans le serveur et chez le ou les clients destinataire(s) l’envoi et la réception de chaque message. Enfin, pour quitter il faut entrer ‘exit’ dans le terminal et les autres clients seront avertis de son départ ainsi que le serveur UDP.
