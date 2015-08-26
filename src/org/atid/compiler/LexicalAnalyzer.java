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
    private List<String> palavrasReservadas;
    
    //Sinais da Gramatica
    private List<String> sinais;
    
    //Letras da Gramatica
    private List<String> letras;
    
    //Digitos da Gramatica
    private List<String> digitos;
    
    
    private char[] simbolos;
    //String para armazenar cada token encontrado
    private StringBuilder token;
    //Lista de tokens encontrados
    private List<String> tokens;
    //Contador do index
    private int index = 0;
    
    private List<String> tokensSemTipo;

    public LexicalAnalyzer() {
        
        this.palavrasReservadas = new ArrayList<String>(Arrays.asList(
            new String[]{"not", "dataAtual", "p", "di", "df", "in",
        "out", "and", "or", "n", "true", "false"}));
        
        this.sinais = new ArrayList<String>(Arrays.asList( new String[]
            {"=", "<", ">", "!", "."}));
        
        this.letras = new ArrayList<String>(Arrays.asList( new String[]
            {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
        "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "x", "z", "w"}));
        
        this.digitos = new ArrayList<String>(Arrays.asList( new String[]
            {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"}));
        
        this.token = new StringBuilder();
        this.tokens = new ArrayList<String>();
        this.tokensSemTipo = new ArrayList<String>();
        
    }
    
    public boolean analisar(String valor){
        
        String texto = tratarTexto(valor);
        simbolos = texto.toCharArray();
        
        for (char s : simbolos){
            
            String simbolo = String.valueOf(s);
            
            if (isLetra(simbolo)){
                
                if ( (token.length() != 0) && (isSinal(String.valueOf(token.toString().charAt(0)))) ){
                    adicionarToken(TipoTokenEnum.SINAL.name());
                }
                
                else if ( (token.length() != 0) && (isDigito(String.valueOf(token.toString().charAt(0)))) ){
                    return false;
                }
                
                token.append(simbolo);
                
                if ( isUltimoSimbolo() ) analisarPalavra();
                
            }
            else if (isDigito(simbolo)){
                //Se tiver valor no token e for um sinal, ele deve ser adicionado a lista de tokens
                if ( (token.length() != 0) && (isSinal(String.valueOf(token.toString().charAt(0)))) ){
                    adicionarToken(TipoTokenEnum.SINAL.name());
                }
                token.append(simbolo);
                //Se o simbolo for o ultimo do array, ele é adicionado a lista de tokens
                if ( (isUltimoSimbolo()) && (isDigito(String.valueOf(token.toString().charAt(0)))) ){
                    adicionarToken(TipoTokenEnum.NUM.name());
                }
                else if ( (isUltimoSimbolo()) && (isLetra(String.valueOf(token.toString().charAt(0)))) ){
                    analisarPalavra();
                }
            }
            
            else if (isSinal(simbolo)){
                
                if ( isUltimoSimbolo() ) {
                    token.append(simbolo);
                    adicionarToken(TipoTokenEnum.SINAL.name());
                }
                
                else if ( (token.length() != 0) && (isSinal(String.valueOf(token.toString().charAt(0)))) ){
                    
                    token.append(simbolo);
                    
                    if (isSinalDuplo()) adicionarToken(TipoTokenEnum.SINAL.name());
                    
                    else{
                        token.deleteCharAt(token.length()-1);
                        adicionarToken(TipoTokenEnum.SINAL.name());
                        token.append(simbolo);
                        adicionarToken(TipoTokenEnum.SINAL.name());
                    }
                } 
                
                else if ( (token.length() != 0) && (isLetra(String.valueOf(token.toString().charAt(0)))) ){
                    analisarPalavra();
                    token.append(simbolo);
                }
                
                else if ( (token.length() != 0) && (isDigito(String.valueOf(token.toString().charAt(0)))) ){
                    adicionarToken(TipoTokenEnum.NUM.name());
                    token.append(simbolo);
                }
                
                else token.append(simbolo);
            }
            
            else if (isEspaco(simbolo)){
                
                if ( (token.length() != 0) && (isLetra(String.valueOf(token.toString().charAt(0)))) ){
                    analisarPalavra();
                }
                else if ( (token.length() != 0) && (isDigito(String.valueOf(token.toString().charAt(0)))) ){
                    adicionarToken(TipoTokenEnum.NUM.name());
                }
                
                else if ( (token.length() != 0) && (isSinal(String.valueOf(token.toString().charAt(0)))) ){
                    adicionarToken(TipoTokenEnum.SINAL.name());
                }
            }
            
            else {
                return false;
            }
         index++;   
        }
        return true;
    }
    
    private void analisarPalavra(){
        
        if (isPalavraReservada(token.toString())){
            adicionarToken(TipoTokenEnum.PALAVRARESERVADA.name());
        }
        
        else{
            adicionarToken(TipoTokenEnum.ID.name());
        }
    }
    
    private void adicionarToken(String tipo){
        token.append(",");
        token.append(tipo);
        tokens.add(token.toString());
        
        token.delete(0, token.length());
    }
    
    private String tratarTexto(String valor){
        String texto = valor.replaceAll("\n", " ");
        texto = texto.replaceAll("\t", " ");
        
        return texto;
    }
    
    
    private boolean isSinalDuplo(){
        
        String sinal1 = String.valueOf(token.toString().charAt(0));
        String sinal2 = String.valueOf(token.toString().charAt(1));
        
        if ( (sinal1.equals(">")) && (sinal2.equals("="))) {
            return true;
        }
        else if ( (sinal1.equals("<")) && (sinal2.equals("=")) ){
            return true;
        }
        else if ( (sinal1.equals("!")) && (sinal2.equals("=")) ){
            return true;
        }
        
        return false;
    }
    
    private boolean isEspaco(String valor){
        return (valor.equals(" "));
    }
    
    private boolean isLetra(String valor){
        return (letras.contains(valor.toLowerCase()));
    }
    
    private boolean isSinal(String valor){
        return (sinais.contains(valor));
    }
    
    private boolean isDigito(String valor){
        return (digitos.contains((valor)));
    }
    
    private boolean isPalavraReservada(String valor){
        return (palavrasReservadas.contains(valor));
    }
    
    public List<String> getTokens(){
        return tokens;
    }
    
    private boolean isUltimoSimbolo(){
        return (index == simbolos.length-1);
    }
}
