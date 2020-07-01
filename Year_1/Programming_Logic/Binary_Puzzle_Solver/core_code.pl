:-[codigo_comum].
%-----------------------------------------------------------------------------
%                                                                			 *
%     Name:                	Pavle Arandjelovic					 *                 
%                                                             				 *
%     File Name:            	TP-193745-BinaryPuzSolver       			 *
%                                                            				 *
%     BinaryPuzSolver:    	Binary puzzle solver created in Prolog			 *
%                                                               			 *
%-----------------------------------------------------------------------------


%-----------------------------------------------------------------------------
% count(_, [], 0)
% Y -> Desired number to count
% N -> Number of times Y appears
%-----------------------------------------------------------------------------

count(_, [], 0).
count(Y, [H|T], N) :- Y == H, count(Y,T,N1), !, N is N1 + 1.
count(Y, [H|T], N) :- Y \== H, !, count(Y,T,N).

%-----------------------------------------------------------------------------
% aplica_R1_triplo([E1,E2,E3], Triplo)
% Applies R1 rule to first 3 elements of the list 
% Triplo -> Output List after R1 has been applied to [E1,E2,E3]
%-----------------------------------------------------------------------------

aplica_R1_triplo([E1,E2,E3],Triplo) :-
	count(0, [E1,E2,E3], N1), N1 < 3,
	count(1, [E1,E2,E3], N2), N2 < 3,
	(E2 == E3 -> !, N is E2 xor 1, Triplo = [N,E2,E3]
	; 
	E1 == E3 ->	!, N is E1 xor 1, Triplo = [E1,N,E3]
	;
	E1 == E2 ->	!, N is E1 xor 1, Triplo = [E1,E2,N]
	;
	Triplo = [E1,E2,E3]).

%-----------------------------------------------------------------------------
% aplica_R1_fila_aux([E1,E2,E3], Row)
% auxiliary predicates which apply R1 to the whole row once (only applied if possible)
% Row -> Output after applying R1 once to [E1,E2,E3]
%-----------------------------------------------------------------------------

aplica_R1_fila_aux([E1,E2,E3], Row) :- !, aplica_R1_triplo([E1,E2,E3], Row).
aplica_R1_fila_aux([E1,E2,E3|T1], [H|Resto]) :-
	aplica_R1_triplo([E1,E2,E3], [H|T2]),
	append([T2, T1], Insert),
	!, aplica_R1_fila_aux(Insert, Resto).

%-----------------------------------------------------------------------------
% aplica_R1_fila(Fila, N_Fila)
% Applies R1 to the whole row (Fila), until no further change is made
% N_Fila -> Output after R1 has been applied to Fila
%-----------------------------------------------------------------------------

aplica_R1_fila([], _).
aplica_R1_fila(Fila, X) :-	aplica_R1_fila_aux(Fila, X), X == Fila, !, aplica_R1_fila([], X).
aplica_R1_fila(Fila, N_Fila) :- aplica_R1_fila_aux(Fila, X), !, aplica_R1_fila(X, N_Fila).

%-----------------------------------------------------------------------------
% filler_R2(Fila, X, N_Fila)
% All anonymous variables in the row (Fila) are filled in with X (0 or 1)
% N_Fila -> Output row after applying filler_R2 to Fila
%-----------------------------------------------------------------------------

filler_R2([], _, []).
filler_R2([H|T], X, [X|Out]) :- var(H), filler_R2(T, X, Out).
filler_R2([H|T], X, [H|Out]) :- not(var(H)), filler_R2(T, X, Out).

%-----------------------------------------------------------------------------
% aplica_R2_fila(Fila, N_Fila)
% Applies R2 to the whole row (Fila)
% N_Fila -> Output row after applying R2 to Fila
%-----------------------------------------------------------------------------

aux(Fila,Fila).
aplica_R2_fila(Fila, N_Fila) :-
	length(Fila, N),
	Half is N/2,
	count(0, Fila, N1), N1 @=< Half,
	count(1, Fila, N2), N2 @=< Half,

	(N1 == Half -> filler_R2(Fila, 1, N_Fila)
	;
	N2 == Half -> filler_R2(Fila, 0, N_Fila)
	;
	aux(Fila, N_Fila)).

%-----------------------------------------------------------------------------
% aplica_R1_R2_fila_aux(Fila, ApR1R2)
% Applies R1 and R2 once to the row (Fila)
% ApR1R2 -> Output after applying R1 and R2 to the row once
%-----------------------------------------------------------------------------

