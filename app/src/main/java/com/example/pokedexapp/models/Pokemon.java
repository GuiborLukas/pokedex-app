
package com.example.pokedexapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class Pokemon implements Serializable {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("nome")
    @Expose
    private String nome;
    @SerializedName("tipo")
    @Expose
    private String tipo;
    @SerializedName("habilidade")
    @Expose
    private List<String> habilidade;
    @SerializedName("foto")
    @Expose
    private String foto;
    @SerializedName("usuario")
    @Expose
    private Long usuario;

    /**
     * No args constructor for use in serialization
     */
    public Pokemon() {
    }

    /**
     * @param id
     * @param nome
     * @param tipo
     * @param habilidade
     * @param foto
     * @param id
     */
    public Pokemon(Long id, String nome, String tipo, List<String> habilidade, String foto, Long usuario) {
        super();
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.habilidade = habilidade;
        this.foto = foto;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<String> getHabilidade() {
        return habilidade;
    }

    public void setHabilidade(List<String> habilidade) {
        this.habilidade = habilidade;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Long getUsuario() {
        return usuario;
    }

    public void setUsuario(Long usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Pokemon.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null) ? "<null>" : this.id));
        sb.append(',');
        sb.append("nome");
        sb.append('=');
        sb.append(((this.nome == null) ? "<null>" : this.nome));
        sb.append(',');
        sb.append("tipo");
        sb.append('=');
        sb.append(((this.tipo == null) ? "<null>" : this.tipo));
        sb.append(',');
        sb.append("habilidade");
        sb.append('=');
        sb.append(((this.habilidade == null) ? "<null>" : this.habilidade));
        sb.append(',');
        sb.append("foto");
        sb.append('=');
        sb.append(((this.foto == null) ? "<null>" : this.foto));
        sb.append(',');
        sb.append("usuario");
        sb.append('=');
        sb.append(((this.usuario == null) ? "<null>" : this.usuario));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
