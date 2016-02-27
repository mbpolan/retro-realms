#WebGL and WebSockets
This is a simple project that makes use of WebGL and WebSockets.

I was inspired by some pretty cool examples of people creating interactive games and multimedia content
with the help of these two technologies, so I figured that the time has come for me to learn it. For an
extra challenge, I tried my hand at learning Scala for the server-side code.

This repo is basically a rolling "log" of my progress.

##Project
You will find here a Gradle project that has a frontend written in AngularJS 1.x and PIXI.JS, and a backend
written in Scala using the Spring framework. The code is for a very watered-down multiplayer RPG with some
basic controls, like chatting and server-side collision detection. It uses the STOMP protocol to deliver messages
over a web socket between clients and the server. I borrowed sprites and tiles from my favorite SNES games to
give the demo some color - a big thanks to [this project](https://github.com/christopho/solarus-alttp-pack) for
making them available to everyone!


##Building
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
