;/////////////////////////////////////////////////////////////////////////////////
;/ 										//
;/ 			FINAL VERSION -- GALAXIA DE PULSARES			//
;/ 										//
;/ 			GROUP 37						//
;/										//
;/ 			Name:			Number:				//
;/ 			Pavle Arandjelovic 	93745				//
;/ 			German Voronin		95481				//
;/										//
;/////////////////////////////////////////////////////////////////////////////////

;-------------Peripheral Addresses-----------
;--------Pixel Screen Command Addresses------
DEFINE_RED			EQU 6000H			;address of red component of pixel
DEFINE_GREEN			EQU 6002H			;address of green component of pixel
DEFINE_BLUE			EQU 6004H			;address of blue component of pixel
DEFINE_ALPHA			EQU 6006H 			;address of alpha component of pixel
DEFINE_PIXEL			EQU 6008H			;address of command to alter pixel weight
CLEAR_SCREEN			EQU 6010H			;address for clearance of pixel screen
DEFINE_LINE			EQU 600AH			;address for command to alter line
DEFINE_COLUMN			EQU 600CH			;address for command to alter column
SCENARIO_SELECT			EQU 600EH			;address to select scenarios
;-------------------////---------------------

;---------------Sound Addresses--------------
SOUND_SELECT			EQU 6012H			;address to select sounds
;-------------------////---------------------

; -------------Displays Addresses------------
DISPLAYS			EQU 0A000H			;address of the 7 segment display (peripheral POUT-1)
;-------------------////---------------------

;-------------Keyboard Addresses-------------
KEYBOARD_ROWS			EQU 0C000H			;address of the rows of the keyboard (peripheral POUT-2)
KEYBOARD_COLUMNS		EQU 0E000H			;address of the columns of the keyboard (peripheral PIN)
;-------------------////---------------------

;----------Ship and Ray Boundaries-----------
MAX_X					EQU 63			;pixel screen boundary for x
MAX_Y					EQU 31			;pixel screen boundary for y

MIN_SHIP_X				EQU 3
MIN_SHIP_Y				EQU 3
MAX_SHIP_X				EQU 56
MAX_SHIP_Y				EQU 24

MIN_RAY_X				EQU 3
MAX_RAY_X				EQU 60
MIN_RAY_Y				EQU 3
MAX_RAY_Y				EQU 28
;-------------------////---------------------

;-----------Ship Start Coordinates-----------
X_SHIP_START			EQU 30
Y_SHIP_START			EQU 15
;-------------------////---------------------

;-------Coordinates For All 4 Pulsars--------
X_TOP_LEFT_PULSAR		EQU 0
Y_TOP_LEFT_PULSAR		EQU 0

X_TOP_RIGHT_PULSAR		EQU 61
Y_TOP_RIGHT_PULSAR		EQU 0

X_BOTTOM_LEFT_PULSAR		EQU 0
Y_BOTTOM_LEFT_PULSAR		EQU 29

X_BOTTOM_RIGHT_PULSAR		EQU 61
Y_BOTTOM_RIGHT_PULSAR		EQU 29
;-------------------////---------------------

;--------------------------------------------
;- 	  		  Pulsar Start States
;-
;- >>0 - good opaque 
;- >>1 - good translucent
;- >>2 - bad opaque
;- >>3 - bad translucent
;-------------------////---------------------
TL_PULSAR_START_STATE	EQU 2
TR_PULSAR_START_STATE	EQU 1
BL_PULSAR_START_STATE	EQU 3
BR_PULSAR_START_STATE	EQU 2

;-------------Game Sound Values--------------
START_GAME		EQU 0
END_GAME		EQU 1
END_GAME_NO_ENERGY	EQU 2
GAME_PAUSE		EQU 3
ADD_ENERGY		EQU 4
BAD_RAY_SHIELD_ON	EQU 5
SHIP_SHIELD		EQU 6
SHIP_EMERGENCY_ENERGY	EQU 7
;-------------------////---------------------

;---Potential Keys Pressed(exluding pause)---
EMERGENCY_ENERGY_KEY	EQU 3
START_GAME_KEY		EQU 0CH
END_GAME_KEY		EQU 0EH
PAUSE_KEY		EQU 0DH
;-------------------////---------------------

;------------Game Scenario Values------------
GAME_PAUS_SCEN			EQU 0
GAME_RUN_SCEN			EQU 1
START_GAME_SCEN			EQU 2
END_EXIT_SCEN			EQU 3
END_NO_ENERGY_SCEN		EQU 4
;-------------------////---------------------

MAX_SHIP_ENERGY			EQU 100			;max energy value = 100


PLACE       			1000H

keyValue:			WORD -1			;initialized to "no key"
shieldSoundState:		WORD 0			;0 = off, 1 = on
emergencyEnergy:		WORD 1			;0 = off, 1 = on
gameState:			WORD 1
;--------------------------------------------
;-1 = game end key pressed
; 0 = game paused
; 1 = game running
; 2 = game ended, out of energy
;-------------------////---------------------

pauseState:			WORD 0			;0 = unpaused, 1 = paused
generatorValue:			WORD 0

;----------Ship Position and Shield----------
xCoordShip:			WORD X_SHIP_START	;initial x coordinate for ship
yCoordShip:			WORD Y_SHIP_START	;initial y coordinate for ship

shieldState:			WORD 0			;0 = off, 1 = on <------- key "5" to activate
enableShieldToggle:		WORD 1			;0 = off, 1 = on
energyValue:			WORD MAX_SHIP_ENERGY	;initial energy value
clearShip:			WORD 0			;0 = off, 1 = on
xCoordShipToClear:		WORD 0				
yCoordShipToClear:		WORD 0
;-------------------////---------------------

;----------------Pulsar States---------------
stateTopLeftPulsar:		WORD TL_PULSAR_START_STATE
stateTopRightPulsar:		WORD TR_PULSAR_START_STATE
stateBottomLeftPulsar:		WORD BL_PULSAR_START_STATE
stateBottomRightPulsar:		WORD BR_PULSAR_START_STATE
;-------------------////---------------------

;---------Ray Coordinates and States---------
xTopLeftRay:			WORD MIN_RAY_X
yTopLeftRay:			WORD MIN_RAY_Y
stateTLRay:				WORD 1

xTopRightRay:			WORD MAX_RAY_X
yTopRightRay:			WORD MIN_RAY_Y
stateTRRay:				WORD 3

xBottomLeftRay:			WORD MIN_RAY_X
yBottomLeftRay:			WORD MAX_RAY_Y
stateBLRay:				WORD 2

xBottomRightRay:		WORD MAX_RAY_X
yBottomRightRay:		WORD MAX_RAY_Y
stateBRRay:				WORD 0
;-------------------////---------------------

;-----------Pulsar Positions(X, Y)-----------
pulsarPositions:
	STRING X_TOP_LEFT_PULSAR, Y_TOP_LEFT_PULSAR
	STRING X_TOP_RIGHT_PULSAR, Y_TOP_RIGHT_PULSAR
	STRING X_BOTTOM_LEFT_PULSAR, Y_BOTTOM_LEFT_PULSAR
	STRING X_BOTTOM_RIGHT_PULSAR, Y_BOTTOM_RIGHT_PULSAR
;-------------------////---------------------

;----Stack and Interrupt Initializations-----
stack:      			TABLE 100H     		;space reserved for the stack
initialSP: 						;(200H bytes, are equal to 100H words)

tableBTE:			WORD pulsarInt0    	;routine for pulsar interrupt (0)
				WORD rayInt1		;routine for pulsar rays interrupt (1)
				WORD energyInt2		;routine for ship energy interrupt (2)

pulsarIntState0:		WORD 0
rayIntState1:			WORD 0
energyIntState2:		WORD 0
;-------------------////---------------------

;********************************************************************************
; OBJECT TABLES
;
; ->Dimensions of table to draw
; ->(Red, Green, Blue, Alpha) content in all pixels in table
; ->table to draw
;********************************************************************************

shipWithShieldEnergy:
	STRING 5,5
	STRING 255,255,0,255
	STRING 2,2,2,2,2
	STRING 2,1,0,1,2
	STRING 2,0,1,0,2
	STRING 2,1,0,1,2
	STRING 2,2,2,2,2

shipWithoutShieldEnergy:
	STRING 5,5
	STRING 255,255,0,255
	STRING 0,0,0,0,0
	STRING 0,1,0,1,0
	STRING 0,0,1,0,0
	STRING 0,1,0,1,0
	STRING 0,0,0,0,0
	
shipWithoutShield:
	STRING 5,5
	STRING 0,140,255,255
	STRING 0,0,0,0,0
	STRING 0,1,0,1,0
	STRING 0,0,1,0,0
	STRING 0,1,0,1,0
	STRING 0,0,0,0,0

