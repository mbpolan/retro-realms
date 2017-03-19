# Retro Realms

This is a simple project that makes use of WebGL and WebSockets.

I was inspired by some pretty cool examples of people creating interactive games and multimedia content
with the help of these two technologies, so I figured that the time has come for me to learn it.

This repo is basically a rolling "log" of my progress. There exists a `legacy-1.x` branch that contains my initial
attempt at this type of project. After a brief hiatus, I decided to rewrite it from scratch to give myself a better
codebase to work with. The first design had some fundamental design flaws that were hard to fix without introducing
bugs, hence this new attempt.

## Project
You will find here a Gradle project that has a frontend written in AngularJS 2.x and PIXI.JS, and a backend
written in Java using the Spring framework. The code is for a very watered-down multiplayer RPG with some
basic controls, like chatting and server-side collision detection. It uses the STOMP protocol to deliver messages
over a web socket between clients and the server. I borrowed sprites and tiles from RPG Maker resources to
give the demo some color - a big thanks to [this project](https://vxresource.wordpress.com) for
making them available to everyone!


## Building
You can start the project up right away by first cloning this repository, and then running the following command
in the root directory on Linux:

`gradle bootRun`

Or on Windows:

`gradlew.bat bootRun`

Then open http://localhost:8080 in your favorite browser, preferably Chrome or Firefox.

## TODO List
Here are some things that I still plan on trying to implement:

- Multiple map areas, instead of just the one map block today
- Slightly more intelligent collision detection and proper layering of sprites
- NPCs and other computer-controlled entities
- Customizing player sprites
- Mouse interactions (moving, right-clicking, etc.)
- Items and player inventory
