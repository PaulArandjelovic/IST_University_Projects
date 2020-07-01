% Pavle Arandjelovic 93745
:-[codigo_comum].
%-----------------------------------------------------------------------------
%                                            								 
%     Name and Number: 		Pavle Arandjelovic | TP-193745					 
%                                                             				 
%     File Name:            TP-193745-CrosswordSolver    		   			 
%                                                            				 
%     Description:   		Crossword puzzle solver created in Prolog		 
%                                                               			 
%-----------------------------------------------------------------------------


%------------------------------------------------------------------------------
% obtem_letras_palavras/2 (Word_Lst, Letter_Lst)
% Sort given list in alphabetical order and split words into sublists of chars
% Word_Lst   -> Contains list of words to split
% Letter_Lst -> Output after all words have been split into chars
%------------------------------------------------------------------------------

obtem_letras_palavras(Word_Lst, Letter_Lst) :- 
	sort(Word_Lst, X), 
	maplist(atom_chars(), X, Letter_Lst).
	
%------------------------------------------------------------------------------
% split/3 (Row, Divisor, Spaces)
% Given a row, split at divisors and return list of lists separated at Divisor
% Row     -> Row to split
% Divisor -> Divisor to split at, in this program, it will be '#'
% Spaces  -> List of lists sliced at divisor
%------------------------------------------------------------------------------

split([], _, [[]]) :- !.
split([H|T], Div, [[]|Rest]) :- H == Div, split(T, Div, Rest), !.
split([H|T], Div, [[H|First]|Rest]) :- split(T, Div, [First|Rest]).

%------------------------------------------------------------------------------
% espaco_fila/2 (Row, Space)
% Return a space with length > 2 in a given row
% Row   -> Row to scan for spaces
% Space -> List containing encountered space 
%------------------------------------------------------------------------------

