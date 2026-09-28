/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.udptokenclienteservidor;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
    
    private InetAddress ipCliente;
    private int portaCliente;
    private DatagramSocket socketServidor;

    public GeradorToken(String nome, String email, InetAddress ipCliente, int portaCliente, DatagramSocket socketServidor) {
        this.nome = nome;
        this.email = email;
        this.ipCliente = ipCliente;
        this.portaCliente = portaCliente;
        this.socketServidor = socketServidor;
    }

    private void gerarEEnviarToken() {
        this.tokenAtual = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        System.out.println("gerou e enviou token automatico para " + email + ": " + tokenAtual);
        
        String mensagemPush = "NOVO_TOKEN;" + tokenAtual;
        DatagramPacket pacotePush = ComunicadorUDP.montaMensagem(mensagemPush, ipCliente.getHostAddress(), portaCliente);
        ComunicadorUDP.enviaMensagem(socketServidor, pacotePush);
    }
    
    public void parar() {
        this.rodando = false;
        this.interrupt();
    }

    @Override
    public void run() {
        gerarEEnviarToken();
        
        while (rodando) {
            try {
                Thread.sleep(10000); // o token dura 10s
                gerarEEnviarToken();
            } catch (InterruptedException e) {
                rodando = false;
            }
        }
    }
    
}
