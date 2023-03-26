# Stocks

Repository consists of 3 main parts:

- `server/stock` - stock market service which can be packed into docker image
  using `./gradlew :server:stock:prepareStock` task
- `server/application` - backend of the application that hosts users and replays to their answers
- `client` - cli client that talks to `server/application`

## Tests

Integration test done with `testcontainers` inside `TestUserService`. It packs fresh `server/stock` docker image before
executing test and runs it and `postgres`, then checks if everything works as expected. 

## Postgres

Both `server/stock` and `server/application` use same db just make things easier
