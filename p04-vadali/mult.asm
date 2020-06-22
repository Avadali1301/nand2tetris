// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
// Since we are only performing multiplication with whole numbers, I can just use iterative addition to do the job
// Put your code here.

// I initialize R2 to 0 because I will be storing my value in there
      @R2
      M = 0
// I initialize my counter so that I can keep track of where I am in the LOOP
      @counter
      M = 0
  (LOOP)
// I first compare my counter to R1 to see if I should break out of the LOOP.
    @counter
    D = M
    @R1
    D = D - M
    @END
    D; JEQ
    @R0
    D = M
// I add R0 to R2 and then I increment my counter, and then I jump back to the beginning of the LOOP.
    @R2
    M = M + D
    @counter
    M = M + 1
    @LOOP
    0; JMP
// I get my program stuck in an infinite loop so that it does not attempt to access a memory address that does not exist.
  (END)
    @END
    0; JMP