shipWithShield:
	STRING 5,5
	STRING 0,140,255,255
	STRING 2,2,2,2,2
	STRING 2,1,0,1,2
	STRING 2,0,1,0,2
	STRING 2,1,0,1,2
	STRING 2,2,2,2,2
	
shipClearanceSchematic:
	STRING 5,5
	STRING 255,255,0,255
	STRING 0,0,0,0,0
	STRING 0,0,0,0,0
	STRING 0,0,0,0,0
	STRING 0,0,0,0,0
	STRING 0,0,0,0,0
	
goodPulsar:
	STRING 3,3
	STRING 0,255,0,255
	STRING 0,1,0
	STRING 1,1,1
	STRING 0,1,0

goodPulsarTrans:
	STRING 3,3
	STRING 0,255,0,122
	STRING 0,1,0
	STRING 1,1,1
	STRING 0,1,0
	
badPulsar:
	STRING 3,3
	STRING 255,0,0,255
	STRING 1,1,1
	STRING 1,1,1
	STRING 1,1,1
	
badPulsarTrans:
	STRING 3,3
	STRING 255,0,0,122
	STRING 1,1,1
	STRING 1,1,1
	STRING 1,1,1
	
goodRay:
	STRING 0,255,0,255
badRay:
	STRING 255,0,0,255

;********************************************************************************
; START SCREEN
;********************************************************************************

PLACE 0000H
	MOV  BTE, tableBTE		  		;initialize Base da Tabela de Excecoes
	MOV  SP, initialSP				;initialize stack pointer
	CALL clearScreen				;cleans the pixelscreen and initializes all pixels 	
	MOV  R1, START_GAME_SCEN			;holds select for start scenario to be selected
	CALL selectScenario				;accepts argument in R1
	
	EI0  						;enable interrupt 0 -> pulsar interrupt
	EI1						;enable interrupt 1 -> ray interrupt
	EI2						;enable interrupt 2 -> energy interrupt
	EI						;enable use of interrupts
	
startScreen:						;loop, waits until correct key has been pressed to start game
	CALL numberGenerator				;start of game is different every time
	CALL keyboard					;get pressed key value
	CALL getCurrentKeyValue				;current key value stored in R0
	MOV  R1, START_GAME_KEY				;desired key required to enter the game (key set to "c")
	CMP  R0, R1					;we verify to see if pressed key is equal to desired key to start game
		JNZ  startScreen			;start key not pressed, restart loop		

;********************************************************************************
; GAME INITIALIZATIONS
;********************************************************************************

;----Initialize gameState, emergencyEnergy----
	MOV  R2, 1					;register used to set states to 1 (running)
	MOV  R1, gameState				;copy address of gameState to R1
	MOV  [R1], R2					;set gameState to 1 (running)
	MOV  R1, emergencyEnergy			;copy address of emergencyEnergy to R1
	MOV  [R1], R2					;set emergencyEnergy state to 1
	MOV  R1, enableShieldToggle			;copy address of enableShieldToggle to R1
	MOV  [R1], R2					;set enableShieldToggle to 1
	MOV  R2, 0					;register used to set shieldState to 0
	MOV  R1, shieldState				;copy address of shieldState to R1
	MOV  [R1], R2					;set shieldState to 0
;-------------------////----------------------

;----------Initialize Ray Positions-----------
	CALL initAllPulsars
;-------------------////----------------------

;-----Initialize Ray States and Positions-----
	MOV  R0, xTopLeftRay				;copy address of x coordinate of top left ray
	CALL initTopLeftRay				;init top left ray coordinates and state
	MOV  R0, xTopRightRay				;copy address of x coordinate of top right ray
	CALL initTopRightRay				;init top right ray coordinates and state
	MOV  R0, xBottomLeftRay				;copy address of x coordinate of bottom left ray
	CALL initBottomLeftRay				;init bottom left ray coordinates and state
	MOV  R0, xBottomRightRay			;copy address of x cordinate of bottom right ray
	CALL initBottomRightRay				;init bottom right ray coordinates and state
;-------------------////----------------------

;----------Initialize Ship Position-----------
	MOV  R0, xCoordShip				;copy address of x coordinate of ship to R0
	MOV  R1, X_SHIP_START				;move value of X_SHIP_START to R1 (30)
	MOV  [R0], R1					;set ship x value
	
	MOV  R0, yCoordShip				;copy address of y coordinate of ship to R1			
	MOV  R1, Y_SHIP_START				;move value of Y_SHIP_START to R1 (15)
	MOV  [R0], R1					;set ship y value
;-------------------////----------------------

;-----------Initialize Ship Energy------------
	MOV  R1, MAX_SHIP_ENERGY			;move (100) to R1
	MOV  R2, energyValue				;copy address of energyValue to R2
	MOV  [R2], R1					;set energyValue to max energy value (100) 
	
	MOV  R1, GAME_RUN_SCEN				;select game running scenario and put on pixel screen
	CALL selectScenario				;accepts argument in R1
	MOV  R1, START_GAME				;move start game sound to R1
	CALL selectSound				;play start game sound
;-------------------////----------------------
	
;********************************************************************************
; GAME LOOP
;********************************************************************************

gameLoop:						;main loop where game is run
	CALL keyboard					;accepts commands from the keyboard
	CALL runConditions				;see if game ends/paused

;----Game Ended/Paused Conditions----
	MOV  R0, gameState				;address of gameState copied to R0
	MOV  R0, [R0]					;value of gameState copied to R0
	CMP  R0, -1 					;check if game has ended
		JZ   preEndGameLoop			;game has ended, stays in infinite loop until game re-commences
	CMP  R0, 2					;check if game has ended (ship out of energy)
		JZ   outOfEnergyEndGame			;game ended, no energy
	CMP  R0, 0					;check if game is paused
		JZ   gameLoop				;game paused, loop first two routines until game ended or unpaused
;----------------////----------------

	CALL shipState					;update ship position
	CALL pulsarsStates				;update pulsar states
	CALL rayStates					;update ray states
	CALL numberGenerator				;generate pseudo-random number
	CALL changeDisplayValue				;display counter 0-100
	JMP  gameLoop					;restarts the loop

outOfEnergyEndGame:
	CALL clearScreen				;clear screen to prepare placement of end game energy scenario
	MOV  R1, END_NO_ENERGY_SCEN			;select scenario to be used for placement
	CALL selectScenario				;select end game energy scenario
	MOV  R1, END_GAME_NO_ENERGY			;select end game energy sound
	CALL selectSound				;play end game energy sound
	JMP  endGameLoop
preEndGameLoop:
	CALL clearScreen				;clear screen to prepare placement of end game scenario
	MOV  R1, END_EXIT_SCEN				;select scenario to be used for placement
	CALL selectScenario				;select end scenario
	MOV  R1, END_GAME				;select sound used to play end game sound
	CALL selectSound				;play end game sound
endGameLoop:
	JMP  startScreen

;********************************************************************************
; END/PAUSE GAME CONDITIONS
;
; Verify if  game has changed state - Paused/Ended
; IN  -> NONE
; RET -> NONE
;********************************************************************************

runConditions:
	PUSH R0
	PUSH R1
	PUSH R2
	PUSH R3

	CALL getCurrentKeyValue				;current key value copied to R0
	MOV  R3, pauseState				;address of pauseState copied to R3
	MOV  R2, [R3]					;value of pauseState copied into R2

;------------Pause Condition-------------
	CMP  R2, 1					;verify if game is paused
		JZ   changePause
	MOV  R1, PAUSE_KEY				;hexadecimal value of key "D" to R1
	CMP  R0, R1							;check if pause key pressed ("D")
		JNZ  endGameCondition			;if pause key not pressed, check if game needs to end
	
	CALL getScenario				;current scenario saved in R1
	MOV  R2, 1					;register used for xor operation
	XOR  R1, R2					;current scenario saved in R1
	CALL selectScenario				;toggle pause scenario to game scenario and vice versa
	
	MOV  R0, gameState				;gameState address copied to R0
	MOV  R2, [R0]					;value of pause state in R2	
	MOV  R1, 1					;used for XOR operation
	XOR  R1, R2					;toggle game state 0->1, 1->0
	MOV  [R0], R1					;toggled game state saved gameState variable
	MOV  R1, [R3]					;value of pauseState copied to R1
	MOV  R2, 1					;used for XOR operation
	XOR  R1, R2					;toggle pause state 0->1, 1->0
	MOV  [R3], R1					;update pauseState in memory
	MOV  R1, GAME_PAUSE				;move pause sound to R1
	CALL selectSound				;play pause sound
	JMP  endConditions				;check end conditions
	
changePause:						;check if pause key has been continually pressed
	MOV  R1, 13					;move pause key "D" to R1
	CMP  R0, R1					;if key is still pressed, game remains paused
		JZ   endGameCondition			;check end conditions
	MOV  R2, 0					;key not pressed anymore, pause can now be toggled
	MOV  [R3], R2					;update pauseState
