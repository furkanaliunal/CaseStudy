# CaseStudy

[![wakatime](https://wakatime.com/badge/user/96162fe4-764c-435f-8a96-8e183553f72d/project/37d1a3b4-0737-4f82-9275-d9584dd8dc6d.svg)](https://wakatime.com/badge/user/96162fe4-764c-435f-8a96-8e183553f72d/project/37d1a3b4-0737-4f82-9275-d9584dd8dc6d)

<br>

This repository is a simple test case about how I use:
- VelocityAPI
- SpigotAPI
- MongoDB
- Redis
- Yaml
- Maven
- OOP
- JavaDocs

techs.

## Here is the idea of work:
> • Player will be able to teleport each other using /tpa command. 
> <br><br>• They can also check their history using /tpa list
> <br><br>• When a player types command /tpa it will see available players as suggestion to the command
> <br><br>• When a player executes command /tpa list, it will see it's own teleport history and teleport history will be saved in MongoDB
> <br><br>• It will use redis to get target player's location and then teleport the player to the target location

Features:
- Teleport request across servers in proxy
- Detailed teleport history
- Teleport accept and reject commands
- Timeout for teleport requests
- Countdown to block spam on teleport requests
- Restricted servers to prevent staff from abuse
- Totally configurable messages
- Beautiful messages

Can be done:
- Teleportation throttler with some animations
- Sound notifications on request received