espaco_fila(Row, Space) :- 
	split(Row, #, Lst), member(X, Lst), length(X, Len), Len > 2, Space = X.

%------------------------------------------------------------------------------
% espacos_fila/2 (Row, Spaces)
% Return a space with length > 2 in a given row
% Row    -> Row to scan for spaces in
% Spaces -> List of encountered spaces in Row
%------------------------------------------------------------------------------

espacos_fila(Row, Spaces) :- 
	bagof(Space, espaco_fila(Row, Space), Spaces), ! ; Spaces = [], !.

%------------------------------------------------------------------------------
% espacos_puzzle_aux/2 (Row, Spaces)
% Return spaces in a given row
% Row    -> Row to scan for spaces in
% Spaces -> List of spaces in Row
%------------------------------------------------------------------------------

espacos_puzzle_aux([], []).
espacos_puzzle_aux([Fila|Rest], Espacos) :- 
	espacos_fila(Fila, X), !, espacos_puzzle_aux(Rest, T), append(X, T, Espacos).

%------------------------------------------------------------------------------
% espacos_puzzle/2 (Grid, Spaces)
% Finds all spaces in a given puzzle
% Grid   -> Grid to scan for spaces in
% Spaces -> List of spaces in Grid
%------------------------------------------------------------------------------

espacos_puzzle(Grid, Spaces) :-
	espacos_puzzle_aux(Grid, Spaces1),
	mat_transposta(Grid, Grid_Transpose),
	espacos_puzzle_aux(Grid_Transpose, Spaces2),
	append(Spaces1, Spaces2, Spaces).

%------------------------------------------------------------------------------
% espacos_com_posicoes_comuns/3 (Spaces, Space, Spaces_com)
% Return all spaces that intersect with a given space
% Spaces     -> List of all spaces in grid
% Space      -> Space to find other intersecting spaces
% Spaces_com -> List of spaces that intersect with Space
%------------------------------------------------------------------------------

in_list(Pos, Orig_Esp, [H2|T2]) :- Orig_Esp \== [H2|T2], (Pos == H2; in_list(Pos, Orig_Esp, T2)).
espacos_com_posicoes_comuns(Espacos, Esp, Esps_com) :-
	bagof(Ret1, Pos^(member(Pos, Esp), 
		bagof(Space, (member(Space, Espacos), in_list(Pos, Esp, Space)), Ret1)), Ret2),
	bagof(Ret, Space^(member(Space, Ret2), append(Space, Ret)), Esps_com).

%------------------------------------------------------------------------------
% palavra_possivel_esp/4 (Word, Space, Spaces, Words)
% Verify if a word can be placed into a space, while allowing other spaces to
% have words placed in them
% Word   -> Word to be verified
% Space  -> Space for insertion of Word
% Spaces -> List of grid spaces
% Words  -> List of possible words for grid solution
%------------------------------------------------------------------------------

cross_check_other_words(Words, Space) :- bagof(_, (member(X, Words), X = Space), _), !.
palavra_possivel_esp(Word, Space, Spaces, Words) :-
	espacos_com_posicoes_comuns(Spaces, Space, Esp_Comuns),
	same_length(Word, Space),
	Space = Word,
	maplist(cross_check_other_words(Words), Esp_Comuns).

%------------------------------------------------------------------------------
% palavras_possiveis_esp/4 (Words, Spaces, Space, Poss_Words)
% Return words that fit into Space while following previous predicate's criteria
% Words      -> List of possible words for grid solution
% Spaces     -> List of grid spaces
% Space      -> Space for insertion of words
% Poss_Words -> Possible words that can be placed in Space
%------------------------------------------------------------------------------

palavras_possiveis_esp(Words, Spaces, Space, Poss_Words) :-
	bagof(Word, Spaces^(member(Word, Words), 
		palavra_possivel_esp(Word, Space, Spaces, Words)), Poss_Words).

%------------------------------------------------------------------------------
% palavras_possiveis/3 (Words, Spaces, Poss_Words)
% Return list of spaces with corresponding possible words
% Words      -> List of possible words for grid solution
% Spaces     -> List of grid spaces
% Poss_Words -> List of spaces with corresponding possible words
%------------------------------------------------------------------------------

palavras_possiveis(Words, Spaces, Poss_Words) :-
	bagof([Space, Ret], (member(Space, Spaces), 
		palavras_possiveis_esp(Words, Spaces, Space, Ret)), Poss_Words). 

%------------------------------------------------------------------------------
% letras_comuns/2 (Words, Com_Words)
% Return List of (index, letter) pairs of common letters for each word in Words
% Words     -> List of words to scan through
% Com_Words -> (index, letter) pair for each common letter in every word
%------------------------------------------------------------------------------

letras_comuns([H|T], Com_Words) :-
	length(H, Len),
	findall((Index, Letter), (between(1, Len, Index), nth1(Index, H, Letter),
		forall(member(Word, [H|T]), nth1(Index, Word, Letter))), Com_Words).

%------------------------------------------------------------------------------
% atribui_comuns/1 (Poss_Words)
% Unify all Spaces with common letters in each of its corresponding possible 
% words
% Poss_Words -> List of spaces with corresponding possible words
%------------------------------------------------------------------------------

% Unify space with Letter at Index
set_word(Space, (Index, Letter)) :- nth1(Index, Space, Letter). 
atribui_comuns(Poss_Words) :-
	findall([Space, Words], (member(X, Poss_Words), nth1(1, X, Space), nth1(2, X, Words),
		letras_comuns(Words, Ind_Let), maplist(set_word(Space), Ind_Let)), Poss_Words).

%------------------------------------------------------------------------------
% retira_impossiveis/2 (Poss_Words, New_Poss_Words)
% Remove words that no longer unify with its corresponding space in Poss_Words
% Poss_Words     -> List of spaces with corresponding possible words
% New_Poss_Words -> Poss_Words after impossible word removal
%------------------------------------------------------------------------------

retira_impossiveis([], _).
retira_impossiveis([H|T], New_Poss_Words) :-
	nth1(1, H, Space), nth1(2, H, Words),
	findall(Word, (member(Word, Words), Word = Space), New_Words),
	append([[Space], [New_Words]], Temp1),
	retira_impossiveis(T, Temp2), !,
	append([[Temp1], Temp2], New_Poss_Words).

%------------------------------------------------------------------------------
% obtem_unicas/2 (Poss_Words, Solo_Words)
% Obtain all words that are already unified completely with a space
% Poss_Words -> List of spaces with corresponding possible words
% Solo_Words -> List of words that are already completely unified wtih a space
%------------------------------------------------------------------------------

obtem_unicas(Poss_Words, Solo_Words) :-
	findall(Word, (member(Row, Poss_Words), nth1(2, Row, Words), length(Words, Len), Len = 1, flatten(Words, Word)), Solo_Words).

%------------------------------------------------------------------------------
% retira_unicas_aux/3 (Row, Solo_Words, New_Row)
% Return New_Row after removal of solo words from possible word list of Row
% Row        -> (Space, Possible Words) list pair 
% Solo_Words -> List of words that are already completely unified wtih a space
% New_Row    -> Row after removal of solo words from possible word list
%------------------------------------------------------------------------------

retira_unicas_aux([Space, Words], _, [Space, Words]) :- 
	length(Words, Len), Len == 1, !.
retira_unicas_aux([Space, Words], Unicas, [Space, New_Words]) :-
	subtract(Words, Unicas, New_Words).

%------------------------------------------------------------------------------
% retira_unicas/2 (Poss_Words, New_Poss_Words)
% Remove all Solo_Words from space's corresponding words length(Words) > 1
% Poss_Words     -> List of spaces with corresponding possible words
% New_Poss_Words -> Poss_Words after removal of solo words 
%------------------------------------------------------------------------------

retira_unicas(Pals_Possiveis, Novas_Pals_Possiveis) :-
	obtem_unicas(Pals_Possiveis, Unicas),
	bagof(New_Row, Row^(member(Row, Pals_Possiveis), 
		retira_unicas_aux(Row, Unicas, New_Row)), Novas_Pals_Possiveis).

%------------------------------------------------------------------------------
% simplifica_aux/2 (Poss_Words, New_Poss_Words)
% Apply Retira_unicas, retira_impossiveis and atribui_comuns to Poss_Words
% Poss_Words     -> List of spaces with corresponding possible words
% New_Poss_Words -> Poss_Words after application of predicates
%------------------------------------------------------------------------------

simplifica_aux(Poss_Words, New_Poss_Words) :-
	atribui_comuns(Poss_Words),
	retira_impossiveis(Poss_Words, New_Poss_Words1),
	retira_unicas(New_Poss_Words1, New_Poss_Words).

%------------------------------------------------------------------------------
% simplifica/2 (Poss_Words, New_Poss_Words)
% Repeat simplfica_aux until there is no change in Poss_Words
% Poss_Words     -> List of spaces with corresponding possible words
% New_Poss_Words -> Poss_Words after application of predicates has no effect
%------------------------------------------------------------------------------

simplifica([], _).
simplifica(Pals_Possiveis, New_Poss_Words) :- 
	simplifica_aux(Pals_Possiveis, New_Poss_Words), 
	New_Poss_Words == Pals_Possiveis, 
	simplifica([], New_Poss_Words), !.
simplifica(Pals_Possiveis, New_Poss_Words) :- 
	simplifica_aux(Pals_Possiveis, Ret1), 
	simplifica(Ret1, New_Poss_Words).

%------------------------------------------------------------------------------
% inicializa/2 (Puz, Poss_Words)
% Initializes a puzzle with the following format: [Word_List, Grid]
% Puz        -> Puzzles to be initialized
% Poss_Words -> Generated list of spaces with corresponding possible words
%------------------------------------------------------------------------------

inicializa([Words,Grid], Poss_Words) :-
	obtem_letras_palavras(Words, Letter_lst),
	espacos_puzzle(Grid, Spaces),
	palavras_possiveis(Letter_lst, Spaces, Poss_word_lst),
	simplifica(Poss_word_lst, Poss_Words), !.

%------------------------------------------------------------------------------
% escolhe_menos_alternativas/2 (Poss_Words, Choice)
% Return first encounter of [Space, Words] pair with lowest number of Words and
% length(Words) > 1
% Poss_Words -> List of spaces with corresponding possible words
% Choice     -> Encountered [Space, Words] pair
%------------------------------------------------------------------------------

escolhe_menos_alternativas([Row|T], Choice) :- escolhe_menos_alternativas(T, Row, Choice).
escolhe_menos_alternativas([], X, X) :- nth1(2, X, Words), length(Words, Len), Len \== 1.
escolhe_menos_alternativas([Row|T], Prev, Choice) :-
	nth1(2, Prev, Prev_Words), nth1(2, Row, N_Words),
	length(Prev_Words, Prev_Len), length(N_Words, N_Len),
	((N_Len \== 1, (N_Len @< Prev_Len ; Prev_Len == 1)) -> escolhe_menos_alternativas(T, Row, Choice)
	;
	escolhe_menos_alternativas(T, Prev, Choice)).

%------------------------------------------------------------------------------
% experimenta_pal_aux/3 (Row, Poss_Words, New_Poss_Words)
% Given a Row in Poss_Words, replace all words associated with the space with
% the word already occupying it
% Row            -> Row in Poss_Words [Space, Words]
% Poss_Words     -> List of spaces with corresponding possible words
% New_Poss_Words -> Poss_Words after word replacement
%------------------------------------------------------------------------------

experimenta_pal_aux(_, [], _).
experimenta_pal_aux([Space, Words], [[H1|T1]|T2], New_Poss_Words):-
	(Space == H1, X = [[Space, [Space]]] ; X = [[H1|T1]]), !, 
	experimenta_pal_aux([Space, Words], T2, Ret), !, 
	append([X, Ret], New_Poss_Words).

%------------------------------------------------------------------------------
% experimenta_pal/3 (Choice, Poss_Words, New_Poss_Words)
% Given a choice of words and a space, unify word with space and apply 
% experimenta_pal_aux
% Choice         -> Words to experiment with a space: [Space, Words]
% Poss_Words     -> List of spaces with corresponding possible words
% New_Poss_Words -> Poss_Words after word replacement
%------------------------------------------------------------------------------

experimenta_pal([Space, Words], Poss_Words, New_Poss_Words) :-
	member(Space, Words),
	experimenta_pal_aux([Space, Words], Poss_Words, New_Poss_Words).

%------------------------------------------------------------------------------
% resolve_aux/2 (Poss_Words, New_Poss_Words)
% Find all possible Space-Word combinations for resoltion of the puzzle
% Poss_Words     -> List of spaces with corresponding possible words
% New_Poss_Words -> Solved Poss_Words, all spaces are unified with a word
%------------------------------------------------------------------------------

resolve_aux([], _).
resolve_aux(Poss_Words, New_Poss_Words) :- 
	escolhe_menos_alternativas(Poss_Words, Choice), !,
	experimenta_pal(Choice, Poss_Words, Ret1),
	simplifica(Ret1, Ret2), 
	resolve_aux(Ret2, New_Poss_Words).
resolve_aux(Poss_Words, Poss_Words) :- resolve_aux([], Poss_Words).
%------------------------------------------------------------------------------
% resolve/1 (Puz)
% Initialize and solve the puzzle
% Puz -> Puzzle to be initialized and solved 
%------------------------------------------------------------------------------

resolve(Puz) :-
	inicializa(Puz, Poss_Words),
	resolve_aux(Poss_Words, _), !.

