/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.udptokenclienteservidor;
import java.util.UUID;
/**
 *
 * @author edusmrs
 */
public class GeradorToken extends Thread {
    private String nome;
    private String email;
    private String tokenAtual;
    private boolean rodando = true;

    public GeradorToken(String nome, String email) {
        this.nome = nome;
        this.email = email;
        gerarNovoToken();
    }

    private void gerarNovoToken() {
        // Gera um token aleatório simples de 6 caracteres
        this.tokenAtual = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        System.out.println("[Servidor] Novo token para " + email + ": " + tokenAtual);
    }

    public String getTokenAtual() {
        return tokenAtual;
    }
    
    public String getNome() {
        return nome;
    }

    @Override
    public void run() {
        while (rodando) {
            try {
                Thread.sleep(10000); // o token dura 10s
                gerarNovoToken();
            } catch (InterruptedException e) {
                rodando = false;
            }
        }
    }
    
}
