# Preface

Back in ye olden times we wrote games in assembly. The notion of a reusable game "engine" didn't yet exist because no distinction between game and system could be made. Code was written once for the hardware it ran on and then thrown away forever.

As more powerful hardware emerged, so too did the demand for more engaging experiences. Writing a memory manager or timestep from scratch for every new game quickly became a chore and so development shifted away from hardware and towards gameplay. The "game engine" was born.



## History

My journey in game development began as a dorky high-school freshman in the early 2010s. By then game engines were well established, but still required extensive knowledge of various programming languages to use effectively.



![DarkBASIC Pro - The first game engine I ever used.](C:\Users\thesk\Documents\Projects\XJGE\XJGE-2\wiki\img_wiki_dbpro.png)

(Pictured: DarkBASIC Pro - The first game "engine" I ever used.)



Today, the interfaces of game engines are significantly more user friendly, often oriented to the perspective of an artist rather than a programmer. Because I fall into the latter category I prefer using code to create gameplay systems, even if its a little old fashioned ;)



XJGE is not the first game engine I've created. It is however the first <i>public</i> game engine I've been willing to release. As such, I felt it necessary to provide this user guide in the event anyone else was interested in making use of it. This guide will demonstrate how to utilize every feature necessary to write games in Java using XJGE 2.



## Engine Overview

XJGE provides developers with an extensible architecture and several gameplay utilites useful for the creation of both 2D and 3D games. It takes the form a Java library coupled with various abstractions of low-level dependencies.



The original XJGE was an open box 



## Who is This For?

This engine is not designed with your average weekend hobbyist in mind- If the thought of coding bores you, an engine like Unity or GameMaker Studio may be a better fit.



Ideally the perfect candidate for this engine is:

​	A.   Someone already familiar with high-level programming languages or

​	B.   Someone with an attitude tenacious enough to disregard that last sentence.



## License

XJGE is freely available under a GNU Lesser General Public License (or LGPL). The source code of the engine itself may be copied, modified, and redistributed so long as derivative works contain the same notice. **You can still release a commercial product such as a game without legal consequence.**



With this license the implementation of the library (or the game itself) is regarded as a distinct legal entity from the engine so whatever code you write *using* the engine belongs to you.



*Free as in free comrade.*



## Resources

