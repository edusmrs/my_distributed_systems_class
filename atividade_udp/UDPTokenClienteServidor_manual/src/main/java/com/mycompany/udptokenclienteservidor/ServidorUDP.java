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
    // ConcurrentHashMap é crucial aqui para evitar problemas com múltiplas threads acessando a lista ao mesmo tempo.
    private ConcurrentHashMap<String, GeradorToken> usuariosRegistrados;

    public ServidorUDP() {
        usuariosRegistrados = new ConcurrentHashMap<>();
        try {
            socket = new DatagramSocket(1234); 
            System.out.println("Servidor ativo (Porta 1234) sem interface gráfica. Aguardando clientes...");
            
            while (true) {
                DatagramPacket pacote = ComunicadorUDP.recebeMensagem(socket);
                if (pacote != null) {
                    processarRequisicao(pacote);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private String gerarEmailUFN(String nomeCompleto) {
        // Remove acentos e deixa tudo minúsculo (boa prática para emails)
        String nomeLimpo = Normalizer.normalize(nomeCompleto.trim().toLowerCase(), Normalizer.Form.NFD)
                                     .replaceAll("[^\\p{ASCII}]", "");
        
        String[] partes = nomeLimpo.split("\\s+");
        
        if (partes.length == 1) {
            return partes[0] + "@ufn.edu.br"; // Se a pessoa mandou só um nome
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
        
        System.out.println("[LOG] Pacote recebido de " + ipCliente + ":" + portaCliente + " -> Comando: " + mensagem);
        
        String[] partes = mensagem.split(";");
        String comando = partes[0];
        
        if (comando.equals("CADASTRO") && partes.length == 2) {
            String nome = partes[1];
            String emailGerado = gerarEmailUFN(nome);
            
            if (!usuariosRegistrados.containsKey(emailGerado)) {
                GeradorToken novaTask = new GeradorToken(nome, emailGerado);
                novaTask.start();
                usuariosRegistrados.put(emailGerado, novaTask);

                responderCliente("OK;" + emailGerado, ipCliente, portaCliente);
            } else {
                responderCliente("ERRO;Email " + emailGerado + " já está em uso.", ipCliente, portaCliente);
            }
            
        } else if (comando.equals("SOLICITAR_TOKEN") && partes.length == 2) {
            String email = partes[1];
            if (usuariosRegistrados.containsKey(email)) {
                String token = usuariosRegistrados.get(email).getTokenAtual();
                responderCliente("TOKEN;" + token, ipCliente, portaCliente);
            } else {
                responderCliente("ERRO;Usuário não encontrado.", ipCliente, portaCliente);
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