aplica_R1_R2_fila_aux(Fila, ApR1R2) :- 
	aplica_R1_fila(Fila, ApR1),
	aplica_R2_fila(ApR1, ApR1R2).

%-----------------------------------------------------------------------------
% aplica_R1_R2_fila(Fila, N_Fila)
% applies R1 and R2 to the row (Fila) until no longer possible
% N_Fila -> Output after applying to Fila 
%-----------------------------------------------------------------------------

aplica_R1_R2_fila([], _).
aplica_R1_R2_fila(Fila, ApR1R2) :- 	aplica_R1_R2_fila_aux(Fila, ApR1R2), ApR1R2 == Fila, !, aplica_R1_R2_fila([], ApR1R2).
aplica_R1_R2_fila(Fila, N_Fila) :- aplica_R1_R2_fila_aux(Fila, ApR1R2), !, aplica_R1_R2_fila(ApR1R2, N_Fila).

%-----------------------------------------------------------------------------
% aplica_R1_R2_puzzle_aux(Puz, N_Puz)
% Applies R1 and R2 to each line or column of the puzzle
% N_Puz -> Output after applying to Puz
%-----------------------------------------------------------------------------

aplica_R1_R2_puzzle_aux([], []).
aplica_R1_R2_puzzle_aux([E1|Rest], [X|N_Puz]) :- aplica_R1_R2_fila(E1, X), !, aplica_R1_R2_puzzle_aux(Rest, N_Puz).

%-----------------------------------------------------------------------------
% aplica_R1_R2_puzzle(Puz, N_Puz)
% applies R1 and R2 to both rows and columns of the puzzle
% N_Puz -> Resultant puzzle after applying R1 and R2
%-----------------------------------------------------------------------------

aplica_R1_R2_puzzle(Puz, N_Puz) :-
	aplica_R1_R2_puzzle_aux(Puz, Naa_Puz),
	mat_transposta(Naa_Puz, N_Puz_Transposta),
	aplica_R1_R2_puzzle_aux(N_Puz_Transposta, N_Puz_Final),
	mat_transposta(N_Puz_Final, N_Puz).

%-----------------------------------------------------------------------------
% inicializa(Puz, N_Puz)
% applies R1 and R2 to both rows and columns of the puzzle until no longer possible
% N_Puz -> Resultant puzzle after applying R1 and R2
%-----------------------------------------------------------------------------

inicializa([], _).
inicializa(Puz, X) :- aplica_R1_R2_puzzle(Puz, X), Puz == X, !, inicializa([], X).
inicializa(Puz, N_Puz) :- aplica_R1_R2_puzzle(Puz, X), !, inicializa(X, N_Puz).

%-----------------------------------------------------------------------------
% aux_verifica_R3([H|_], First)
% applies R3 to each row and column
% RowToCheck -> Used to apply R3 with against all following rows 
%-----------------------------------------------------------------------------

aux_verifica_R3([], _).
aux_verifica_R3([H|_], RowToCheck) :- RowToCheck == H, false.
aux_verifica_R3([H|T], RowToCheck) :- RowToCheck \== H, aux_verifica_R3(T, RowToCheck).

%-----------------------------------------------------------------------------
% verifica_R3_rows_columns([H|T])
% cycles through rows to check against 
%-----------------------------------------------------------------------------

verifica_R3_rows_columns([]).
verifica_R3_rows_columns([H|T]) :- aux_verifica_R3(T, H), verifica_R3_rows_columns(T).

%-----------------------------------------------------------------------------
% verifica_R3(Puz)
% applies R3 to all rows and columns of the puzzle (Puz)
%-----------------------------------------------------------------------------

verifica_R3(Puz) :-
	verifica_R3_rows_columns(Puz),
	mat_transposta(Puz, TPuz),
	verifica_R3_rows_columns(TPuz).

%-----------------------------------------------------------------------------
% propaga_line_col_changes(Linha, NewLinha, (L, Count), ReturnList)
% Linha -> Line or column to check against NewLine or NewColumn
% L, Count -> Contain the line or column and a counter to check each elements
% ReturnList -> Output which returns list of positions to be propogated
%-----------------------------------------------------------------------------

propaga_linechanges([], [], _, []).
propaga_linechanges([LH1|T1], [LH2|T2], (L, Count), AReturnList) :-
	NewCount is Count + 1, var(LH1), nonvar(LH2), append([(L, Count)], ReturnList, AReturnList),
	!, propaga_linechanges(T1, T2, (L, NewCount), ReturnList).

