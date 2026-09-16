package cadastroacademico;

import java.io.Serializable;
import java.util.Objects;

public class Pessoa implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String nome;
    private String dataNascimento;
    private String email;

    public Pessoa(String nome, String dataNascimento) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
    }

    public String getNome() { return nome; }
    public String getDataNascimento() { return dataNascimento; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pessoa other = (Pessoa) obj;
        // ignora duplicidade
        return Objects.equals(this.nome, other.nome) && 
               Objects.equals(this.dataNascimento, other.dataNascimento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, dataNascimento);
    }

    @Override
    public String toString() {
        return nome + " | " + dataNascimento + " | " + (email != null ? email : "Sem email");
    }
}