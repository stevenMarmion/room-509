# Manage your little fish

## Collaborators

- MARMION Steven
- PAQUIER Mael
- MIAUD Jules

## Deadline

Deadline to produce website is 30th June 2026

## Idea

Manage your little fish is a game web application used to manage little fishs. 
Every day, your fish will need to be feed, you will need to collect coins in order to buy food for fish and you can collect coins by clicking on your fish or completing daily tasks. 
Fishs are losing point of life every minutes so you had to often check lifecycle of your aquarium in order to keep fishs alives.

## Technical stack

- Backend could be done in Java 17/21 Spring Boot - mvn
- Frontend could be done in Vue.JS, JS with bootstraps
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

## Commands

### Build and run with Docker

The `backend` and `frontend` Dockerfiles are multi-stage : each image builds itself
from source (Maven for the backend, npm/Vite for the frontend), so the produced
images always reflect the latest version of your files — no manual build step needed
beforehand.

From the project root :

```sh
docker compose up --build -d
```

This builds (or rebuilds) the `db`, `backend` and `frontend` images and starts the
three containers :

- `db` — PostgreSQL on `localhost:5432`
- `backend` — Spring Boot API on `localhost:8080`
- `frontend` — Vue.js app served on `localhost:5173`

To rebuild only one service, e.g. after editing frontend code :

```sh
docker compose up --build -d frontend
```

To stop the stack :

```sh
docker compose down
```

### FRONTEND (local development, without Docker)

#### Project Setup

```sh
npm install
```

#### Compile and Hot-Reload for Development

```sh
npm run dev
```

#### Compile and Minify for Production

```sh
npm run build
```

#### Lint with [ESLint](https://eslint.org/)

```sh
npm run lint
```

## Testing applications

### Users -- /api/users

#### GET all users
```sh
curl -X GET http://localhost:8080/api/users
```

#### GET user by ID
```sh
curl -X GET http://localhost:8080/api/users/1
```

#### POST create user
```sh
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "pseudo": "nemo",
    "email": "nemo@ocean.com",
    "password": "secret",
    "avatar": "nemo.png",
    "theme": "DARK",
    "coins": 100,
    "role": "USER",
    "createdAt": "2026-06-15T10:00:00"
  }'
```

#### DELETE user by ID
```sh
curl -X DELETE http://localhost:8080/api/users/1
```

### Aquariums — /api/aquariums

#### GET all aquariums
```sh
curl -X GET http://localhost:8080/api/aquariums
```

#### GET aquarium by ID
```sh
curl -X GET http://localhost:8080/api/aquariums/1
```

#### POST create aquarium
```sh
curl -X POST http://localhost:8080/api/aquariums \
  -H "Content-Type: application/json" \
  -d '{
    "isPublic": true,
    "level": 1,
    "capacity": 10
  }'
```

#### DELETE aquarium by ID
```sh
curl -X DELETE http://localhost:8080/api/aquariums/1
```

### Fish — /api/fish

#### GET all fish
```sh
curl -X GET http://localhost:8080/api/fish
```

#### GET fish by ID
```sh
curl -X GET http://localhost:8080/api/fish/1
```

#### POST create fish
```sh
curl -X POST http://localhost:8080/api/fish \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Bubbles",
    "species": "Clownfish",
    "color": "orange",
    "size": 5,
    "age": 2,
    "lifePoints": 100,
    "lastFedAt": "2026-06-15T08:00:00"
  }'
```

#### DELETE fish by ID
```sh
curl -X DELETE http://localhost:8080/api/fish/1
```

### Trades — /api/trades

#### GET all trades
```sh
curl -X GET http://localhost:8080/api/trades
```

#### GET trade by ID
```sh
curl -X GET http://localhost:8080/api/trades/1
```

#### POST create trade
```sh
curl -X POST http://localhost:8080/api/trades \
  -H "Content-Type: application/json" \
  -d '{
    "status": "PENDING",
    "createdAt": "2026-06-15T09:00:00",
    "fish": []
  }'
```

#### DELETE trade by ID
```sh
curl -X DELETE http://localhost:8080/api/trades/1
```

### Daily Challenges — /api/challenges

#### GET all challenges
```sh
curl -X GET http://localhost:8080/api/challenges
```

#### GET challenge by ID
```sh
curl -X GET http://localhost:8080/api/challenges/1
```

#### POST create challenge
```sh
curl -X POST http://localhost:8080/api/challenges \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Feed 3 fish",
    "reward": 50,
    "date": "2026-06-15",
    "completed": false
  }'
```

#### DELETE challenge by ID
```sh
curl -X DELETE http://localhost:8080/api/challenges/1
```

### Shop — Food — /api/shop/food

#### GET all food items
```sh
curl -X GET http://localhost:8080/api/shop/food
```

#### GET food by ID
```sh
curl -X GET http://localhost:8080/api/shop/food/1
```

#### POST create food
```sh
curl -X POST http://localhost:8080/api/shop/food \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Premium Pellets",
    "price": 20,
    "nutritionValue": 80
  }'
```

#### DELETE food by ID
```sh
curl -X DELETE http://localhost:8080/api/shop/food/1
```

