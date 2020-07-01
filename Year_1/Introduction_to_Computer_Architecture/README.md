# Galaxy_Of_Pulsars

This project was part of an Introduction to the Architecture of Computers course at Instituto Superior Técnico (2018-2019) to understand how a computer works from the ground up; from hardware to software.

The focus of this project was to have a ship which is able to toggle its shield on and off traverse the pixel screen catching good rays from pulsars and avoiding bad rays. For a more extensive explanation of the project and its requirements see /Documentation/project_description_and_guidelines.pdf. Final thoughts and a general overview on how the game functions can be found in /Documentation/project_final_thoughts.pdf.

**NOTE - The assembly in this project was run on a proprietary processor simulator "PEPE-16" used at Instituto Superior Técnico (Tagus-Park), for more information on the processor and its architecture see /Documentation/PEPE_manual.pdf for its manual and /Documentation/most_used_instructions_PEPE.pdf for its most used instructions.**

## Setup

**IMPORTANT! ALL FILES NEED TO BE IN THE SAME DIRECTORY, THIS MEANS THAT YOU NEED TO EXTRACT ALL SCENARIO AND SOUND FILES AND PUT THEM IN THE SAME DIRECTORY AS THE "Circuit.cir" FILE. FAILURE TO DO SO WILL RUN THE GAME WITHOUT SOUNDS AND SCENARIOS.**

To set the game environment up, we first need to open the simulator_PEPE.jar file found in the base directory. Once opened, you should be presented with the following screen: ![Opened_Simulator](https://github.com/PaulArandjelovic/GalaxyOfPulsars_IAC/blob/master/README_pictures/simulator_opened.PNG)

Click File -> Load -> Circuit.cir
(Circuit.cir is found in the base directory of this repository)

You will be presented with this screen: ![Loaded_Simulator](https://github.com/PaulArandjelovic/GalaxyOfPulsars_IAC/blob/master/README_pictures/simulator_loaded.PNG)

In the top left corner, there are two tabs: "Design" and "Simulation", click on the "Simulation" tab and you should be presented with the following screen: ![Simulation_Screen](https://github.com/PaulArandjelovic/GalaxyOfPulsars_IAC/blob/master/README_pictures/simulation_screen.PNG)

Press the "Play" button, after pressing this button, you will be presented with the start screen and the game is now playing (See /Documentation/project_final_thoughts.pdf for a quickstart on how to play the game or continue reading).
You will be presented with this screen after pressing the "Play" button: ![Play_Screen](https://github.com/PaulArandjelovic/GalaxyOfPulsars_IAC/blob/master/README_pictures/play_screen.PNG)

## Basic Rules

* If the ship comes into contact with a good ray and its shield is off, the ship's energy is increased. If the shield is on, there is no change.
* If the ship comes into contact with a bad ray and its shield is off, the game ends and the scenario is changed to Game Over Energy. If the shield is on, the energy of the ship decreases.
* Extra functionality was added to this game which is "emergency energy", with this, a player can use a one time "power-up" to help him get out of a difficult situation.

## Controls

0 - Move ship North-West <br/>
1 - Move ship North <br/>
2 - Move ship North-East <br/>
3 - Emergency Energy <br/>
<br/>
4 - Move ship West <br/>
5 - Toggle Shield <br/>
6 - Move ship East <br/>
7 - NO EFFECT <br/>
<br/>
8 - Move ship South-West <br/>
9 - Move ship South <br/>
A - Move ship South-East <br/>
B - NO EFFECT <br/>
<br/>
C - Start game <br/>
D - Pause game <br/>
E - End game  <br/>
F - NO EFFECT <br/>

