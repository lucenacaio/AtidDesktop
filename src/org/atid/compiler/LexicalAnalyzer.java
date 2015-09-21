/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.atid.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Fabricia
 */
public class LexicalAnalyzer {
    
    //Palavras Reservadas da Gramatica
    private List<String> keywords;
    
    //Sinais da Gramatica
    private List<String> operators;
    
    //Letras da Gramatica
    private List<String> letters;
    
    //Digitos da Gramatica
    private List<String> digits;
    
    private char[] symbols;
    
    //String para armazenar cada token encontrado
    private StringBuilder token;
    //Lista de tokens encontrados
    private List<String> tokens;
    //Contador do index
    private int index = 0;
    

    public LexicalAnalyzer() {
        
        this.keywords = new ArrayList<String>(Arrays.asList(
            new String[]{"not", "dataAtual", "di", "df", "in",
        "out", "and", "or", "n", "true", "false"}));
        
        this.operators = new ArrayList<String>(Arrays.asList( new String[]
            {"=", "<", ">", "!", "."}));
        
        this.letters = new ArrayList<String>(Arrays.asList( new String[]
            {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
        "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "x", "z", "w"}));
        
        this.digits = new ArrayList<String>(Arrays.asList( new String[]
            {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"}));
        
        this.token = new StringBuilder();
        this.tokens = new ArrayList<String>();
        
    }
    
    public boolean analyze(String value){
        
        String text = prepareText(value);
        symbols = text.toCharArray();
        
        for (char s : symbols){
            
            String symbol = String.valueOf(s);
            
            if (isLetter(symbol)){
                
                if ( (token.length() != 0) && (isOperator(String.valueOf(token.toString().charAt(0)))) ){
                    addToken(TokenTypesEnum.OPERATOR.name());
                }
                
                else if ( (token.length() != 0) && (isDigit(String.valueOf(token.toString().charAt(0)))) ){
                    return false;
                }
                
                token.append(symbol);
                
                if ( isLastSymbol() ) analyzeWord();
                
            }
            else if (isDigit(symbol)){
                //Se tiver value no token e for um sinal, ele deve ser adicionado a lista de tokens
                if ( (token.length() != 0) && (isOperator(String.valueOf(token.toString().charAt(0)))) ){
                    addToken(TokenTypesEnum.OPERATOR.name());
                }
                token.append(symbol);
                //Se o symbol for o ultimo do array, ele é adicionado a lista de tokens
                if ( (isLastSymbol()) && (isDigit(String.valueOf(token.toString().charAt(0)))) ){
                    addToken(TokenTypesEnum.NUM.name());
                }
                else if ( (isLastSymbol()) && (isLetter(String.valueOf(token.toString().charAt(0)))) ){
                    analyzeWord();
                }
            }
            
            else if (isOperator(symbol)){
                
                if ( isLastSymbol() ) {
                    token.append(symbol);
                    addToken(TokenTypesEnum.OPERATOR.name());
                }
                
                else if ( (token.length() != 0) && (isOperator(String.valueOf(token.toString().charAt(0)))) ){
                    
                    token.append(symbol);
                    
                    if (isDoubleOperator()) addToken(TokenTypesEnum.OPERATOR.name());
                    
                    else{
                        token.deleteCharAt(token.length()-1);
                        addToken(TokenTypesEnum.OPERATOR.name());
                        token.append(symbol);
                        addToken(TokenTypesEnum.OPERATOR.name());
                    }
                } 
                
                else if ( (token.length() != 0) && (isLetter(String.valueOf(token.toString().charAt(0)))) ){
                    analyzeWord();
                    token.append(symbol);
                }
                
                else if ( (token.length() != 0) && (isDigit(String.valueOf(token.toString().charAt(0)))) ){
                    //adicionarToken(TokenTypesEnum.NUM.name());
                    token.append(symbol);
                }
                
                else token.append(symbol);
            }
            
            else if (isSpace(symbol)){
                
                if ( (token.length() != 0) && (isLetter(String.valueOf(token.toString().charAt(0)))) ){
                    analyzeWord();
                }
                else if ( (token.length() != 0) && (isDigit(String.valueOf(token.toString().charAt(0)))) ){
                    addToken(TokenTypesEnum.NUM.name());
                }
                
                else if ( (token.length() != 0) && (isOperator(String.valueOf(token.toString().charAt(0)))) ){
                    addToken(TokenTypesEnum.OPERATOR.name());
                }
            }
            
            else {
                return false;
            }
         index++;   
        }
        return true;
    }
    
    private void analyzeWord(){
        
        if (isKeyword(token.toString())){
            addToken(TokenTypesEnum.KEYWORD.name());
        }
        
        else{
            addToken(TokenTypesEnum.ID.name());
        }
    }
    
    private void addToken(String tipo){
        token.append(",");
        token.append(tipo);
        tokens.add(token.toString());
        
        token.delete(0, token.length());
    }
    
    private String prepareText(String value){
        String text = value.replaceAll("\n", " ");
        text = text.replaceAll("\t", " ");
        
        return text;
    }
    
    
    private boolean isDoubleOperator(){
        
        String operator1 = String.valueOf(token.toString().charAt(0));
        String operator2 = String.valueOf(token.toString().charAt(1));
        
        if ( (operator1.equals(">")) && (operator2.equals("="))) {
            return true;
        }
        else if ( (operator1.equals("<")) && (operator2.equals("=")) ){
            return true;
        }
        else if ( (operator1.equals("!")) && (operator2.equals("=")) ){
            return true;
        }
        
        return false;
    }
    
    private boolean isSpace(String valor){
        return (valor.equals(" "));
    }
    
    private boolean isLetter(String valor){
        return (letters.contains(valor.toLowerCase()));
    }
    
    private boolean isOperator(String valor){
        return (operators.contains(valor));
    }
    
    private boolean isDigit(String valor){
        return (digits.contains((valor)));
    }
    
    private boolean isKeyword(String valor){
        return (keywords.contains(valor));
    }
    
    public List<String> getTokens(){
        return tokens;
    }
    
    private boolean isLastSymbol(){
        return (index == symbols.length-1);
    }
}