;------------------////------------------

;-------------End Condition--------------
endGameCondition:
	CALL getCurrentKeyValue				;copy current key value to R0
	MOV  R1, END_GAME_KEY				;value of required key saved in R1 (key set to "E")
	CMP  R0, R1					;verify if key to end game has been pressed
		JNZ  endConditions			;game ends if "end key" has been pressed
	MOV  R0, gameState		 		;address of gameState copied to R0
	MOV  R1, -1					;"-1" for game state is the end state for game
	MOV  [R0], R1					;update game state to "-1", game has ended

endConditions:
	POP R3
	POP R2
	POP R1
	POP R0
	
	RET	
;------------------////------------------
	
	
;********************************************************************************
; DISPLAYS
;
; Updates values on 3 7-segment displays
; IN  -> NONE
; RET -> NONE
;
; R0 - value to be updated to displays
; R1 - energy value address and constant divisor
; R2 - hold energy value and number to in hexadecimal (0AH) for modulus operations
; R3 - copy of energy value
; R4 - digit counter
; R5 - nibble shifter
; R6 - modulus constant
;********************************************************************************	
	
changeDisplayValue:
	PUSH R0
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4
	PUSH R5
	PUSH R6
	
	MOV R0, energyIntState2				;copy energy interrupt address to R0
	MOV R1, [R0]					;copy energy interrupt value to R0
	CMP R1, 0					;if interrupt off, do not run this routine 
		JZ  endDisplays				;end routine
	MOV R1, 0					;turn interrupt off and run routine
	MOV [R0], R1
	
	MOV R1, energyValue				;move address of energy value to R1
	MOV R2, [R1]					;move energy value to R2

	MOV R0, 0					;register used to join number values
	MOV R1, 1    					;register used for division (splitting the number)
	MOV R5, 1					;use to shift the number x nibbles
	MOV R6, 10					;used to extract unit digit from number
	MOV R4, 0					;digit counter
convertDecimal:						;convertDecimal for decimal conversion
	MOV R3, R2					;copy energyValue to R3
	DIV R3, R1					;divide by 10 to get 10s and 100s digit
	MUL R1, R6					;prepare R1 to divide number in next pass for next digit extraction
	MOD R3, R6   					;to obtain unit digit
	MUL R3, R5					;pass tens digit to next nibble	
	OR  R0, R3   					;copy unit digit to R0
	SHL R5, 4					;move to next nibble
	ADD R4, 1					;increment digit counter
	CMP R4, 3					;number of digits in a number (100 - 3 digits)
		JNZ convertDecimal
		
;-------------Update Displays------------
	MOV R3, DISPLAYS  				;address of displays peripheral
	MOV [R3], R0					;show energyValue on displays
;------------------////------------------

decrement:
	CMP  R2, 0					;see if value on displays = 0
		JZ   noEnergy				;if true, end game
	MOV  R3, shieldState				;copy address of shieldState to R3
	MOV  R3, [R3]					;copy value of shieldState to R3
	CMP  R3, 1					;see if shield is on
		JZ   shieldOn						
	MOV  R3, 5					;shield off, we drain 5 energy every 3 secs
	JMP  decrementEnergy
shieldOn:
	MOV  R3, 10					;shield on, we drain 10 energy every 3 secs
	CMP  R2, R3					;see if current energy < 10, to avoid having negative energy
		JGE  decrementEnergy			
	MOV  R2, 0					;current energy < 10, set energy to 0
	JMP  updateEnergyValue
decrementEnergy:
	SUB  R2, R3					;subtract 1 from current value on displays

updateEnergyValue:					;save energy value in memory
	MOV  R1, energyValue
	MOV  [R1], R2					;update energyValue
	JMP endDisplays
	
noEnergy:
	MOV  R0, gameState				;copy gameState address to R0
	MOV  R1, 2							
	MOV  [R0], R1					;set gameState to 2 (game over, ran out of energy)
	
endDisplays:
	POP R6
	POP R5
	POP R4
	POP R3
	POP R2
	POP R1
	POP R0
	RET
	
clearDisplays:
	PUSH R0
	PUSH R1
	
	MOV  R0, DISPLAYS				;address of the peripheral of the displays
	MOV  R1, 0					;value to be placed in displays
	MOV  [R0], R1					;initialize both displays to 0
	
	POP R1
	POP R0
	RET
	
;********************************************************************************
; KEYBOARD
;
; Check if a key has been pressed and react accordingly
; IN  -> NONE
; RET -> NONE
;********************************************************************************	
	
keyboard:
	PUSH R0				
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R5
	PUSH R6

	MOV  R1, 8					;line we start reading from (line 4)
	MOV  R2, KEYBOARD_ROWS				;address of the peripheral for lines
	MOV  R3, KEYBOARD_COLUMNS  			;address of the peripheral for columns
	
lineSweep:          					;this loop sweeps all lines to see if any key pressed
	MOVB [R2], R1    				;write to exit peripheral (lines)
	MOVB R0, [R3]      				;read from entrance peripheral (columns)
	CMP  R0, 0         				;verify if a key is pressed
		JNZ  keyPressed    			;if key pressed, start sweeping columns
	SHR  R1, 1		  			;test next line
		JZ   noKey				;all lines checked; no key pressed
	JMP  lineSweep	  				;restart loop

keyPressed:   
	MOV  R5, -1       				;R5 is a counter for how many SHR we will do
convertLineToDecimal:  					;convert lines (1, 2, 4, 8) to (0, 1, 2, 3)
	SHR  R1, 1      					
	ADD  R5, 1     					;one more shift right completed
	CMP  R1, 0      				;check if end of loop reached
		JNZ  convertLineToDecimal		;continue until R1 is 0

	MOV  R6, -1					;R6 is a counter for how many SHR we will do
convertColumnToDecimal:	  				;convert columns (1, 2, 4, 8) to (0, 1, 2, 3)
	SHR  R0, 1		
	ADD  R6, 1     					;one more shift right completed	
	CMP  R0, 0	 				;check if end of loop reached
		JNZ convertColumnToDecimal		;continue until R0 is 0

	SHL  R5,2  					;calculate to decimal 4*line + column to get key
	ADD  R5, R6
	JMP  updateKeyValueEnd				;update key value in memory
	
noKey:							;if no key pressed, save -1
	MOV  R5, -1
	
updateKeyValueEnd:	
	MOV  R0, keyValue				;memory address of keyValue copied to R0
	MOV  [R0], R5					;update new key value in memory
	
	POP R6							
	POP R5
	POP R3
	POP R2
	POP R1
	POP R0	
	RET	
	
	
; IN  -> NONE
; OUT -> CurrentKeyValue in R0
getCurrentKeyValue:
	MOV  R0, keyValue				;saves the address of the value of the current pressed key (if no key pressed, defaults to -1)
	MOV  R0, [R0]					;copy value of pressed key to R0
	
	RET

;********************************************************************************
; SHIP STATE
;
; update ship coordinates, update ship's state (shield), emergency energy etc
; IN  -> NONE
; RET -> NONE
;
; R0 - ship state (shield on/off)
; R1 - y coordinate of ship
; R2 - x coordinate of ship
; R3 - ship table schematic
; R4 - shield state/ clear ship variables
;********************************************************************************

shipState:
	PUSH R0
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4
	
	
	CALL getCurrentKeyValue				;copy current value of keyValue to R0	
	CALL moveShip					;updates ship coordinates after movement
	
;------------Emergency Energy----------
	MOV R1, EMERGENCY_ENERGY_KEY			;move 3 to R1 to prepare for comparison
	CMP R0, R1					;check if current key pressed == "3"
		JNZ shipStateShield

useEmergencyEnergy:
	MOV  R1, emergencyEnergy			;copy address of emergencyEnergy to R1
	MOV  R2, [R1]					;copy value of emergencyEnergy to R2
	CMP  R2, 1					;emergencyEnergy == 1
		JNZ  shipStateShield			;emergencyEnergy != 1
addEmergencyEnergy:
	MOV R2, 0					;prepare R2 to set emergencyEnergy to 0 (one use only)
	MOV [R1], R2					;set emergencyEnergy to 0
	MOV R2, 50					;energy to be added
	MOV R3, energyValue				;copy address of energyValue to R3
	MOV R4, [R3]					;copy value of energyValue to R4
	
	MOV R1, SHIP_EMERGENCY_ENERGY			;sound select (emergency energy)
	CALL selectSound				;play emergency energy sound
	MOV R0, MAX_SHIP_ENERGY				;register used for comparison
	ADD R2, R4					;current energy value + 50
	CMP R2, R0					;new energy value > MAX_SHIP_ENERGY?
		JGE	setToMaxEnergy			;True, set ship energy to max energy
	MOV [R3], R2					;update ship energy value
	JMP shipStateShield	

setToMaxEnergy:
	MOV [R3], R0					;update ship energy value
