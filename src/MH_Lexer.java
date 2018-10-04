
// File:   MH_Lexer.java
// Date:   October 2013, subsequently modified each year.

// Java template file for lexer component of Informatics 2A Assignment 1.
// Concerns lexical classes and lexer for the language MH (`Micro-Haskell').


import java.io.* ;

class MH_Lexer extends GenLexer implements LEX_TOKEN_STREAM {

    static class VarAcceptor extends Acceptor implements DFA {
        public String lexClass() {return "VAR" ;}
        public int numberOfStates() {return 3 ;}

        int next (int state, char c) {
            switch (state) {
                case 0: if (CharTypes.isSmall(c)) return 1 ; else return 2 ;
                case 1: if (CharTypes.isSmall(c) || CharTypes.isLarge(c) ||
                        CharTypes.isDigit(c) || c == '\'') return 1 ; else return 2 ;
                default: return 2 ; // garbage state, declared "dead" below
            }
        }

        boolean accepting (int state) {return (state == 1) ;}
        int dead () {return 2 ;}
    }

    static class NumAcceptor extends Acceptor implements DFA {
        public String lexClass() {return "NUM" ;}
        public int numberOfStates() {return 4 ;}

        int next (int state, char c) {
            switch (state) {
                case 0: if (c == '0') return 1 ; else if(CharTypes.isDigit(c) && c != '0') return 2; else return 3;
                case 1: return 3;
                case 2: if(CharTypes.isDigit(c)) return 2; else return 3;
                default: return 3 ; // garbage state, declared "dead" below
            }
        }

        boolean accepting (int state) {return (state == 1 || state == 2) ;}
        int dead () {return 3 ;}
    }

    static class BooleanAcceptor extends Acceptor implements DFA {
        public String lexClass() {return "BOOLEAN" ;}
        public int numberOfStates() {return 11 ;}

        int next (int state, char c) {
            switch (state) {
                case 0: if (c == 'T') return 1 ; else if(c == 'F') return 5; else return 10 ;
                case 1: if(c == 'r') return 2; else return 10;
                case 2: if(c == 'u') return 3; else return 10;
                case 3: if(c == 'e') return 4; else return 10;
                case 4: return 10;
                case 5: if(c == 'a') return 6; else return 10;
                case 6: if(c == 'l') return 7; else return 10;
                case 7: if(c == 's') return 8; else return 10;
                case 8: if(c == 'e') return 9; else return 10;
                case 9: return 10;
                default: return 10 ; // garbage state, declared "dead" below
            }
        }

        boolean accepting (int state) {return (state == 4 || state == 9) ;}
        int dead () {return 10 ;}
    }

    static class SymAcceptor extends Acceptor implements DFA {
        public String lexClass() {return "SYM" ;}
        public int numberOfStates() {return 3 ;}

        int next (int state, char c) {
            switch (state) {
                case 0: if (CharTypes.isSymbolic(c)) return 1 ; else return 2 ;
                case 1: if (CharTypes.isSymbolic(c)) return 1 ; else return 2 ;
                default: return 2 ; // garbage state, declared "dead" below
            }
        }

        boolean accepting (int state) {return (state == 1) ;}
        int dead () {return 2 ;}
    }

    static class WhitespaceAcceptor extends Acceptor implements DFA {
        public String lexClass() {return "" ;}
        public int numberOfStates() {return 3 ;}

        int next (int state, char c) {
            switch (state) {
                case 0: if (CharTypes.isWhitespace(c)) return 1 ; else return 2 ;
                case 1: if (CharTypes.isWhitespace(c)) return 1 ; else return 2 ;
                default: return 2 ; // garbage state, declared "dead" below
            }
        }

        boolean accepting (int state) {return (state == 1) ;}
        int dead () {return 2 ;}
    }

    static class CommentAcceptor extends Acceptor implements DFA {
        public String lexClass() {return "" ;}
        public int numberOfStates() {return 6 ;}

        int next (int state, char c) {
            switch (state) {
                case 0: if (c == '-') return 1; else return 5 ;
                case 1: if(c == '-') return 2; else return 5;
                case 2: if(c == '-') return 2; else if(!CharTypes.isSymbolic(c) && !CharTypes.isNewline(c)) return 3; else return 5;
                case 3: if(!CharTypes.isNewline(c)) return 4; else return 5;
                case 4: if(!CharTypes.isNewline(c)) return 4; else return 5;
                default: return 5 ; // garbage state, declared "dead" below
            }
        }

        boolean accepting (int state) {return (state == 2 || state == 4) ;}
        int dead () {return 5 ;}
    }

    static class TokAcceptor extends Acceptor implements DFA {

        String tok ;
        int tokLen ;
        TokAcceptor (String tok) {
            this.tok = tok ;
            tokLen = tok.length();
        }

        @Override
        public String lexClass() {return this.tok ;} ;

        @Override
        public int numberOfStates() {return tokLen + 2 ;} ;

        // garbage state: tokLen + 1
        // accepting state: tokLen

        @Override
        int next (int state, char input) {
            if (state < tokLen && tok.charAt(state) == input) {
                return state + 1;
            } else {
                return tokLen + 1;
            }
        }

        @Override
        boolean accepting (int state) {return state == tokLen ;}

        @Override
        int dead()  {return tokLen + 1 ;}
    }

    static DFA varAcc = new VarAcceptor() ;
    static DFA numAcc = new NumAcceptor() ;
    static DFA booleanAcc = new BooleanAcceptor() ;
    static DFA symAcc = new SymAcceptor() ;
    static DFA whiteSpaceAcc = new WhitespaceAcceptor() ;
    static DFA commentAcc = new CommentAcceptor() ;
    static DFA integerAcc = new TokAcceptor("Integer") ;
    static DFA boolAcc = new TokAcceptor("Bool") ;
    static DFA ifAcc = new TokAcceptor("if") ;
    static DFA thenAcc = new TokAcceptor("then") ;
    static DFA elseAcc = new TokAcceptor("else") ;
    static DFA leftBracketAcc = new TokAcceptor("(") ;
    static DFA rightBracketAcc = new TokAcceptor(")") ;
    static DFA semicolonAcc = new TokAcceptor(";") ;
    static DFA[] MH_acceptors =

    new DFA[] {boolAcc, integerAcc, ifAcc, thenAcc,  elseAcc, semicolonAcc, leftBracketAcc, rightBracketAcc, varAcc,
            numAcc, booleanAcc, whiteSpaceAcc, commentAcc, symAcc } ;

    MH_Lexer (Reader reader) {
        super(reader,MH_acceptors) ;
    }

}


