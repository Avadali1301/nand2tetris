// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed.
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.
// In an infinite loop, I check if the keyboard input is not 0, if so,
// I turn a pixel black, update my bit number, and then go back, and if it is 0, I turn a pixel white, update my bit number, and then jump back
// I computed that there will be a total of 8192 bits, so that will be my hard max
  @bitnum
  M = 0
  (LOOP)
// I take the keyboard input and if it is not zero I evaluate BLACK, else I evaluate WHITE
    @KBD
    D = M
    @BLACK
    D; JNE
// I see that a key is not pressed, so I color the current bit in white and decrement my BITNUM and Jump back to the LOOP
      (WHITE)
        @bitnum
        M = M - 1
        D = M
        @SCREEN
        A = A + D
        M = 0
        @LOOP
        0; JMP
// I see that a key is pressed, so I color the current bit in black and increment my BITNUM
// I also check if I am at the max bit, and if I am, I just jump back to the loop and keep doing this until the key is released
      (BLACK)
          @bitnum
          D = M
          @8192
          D = D - A
          @LOOP
          D; JEQ
          @bitnum
          M = M + 1
          D = M
          @SCREEN
          A = A + D
          M = -1
          @LOOP
          0; JMP