### Shop — Upgrades — /api/shop/upgrades

#### GET all upgrades
```sh
curl -X GET http://localhost:8080/api/shop/upgrades
```

#### GET upgrade by ID
```sh
curl -X GET http://localhost:8080/api/shop/upgrades/1
```

#### POST create upgrade
```sh
curl -X POST http://localhost:8080/api/shop/upgrades \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Large Tank",
    "price": 200,
    "capacityBonus": 5,
    "levelBonus": 1
  }'
```

#### DELETE upgrade by ID
```sh
curl -X DELETE http://localhost:8080/api/shop/upgrades/1
```

# Tests unitaires & jeu de données — Little Fish

## 1. Jeu de données SQL (`V2__seed_data.sql`)

### Où le placer

```
backend/src/main/resources/db/migration/V2__seed_data.sql
```

Flyway détecte automatiquement les fichiers nommés `V{n}__*.sql`
et les exécute dans l'ordre au démarrage du backend.

### Ce que le fichier insère

| Table                    | Lignes | Détail                                                    |
|--------------------------|--------|-----------------------------------------------------------|
| `users`                  | 6      | 1 admin + 5 users (alice, bob, charlie, diana, eve)       |
| `notification_preference`| 6      | Une préférence par user                                   |
| `aquarium`               | 6      | Un aquarium par user (niveaux et capacités variés)        |
| `food`                   | 5      | Catalogue boutique                                        |
| `aquarium_upgrade`       | 5      | Catalogue boutique                                        |
| `fish`                   | 22     | 8 templates boutique (aquarium_id NULL) + 14 dans des aquariums |
| `friendship`             | 6      | alice↔bob ACCEPTED, alice↔charlie ACCEPTED, charlie→diana PENDING, bob→eve BLOCKED |
| `daily_challenge`        | 8      | Catalogue de défis                                        |
| `daily_challenge_user`   | 11     | Progression du jour pour chaque user                      |
| `trade`                  | 3      | 1 PENDING, 1 ACCEPTED, 1 REJECTED                         |

### Comptes de test

| Pseudo    | Email                      | Mot de passe | Rôle  |
|-----------|----------------------------|--------------|-------|
| `admin`   | admin@littlefish.dev       | admin123     | ADMIN |
| `alice`   | alice@littlefish.dev       | password123  | USER  |
| `bob`     | bob@littlefish.dev         | password123  | USER  |
| `charlie` | charlie@littlefish.dev     | password123  | USER  |
| `diana`   | diana@littlefish.dev       | password123  | USER  |
| `eve`     | eve@littlefish.dev         | password123  | USER  |

> Note : les hashs BCrypt correspondent au mot de passe indiqué ci-dessus
> (coût 10, générés avec `BCryptPasswordEncoder`).

---

## 2. Tests unitaires

### Où placer les fichiers

Tous les fichiers `.java` vont dans le dossier des tests du backend :

```
backend/src/test/java/com/littlefish/app/service/
```

Structure après ajout :

```
backend/src/test/java/com/littlefish/app/
├── AppApplicationTests.java          (existant)
└── service/
    ├── UserServiceTest.java
    ├── FishServiceTest.java
    ├── AquariumServiceTest.java
    ├── ShopServiceTest.java
    ├── TradeServiceTest.java
    ├── FriendshipServiceTest.java
    ├── JwtAndScheduledServiceTest.java
    └── DailyAndNotificationServiceTest.java
```

### Lancer les tests

```bash
cd backend
mvn test
```

Ou pour un seul fichier :

```bash
mvn test -Dtest=UserServiceTest
```

### Ce qui est testé

| Fichier de test                    | Service(s) couverts                     | Cas testés |
|------------------------------------|-----------------------------------------|------------|
| `UserServiceTest`                  | UserService                             | 12         |
| `FishServiceTest`                  | FishService                             | 9          |
| `AquariumServiceTest`              | AquariumService                         | 10         |
| `ShopServiceTest`                  | ShopService                             | 11         |
| `TradeServiceTest`                 | TradeService                            | 9          |
| `FriendshipServiceTest`            | FriendshipService                       | 10         |
| `JwtAndScheduledServiceTest`       | JwtService + ScheduledTaskService       | 12         |
| `DailyAndNotificationServiceTest`  | DailyChallengeService + NotifPrefService| 11         |
| **Total**                          |                                         | **~84**    |

### Dépendances de test utilisées

Toutes incluses dans `spring-boot-starter-test` (déjà dans le `pom.xml`) :
- **JUnit 5** (`@Test`, `@Nested`, `@DisplayName`)
- **Mockito** (`@Mock`, `@InjectMocks`, `when/verify`)
- **AssertJ** (`assertThat(...)`)

Aucune dépendance supplémentaire à ajouter.

### Points de vigilance

- Les tests utilisent `@ExtendWith(MockitoExtension.class)` — pas de Spring context,
  donc très rapides (< 1 sec total).
- `JwtService` est testé sans mock car il n'a aucune dépendance externe.
- Si les records DTOs (`FishCreateDTO`, `FishUpdateDTO`, etc.) changent de signature,
  les constructeurs dans les tests devront être mis à jour en conséquence.