;------------------////------------------

shipStateShield:
	MOV  R1, enableShieldToggle			;see if we are able to toggle ship shield
	MOV  R2, [R1]					;enableShieldToggle value copied to R2
	MOV  R3, shieldState				;copy shieldState address to R3
	MOV  R4, [R3]					;copy shieldState value to R4

	CMP	 R0, 5					;see if key "5" has been pressed (activate/deactivate shield)
		JNZ	 onShieldToggle			;key 5 not pressed or has been released, allow toggling of shield state 
	CMP	 R2, 1					;see if we can toggle shield state
		JZ   toggleShipState			;jump to toggle shield state
	JMP  shipStateFinished				;re-draw ship

toggleShipState:					
	MOV R2, 0					;disable toggling of ship shield until key "5" released
	MOV [R1], R2					;update in memory
	MOV R2, 1					;register R2 used for XOR operation
	XOR R4, R2					;toggle bit on or off
	MOV [R3], R4					;update new shield state in memory
	MOV R1, SHIP_SHIELD				;move ship shield toggle sound to R1
	CALL selectSound				;play ship shield toggle sound
	JMP shipStateFinished
	
onShieldToggle:
	MOV R2, 1					;allow ship shield to be toggled, updated in enableShieldToggle variable
	MOV [R1], R2					;update in memory
	
shipStateFinished:
	MOV R1, yCoordShip				;copy address of yCoordShip to R1
	MOV R1, [R1]					;copy y coordinate of ship to R1
	MOV R2, xCoordShip				;copy address of xCoordShip to R2
	MOV R2, [R2]					;copy x coordinate of ship to R2
	MOV R0, shieldState				;copy address value of shieldState to R0
	MOV R0, [R0]					;copy value of shield state to R0
	CMP R0, 1					;if shield is activated, we draw ship with shield
		JZ  drawShipWithShield

drawShipWithoutShield:
	MOV R3, emergencyEnergy				;copy address of emergencyEnergy to R3
	MOV R3, [R3]					;copy value of emergency energy to R3
	CMP R3, 1					;see if emergency energy available
		JZ  drawShipNoShieldEnergy		;emergency energy available, draw energised ship with shield
	MOV R3, shipWithoutShield			;emergency energy unavailable, draw normal ship with shield
	JMP drawShip					;draw ship
drawShipNoShieldEnergy:
	MOV R3, shipWithoutShieldEnergy			;sets schematic to be drawn
	JMP drawShip					;draw ship
	
drawShipWithShield:
	MOV R3, emergencyEnergy				;copy address of emergencyEnergy to R3
	MOV R3, [R3]					;copy value of emergency energy to R3
	CMP R3, 1					;see if emergency energy available
		JZ  drawShipShieldEnergy		;emergency energy available, draw energised ship with no shield
	MOV R3, shipWithShield				;emergency energy unavailable, draw normal ship with no shield
	JMP drawShip					;draw ship

drawShipShieldEnergy:
	MOV R3, shipWithShieldEnergy			;sets schematic to be drawn
	
drawShip:
	MOV  R4, clearShip				;move address of clearShip variable to R4
	MOV  R4, [R4]					;move value of clearShip to R4
	CMP  R4, 1					;if clearShip == 1, we can proceed to clear the ship
		JZ  shipClearance			;clean ship
	JMP  noCleanShip				;clearShip != 1, ship does not get cleared
shipClearance:
	CALL cleanShip					;clean the ship
noCleanShip:
	CALL drawTable					;routine used to draw images from set tables
	
	POP R4
	POP R3
	POP R2
	POP R1
	POP R0
	
	RET

; IN  -> NONE
; RET -> NONE
cleanShip:
	PUSH R1
	PUSH R2
	PUSH R3
	
	MOV  R1, xCoordShipToClear			;copy address of x coordinate of ship to clear to R1
	MOV  R2, [R1]					;copy value of x coordinate of ship to clear to R2
	MOV  R1, [R1+2]					;copy value of y coordinate of ship to clear to R1
	MOV  R3, shipClearanceSchematic			;copy address of shipClearanceSchematic to R3
	CALL drawTable					;clear ship
	
	POP R3
	POP R2
	POP R1
	RET

;********************************************************************************
; MOVE SHIP -- AUX ROUTINE FOR SHIP STATE
;
; Auxillary Routine for shipState used for ship movements
; IN  -> NONE
; RET -> NONE
;
; R0 - current key value
; R1 - address for x coordinate of ship
; R2 - value for x coordinate of ship
; R3 - copy of initial x coordinate of ship
; R4 - value for y coordinate of ship
; R5 - copy of initial y coordinate of ship
; R6 - holds constants used for comparisons
;********************************************************************************
	
moveShip:
	PUSH R0
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4
	PUSH R5
	PUSH R6
	
	MOV  R1, xCoordShip				;copy address of x coordinate to R1
	MOV  R2, [R1]					;copy value of x coordinate to R2
	MOV  R3, R2					;save copy of initial value of x coordinate
	MOV  R4, [R1+2]					;copy value of y coordinate to R4
	MOV  R5, R4					;copy initial value of y coordinate
	
	CALL getCurrentKeyValue				;copy current value of keyValue to R0	
	MOV  R6, -1					;move value of "no-key" to R6
	CMP  R0, R6					;check if no key has been pressed
		JZ   endShipMovement			;if no key pressed, end routine
	
	CMP  R0, 3					;check if any of first 3 buttons pressed
		JLT  shipUp				;button pressed, move ship up
	JZ   endShipMovement				;key 3 pressed, not associated with ship movement, end routine
	
	CMP  R0, 4					;key to move ship left
		JZ   shipLeft				;clear ship then move ship left
	CMP  R0, 6					;key to move ship right
		JZ   shipRight				;clear ship then move ship right
	
	CMP  R0, 5					;check if key "5" pressed
		JZ   endShipMovement			;key 5 pressed, not associated with ship movement, end routine
	CMP  R0, 7					;check if key "7" pressed
		JZ   endShipMovement			;key 7 pressed, not associated with ship movement, end routine
	MOV  R6, 0BH					;move value of key "B" to R6
	CMP  R0, R6					;check if any buttons in {8,9,A} pressed
		JLT  shipDown				;button pressed in set aforementioned
	JMP  endShipMovement				;no button pressed associated with ship movement, end routine
	
shipUp:
	CMP R5, MIN_SHIP_Y				;verify if ship has reached boundary set
		JZ  endShipMovement			;ship has reached boundary, exit routine, no ship movement
	SUB R5, 1					;move ship up
	MOV [R1+2], R5					;save new y coordinate of ship in memory
	CMP R0, 0					;see if key "0" pressed to further move ship left
		JZ  shipLeft				;move ship left
	CMP R0, 2					;see if key "2" pressed to further move ship right
		JZ  shipRight				;move ship right
	JMP endShipMovement				;ship movement finalized, end routine
shipDown:
	MOV R6, MAX_SHIP_Y				;copy 24 to R6
	CMP R5, R6					;verify if ship has reached boundary set
		JZ endShipMovement			;ship has reached boundary, exit routine, no ship movement
	ADD R5, 1					;move ship down
	MOV [R1+2], R5					;save new y coordinate of ship in memory
	MOV R6, 08H					;move key value "8" to R6
	CMP R0, R6					;check if key pressed == "8"
		JZ  shipLeft				;move ship left if key pressed == 8
	MOV R6, 0AH					;move key value "A" to R6
	CMP R0, R6					;check if key pressed == "A" 
		JZ  shipRight				;move ship right if key pressed == "A"
	JMP endShipMovement				;ship movement finalized, end routine
shipLeft:
	CMP R2, MIN_SHIP_X				;copy 3 to R2
		JZ  endShipMovement			;ship has reached boundary, exit routine, no ship movement
shipLeftCont:
	SUB R2, 1					;move ship left
	MOV [R1], R2					;save new x coordinate of ship in memory
	JMP endShipMovement				;ship movement finalized, end routine
shipRight:
	MOV R0, MAX_SHIP_X				;copy 54 to R0
	CMP R0, R2					;verify if ship has reached boundary set
		JZ  endShipMovement			;ship has reached boundary, exit routine, no ship movement
shipRightCont:
	ADD R2, 1					;move ship right
	MOV [R1], R2					;save new x coordinate of ship in memory
	JMP endShipMovement				;ship movement finalized, end routine
	
endShipMovement:
	CMP R4, R5					;check if y coordinate of ship has changed
		JNZ  shipChanged			;y changed, update ship clearance coordinates
nextStep:
	CMP R3, R2					;check if x coordinate of ship has changed
		JZ noShipChange				;x changed, update ship clearance coordinates
