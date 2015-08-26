/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.atid.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Fabricia
 */
public class CompilerATID {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Scanner sc = new Scanner(System.in);
        
        System.out.println("Digite o trecho de codigo:\n");
        String trecho = sc.nextLine();
        
        LexicalAnalyzer lexico = new LexicalAnalyzer();
        lexico.analisar(trecho);
        
        List<String> lista = lexico.getTokens();
        
        for (String token : lista){
            
            System.out.println(token);
        }
        
    }
    
}
