# Projet - TWIC - Décembre 2019
Conception d'un jeu de type RPG en 2D. À noter que ce jeu a été réalisé dans le cadre d'un cours de première année à 
l'Ecole Polytechnique Fédérale de Lausanne en Suisse. Notre implémentation du jeu se limite (quasiment) à l'ajout du 
dossier `twic` dans `java.ch.epfl.cs107.play` ainsi que certaines ressources.

## Présentation
TWIC est un jeu dans lequel vous serez accompagné par une gentille harpie nommée Aeris, qui vous conduira tout au long 
du jeu dans votre quête. Ce jeu vous demandera de faire des choix, vous déciderez vous-même la fin de votre histoire !


## Lancement des Jeux
Il vous suffit de run le fichier Play.java qui se situe dans Java/ch/epfl/cs107/play

## Commandes...
Regardons à présent les commandes du jeu afin de déplacer votre personnage aussi facilement que possible dans son 
univers.

### ...de Base
Les commandes de base sont les déplacements, avec les touches
- LEFT
- UP
- RIGHT
- DOWN

Si vous appuyez sur la touche `S` (une seule fois suffit), vous commencerez à courir. Lorsque vous restez immobile plus 
d'un certain temps, vous repasserez en mode marche.
Si vous appuyez sur la touche `TAB`, vous changez d'item courant.
Si vous appuyez sur la touche `ESPACE`, vous utilisez l'item courant.
Si vous appuyez sur la touche `E` et que vous avez une clef vous ouvrez la porte du château.

### ...Avancées
Les commandes avancées ne sont disponibles uniquement dans la partie bonus.

Si vous appuyez sur la touche `ENTER` lors d'un dialogue, celui-ci disparaît (ou passe au suivant), sauf dans le cas où 
il s'agit d'une instruction. Generalement, les instructions sont en gris.

Vous pouvez à tout temps consulter votre inventaire grâce à la touche `I`. À l'intèrieur de ce dernier, vous pouvez 
naviguer entre vos items grâce aux touches directionnelles `LEFT`, `UP`, `RIGHT` et `DOWN`. Faites attention, car 
lorsque vous consultez votre inventaire, le jeu tourne toujours... et les monstres aussi.

Pour le fermer, vous pouvez réappuyer sur la touche `I` ou `ENTER`. La modification de l'item courant se fait 
automatiquement lorsque vous naviguez avec les touches directionnelles.

L'interface de la boutique s'ouvre comme une interaction (touche `E`) et se ferme au moyen de la touche `I`. Si vous 
avez l'argent necessaire, vous pouvez acheter un item au moyen de la touche `ENTER`.

Dans ce mode de jeu, vous pouvez interagir avec les villageois en pressant la touche `E` lorsque vous vous trouvez à 
proximité d'un de ces derniers, en plus de pouvoir ouvrir le château, une fois la clef obtenue.

## Attention Spoilers
Avant de continuer de lire ce document, nous vous invitons à tester le jeu bonus, afin de ne pas vous gâcher la 
surprise.

## Déroulement de l'Histoire
Si vous avez bien suivi notre conseil, vous connaissez à présent l'histoire, mais nous allons vous l'expliquer en 
détail.

En tout premier point, vous apparaissez dans une maisonnette. Lorsque vous sortez de cette maison, une gentille Harpie 
du nom de Aeris vient vous expliquer son problème familial avec sa soeur Maléfique qui a enfermé le roi dans son 
château, et appelé toutes les créatures obscures que ce monde peut connaître.

Elle vous dit d'aller voir les villageois afin qu'ils vous aident à trouver un bâton magique. Vous devez donc descendre 
au village, et parler aux personnages. Ils vous diront qu'un bâton magique est caché dans la grotte à côté de la 
rivière, mais qu'avant cela vous devez vous procurer un arc pour activer le pont.

Vous devez donc tuer quelques monstres afin de récupérer des pièces et acheter ainsi un arc dans le shop. Notez que si 
vous parlez aux personnages passifs presents, il se peut que l'un d'eux vous suggerent de tuer le forgeron du shop. En 
effet, il est tout a fait possible de le tuer ! Et en prime, les objets du shop deviennent gratuits ! Une fois votre 
arc récupéré, vous allez près de la rivière afin de tirer une flèche dans le cercle et ainsi débloquer le pont, qui 
vous permet de traverser la rivière.

Une fois votre bâton magique récupéré, vous pouvez aller au château afin de combattre le seigneur des ténèbres, 
vulnérable uniquement à la magie.

Dès que vous gagnez contre le seigneur, un coeur apparaît à la place de la clef, et dès que vous le prenez, Aeris 
revient vous conseiller. Après une petite discussion et le fait que vous ne pouvez pas entrer dans le château, 
Maléfique apparaît enfin pour dire qu'elle va massacrer tout le village. Aeris vous demande de sauver ainsi les 
villageois.

Vous devez donc retourner au village, et là vous apprenez qu'il y a plein de "ZOMBIIIIIIEEEES" !!! Vous devez donc vous 
dépécher de tuer tous les zombies avant que tous les villageois soient contaminés. Une fois les zombies tués, vous 
pouvez accéder à une grotte secrète qui apparrait juste au-dessus d'une des maisons du village. Vous pourrez trouver 
dans cette grotte afin la flèche magique, autrement appeler flèche à tête chercheuse. Cette fleche a une incroyable 
capacite. Lorsque vous la lancez, elle va se diriger vers la cible la plus proche, lui causer des degats, puis revenir 
vers vous. C'est... Magique ! Attention tout de meme, cette grotte est remplie de zombie, mais ce n'est qu'un detail.

Avec ou sans flèche magique, vous pouvez retourner au-dessus de votre maison vous aventurer dans la grande grotte 
désormais accessible. On y voit rien et il y a toute sorte de monstre, avec également quelques items. Il existe deux 
portes dans cette grotte (sans compter celle aui vous ramene a votre ferme). La premiere porte se situe en haut à 
gauche. Vous arrivez dans une pièce où il y a beaucoup de FlameSkull avec quelques coeurs au centre, et si vous 
traversez complètement la salle pour prendre la porte en face, vous retournez au début de la grotte initiale. Il s'agit 
donc d'une porte "piège". Cependant si vous trouvez la deuxieme porte située au coin droit en haut de la map, vous 
arrivez dans une salle avec un dragon en son centre. Si vous battez le dragon, vous gagnez une clef, et les 2 soeurs 
jumelles arrivent. Vous devez tuer l'une d'entre elles.

Maintenant, il y a deux fins possibles. Soit vous tuez Aeris, et à ce moment-là vous devez également aller tuer le roi 
dans son château, afin de vous autoproclamer roi avec Maléfique. Soit vous tuez Maléfique, et à ce moment-là vous devez 
retourner au château délivrer le roi, et il vous félicitera.

## Sources
Voici les sources de quelques entités que nous avons utilisées.
- [Harpie Aerish](https://www.deviantart.com/tsarcube/art/Harpy-Monster-Girl-Sprite-210652764)
- [Harpie Maléfique](https://www.deviantart.com/tsarcube/art/Black-Harpy-Monster-Girl-Sprit-210800060)
- [Dragon](https://www.pngkey.com/download/u2q8w7y3a9u2i1e6_preview-rpg-maker-dragon-sprite/)

Pour les autres entités, soit nous avons pris ceux de base et les avons modifiés, comme le zombie, grâce au logiciel 
`Aseprite`, soit nous les avons créés nous-même, comme la flèche magique.

## Auteurs
* **Jean-Baptiste Moreau**
* **Vincent Jeannin** 
***
