# ProgettoAlgoritmi
Algoritmi
Known Issues: quando controlli se l'avversario mettesse li se riuscirebbe a vincere o forzare stiamo guardando una board senza ancora il nostro segno, che va bene se stiamo parlando di vittoria immediata ma con il forcing tecnicamente no(se riuscisse a forzare tutta la board fino alla fine e vincere dovunque la mettiamo va bene e non importa metterla esattamente nel primo)

		//mark each us
		//check win if win(""104"" and break)
		//check forced (forced tree if win 102(check accidental opponent win))
		//unmark
		//general?
		//mark random or 102
		//mark each them
		//check win (if win&& not same column ""103"" and break of found ,else 0 of our mark)
		//check forced (0 of our mark if !=102)
		//if change <=101 go back (check the previously marked)
		//general?
		//if<=100
		//deep analysis
		//general?
		//notes: BFS sarebbe meglio ma non implementabile quindi prob. DFS per deep
		//deep: check recursive (if follows best moves?)
		//all in time
		//max every time(time safety) or at the end (time efficient)
		//general cares of?? closeness??center??
		//hard coded for beginning???