shipChanged:
	MOV R1, xCoordShipToClear			;copy address of x coordinate of ship to clear
	MOV [R1], R3					;copy x coordinate of ship prior to movement to be cleared
	MOV [R1+2], R4					;copy y coordinate of ship prior to movement to be cleared
	MOV R4, 1					;copy 1 to R4 to set clearShip variable to 1
	MOV R1, clearShip				;copy clearShip address to R1
	MOV [R1], R4					;set clearShip variable to 1 (we can clear the ship)
	JMP endMoveShip
noShipChange:
	MOV R4, 0					;copy 0 to R4 to set clearShip variable to 0
	MOV R1, clearShip				;copy clearShip address to R1
	MOV [R1], R4					;set clearShip variable to 0 (ship will not be cleared)
endMoveShip:
	POP R6
	POP R5
	POP R4
	POP R3
	POP R2
	POP R1
	POP R0
	RET

;********************************************************************************
; PULSARS STATE
;
; Loop through all pulsars "pulse" them, set state and draw
; IN  -> NONE
; RET -> NONE
;
;---------------------------
;- 		   pulsar states
;-
;- >>0   - good opaque 
;- >>1   - good translucent
;- >>2   - bad opaque
;- >>3   - good translucent
;- >>3<x - bad
;---------------------------
;
; R0 - address of pulsar state
; R1 - y coordinate of pulsar
; R2 - x coordinate of pulsar
; R3 - pulsar state & pulsar schematic
; R4 - pulsars drawn counter
; R5 - address of pulsar position
;********************************************************************************

pulsarsStates:
	PUSH R0
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4
	PUSH R5
	
	MOV  R0, pulsarIntState0			;copy energy interrupt address to R0
	MOV  R1, [R0]					;copy energy interrupt value to R0
	CMP  R1, 0					;if interrupt off, do not run this routine 
		JZ   endPulsarStates			;end routine
	MOV  R1, 0					;turn interrupt off and run routine
	MOV  [R0], R1

	MOV  R0, stateTopLeftPulsar			;set address of pulsar states in R0
	MOV  R5, pulsarPositions			;set address of pulsar positions in R5 
	MOV  R4, 0					;pulsars drawn counter

selectPulsar:
	MOVB R2, [R5]					;move x coordinate of pulsar to R2
	ADD  R5, 1					;move along the table one address
	MOVB R1, [R5]					;move y coordinate of pulsar to R1
	CMP  R4, 4					;see if all pulsars drawn
		JZ   endPulsarStates			;return to main loop if all pulsars drawn	
	CALL analysePulsar				;check what type of pulsar to draw
	JMP  selectPulsar				;restart loop for next pulsar

;-------------Pulsar Analysis------------
; IN  -> R0 = state of pulsar address
; IN  -> R1 = y coordinate of pulsar
; IN  -> R2 = x coordinate of pulsar
; RET -> R0 - state of pulsar address
analysePulsar:
	PUSH R2
	PUSH R3
	
	MOV  R3, [R0]					;copy value of pulsarState to R3
	CMP  R3, 0					;if pulsarState == 0
		JZ   setGoodPulsar			;pulsarState = 0, set Good and Opaque pulsar
	CMP  R3, 1					;if pulsarState == 1
		JZ   setGoodTransPulsar			;pulsarState = 1, set Good and Translucent pulsar
	CMP  R3, 2					;if pulsarState == 2
		JZ   setBadPulsar			;pulsarState = 2, set Bad and Opaque pulsar
	CMP  R3, 3					;if pulsarState == 3
		JZ  setBadTransPulsar			;pulsarState = 3, set Bad and Translucent pulsar
	CMP  R3, 4					;if pulsarState == 4
		JZ  setBadPulsar			;pulsarState = 4, set Bad and Opaque pulsar
	CMP  R3, 5					;if pulsarState == 5
		JZ  setBadTransPulsar			;pulsarState = 5, set Bad and Translucent pulsar
	CMP  R3, 6					;if pulsarState == 6
		JZ  setBadPulsar			;pulsarState = 6, set Bad and Opaque pulsar
	JMP  setBadTransPulsar				;pulsarState = 7, set Bad and Translucent pulsar
	
setGoodPulsar:
	MOV  R3, goodPulsar				;set schematic of pulsar to draw
	CALL drawTable					;routine draws table, given schematic and (x,y) coordinates
	MOV  R2, 1					;change state of pulsar for pulsating effect opaque->translucent & vice-versa
	JMP  endAnalysis
	
setGoodTransPulsar:
	MOV  R3, goodPulsarTrans			;set schematic of pulsar to draw
	CALL drawTable					;routine draws table, given schematic and (x,y) coordinates
	MOV  R2, 0					;change state of pulsar for pulsating effect opaque->translucent & vice-versa
	JMP  endAnalysis
	
	
setBadPulsar:
	MOV  R3, badPulsar				;set schematic of pulsar to draw
	CALL drawTable					;routine draws table, given schematic and (x,y) coordinates
	MOV  R2, 3					;change state of pulsar for pulsating effect opaque->translucent & vice-versa
	JMP  endAnalysis
	
	
setBadTransPulsar:
	MOV  R3, badPulsarTrans				;set schematic of pulsar to draw
	CALL drawTable					;routine draws table, given schematic and (x,y) coordinates
	MOV  R2, 2					;change state of pulsar for pulsating effect opaque->translucent & vice-versa
	
endAnalysis:
	MOV  [R0], R2					;update new pulsar state in memory
	ADD  R0, 2					;set address for next pulsar state
	ADD  R5, 1					;set address for next pulsar coordinate pair
	ADD  R4, 1					;increment pulsar counter
	POP  R2
	POP  R3
	RET
;------------------////------------------


endPulsarStates:
	POP  R5
	POP  R4
	POP  R3
	POP  R2
	POP  R1
	POP  R0
	
	RET
	
;********************************************************************************
; RAY STATES
; 
; Loop through all rays, move them and check for collisions
; IN  -> NONE
; RET -> NONE 
;
; R0 - address of x coordinate of ray
; R1 - value for y coordinate of ship
; R2 - value for x coordinate of ship
; R3 - holds constants used for mathematical operations
; R4 - address of ray states
; R5 - ray counter
; R6 - ray collision status (Returned in R6 from checkCollisionBoundaries)
;********************************************************************************

rayStates:
	PUSH R0
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4
	PUSH R5
	PUSH R6
	
	MOV R0, rayIntState1				;copy ray interrupt address to R0
	MOV R1, [R0]					;copy ray interrupt value to R0
	CMP R1, 0					;if interrupt off, do not run this routine 
		JZ  endRays				;end routine
	MOV R1, 0					;turn interrupt off and run routine
	MOV [R0], R1

	CALL moveRays					;update ray positions
	MOV  R5, 0					;ray counter to iterate through all rays
	MOV  R0, xTopLeftRay				;copy address of x coordinate of Top left ray to R0

rayLoop:
	MOV  R2, [R0]					;x coordinate for ray
	MOV  R1, [R0+2]					;y coordinate for ray
	CALL checkCollisionBoundaries			;collision status returned to R6
	CMP  R6, 1					;check ray collision with border
		JZ   handleRayCollision			;handle ray collision with border
	CMP  R6, 2					;check ray collision with ship
		JZ   handleShipCollision		;handle ray collision with ship
	
nextRayToHandle:
	ADD R0, 6					;move to x coordinate of next ray
	ADD R5, 1					;increment ray counter
	CMP R5, 4					;check to see if we have looped through all rays
		JZ  drawRays				;all rays have been looped through, proceed to draw them
	JMP rayLoop					;handle next ray


handleShipCollision:
	MOV R1, [R0+4]					;copy value of ray state to R1
	CMP R1, 2					;check if ray state is in the set {0,1} (good)
		JLT handleGoodRay			;if ray is in the aforementioned set, it is good
handleBadRay:						;ray was not in aforementioned set, but in the set {2,3} (bad)
	MOV R1, shieldState				;copy address of shieldState to R1
	MOV R1, [R1]					;copy value of shieldState to R1
	CMP R1, 1					;check if shield is on (1)
		JNZ setEnergyToZero			;shield is off, set energy to zero and end game
	
	MOV R1, energyValue				;shield is on, copy address of energyValue to R1
	MOV R2, [R1]					;copy value of energyValue to R2
	MOV R3, 20					;move constant of 20 to R3
	CMP R2, R3					;check if current energy < 20
		JLT setEnergyToZero			;if energy < 20, set energy to zero to avoid negative energy values
	SUB R2, R3					;energy >= 20, current energy - 20 after collision
	MOV [R1], R2					;update energy value after subtraction
	MOV R1, BAD_RAY_SHIELD_ON			;sound select for absorbing bad ray through shield
	CALL selectSound				;play bad ray absorption sound
	JMP handleRayCollision				;after handling ray collision with ship, reset ray coordinates
	
setEnergyToZero:
	CALL clearDisplays				;game over, ran out of energy, clear displays
	MOV  R0, gameState				;copy gameState address to R0
	MOV  R1, 2					;register used to set gameState to 2
	MOV  [R0], R1					;set gameState to 2 (game over, ran out of energy)
	JMP  endRays					;end routine
