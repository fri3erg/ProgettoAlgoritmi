# ProgettoAlgoritmi
Algoritmi
Known Issues: quando controlli vittoria immediata avversaria(103) o forzare(101) stiamo guardando una board senza ancora il nostro segno, che va bene se stiamo parlando di vittoria immediata(103) ma con il forcing tecnicamente no(non è la stessa board)

non guarda diverse mosse avanti, soprattutto sopra l'ultimo messo

attenzione a non vincere per sbaglio

		//check win if win(""104"" and break)
  		//check if lost( mark above is his win) 103
    	// decide if above is separate or integrated
		//check forced (forced tree if win 102(check accidental opponent win))
  		//for each mark
  		//check his forced 101
		//general? 0-100
		//deep analysis
		//notes: BFS sarebbe meglio ma non implementabile quindi prob. DFS per deep
		//deep: check recursive (if follows best moves?)
  		//does general only look first move?
		//all in time
  		//use all of time on general?
		//max every time(time safety) or at the end (time efficient)
		//general cares of?? closeness??center??
		//hard coded for beginning???
		//unmark faster than copy
il canny aveva anche detto qualcosa su che hanno usato hashmap, da guardare 
