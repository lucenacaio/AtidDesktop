/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.atid.compiler;

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
        trecho = trecho.replace(',', '.');
        
        LexicalAnalyzer lexico = new LexicalAnalyzer();
        Parser sintatico = new Parser();
        if (lexico.analyze(trecho)){
            for(String n : lexico.getTokens()){
                System.out.println(n);
            }
            System.out.println(sintatico.analyze(lexico.getTokens()));
        }
        
//        List<String> lista = lexico.getTokens();
//        
//        for (String token : lista){
//            
//            System.out.println(token);
//        }
        
    }
    
}
