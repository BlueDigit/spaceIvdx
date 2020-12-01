# Multiboard

Multiboard is a toy project to test various software design ideas and
development approaches.

The main concept is to provide a generic approach for two d. games. The
engine is made for old-school style games and does not pretend to be more
performant than that.

For now, the project offer only one game: a Space Invader implementation
named SpaceIvdx.

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
