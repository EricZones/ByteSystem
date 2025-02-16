# ByteSystem
___

ByteSystem is a Teamspeak Query which administrates your Teamspeak server. It is fully configurable with database integration and allows running multiple instances simultaneously.

## Features
___
- **Channel:**
    - Automatically create channels for users by joining a predefined channel
    - Integrated spam protection for channel creation
    - Assigning predefined channel admin group to creator
    - Configurable dynamic channel name
- **Support:**
    - Notify all members of a configured support when user join a predefined channel
    - Opening and closing the support channel by messaging the query
    - Automation of opening/closing depending on online support members
    - Configurable channel name and channel max clients for open and closed state
- **Moderation:**
    - Automatic administration of users for enabled options
    - **NameCheck:** Check if name contains spaces or configured forbidden words
    - **SwitchCheck:** Check if user switches channels too fast (Channelhopping)
    - Ignored groups can be added to be skipped by the checks
    - Users are kicked after configured amount of complaints or instant
- **Punish:**
    - Addition for **Moderation** to automatically ban users
    - Configurable ban duration and complaints limit
- **Others:**
    - Configurable bot admins to edit options by contacting the query
    - List channel ids for channels
    - Check information about users by nickname even if they are offline
    - Display the onlinetime of users and top 5 onlinetimes
    - Let the query send a message to a specific user
    - Stop the bot or disconnect the query with commands
- **Logging:**
    - Logging of server events and feature actions in local files

## Installation & Execution
___
### Requirements
- Java Development Kit (JDK) 8 or higher
- Apache Maven

### Execution
```bash
  mvn package
  
  cd target
  
  java -jar ByteSystem-1.0-SNAPSHOT.jar
  ```

### Configuration
1. After first launch edit 'config.properties' with database credentials (Do not change ID)
2. After second launch edit the 'botconfigs' table in the database by filling in the Teamspeak server login information (Port can be null if not necessary)
3. The configuration can be changed in the console and/or by messaging the query on the server later on
4. To add yourself as bot admin you need to do this in the console

## Controls
___
| Command | Result                          |
|---------|---------------------------------|
| !help   | List of all available commands  |

- Commands can be executed by sending direct messages to the query on the Teamspeak server
- Some commands available in the console as well

## Contributors
___
- **EricZones** - Developer

## Purpose
___
The query was created to have a better and automated experience administrating Teamspeak servers.
It was originally developed in 2021 without git.