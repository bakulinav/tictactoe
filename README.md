How to run
-----

Run following command to build a runnable JAR file:

    ./gradlew clean bootJar

First run the host instance with TictactoeApplication main class.
This instance will run the game server on port 8080 with REST-like API

    java -jar build/libs/tictactoe-0.0.1-SNAPSHOT.jar

Then run the same app with `player` Spring profile but on port 8081:

    java -Dspring.profiles.active=player -jar build/libs/tictactoe-0.0.1-SNAPSHOT.jar

How to use
-----

You can use Postman to make requests to host instance.
Files to import a collection and an environment can be found in `forPostman` folder.
