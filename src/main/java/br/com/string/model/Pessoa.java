package br.com.string.model;

import java.time.Instant;
import java.util.Date;

public class Pessoa {
    private String nome;
    private String cpf;
    private Instant dataNascimento ;
    private String novoAtributo;

    public Pessoa() {
    }

    public Pessoa(String nome, String cpf) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = Instant.parse("2024-01-01T00:00:00Z");
    }

    public Pessoa(String nome, String cpf, Instant agora) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = agora;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNovoAtributo() {
        return novoAtributo;
    }

    public void setNovoAtributo(String novoAtributo) {
        this.novoAtributo = novoAtributo;
    }

    public Instant getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Instant dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    @Override
    public String toString() {
        return "Pessoa{" +
                "nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", dataNascimento=" + dataNascimento +
                '}';
    }
}
