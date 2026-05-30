# Manage your little fish

## Collaborators

- MARMION Steven
- PAQUIER Mael
- MIAUD Jules

## Idea

Manage your little fish is a game web application used to manage little fishs. 
Every day, your fish will need to be feed, you will need to collect coins in order to buy food for fish and you can collect coins by clicking on your fish or completing daily tasks. 
Fishs are losing point of life every minutes so you had to often check lifecycle of your aquarium in order to keep fishs alives.

## Technical stack

- Backend could be done in Java 17/21 Spring Boot - mvn
- Frontend could be done in HTML, CSS, JS with bootstraps
- Docker will be used in order to contnairized applications and use compose technology to organized deployment
- PostgreSQL will be used as SGBD in order to store app datas

## Architecture design

TO DO (indicate diagram designed through md link to ./doc/uml folder)

## Feature keys

- Without login :
    - User can only search others users through a search bar and check the fish of others users
    - User can check best aquarium of others users if they put their aquarium in public
- If registering :
    - User won 200 coins to start its journey, he can buy his first fish and start with minimal aquarium which is free to start
- After login :
    - User can create, update, read and delete their fish.
    - User can access to a profil page in order to change its profil setting like pseudo, avatar, light/dark theme, notifications preferences (if he wants to be notificated for every fish which died, when fishes are going to die, etc.)
    - User can add other users as friend in order to make fish trade
    - User can see coins they have because they accumulate every minute coins
    - User can access to shop in order to buy food for fish, upgrade aquarium, etc.
    - User can make three or four daily games/little challenge in order to gain coins quicker
    - User can click on their fish by visualizing them in the aquarium interface and gain coins each time user clickes on fish he has
    - User can access to its fish page with multiple filter like filtering fish by size, by color, by name, by species, by age, etc.
    - User has a role, it can be user or admin. If user is admin, he can access through profil page to a button which redirect him to a panel admin in order to manage every user datas