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
    
    private List<String> tiposTokens;
    private List<String> tokensSemTipo;
    private int index = 0;
    
    public boolean analisar(ArrayList<String> tokens){
        this.tiposTokens = this.getTiposTokens(tokens);
        this.tokensSemTipo = this.getTokensSemTipo(tokens);
        return S(index);
    }
    
    private boolean S(int index){
        
        if (tokensSemTipo.get(index).equals("not")) return A(index++);
        else return A(index);

    }
    
    private boolean A(int index){
        
        if (tiposTokens.get(index).equals("ID")){
            if (tokensSemTipo.get(++index).equals(".")) return G(++index);
            else return false;
        }
        
        else if (tokensSemTipo.get(index).equals("dataAtual")){
            return (A1(++index) && C(index));
        }
        
        return false;
    }
    
    private boolean A1(int index){
        
        if (tiposTokens.get(index).equals("SINAL") && !(tokensSemTipo.get(index).equals("."))){
            return A2(++index);
        }
        else return false;
        
    }
    
    private boolean A2(int index){
        
        if (tiposTokens.get(index).equals("ID")){
            
            if(tokensSemTipo.get(++index).equals(".")) return A3(++index);
            else return false;
            
        }
        
        else return false;
        
    }
    
    private boolean A3(int index){
        
        return (tokensSemTipo.get(index).equals("di")) || 
                (tokensSemTipo.get(index).equals("df"));
        
    }
    
    private boolean B(int index){
        
        if (tiposTokens.get(index).equals("SINAL") && !(tokensSemTipo.get(index).equals("."))){
            return D(++index);
        }
        else return false;
        
    }
    
    
    private boolean C(int index){
        
        if ((tokensSemTipo.get(index).equals("and")) || 
                (tokensSemTipo.get(index).equals("or"))) return S(++index);
        
        else return (index == tokensSemTipo.size()-1);
    }
    
    private boolean D(int index){
        
        if (tiposTokens.get(index).equals("NUM")) return true;
        
        else if (tiposTokens.get(index).equals("ID")){
            return ((tokensSemTipo.get(++index).equals(".")) && 
                    (tokensSemTipo.get(++index).equals("n")));
        }
        
        return false;
    }
    
    private boolean E(int index){
        
        return ((tokensSemTipo.get(index).equals("true")) ||
                tokensSemTipo.get(index).equals("false"));

    }

    
    private boolean G(int index){
        
        if (tokensSemTipo.get(index).equals("n")) return (B(++index) && C(index));
        
        else if ((tokensSemTipo.get(index).equals("p")) && 
                (tokensSemTipo.get(++index).equals("="))) return (E(++index) && C(index));
        
        else if ((tokensSemTipo.get(index).equals("in")) || 
                (tokensSemTipo.get(index).equals("out"))) {
            
            if (tokensSemTipo.get(++index).equals("=")) return (E(++index) && C(index));
        }
        
        return false;
    }
    
    public List<String> getTokensSemTipo(ArrayList<String> tokens){
        
        for (String token1 : tokens) {
            tokensSemTipo.add(token1.split(",")[0]);
        }
        
        return tokensSemTipo;
    }
    
    public List<String> getTiposTokens(ArrayList<String> tokens){
        
        for (String token1 : tokens) {
            tokensSemTipo.add(token1.split(",")[1]);
        }
        
        return tokensSemTipo;
    }
}
