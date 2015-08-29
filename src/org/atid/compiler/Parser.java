/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.atid.compiler;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Fabricia
 */
public class Parser {
    
    private List<String> tokenTypes;
    private List<String> tokens;
    private int index = 0;
    
    public Parser(){
        this.tokenTypes = new ArrayList<String>();
        this.tokens = new ArrayList<String>();
    }
    
    public boolean analyze(List<String> listToken){
        this.tokenTypes = this.getTokenTypes(listToken);
        this.tokens = this.getTokens(listToken);
        return S();
    }
    
    private boolean S(){
        
        if (tokens.get(index).equals("not")){
            index++;
            return (A());
        }
        else{
            return (A());
        }

    }
    
    private boolean A(){
        
        if (tokenTypes.get(index).equals(TokenTypesEnum.ID.name())){
            index++;
            if (tokens.get(index).equals(".")){
                index++;
                return G();
            }
            else return false;
        }
        
        else if (tokens.get(index).equals("dataAtual")){
            index++;
            return (A1() && C());
        }
        
        return false;
    }
    
    private boolean A1(){
        
        if (tokenTypes.get(index).equals(TokenTypesEnum.OPERATOR.name()) &&
                !(tokens.get(index).equals("."))){
            index++;
            return A2();
        }
        else return false;
        
    }
    
    private boolean A2(){
        
        if (tokenTypes.get(index).equals(TokenTypesEnum.ID.name())){
            index++;
            if(tokens.get(index).equals(".")){
                index++;
                return A3();
            }
            else return false;
            
        }
        
        else return false;
        
    }
    
    private boolean A3(){
        
        if ((tokens.get(index).equals("di")) || 
                (tokens.get(index).equals("df"))){
            index++;
            return true;
        }
        else return false;
        
    }
    
    private boolean B(){
        
        if (tokenTypes.get(index).equals(TokenTypesEnum.OPERATOR.name()) && !(tokens.get(index).equals("."))){
            index++;
            return D();
        }
        else return false;
        
    }
    
    
    private boolean C(){
        
        if (index == (tokens.size())) return true;
        
        else if ((tokens.get(index).equals("and")) || 
                (tokens.get(index).equals("or"))){
            index++;
            return S();
        }
        
        else return false;
    }
    
    private boolean D(){
        
        if (tokenTypes.get(index).equals(TokenTypesEnum.NUM.name())){
            index++;
            return true;
        }
        
        else if (tokenTypes.get(index).equals(TokenTypesEnum.ID.name())){
            index++;
            if ((tokens.get(index).equals(".")) && 
                    (tokens.get(++index).equals("n"))){
                index++;
                return true;
            }
            else return false;
        }
        
        return false;
    }
    
    private boolean E(){
        
        if ((tokens.get(index).equals("true")) ||
                tokens.get(index).equals("false")){
            index++;
            return true;
        }
        
        else return false;
    }

    
    private boolean G(){
        
        if (tokens.get(index).equals("n")){
            index++;
            return (B() && C());
        }
        
        else if ((tokens.get(index).equals("p")) && 
                (tokens.get(++index).equals("="))){
            index++;
            return (E() && C());
        }
        
        else if ((tokens.get(index).equals("in")) || 
                (tokens.get(index).equals("out"))) {
            
            if (tokens.get(++index).equals("=")){
                index++;
                return (E() && C());
            }
        }
        
        return false;
    }
    
    public List<String> getTokens(List<String> tokens){
        
        for (String token1 : tokens) {
            this.tokens.add(token1.split(",")[0]);
        }
        
        return this.tokens;
    }
    
    public List<String> getTokenTypes(List<String> tokens){

        for (String token1 : tokens) {
            tokenTypes.add(token1.split(",")[1]);
        }
        
        return tokenTypes;
    }
}
