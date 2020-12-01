# SpaceIvdx

SpaceIvdx is a toy project to test various software design ideas and
development approaches.

## Compile
### Requirements
- mvn >= 3.6.3
- Javac >= 15.0.1

### Commands
```bash
mvn clean package
```

## Run
### Commands
From the root project directory
```bash
cd target
java -jar --enable-preview multiboard-1.0-SNAPSHOT.jar
```

## Playing the game
### SpaceIvdx

The game will continue to play forever. Use those commands to interact:
- `esc`: Escape the game
- `left arrow`: move ship left
- `right arrow`: move ship right
- `space bar`: shot a missile

![Alt text](space-ivdx.png?raw=true "SpaceIvdx screen shot")
