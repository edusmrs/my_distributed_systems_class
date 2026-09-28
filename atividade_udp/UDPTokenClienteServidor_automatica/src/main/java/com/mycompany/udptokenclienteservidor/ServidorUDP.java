/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.udptokenclienteservidor;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.text.Normalizer;
import java.util.concurrent.ConcurrentHashMap;
/**
 *
 * @author edusmrs
 */
public class ServidorUDP {
    private DatagramSocket socket;
    private ConcurrentHashMap<String, String> usuariosCadastrados = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, GeradorToken> threadsAtivas = new ConcurrentHashMap<>();
    
    public ServidorUDP() {
        try {
            socket = new DatagramSocket(1234);
            System.out.println("escutando porta 1234");
            while (true) {
                DatagramPacket pacote = ComunicadorUDP.recebeMensagem(socket);
                
                if (pacote != null) {
                    processarRequisicao(pacote);
                }
            }
        } catch (Exception ex) {
            System.err.println("erro");
            ex.printStackTrace();
        }
    }
    
    private String gerarEmailUFN(String nomeCompleto) {
        String nomeLimpo = Normalizer.normalize(nomeCompleto.trim().toLowerCase(), Normalizer.Form.NFD)
                                     .replaceAll("[^\\p{ASCII}]", "");
        
        String[] partes = nomeLimpo.split("\\s+");
        
        if (partes.length == 1) {
            return partes[0] + "@ufn.edu.br"; 
        } else {
            String primeiroNome = partes[0];
            String ultimoSobrenome = partes[partes.length - 1];
            return primeiroNome + "." + ultimoSobrenome + "@ufn.edu.br";
        }
    }
    
    private void processarRequisicao(DatagramPacket pacote) {
        String mensagem = new String(pacote.getData()).trim();
        String ipCliente = pacote.getAddress().getHostAddress();
        int portaCliente = pacote.getPort();
        
        System.out.println("pacote recebido de " + ipCliente + ":" + portaCliente + " -> comando: " + mensagem);
        
        String[] partes = mensagem.split(";");
        String comando = partes[0];
        
        if (comando.equals("CADASTRO") && partes.length == 2) {
            String nome = partes[1];
            String emailGerado = gerarEmailUFN(nome);
            
            if (!usuariosCadastrados.containsKey(emailGerado)) {
                usuariosCadastrados.put(emailGerado, nome);
                responderCliente("OK_CADASTRO;" + emailGerado, ipCliente, portaCliente);
            } else {
                responderCliente("ERRO;Email " + emailGerado + " já está em uso.", ipCliente, portaCliente);
            }
            
        } else if (comando.equals("LOGIN") && partes.length == 2) {
            String email = partes[1];
            
            if (usuariosCadastrados.containsKey(email)) {
                // impedir login duplicado
                if (threadsAtivas.containsKey(email)) {
                    threadsAtivas.get(email).parar(); 
                }
                
                String nomeUsuario = usuariosCadastrados.get(email);
                GeradorToken novaTask = new GeradorToken(nomeUsuario, email, pacote.getAddress(), portaCliente, socket);
                threadsAtivas.put(email, novaTask);
                novaTask.start();
                
                responderCliente("OK_LOGIN;autenticado. enviando token", ipCliente, portaCliente);
            } else {
                responderCliente("ERRO;email n cadastrado, faça o cadastro primeiro", ipCliente, portaCliente);
            }
        }
    }

    private void responderCliente(String resposta, String ip, int porta) {
        DatagramPacket pacoteResposta = ComunicadorUDP.montaMensagem(resposta, ip, porta);
        ComunicadorUDP.enviaMensagem(socket, pacoteResposta);
    }

    public static void main(String[] args) {
        new ServidorUDP();
    }
}
