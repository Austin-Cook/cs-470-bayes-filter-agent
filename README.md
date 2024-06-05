# To Compile
Move into the folders `Robot` and `Server` and run `javac *.java` in each.

# To Run
## Server

Go to the `Server` folder and type:

`java BayesWorld [world] [motor_probability] [sensor_probability] [known/unknown]`  

where `world` can be any of the worlds specified in the “Mundo” directory, [motor_probably] is a value between 0 and 1 specifying pm, [sensor_probability] is a value between 0 and 1 specifying ps, and “known” specifies that the robot’s initial position is given to the robot at the start of the simulation, and “unknown” is specified to say that the robot’s initial position is not given to the robot at the start of the simulation. For example:

`java BayesWorld mundo_maze.txt 0.9 0.8 unknown`

starts the server in the world mundo_maze.txt, with pm= 0.9, ps= 0.8, and the robot’s initial position is unknown. 

To provide a fully observable environment to the client, run the server with:

`java BayesWorld mundo_maze.txt 1.0 1.0 known`.

Note: When running the client in `manual` mode, you should always set the last parameter to “unknown.”

## Client

Once the server is running, you can connect the robot (client) to it. In a separate terminal, go to the `Robot` folder. The general command is:

`java theRobot [manual/automatic] [decisionDelay]`

### (a) Manual Mode

In manual mode, the user (you) will specify the robot’s actions by pressing keys to have the robot move when the client GUI window is active. `i` is up, `,` is down, `j` is left, `l` is right, and `k` is stay. Note that the client GUI must be the active window in order for the key commands to work.

For manual mode, simply specify 0 for decisionDelay because it is only applicable in automatic mode. Run

`java theRobot manual 0`

to run the program.

### (b) Automatic Mode

In automatic mode, the robot will control it’s own actions, and [decisionDelay] is a time in milliseconds used to slow down the robot’s movements when it chooses automatically (so you can see it move).

For automatic mode, run the following command:

`java theRobot automatic 1000`

to run the program.

# Notes

## Bayes Filter

Both the client's `manual` and `automatic` modes use a Bayes Filter. This uses a `transition model` (knowledge of which direction the robot attempted to move in as well as motor inaccuracy) as well as a `sensor model` (current sensor input of surrounding walls, a map of the static world, and knowledge of sensor inaccuracy) to determine its likelyhood of being in any given location.

On the client grid, each square is highlighted by probability of the robot being there. A darker blue highlight means the robot is more likely to be in a square.

## Value Iteration

In the client's `automatic` mode, move decisions are made using a value iteration algorithm. This algorithm aims to maximize reward. Reaching the goal is worth a lot, falling in a stairwell is a negative reward, and staying in the same place is slightly harmful.

A gamma value is used, reducing the reward for the robot staying in one place for too long.