propaga_linechanges([_|T1], [_|T2], (L, Count), AReturnList) :-
	NewCount is Count + 1, !, propaga_linechanges(T1, T2, (L, NewCount), AReturnList).


propaga_colchanges([], [], _, []).
propaga_colchanges([LH1|T1], [LH2|T2], (Count,C), AReturnList) :-
	NewCount is Count + 1, var(LH1),nonvar(LH2), append([(Count, C)],ReturnList, AReturnList),
	!, propaga_colchanges(T1, T2, (NewCount, C), ReturnList).

propaga_colchanges([_|T1], [_|T2], (Count,C), AReturnList) :-
	NewCount is Count + 1, !, propaga_colchanges(T1, T2, (NewCount, C), AReturnList).

%-----------------------------------------------------------------------------
% propaga_posicoes([(Pos)|T], Puz, NPuz)
% 						^list of positions to propogate
% Recursively propogates each position of Puz
% NPuz -> Output after applying propaga_posicoes to Puz
%-----------------------------------------------------------------------------

propaga_posicoes([],P,P).
propaga_posicoes([(L,C)|T], Puz, NPuz) :-
	mat_elementos_coluna(Puz, C, Coluna),
	aplica_R1_R2_fila(Coluna, NewColuna),
	mat_muda_coluna(Puz, C, NewColuna, N_Mat),
	nth1(L,N_Mat,Linha),
	aplica_R1_R2_fila(Linha, NewLinha),
	mat_muda_linha(N_Mat, L, NewLinha, N_Puz),

	propaga_linechanges(Linha, NewLinha, (L, 1), ReturnList2),
	propaga_colchanges(Coluna, NewColuna, (1 ,C), ReturnList1),
	append([T, ReturnList1, ReturnList2], FinalReturn),

	propaga_posicoes(FinalReturn, N_Puz, NPuz).

%-----------------------------------------------------------------------------
% poscol([H|_], Index)
% returns the column index of the next variable available
% Index -> Column Index 
%-----------------------------------------------------------------------------

poscol([], 1).
poscol([H|_], Index):- var(H), poscol([], Index).
poscol([H|T], Index):- nonvar(H), poscol(T, Index1), Index is Index1 + 1.
 
%-----------------------------------------------------------------------------
% get_pos(Puz, Linha, Len, Pos)
% returns the position of the next variable available in the puzzle (Puz)
% scans line by line for a new position
% Len -> length of line
% Linha -> line to scan
% Pos -> Output after applying get_pos to the puzzle (Puz)
%-----------------------------------------------------------------------------

get_pos([], _, _, _).
get_pos([H|T], Linha, Len, Pos) :- poscol(H, Col), Col > Len, NewLinha is Linha + 1, !, get_pos(T, NewLinha, Len, Pos).
get_pos([H|_], Linha, Len, (Linha,Col)) :- poscol(H, Col), Col < Len, !, get_pos([], _, _, _).

%-----------------------------------------------------------------------------
% are_variables(Puz)
% checks if there are any variables left in the puzzle (Puz)
%-----------------------------------------------------------------------------

nonvariable(X) :- nonvar(X).
are_variables([]).
are_variables([H|T]) :- maplist(nonvariable, H), !, are_variables(T).

%-----------------------------------------------------------------------------
% resolve_aux(Puz, Len, Sol)
% propagates the puzzle (Puz) until it has been solved
% Len -> Length of the puzzles lists
% Sol -> Resolved Puzzle after filling all empty positions of Puz
%-----------------------------------------------------------------------------

resolve_aux([], _, []).
resolve_aux(Puz, Len, Sol) :- 
	(are_variables(Puz) -> Sol = Puz
		;
	get_pos(Puz, 1, Len, Pos),
		(mat_muda_posicao(Puz, Pos, 0, N_Mat), propaga_posicoes([Pos], N_Mat, FinalPuz) ->	resolve_aux(FinalPuz, Len, Sol)
		;
		mat_muda_posicao(Puz, Pos, 1, N_Mat), propaga_posicoes([Pos], N_Mat, FinalPuz) ->	resolve_aux(FinalPuz, Len, Sol))).

%-----------------------------------------------------------------------------
% resolve(Puz, Sol)
% resolves the puzzle (Puz), starts by initializing it and verifying if solvable
% Sol -> Resolved Puzzle of Puz
%-----------------------------------------------------------------------------

resolve([], []).
resolve([H|T], Sol) :-
	inicializa([H|T], NPuz),
	verifica_R3(NPuz),
	length(NPuz, Len),

	!, resolve_aux(NPuz, Len, Sol).