handleGoodRay:
	MOV R1, shieldState				;copy address of shieldState to R1
	MOV R1, [R1]					;copy value of shieldState to R1
	CMP R1, 1					;check if shield is on (1)
		JZ  handleRayCollision			;shield is on, no energy change, reset ray position
	MOV R1, energyValue				;shield is off, copy energyValue to R1
	MOV R2, [R1]					;copy value of energyValue to R2
	
	MOV R3, 30					;move constant of 30 to R3
	ADD R2, R3					;add 30 energy to current energy
	MOV R3, MAX_SHIP_ENERGY				;move constant of 100 to R3
	
	CMP R2, R3					;check if current energy <= 100
		JGE setEnergyTo100			;current energy > 100, set it to 100 (max energy)
	MOV [R1], R2					;current energy <= 100, update it in memory
	MOV R1, ADD_ENERGY				;move add energy sound to R1
	CALL selectSound				;play add energy sound
	JMP handleRayCollision				;handle ray, reset ray coordinates
setEnergyTo100:
	MOV [R1], R3					;update current energy to 100
	MOV R1, ADD_ENERGY				;move add energy sound to R1
	CALL selectSound				;play add energy sound
handleRayCollision:
	CMP R5, 0					;if R5 == 0, the ray belongs to top left pulsar	
		JZ  handleTL_Ray			;handle top left ray
	CMP R5, 1					;if R5 == 1, the ray belongs to top right pulsar
		JZ  handleTR_Ray			;handle top right ray
	CMP R5, 2					;if R5 == 2, the ray belongs to the bottom left pulsar
		JZ  handleBL_Ray			;handle bottom left ray
	JMP handleBR_Ray				;handle bottom right ray

;--------------Ray Handling--------------
handleTL_Ray:
	CALL resetTL_Ray
	JMP  nextRayToHandle				;handle next ray

handleTR_Ray:
	CALL resetTR_Ray
	JMP  nextRayToHandle				;handle next ray
	
handleBL_Ray:
	CALL resetBL_Ray
	JMP  nextRayToHandle				;handle next ray
	
handleBR_Ray:
	CALL resetBR_Ray
	JMP  nextRayToHandle				;handle next ray
;------------------////------------------

; IN  -> R1 = Ray y coordinate
; IN  -> R2 = Ray x coordinate
; RET -> NONE
resetRayCoords:
	MOV [R0], R2					;update ray's x coordinate
	MOV [R0+2], R1					;update ray's y coordinate
	RET							
	
drawRays:
	CALL drawAllRays				;draw all rays after movement/collisions
endRays:
	POP R6
	POP R5
	POP R4
	POP R3
	POP R2
	POP R1
	POP R0
	RET
	
;********************************************************************************
; RAY MOVEMENT - RAY STATES AUXILLARY MOVEMENT ROUTINE
;
; Ray states aux. routine - loops through all rays and moves them
; IN  -> NONE
; RET -> NONE
;
; R0 - weight of pixel (0) used to clear pixel
; R1 - y position of ray
; R2 - x position of ray
; R3 - address of x coordinate of ray
; R4 - ray counter
;********************************************************************************

moveRays:
	PUSH R0
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4
	
	MOV R4, 0					;ray counter to iterate through all rays
	MOV R0, 0					;R0 passed to routine callPixel to clear the pixel
	MOV R3, xTopLeftRay				;copy address of ray's top left x coordinate
	
rayMovementLoop:
	MOV  R2, [R3]					;copy value of ray's x coordinate to R2
	MOV  R1, [R3+2]					;copy value of ray's y coordinate to R1
	CALL drawPixel					;clear the ray position to prepare for new ray position
	CMP R4, 0					;if R4 == 0, handle top left ray
		JZ  moveTL_Ray				;move top left ray
	CMP R4, 1					;if R4 == 1, handle top right ray
		JZ moveTR_Ray				;move top right ray
	CMP R4, 2					;if R4 == 2, handle bottom left ray
		JZ moveBL_Ray				;move bottom left ray
	CMP R4, 3					;if R4 == 3, handle bottom right ray
		JZ moveBR_Ray				;move bottom right ray
nextRay:
	ADD R3, 6					;move to next ray address
	ADD R4, 1					;increment ray counter
	CMP R4, 4					;see if we have iterated through all rays
		JZ 	endRayMovement			;all rays have been iterated through, end ray movement
	JMP rayMovementLoop				;handle next ray

;---------Ray Movement Selection---------
moveTL_Ray:						
	ADD R1, 1					;increment y position of ray
	ADD R2, 1					;increment x position of ray
	CALL updateRayCoords				;move new coordinates to memory
	JMP nextRay					;handle next ray

moveTR_Ray:
	ADD R1, 1					;increment y position of ray
	SUB R2, 1					;decrement x position of ray
	CALL updateRayCoords				;move new coordinates to memory
	JMP nextRay					;handle next ray

moveBL_Ray:
	SUB R1, 1					;decrement y position of ray
	ADD R2, 1					;increment x position of ray
	CALL updateRayCoords				;move new coordinates to memory
	JMP nextRay					;handle next ray

moveBR_Ray:
	SUB R1, 1					;decrement y position of ray
	SUB R2, 1					;decrement x position of ray
	CALL updateRayCoords				;move new coordinates to memory
	JMP nextRay					;handle next ray

; IN  -> R1 = y position of ray
; IN  -> R2 = x position of ray
; RET -> NONE
updateRayCoords:
	MOV [R3], R2					;update x coordinate of ray to memory
	MOV [R3+2], R1					;update y coordinate of ray to memory
	RET
;------------------////------------------

endRayMovement:
	POP R4
	POP R3
	POP R2
	POP R1
	POP R0
	RET
	
;********************************************************************************
; DRAW RAYS
;
; Loop through all rays and draw position of each one
; IN  -> NONE
; RET -> NONE
;
; R0 - weight of pixel
; R1 - value of y coordinate
; R2 - value of x coordinate
; R3 - address of x coordinate of ray
; R4 - ray counter
; R9 - used in sub-routine for colour calculations
;********************************************************************************	

drawAllRays:
	PUSH R0
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4
	PUSH R9
	
	MOV R0, 1					;value of register passed to drawPixel (1 -> turn pixel on)
	MOV R4, 0					;ray counter
	MOV R3, xTopLeftRay				;copy address of ray's x coordinate to R3
	
drawAllRaysLoop:
	MOV  R1, [R3+4]					;copy value of ray state to R1
	CALL determineRayColours			;based on ray state, determine colour content for ray to draw
	MOV  R2, [R3]					;copy value of ray's x coordinate to R2
	MOV  R1, [R3+2]					;copy value of ray's y coordinate to R1
	CALL drawPixel					;draw pixel with determined colour content, x, and y coordinates

	ADD R3, 6					;set address for next ray
	ADD R4, 1					;increment ray counter
	CMP R4, 4					;check if we have iterated through all rays
		JZ  endRayDrawing			;all rays have been drawn, end routine
	JMP drawAllRaysLoop				;draw next ray

endRayDrawing:
	POP R9
	POP R4
	POP R3
	POP R2
	POP R1
	POP R0
	RET

; IN  -> R1 = ray state
; RET -> R9 = ray type
determineRayColours:	
	CMP R1, 2					;if value of ray state is in set {0,1}, then it is a good ray 
		JLT green				;good rays are green
	JMP red						;value of ray state is not in set, therefore it is a bad ray
	
green:
	MOV R9, goodRay					;copy address of goodRay colour scheme to R9
	JMP endColours					;end routine
red:
	MOV R9, badRay					;copy address of badRay colour scheme to R9
endColours:
	RET
	
;********************************************************************************
; CHECK COLLISION BOUNDARIES 
; 
; routine which checks if there are collisions with ship or borders
; IN  -> NONE
; RET -> R6
;
; R1 - y coordinate of ray
; R2 - x coordinate of ray
; R3 - holds constants
; R4 - y coordinate of ship
; R5 - x coordinate of ship
; R6 - returns value of collision (0 = no collision, 1 = border collision, 2 = ship collision)
;********************************************************************************

checkCollisionBoundaries:
	PUSH R1	
	PUSH R2
	PUSH R3
	PUSH R4
	PUSH R5
	
	MOV R6, 0					;holds return value of collision		

;----Collisions With Screen Boundaries---	
	CMP R1, MIN_RAY_Y				;check if ray has reached top of screen
		JZ  rayCollision			;ray has reached upper boundary			
	MOV R3, MAX_RAY_Y				;move y max boarder value to R3
	CMP R1, R3					;check if ray has reached bottom of screen
		JZ  rayCollision			;ray has reached lower boundary
;------------------////------------------

;-------Collisions With Ship-------------
	MOV R4, yCoordShip				;copy address of ship y coordinate to R4
	MOV R4, [R4]					;copy value of ship y coordinate to R4
	SUB R4, 1					;used to box in ship coordinates for calculations
	
	MOV R5, xCoordShip				;copy address of ship x coordinate to R5
	MOV R5, [R5]					;copy value of ship x coordinate to R5
	SUB R5, 1					;used to box in ship coordinates for calculations
	
	CMP R1, R4					;y check 1/2 to see if ray is in y range
		JGE possibleCollisionY			;jump for y check 2/2
	JMP endCollision				;ray is not in range, no collision
	
possibleCollisionY:
	ADD R4, 6					;check other part of y interval
	CMP R1, R4					;y check 2/2 to see if ray is in y range
		JLT possibleCollisionX			;ray is in y ship range, procede to test x range
	JMP endCollision				;ray is not in range, no collision

possibleCollisionX:
	CMP R2, R5					;x check 1/2 to see if ray is in x range
		JGE finalCollisionCheck			;jump for x check 2/2
	JMP endCollision				;ray is not in range, no collision
finalCollisionCheck:
	ADD R5, 6					;check other part of x interval
	CMP R2, R5					;x check 2/2 to see if ray is in x range
		JLT shipCollision			;ray collision with ship, handle accordingly
	JMP endCollision				;ray is not in range, no collision
;------------------////------------------

shipCollision:
	MOV R6, 2					;ray has collided with the ship
	JMP endCollision				;end routine
rayCollision:
	MOV R6, 1					;ray has collided with a boundary
endCollision:
	POP R5
	POP R4
	POP R3
	POP R2
	POP R1
	RET
	
;********************************************************************************
; DRAW TABLE 
;
; Draws a given table
; IN  -> R3
; RET -> NONE
;
; R0 - weight of pixel
; R1 - y coordinate
; R2 - x coordinate
; R3 - table address
; R4 - table rows
; R5 - table columns
; R6 - table row counter
; R7 - table column counter
; R8 - column start x coordinate
; R9 - table address for colours passed to drawPixel
;********************************************************************************

drawTable:
	PUSH R0
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4
	PUSH R5
	PUSH R6
	PUSH R7
	PUSH R8
	PUSH R9
	
	MOV  R8, R2					;save x -> initial column for reset when line changes
	
	MOVB R4, [R3]					;table rows
	ADD  R3, 1					;move along one position in the table
	MOVB R5, [R3]					;table columns
	ADD  R3, 1					;move along one position in the table
	MOV  R6, 0					;table row counter
	MOV  R7, 0					;table column counter
	
	MOV  R9, R3
	ADD  R3, 4
	
lineLoop:
	MOVB R0, [R3]					;value of pixel from table put in R3
	CMP  R4, R6					;if line not changed, see if end of table reached
		JZ   tableDrawFinish			;if reached end of table, go back to gameLoop
tableCheck:
	CMP  R5, R7					;see if line change is necessary
		JZ	 changeLine			;if Zero flag activated, next line
	
	CALL drawPixel					;routine draws pixel on pixel screen		
	ADD  R7, 1
	ADD  R2, 1					;go right one column space
	ADD  R3, 1					;advance a position in the table
	JMP  lineLoop				
	
changeLine:
	MOV  R7, 0
	ADD  R6, 1
	ADD  R1, 1					;next line, +1 y coordinate
	MOV  R2, R8					;go back to initial column saved in R8
	JMP  lineLoop					;read next line


tableDrawFinish:
	POP R9
	POP R8
	POP R7
	POP R6
	POP R5
	POP R4
	POP R3
	POP R2
	POP R1
	POP R0	
	RET


;********************************************************************************
; DRAW PIXEL	(R2, R1)
;
; Draws a given pixel
; IN  -> R0
; IN  -> R1
; IN  -> R2
; IN  -> R9
; RET -> NONE
;
; R0 - weight of pixel
; R1 - y coordinate
; R2 - x coordinate
; R3 - pixel defining coordinates
; R4 - define red content in pixel
; R5 - define green content in pixel
; R6 - define blue content in pixel
; R7 - define alpha content in pixel
; R9 - table address for pixel colours
;********************************************************************************

drawPixel:
	PUSH R0
	PUSH R1
	PUSH R2
    PUSH R3
	PUSH R4
	PUSH R5
	PUSH R6
	PUSH R7
	PUSH R9
	
readPixelContent:
	MOVB R4, [R9]					;red content of pixel
	ADD  R9, 1					;move along one position in the table
	MOVB R5, [R9]					;green content of pixel
	ADD  R9, 1					;move along one position in the table
	MOVB R6, [R9]					;blue content of pixel
	ADD  R9, 1					;move along one position in the table
	MOVB R7, [R9]					;alpha content of pixel
	
	
    MOV R3, DEFINE_LINE					;copy address of pixelscreen line address
    MOV [R3], R1           				;select the line
    MOV R3, DEFINE_COLUMN				;copy address of pixelscreen column address
    MOV [R3], R2           				;select the column
	CMP R0, 0					;verify if pixel is off
		JZ   pixelOff				;pixel off, end routine

    MOV R3, DEFINE_RED					;copy address of pixelscreen red definition
	MOV [R3], R4					;set red value required for pixel
	MOV R3, DEFINE_GREEN				;copy address of pixelscreen green definition
	MOV [R3], R5					;set green value required for pixel
	MOV R3, DEFINE_BLUE				;copy address of pixelscreen blue definition
	MOV [R3], R6					;set blue value required for pixel
	CMP R0, 2					;"2" in our table defines a translucent pixel
		JZ   translucentPixel			;if weight of pixel == 2 jump to translucentPixel
	JMP normalPixel					;weight of pixel != 2
	
translucentPixel:					;draw pixel as translucent
	MOV R7, 122					;half alpha content of normal pixel
	MOV R0, 1					;change pixel state 2 -> 1
normalPixel:
	MOV R3, DEFINE_ALPHA				;copy address of pixelscreen alpha definition
	MOV [R3], R7					;set alpha value required for pixel

pixelOff:	
	MOV R3, DEFINE_PIXEL				;copy address of pixelscreen pixel state
    MOV [R3], R0          				;turn pixel on or off on selected line and column
	JMP endDrawPixel				;end routine

endDrawPixel:
	POP R9
	POP R7
	POP R6
	POP R5
	POP R4
    POP R3
	POP R2
	POP R1
	POP R0
    RET

;********************************************************************************
; PSEUDO-RANDOM NUMBER GENERATOR
;
; Increments a number each time game cycles
; IN  -> NONE
; RET -> NONE
;
; R0 - address of generatorValue
; R1 - holds generator value
; R5 - returns value of generatorValue
;********************************************************************************

numberGenerator:
	PUSH R0
	PUSH R1
	
	MOV R0, generatorValue				;copy generatorValue address to R0
	MOV R1, [R0]					;copy value of generatorValue to R1
	
	ADD R1, 1					;increment value of generatorValue
	MOV [R0], R1					;set value of generatorValue to 0
	
endGenerator:
	POP R1
	POP R0
	RET
	
;------get current generator value-------
; IN  -> NONE 
; RET -> R5 = new pulsar state
getNewPulsarState:
	PUSH R0
	
	MOV R0, 8					;used for modulus operator
	MOV R5, generatorValue				;copy address of generatorValue to R5
	MOV R5, [R5]					;copy value of generatorValue to R5
	MOD R5, R0					;return number [0,7], used for pulsarstate selection
	
	POP R0
	RET
;------------------////------------------

;********************************************************************************
; RAY RESET ROUTINES
; 
; after collision, reset ray called here
; IN  -> NONE
; RET -> NONE
;
; R1 - initial y coordinate for ray
; R2 - initial x coordinate for ray
; R3 - address for corresponding pulsar state
; R4 - address for ray state
; R5 - returned from getNewPulsarState routine
;********************************************************************************	

resetTL_Ray:
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4
	PUSH R5
	
	MOV  R2, MIN_RAY_X				;move minimum x ray coordinate value to R2
	MOV  R1, MIN_RAY_Y				;move minimum y ray coordinate value to R1
	CALL resetRayCoords				;reset ray coordinates
	
	MOV  R3, stateTopLeftPulsar			;copy address of top left pulsar's state to R3
	CALL getNewPulsarState				;new pulsar state returned in R5
	MOV  [R3], R5					;update ray state

	MOV  R4, stateTLRay				;copy address of top left ray state to R4
	MOV  [R4], R5					;update new ray state with current corresponding pulsar state
	
	POP R5
	POP R4
	POP R3
	POP R2
	POP R1
	RET

resetTR_Ray:
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4
	PUSH R5

	MOV  R2, MAX_RAY_X				;move max x ray coordinate value to R2
	MOV  R1, MIN_RAY_Y				;move minimum y ray coordinate value to R1
	CALL resetRayCoords				;reset ray coordinates	
	
	MOV  R3, stateTopRightPulsar			;copy address of top right pulsar's state to R3
	CALL getNewPulsarState				;new pulsar state returned in R5
	MOV  [R3], R5					;update ray state

	MOV  R4, stateTRRay				;copy address of top left ray state to R4
	MOV  [R4], R5					;update new ray state with current corresponding pulsar state	
	
	POP R5
	POP R4
	POP R3
	POP R2
	POP R1
	RET
	
resetBL_Ray:
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4
	PUSH R5

	MOV  R2, MIN_RAY_X				;move minimum x ray coordinate value to R2
	MOV  R1, MAX_RAY_Y				;move max y ray coordinate value to R1
	CALL resetRayCoords				;reset ray coordinates
	
	MOV  R3, stateBottomLeftPulsar			;copy address of bottom left pulsar's state to R3
	CALL getNewPulsarState				;new pulsar state returned in R5
	MOV  [R3], R5					;update ray state

	MOV  R4, stateBLRay				;copy address of top left ray state to R4
	MOV  [R4], R5					;update new ray state with current corresponding pulsar state	
	
	POP R5
	POP R4
	POP R3
	POP R2
	POP R1
	RET
	
resetBR_Ray:
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4
	PUSH R5
	
	MOV  R2, MAX_RAY_X				;move max x ray coordinate value to R2
	MOV  R1, MAX_RAY_Y				;move max y ray coordinate value to R1
	CALL resetRayCoords				;reset ray coordinates
	
	MOV  R3, stateBottomRightPulsar			;copy address of bottom right pulsar's state to R3
	CALL getNewPulsarState				;new pulsar state returned in R5
	MOV  [R3], R5					;update ray state

	MOV  R4, stateBRRay				;copy address of top left ray state to R4
	MOV  [R4], R5					;update new ray state with current corresponding pulsar state	
	
	POP R5
	POP R4
	POP R3
	POP R2
	POP R1
	RET

;********************************************************************************
; RAY INIT ROUTINES
;
; initialize a ray
; IN  -> R0
; RET -> NONE 
;
; R0 - x coordinate of ray, used in resetRayCoords routine
; R1 - initial y coordinate for ray
; R2 - initial x coordinate for ray
; R3 - address for corresponding pulsar state
; R4 - address for ray state
;********************************************************************************

initTopLeftRay:
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4
	
	MOV  R2, MIN_RAY_X				;move minimum x ray coordinate value to R2
	MOV  R1, MIN_RAY_Y				;move minimum y ray coordinate value to R1
	SUB  R2, 1
	ADD  R1, 1
	CALL resetRayCoords				;reset ray coordinates
	MOV  R3, stateTopLeftPulsar			;copy top left pulsar's state to R3
	MOV  R3, [R3]					;copy top left pulsar's state value to R3
	MOV  R4, stateTLRay				;copy address of top left ray state to R4
	MOV  [R4], R3					;update new ray state with current corresponding pulsar state	
	
	POP  R4
	POP  R3
	POP  R2
	POP  R1
	RET
	
initTopRightRay:
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4
	
	MOV  R2, MAX_RAY_X				;move max x ray coordinate value to R2
	MOV  R1, MIN_RAY_Y				;move minimum y ray coordinate value to R1
	CALL resetRayCoords				;reset ray coordinates	
	MOV  R3, stateTopRightPulsar			;copy top right pulsar's state to R3
	MOV  R3, [R3]					;copy top right pulsar's state value to R3
	MOV  R4, stateTRRay				;copy address of top left ray state to R4
	MOV  [R4], R3					;update new ray state with current corresponding pulsar state		
	
	POP  R4
	POP  R3
	POP  R2
	POP  R1
	RET

initBottomLeftRay:
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4
	
	MOV  R2, MIN_RAY_X				;move minimum x ray coordinate value to R2
	MOV  R1, MAX_RAY_Y				;move max y ray coordinate value to R1
	ADD  R2, 2					;start at a different position to other rays
	SUB  R1, 2					;start at a different position to other rays
	CALL resetRayCoords				;reset ray coordinates
	MOV  R3, stateBottomLeftPulsar			;copy bottom left pulsar state address to R3
	MOV  R3, [R3]					;copy bottom left pulsar state value to R3
	MOV  R4, stateBLRay				;copy address of top left ray state to R4
	MOV  [R4], R3					;update new ray state with current corresponding pulsar state	
	
	POP  R4
	POP  R3
	POP  R2
	POP  R1
	RET

initBottomRightRay:
	PUSH R1
	PUSH R2
	PUSH R3
	PUSH R4
	
	MOV  R2, MAX_RAY_X				;move max x ray coordinate value to R2
	MOV  R1, MAX_RAY_Y				;move max y ray coordinate value to R1
	SUB  R2, 3					;start at a different position to other rays
	SUB  R1, 3					;start at a different position to other rays
	CALL resetRayCoords				;reset ray coordinates
	MOV  R3, stateBottomRightPulsar			;copy bottom right pulsar state address to R3
	MOV  R3, [R3]					;copy bottom right pulsar state value to R3
	MOV  R4, stateBRRay				;copy address of top left ray state to R4
	MOV  [R4], R3					;update new ray state with current corresponding pulsar state	
	
	POP  R4
	POP  R3
	POP  R2
	POP  R1
	RET

;********************************************************************************
; PULSAR INIT
;
; initialize all pulsars
; IN  -> NONE
; RET -> NONE
;
; R0 - address of pulsar state
; R1 - initial y coordinate for ray
; R2 - initial x coordinate for ray
;********************************************************************************

initAllPulsars:
	PUSH R0
	PUSH R1
	
	MOV R0, stateTopLeftPulsar			;copy address of pulsar state to R0
	MOV R1, TL_PULSAR_START_STATE			;copy pulsar start state to R1
	MOV [R0], R1					;update pulsar state in memory
	
	MOV R0, stateTopRightPulsar			;copy address of pulsar state to R0
	MOV R1, TR_PULSAR_START_STATE			;copy pulsar start state to R1
	MOV [R0], R1					;update pulsar state in memory
	
	MOV R0, stateBottomLeftPulsar			;copy address of pulsar state to R0
	MOV R1, BL_PULSAR_START_STATE			;copy pulsar start state to R1
	MOV [R0], R1					;update pulsar state in memory
	
	MOV R0, stateBottomRightPulsar			;copy address of pulsar state to R0
	MOV R1, BR_PULSAR_START_STATE			;copy pulsar start state to R1
	MOV [R0], R1					;update pulsar state in memory
	
	POP R1
	POP R0
	RET


;********************************************************************************
; INTERRUPT ROUTINES
;
; interrupt routines
; IN  -> NONE
; RET -> NONE
;
; R0 - address of interrupt
; R1 - holds 1 to update interrupt value
; R2 - accepted parameter which determines which interrupt to use
;********************************************************************************	

pulsarInt0:
    PUSH R0
	PUSH R1
	
	MOV  R0, pulsarIntState0	 
    MOV  R1, 1
	MOV  [R0], R1	 
	
	POP  R1
	POP  R0 
    RFE                      				;Return From Exception (diferent to RET)

rayInt1:
	PUSH R0
	PUSH R1

	MOV  R0, rayIntState1	 
    MOV  R1, 1
	MOV  [R0], R1	 
	
	POP  R1
	POP  R0 
    RFE                      				;Return From Exception (diferent to RET)
	
energyInt2:
	PUSH R0
	PUSH R1

	MOV  R0, energyIntState2	 
    MOV  R1, 1
	MOV  [R0], R1	 
	
	POP  R1
	POP  R0 
    RFE                      				;Return From Exception (diferent to RET)

;********************************************************************************
; CLEAR SCREEN
;
; clears screen
; IN  -> NONE
; RET -> NONE
;********************************************************************************	
	
clearScreen:
	PUSH R0
	PUSH R1

	MOV  R0, CLEAR_SCREEN
	MOV  R1, 0AH
	MOV  [R0], R1
	
	POP R1
	POP R0
	RET
	
;********************************************************************************
; SCENARIO/SOUND MANIPULATION
;
; Select/get scenario and set sound
;********************************************************************************

; IN  -> R1 = scenario to select
; RET -> NONE
selectScenario:
	PUSH R0
	
	MOV  R0, SCENARIO_SELECT			
	MOV  [R0], R1					;set scenario passed in R1

	POP R0
	RET

; IN  -> NONE
; RET -> R1 = current scenario
getScenario:	
	MOV  R1, SCENARIO_SELECT
	MOV  R1, [R1]					;return current scenario in R1
	
	RET
	
; IN  -> R1 = sound to select
; RET -> NONE 
selectSound:
	PUSH R0
	
	MOV  R0, SOUND_SELECT				;select sound passed in R1
	MOV  [R0], R1

	POP R0
	RET


